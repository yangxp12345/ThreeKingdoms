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
import org.yang.business.constant.RunEnum;
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

    private transient RunEnum anEnum;//是否正在运行
    public transient final Map<Integer, RoleModel> roleIdMap;//所有角色idMap
    final private IGrid[][] grids;//格子
    final transient private RoleModel[][] roleModels;//地图上的角色
    private int X;//地图坐标上限
    private int Y;
    private transient int round;//回合数
    private Map<ICamp, RoleModel> campLocation;//大本营位置
    private transient IterData<RoleModel> iterData;


    public MapModel() {
        this(50, 15);//设置默认值
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
        anEnum = RunEnum.WAIT;//默认未运行
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
        this.iterData.remove(roleModel);
    }

    /**
     * 撤退角色清理数据
     *
     * @param roleModel 需要撤退的对象
     */
    public void retreatRole(RoleModel roleModel) {
        roleModel.getRoleType().retreatRole(roleModel);//移除阵容中的角色
        this.roleModels[roleModel.getX()][roleModel.getY()] = null;//移除地图上的角色数据
        this.iterData.remove(roleModel);
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
     * @return 是否战斗结束
     */
    public boolean run() throws Exception {
        round++;//增加一回合
        //清空上一次的行动记录
        List<ICamp> campList = new ArrayList<>(this.campMemRole.keySet());//生成一个新的,还在场的阵营
        if (campList.size() == 1) {
            this.anEnum = RunEnum.SUCCESS;
            return true;
        }
        List<RoleModel> allRoleList = this.campMemRole.values().stream().flatMap(roleTypeRoleListMap -> roleTypeRoleListMap.values().stream().flatMap(Collection::stream)).sorted((before, after) -> {
            //根据角色的trick 从大到小排序  如果一样大 使用随机数概率排序
            if (before.getTrick() == after.getTrick()) {
                return new Random().nextInt(2) - 1;
            }
            return before.getTrick() > after.getTrick() ? -1 : 1;
        }).collect(Collectors.toList());
        if (allRoleList.isEmpty()) return false;
        iterData = new IterData<>(allRoleList);
        while (iterData.hasNext()) {
            RoleModel roleModel = iterData.next();
            roleModel.getCommand().proxyRun(roleModel);
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
     * 获取指定距离的敌方角色   校验是否越界,是否空角色,是否同阵容
     *
     * @param x             坐标
     * @param y             坐标
     * @param roleModel     角色
     * @param enemyRoleList 敌方角色列表
     */
    private void addEnemyRole(int x, int y, RoleModel roleModel, List<RoleModel> enemyRoleList) {
        if (isCrossMap(x, y)) return;//越界
        if (this.roleModels[x][y] == null) return;//空节点
        if (this.roleModels[x][y].getCamp() == roleModel.getCamp()) return;//相同阵营
        enemyRoleList.add(this.roleModels[x][y]);
    }

    /**
     * 向目标方向移动
     *
     * @param role      被移动的角色
     * @param enemyRole 移动的终点角色
     */
    public void moveDistance(RoleModel role, RoleModel enemyRole) {
        Map.Entry<Integer, Integer> coordinate = role.calcBestCoordinate(enemyRole);
        //是否可以移动
        if (coordinate != null) {//可以移动
            moveCoordinate(role, coordinate.getKey(), coordinate.getValue());
            return;
        }
        //不存在最优解
        if (DataCalc.isProbabilityTrigger(0.9)) {//90%的概率不越走越远
            role.setCurrentActive(0);
            return;
        }
        log.info("阵容:{}, 级别:{}, 编号:{}, 坐标:({},{}) 指令:{}, {}", role.getCamp().getName(), role.getRoleType().getName(), role.getId(), role.getX(), role.getY(), role.getCommand().getName(), "触发乱走");
        //乱走
        List<Map.Entry<Integer, Integer>> allCoordinate = role.calcAllCoordinate();
        if (allCoordinate.isEmpty()) role.setCurrentActive(0);
        Map.Entry<Integer, Integer> randomUnit = DataCalc.getRandomUnit(allCoordinate);
        moveCoordinate(role, randomUnit.getKey(), randomUnit.getValue());
    }

    /**
     * 强制向指定坐标移动
     *
     * @param role 角色
     * @param x    坐标
     * @param y    坐标
     */
    private void moveCoordinate(RoleModel role, int x, int y) {
        role.getGrid().leave(role);//离开格子
        SocketServer.send(role.getCamp().getName(), new MoveImpl(role, x, y));
        this.roleModels[role.getX()][role.getY()] = null;//删除地图上的角色数据
        //重新设置地图上的角色数据
        this.roleModels[x][y] = role;
        //设置角色的坐标
        role.setCoordinate(x, y);
        role.setGrid(role.getMapModel().getGrids()[x][y]);//设置角色的格子
        role.getGrid().enter(role);//进入格子
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
    }
}