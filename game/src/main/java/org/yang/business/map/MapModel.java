package org.yang.business.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.active.impl.MoveImpl;
import org.yang.business.calc.DataCalc;
import org.yang.business.calc.IterModel;
import org.yang.business.camp.ICamp;
import org.yang.business.grade.IRoleType;
import org.yang.business.grid.ExampleGrid;
import org.yang.business.grid.IGrid;
import org.yang.business.instruction.ICommand;
import org.yang.business.role.RoleModel;

import org.yang.springboot.socket.SocketServer;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 小沙场
 */

@Slf4j
@Data
public class MapModel {

    public final Map<ICamp, Map<Class<? extends IRoleType>, List<RoleModel>>> campMemRole;//战场阵容内存角色归类
    public transient final Map<ICamp, Map<Class<? extends IRoleType>, List<RoleModel>>> campKillRole;//击杀阵容角色归类
    public transient final Map<ICamp, Map<Class<? extends IRoleType>, List<RoleModel>>> campRetreatRole;//撤退阵容角色归类
    public transient final Set<ICamp> allCampSet;//所有阵容


    public transient final Map<Integer, RoleModel> roleIdMap;//所有角色idMap
    final private IGrid[][] grids;//格子
    final transient private RoleModel[][] roleModels;//地图上的角色
    private int X;//地图坐标上限
    private int Y;
    private transient int round;//回合数
    private Map<ICamp, RoleModel> campLocation;//大本营位置


    public MapModel() {
        this(35, 15);//设置默认值
    }

    public MapModel(int x, int y) {
        this.X = x;
        this.Y = y;
        this.grids = new IGrid[this.X][this.Y];//地图范围
        this.roleModels = new RoleModel[this.X][this.Y];//角色范围
        ExampleGrid.defaultGrid(grids);//生成地图
        this.round = 0;

        allCampSet = new HashSet<>();//所有阵容
        campMemRole = new HashMap<>();//战场阵容内存角色归类
        campKillRole = new HashMap<>();//击杀阵容角色归类
        campRetreatRole = new HashMap<>();//撤退阵容角色归类
        roleIdMap = new HashMap<>();//所有角色idMap
        campLocation = new HashMap<>();//大本营位置
    }

    /**
     * 添加大本营
     *
     * @param campClass 阵营类型
     * @param x         坐标
     * @param y         坐标
     */
    public void addCampLocation(Class<? extends ICamp> campClass, int x, int y) {
        ICamp camp = ICamp.classMap.get(campClass);
        if (isCrossMap(x, y))
            throw new RuntimeException(String.format("坐标(%d,%d)越界, 规定范围是([0-%d],[0-%d])", x, y, X - 1, Y - 1));
        if (this.campLocation.containsKey(camp)) {//校验营地是否存在
            RoleModel roleModel = this.campLocation.get(camp);
            throw new RuntimeException(String.format("阵营(%s)已存在,坐标: (%d,%d)", camp.getName(), roleModel.getX(), roleModel.getY()));
        }
        RoleModel roleModel = new RoleModel();
        roleModel.setX(x);
        roleModel.setY(y);
        roleModel.setCamp(camp);
        this.campLocation.put(camp, roleModel);
    }

    /**
     * 添加角色到战场
     *
     * @param roleModel 角色
     * @param x         坐标
     * @param y         坐标
     */
    public synchronized void addRoleModel(RoleModel roleModel, int x, int y) {
        if (isCrossMap(x, y))
            throw new RuntimeException(String.format("坐标(%d,%d)越界, 规定范围是([0-%d],[0-%d])", x, y, X - 1, Y - 1));
        roleModel.load(this, x, y);
        roleModel.getRoleType().addRoleModel(roleModel);//写入阵容
        if (this.roleModels[x][y] != null)
            throw new RuntimeException(String.format("当前坐标(%d,%d)已经有角色%s", x, y, this.roleModels[x][y].getRoleType().getName()));
        this.roleModels[x][y] = roleModel;//写入地图
        this.roleIdMap.put(roleModel.getId(), roleModel);//添加角色索引
    }


    /**
     * 击杀角色清理数据
     *
     * @param roleModel 需要击杀的对象
     */
    public void killRole(RoleModel roleModel) {
        roleModel.getRoleType().killRole(roleModel);//移除阵容中的角色
        this.roleModels[roleModel.getX()][roleModel.getY()] = null;//移除地图上的角色数据
    }

    /**
     * 撤退角色清理数据
     *
     * @param roleModel 需要撤退的对象
     */
    public void retreatRole(RoleModel roleModel) {
        roleModel.getRoleType().retreatRole(roleModel);//移除阵容中的角色
        this.roleModels[roleModel.getX()][roleModel.getY()] = null;//移除地图上的角色数据
    }

    /**
     * 更新指令
     *
     * @param campClass     阵营编号
     * @param roleTypeClass 角色类型
     * @param command       指令
     */
    public void updateCommand(Class<? extends ICamp> campClass, Class<? extends IRoleType> roleTypeClass, Class<? extends ICommand> command) {
        IRoleType.updateCommand(campClass, roleTypeClass, command, this);
    }

    /**
     * 更新指令
     *
     * @param roleId  角色id
     * @param command 指令
     */
    public void updateCommand(Integer roleId, Class<? extends ICommand> command) {
        IRoleType.updateCommand(roleId, command, this);
    }


    /**
     * 运行一回合
     *
     * @return 是否回合结束
     */
    public boolean run() throws Exception {
        round++;//增加一回合
        //清空上一次的行动记录
        List<ICamp> campList = new ArrayList<>(this.campMemRole.keySet());//生成一个新的,还在场的阵营
        if (campList.size() == 1) return true;
        while (!campList.isEmpty()) {
            ICamp iCamp = IterModel.randomPop(campList);//随机获取阵营
            Map<Class<? extends IRoleType>, List<RoleModel>> roleTypeRoleListMap = this.campMemRole.get(iCamp);//
            if (roleTypeRoleListMap == null) break;
            List<Class<? extends IRoleType>> iRoleTypeList = new ArrayList<>(roleTypeRoleListMap.keySet());//生成一个新的,还在场的阵营
            while (!iRoleTypeList.isEmpty()) {
                Class<? extends IRoleType> iRoleType = IterModel.randomPop(iRoleTypeList);//随机获取角色类型
                List<RoleModel> roleModelsList = roleTypeRoleListMap.get(iRoleType);
                List<RoleModel> roleModelsListIter = new ArrayList<>(roleModelsList);
                for (RoleModel roleModel : roleModelsListIter) {
                    roleModel.getCommand().proxyRun(roleModel);
                }
            }
        }
        return false;
    }

    /**
     * 获取距离目标最近的敌方角色
     *
     * @param roleModel 目标角色
     * @return 距离目标角色最近的角色
     */
    public RoleModel getRecentlyEnemyRole(RoleModel roleModel) {
        int maxLength = this.X + this.Y - 1;
        for (int i = 1; i < maxLength; i++) {
            List<RoleModel> roleModelList = getDistanceRole(roleModel, i);//获取距离当前目标指定距离的所有敌方角色
            if (roleModelList.isEmpty()) continue;
            return DataCalc.getRandomUnit(roleModelList);
        }
        log.error("当前角色id: [{}], 没有找到敌方目标", roleModel.getId());//没有目标
        return null;
    }


    /**
     * 获取指定目标相对距离的敌方角色
     *
     * @param roleModel 目标
     * @param distance  相对距离
     * @return 敌方角色列表
     */
    private List<RoleModel> getDistanceRole(RoleModel roleModel, int distance) {
        List<RoleModel> enemyRoleList = new ArrayList<>();
        for (int y = -distance; y <= distance; y++) {
            int x = distance - Math.abs(y);
            if (x != 0) {
                //如果没有越界并且不为空角色 并且是不同阵容 获取敌方角色列表
                addEnemyRole(x + roleModel.getX(), y + roleModel.getY(), roleModel, enemyRoleList);
            }
            addEnemyRole(-x + roleModel.getX(), y + roleModel.getY(), roleModel, enemyRoleList);
        }
        return enemyRoleList;
    }

    /**
     * 获取指定距离的敌方角色 校验是否越界,是否空角色,是否同阵容
     *
     * @param x             坐标
     * @param y             坐标
     * @param roleModel     角色
     * @param enemyRoleList 敌方角色列表
     */
    private void addEnemyRole(int x, int y, RoleModel roleModel, List<RoleModel> enemyRoleList) {
        if (!isNullRole(x, y) && this.roleModels[x][y].getCamp() != roleModel.getCamp()) {
            enemyRoleList.add(this.roleModels[x][y]);
        }
    }


    /**
     * 向目标方向移动
     *
     * @param role      被移动的角色
     * @param enemyRole 移动的终点角色
     */
    public void moveDistance(RoleModel role, RoleModel enemyRole) {
        int x = role.getX();
        int y = role.getY();
        int enemyX = enemyRole.getX();
        int enemyY = enemyRole.getY();
        int gapX = enemyX - x;//相对坐标值X
        int gapY = enemyY - y;//相对坐标值Y
        int tempX;
        int tempY;
        //比较相对坐标gapX,gapY的绝对值 让最大的相对坐标值更靠近0 ,如果最大的相对坐标值无法修改到靠近0(比如靠近的格子被占用) 将小的相对坐标值随机变化,小的相对坐标绝对值值不能变的比最大的坐标绝对值还要大
        List<Map.Entry<Integer, Integer>> coordinateList = new ArrayList<>();
        if (Math.abs(gapX) > Math.abs(gapY)) {//优先修改X
            tempX = x + (gapX > 0 ? 1 : -1);//X距离变小
            coordinateList.add(new AbstractMap.SimpleEntry<>(tempX, y));
            tempY = y + (gapY > 0 ? 1 : -1);//Y距离变小
            coordinateList.add(new AbstractMap.SimpleEntry<>(x, tempY));//其次修改Y
            tempY = y + (gapY > 0 ? -1 : 1);//Y距离反向
            coordinateList.add(new AbstractMap.SimpleEntry<>(x, tempY));//其次修改Y
        } else if (Math.abs(gapX) < Math.abs(gapY)) {//优先修改Y
            tempY = y + (gapY > 0 ? 1 : -1);//Y距离变小
            coordinateList.add(new AbstractMap.SimpleEntry<>(x, tempY));
            tempX = x + (gapX > 0 ? 1 : -1);//X距离变小
            coordinateList.add(new AbstractMap.SimpleEntry<>(tempX, y));
            tempX = x + (gapX > 0 ? -1 : 1);//X距离变大
            coordinateList.add(new AbstractMap.SimpleEntry<>(tempX, y));
        } else {//相等
            //XY随机顺序
            boolean probabilityTrigger = DataCalc.isProbabilityTrigger(0.5);
            if (probabilityTrigger) {
                tempY = y + (gapY > 0 ? 1 : -1);//Y距离变小
                coordinateList.add(new AbstractMap.SimpleEntry<>(x, tempY));
                tempX = x + (gapX > 0 ? 1 : -1);//X距离变小
                coordinateList.add(new AbstractMap.SimpleEntry<>(tempX, y));
            } else {
                tempX = x + (gapX > 0 ? 1 : -1);//X距离变小
                coordinateList.add(new AbstractMap.SimpleEntry<>(tempX, y));
                tempY = y + (gapY > 0 ? 1 : -1);//Y距离变小
                coordinateList.add(new AbstractMap.SimpleEntry<>(x, tempY));
            }
        }
        for (Map.Entry<Integer, Integer> coordinateUnit : coordinateList) {
            Integer currentX = coordinateUnit.getKey();
            Integer currentY = coordinateUnit.getValue();
            if (!isCrossMap(currentX, currentY) &&//没有越界,并且有空间 并且有移动多余的行动力气
                    this.roleModels[currentX][currentY] == null &&//目标位置有空间
                    this.grids[currentX][currentY].getAct() < role.getCurrentAct() //移动消耗行动力小于当前角色行动力
            ) {//移动角色
                //注意顺序 需要先设置行动后再设置坐标,否则行动坐标会被覆盖
                SocketServer.send(role.getCamp().getName(), new MoveImpl(role, currentX, currentY));
                this.roleModels[role.getX()][role.getY()] = null;//删除地图上的角色数据
                //重新设置地图上的角色数据
                this.roleModels[currentX][currentY] = role;
                //设置角色的坐标
                role.setX(currentX);
                role.setY(currentY);
                this.grids[currentX][currentY].enter(role);
                return;
            }

        }
        role.setCurrentAct(0);//无法移动,直接设置行动为0 禁用下一次行动
    }

    public void showBattleReport() {

        Map.Entry<ICamp, Map<Class<? extends IRoleType>, List<RoleModel>>> victoryEntry = campMemRole.entrySet().iterator().next();
        Map<Class<? extends IRoleType>, List<RoleModel>> memVictoryMap = campMemRole.get(victoryEntry.getKey());//战场阵容内存角色归类
        Map<Class<? extends IRoleType>, List<RoleModel>> killVictoryMap = campKillRole.get(victoryEntry.getKey());//击杀阵容角色归类
        Map<Class<? extends IRoleType>, List<RoleModel>> reatRoleVictoryMap = campRetreatRole.get(victoryEntry.getKey());//撤退阵容角色归类
        StringBuilder sbr = new StringBuilder("胜利方: ");
        sbr.append(victoryEntry.getKey().getName());
        if (killVictoryMap != null) {
            for (Map.Entry<Class<? extends IRoleType>, List<RoleModel>> entry : killVictoryMap.entrySet()) {
                IRoleType roleTypeExample = IRoleType.classMap.get(entry.getKey());
                sbr.append(" 损").append(roleTypeExample.getName()).append(":").append(entry.getValue().size()).append(", ");
            }
            sbr.setLength(sbr.length() - 2);
        } else {
            sbr.append(" 无损失");
        }
        sbr.append("\n失败方: \n");
        allCampSet.remove(victoryEntry.getKey());//移除胜利方  弹出失败方
        for (ICamp camp : allCampSet) {
            sbr.append(camp.getName());
            Map<Class<? extends IRoleType>, List<RoleModel>> killFailureMap = campKillRole.get(camp);
            if (killFailureMap != null) {
                for (Map.Entry<Class<? extends IRoleType>, List<RoleModel>> entry : killFailureMap.entrySet()) {
                    IRoleType roleTypeExample = IRoleType.classMap.get(entry.getKey());
                    sbr.append(" 损").append(roleTypeExample.getName()).append(":").append(entry.getValue().size()).append(", ");
                }
                sbr.setLength(sbr.length() - 2);
            } else {
                sbr.append(" 无损失");
            }
            sbr.append("\n");
        }
        sbr.setLength(sbr.length() - 1);
        log.info("战斗报告:\n{}", sbr);
    }

    /**
     * 是否越界
     *
     * @param currentX 地图坐标
     * @param currentY 地图坐标
     * @return 是否越界
     */
    public boolean isCrossMap(int currentX, int currentY) {
        return 0 > currentX || currentX >= this.X || currentY < 0 || currentY >= this.Y;
    }

    /**
     * 是否为空
     *
     * @param currentX 地图坐标
     * @param currentY 地图坐标
     * @return 返回当前坐标是否为空
     */
    public boolean isNullRole(int currentX, int currentY) {
        if (isCrossMap(currentX, currentY)) return true;//越界为空
        return this.roleModels[currentX][currentY] == null;
    }


    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        List<RoleModel> memRoleList = campMemRole.values().stream().flatMap(unit -> unit.values().stream()).flatMap(List::stream).collect(Collectors.toList());
        result.put("X", X);
        result.put("Y", Y);
        result.put("grids", DataCalc.toArray(grids));
        result.put("roleModels", memRoleList);
        result.put("campLocation", new ArrayList<>(campLocation.values()));
        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
//        return "{}";

    }
}