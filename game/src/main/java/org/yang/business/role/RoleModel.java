package org.yang.business.role;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.active.IActive;
import org.yang.business.active.impl.MoveImpl;
import org.yang.business.buff.IBuff;
import org.yang.business.calc.DataCalc;
import org.yang.business.grade.impl.DeputyImpl;
import org.yang.business.grade.impl.GeneralImpl;
import org.yang.business.grade.impl.SoldierImpl;
import org.yang.business.grid.IGrid;
import org.yang.business.instruction.ICommand;
import org.yang.business.instruction.impl.StandByImpl;
import org.yang.business.map.MapModel;
import org.yang.business.camp.ICamp;
import org.yang.business.grade.IRoleType;
import org.yang.business.weapon.IWeapon;
import org.yang.springboot.socket.SocketServer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色的指标值模型
 */
@Slf4j
@Data
public class RoleModel {
    private transient MapModel mapModel;//角色地图
    private int id;//角色的id
    private String name;//角色名称
    private ICommand command;//指令 默认待命
    private int x;//角色所在的坐标
    private int y;//角色所在的坐标
    private ICamp camp;//所属阵营
    private IRoleType roleType; //身份类型
    private IWeapon weapon;//武器
    private transient Map<String, IBuff> iBuffMap;


    private int commander;//统帅力 只有将 士兵没有

    private IGrid grid;
    private long currentHealth;//当前生命值
    private long cumulativeHealth;//累计生命值
    private long attack;//攻击力
    private long trick;//技巧 技巧远远大于防御力
    private long defense;//防御力
    private long exempt;//伤害最终减免
    private long hit;//命中   命中远远大于闪避
    private long dodge;//闪避
    private long unity;//胆魄
    private long currentActive;//当前剩余行动力
    private long cumulativeActive;//累计行动力


    public RoleModel() {//构造函数不允许构造 defaultRoleModel,防止递归死循环
    }


    /**
     * 创建角色对象
     *
     * @param id            角色id
     * @param campClass     阵营
     * @param roleType      角色类型
     * @param weapon        武器
     * @param roleDataModel 数据模型对象
     * @return 角色对象
     */
    @SneakyThrows
    public static RoleModel create(int id, Class<? extends ICamp> campClass, Class<? extends IRoleType> roleType, Class<? extends IWeapon> weapon, RoleDataModel roleDataModel) {
        RoleModel roleModel = new RoleModel();
        roleModel.setId(id);
        roleModel.setCamp(ICamp.classMap.get(campClass));
        roleModel.setRoleType(IRoleType.classMap.get(roleType));
        roleModel.setWeapon(IWeapon.classMap.get(weapon));
        roleModel.setRoleDataModel(roleDataModel);
        roleModel.setCommand(ICommand.classMap.get(StandByImpl.class));
        roleModel.setIBuffMap(new HashMap<>());
        return roleModel;
    }


    /**
     * 添加增益效果
     *
     * @param iBuff 增益状态
     */
    public void addBuff(IBuff iBuff) {
        if (!this.getIBuffMap().containsKey(iBuff.getName())) {
            this.getIBuffMap().put(iBuff.getName(), iBuff);
        }else{
            IBuff oldBuff = this.getIBuffMap().get(iBuff.getName());
            oldBuff.overlayAdd();

        }

    }

    /**
     * 加载坐标和地图数据
     *
     * @param mapModel 地图对象
     * @param x        坐标
     * @param y        坐标
     */
    public void load(MapModel mapModel, int x, int y) {
        this.mapModel = mapModel;
        this.x = x;
        this.y = y;
        this.setGrid(mapModel.getGrids()[x][y]);
    }

    /**
     * 设置坐标数据
     *
     * @param x 坐标
     * @param y 坐标
     */
    public void setCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 设置角色数据
     *
     * @param roleDataModel 数据模型对象
     */
    private void setRoleDataModel(RoleDataModel roleDataModel) {
        this.commander = roleDataModel.getCommander();
        this.currentHealth = roleDataModel.getHealth();
        this.cumulativeHealth = roleDataModel.getHealth();
        this.attack = roleDataModel.getAttack();
        this.defense = roleDataModel.getDefense();
        this.exempt = roleDataModel.getExempt();
        this.hit = roleDataModel.getHit();
        this.dodge = roleDataModel.getDodge();
        this.trick = roleDataModel.getTrick();
        this.unity = roleDataModel.getUnity();
        this.currentActive = roleDataModel.getAct();
        this.cumulativeActive = roleDataModel.getAct();
    }


    /**
     * 判断指定位置是否是敌人
     *
     * @param currentX 横坐标
     * @param currentY 纵坐标
     * @return true:是盟友,false:不是盟友
     */
    public boolean calcEnemyRole(int currentX, int currentY) {
        if (this.getMapModel().isCrossMap(currentX, currentY)) return false;//越界
        if (this.getMapModel().getRoleModels()[currentX][currentY] == null) return false;//空节点
        return this.getMapModel().getRoleModels()[currentX][currentY].getCamp() != this.getCamp();//不同阵营
    }

    /**
     * 判断指定位置是否是队友
     *
     * @param currentX 横坐标
     * @param currentY 纵坐标
     * @return true:是盟友,false:不是盟友
     */
    public boolean calcTeammateRole(int currentX, int currentY) {
        if (this.getMapModel().isCrossMap(currentX, currentY)) return false;//越界
        if (this.getMapModel().getRoleModels()[currentX][currentY] == null) return false;//空节点
        return this.getMapModel().getRoleModels()[currentX][currentY].getCamp() == this.getCamp();//相同阵营
    }


    /**
     * 获取主将角色列表
     *
     * @return 主将角色列表
     */
    public List<RoleModel> calcGeneralRoleModel() {
        return this.getMapModel().getCampMemRole().values().stream().filter(unit -> (unit.getRoleType() instanceof GeneralImpl) && unit.getCamp() == this.getCamp()).collect(Collectors.toList());
    }

    /**
     * 获取副将角色列表
     *
     * @return 副将角色列表
     */
    public List<RoleModel> calcDeputyRoleModel() {
        return this.getMapModel().getCampMemRole().values().stream().filter(unit -> (unit.getRoleType() instanceof DeputyImpl) && unit.getCamp() == this.getCamp()).collect(Collectors.toList());
    }


    /**
     * 获取士兵角色列表
     *
     * @return 副将角色列表
     */
    public List<RoleModel> calcSoldierRoleModel() {
        return this.getMapModel().getCampMemRole().values().stream().filter(unit -> (unit.getRoleType() instanceof SoldierImpl) && unit.getCamp() == this.getCamp()).collect(Collectors.toList());
    }

    /**
     * 计算周围一圈的队友
     *
     * @return 返回队友列表
     */
    public List<RoleModel> calcTeammateRoleList() {
        List<RoleModel> roleList = new ArrayList<>();
        int x = this.getX();
        int y = this.getY();
        RoleModel[][] roleModels = this.getMapModel().getRoleModels();
        List<Map.Entry<Integer, Integer>> coordinateList = new ArrayList<>();
        coordinateList.add(new AbstractMap.SimpleEntry<>(x - 1, y - 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x - 1, y));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x - 1, y + 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x, y - 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x, y + 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x + 1, y - 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x + 1, y));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x + 1, y + 1));
        for (Map.Entry<Integer, Integer> entry : coordinateList) {
            if (calcTeammateRole(entry.getKey(), entry.getValue()))
                roleList.add(roleModels[entry.getKey()][entry.getValue()]);
        }
        return roleList;
    }

    /**
     * 计算周围一圈的敌人
     *
     * @return 返回队友列表
     */
    public List<RoleModel> calcAroundEnemyRoleList() {
        List<RoleModel> roleList = new ArrayList<>();
        int x = this.getX();
        int y = this.getY();
        RoleModel[][] roleModels = this.getMapModel().getRoleModels();
        List<Map.Entry<Integer, Integer>> coordinateList = new ArrayList<>();
        coordinateList.add(new AbstractMap.SimpleEntry<>(x - 1, y - 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x - 1, y));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x - 1, y + 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x, y - 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x, y + 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x + 1, y - 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x + 1, y));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x + 1, y + 1));
        for (Map.Entry<Integer, Integer> entry : coordinateList) {
            if (calcEnemyRole(entry.getKey(), entry.getValue()))
                roleList.add(roleModels[entry.getKey()][entry.getValue()]);
        }
        return roleList;
    }

    /**
     * 计算四周的短兵敌人列表
     *
     * @return 返回短兵敌人列表
     */
    public List<RoleModel> calcShortSoldierEnemyRoleList() {
        List<RoleModel> roleList = new ArrayList<>();
        int x = this.getX();
        int y = this.getY();
        RoleModel[][] roleModels = this.getMapModel().getRoleModels();
        List<Map.Entry<Integer, Integer>> coordinateList = new ArrayList<>();
        coordinateList.add(new AbstractMap.SimpleEntry<>(x - 1, y));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x, y - 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x, y + 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(x + 1, y));
        for (Map.Entry<Integer, Integer> entry : coordinateList) {
            if (calcEnemyRole(entry.getKey(), entry.getValue()))
                roleList.add(roleModels[entry.getKey()][entry.getValue()]);
        }
        return roleList;
    }

    /**
     * 向目标坐标移动的最优选择方案
     *
     * @param targetX     目标坐标
     * @param targetY     目标坐标
     * @param probability 如果角色没有最优的移动方案 多大的概率会乱走
     * @return 返回可以移动的坐标方案
     */
    private Map.Entry<Integer, Integer> calcBestCoordinate(int targetX, int targetY, double probability) {
        int x = this.getX();
        int y = this.getY();

        int gapX = targetX - x;//相对坐标值X
        int gapY = targetY - y;//相对坐标值Y

        int absX = Math.abs(gapX);
        int absY = Math.abs(gapY);
        int maxDistance = Math.max(absX, absY);//计算最大距离

        boolean xExpand = (gapX == 0) ? (DataCalc.isProbabilityTrigger(0.5)) : (gapX > 0);//靠近敌方 x是否变大?
        boolean yExpand = (gapY == 0) ? (DataCalc.isProbabilityTrigger(0.5)) : (gapY > 0);//靠近敌方 y是否变大?
        boolean xPriority = (absX == absY) ? DataCalc.isProbabilityTrigger(0.5) : (absX > absY);//优先移动X轴吗?

        List<Map.Entry<Integer, Integer>> coordinateList = new ArrayList<>();
        if (xPriority) {//优先移动X轴
            coordinateList.add(new AbstractMap.SimpleEntry<>(x + (xExpand ? 1 : -1), y));
            coordinateList.add(new AbstractMap.SimpleEntry<>(x, y + (yExpand ? 1 : -1)));
            coordinateList.add(new AbstractMap.SimpleEntry<>(x, y - (yExpand ? 1 : -1)));
            coordinateList.add(new AbstractMap.SimpleEntry<>(x - (xExpand ? 1 : -1), y));
        } else {//优先移动Y轴
            coordinateList.add(new AbstractMap.SimpleEntry<>(x, y + (yExpand ? 1 : -1)));
            coordinateList.add(new AbstractMap.SimpleEntry<>(x + (xExpand ? 1 : -1), y));
            coordinateList.add(new AbstractMap.SimpleEntry<>(x - (xExpand ? 1 : -1), y));
            coordinateList.add(new AbstractMap.SimpleEntry<>(x, y - (yExpand ? 1 : -1)));
        }
        List<Map.Entry<Integer, Integer>> coordinateBestList = new ArrayList<>();//最优的坐标集合
        List<Map.Entry<Integer, Integer>> coordinateDetourList = new ArrayList<>();//绕开的坐标集合

        for (Map.Entry<Integer, Integer> coordinateUnit : coordinateList) {
            Integer currentX = coordinateUnit.getKey();
            Integer currentY = coordinateUnit.getValue();
            if (this.mapModel.isCrossMap(currentX, currentY)) continue;//越界 非法坐标  不参与计算
            if (this.mapModel.getRoleModels()[currentX][currentY] != null) continue;//节点被占用  不参与计算
            if (this.mapModel.getGrids()[currentX][currentY].isEnterFailure(this)) continue;//行动力不足
            Map.Entry<Integer, Integer> coordinateDetour = new AbstractMap.SimpleEntry<>(currentX, currentY);
            //判断是否绕路
            if (maxDistance < Math.max(Math.abs(targetX - currentX), Math.abs(targetY - currentY))) {//绕路
                coordinateDetourList.add(coordinateDetour);
            } else {//没绕路
                coordinateBestList.add(coordinateDetour);
            }
        }
        if (!coordinateBestList.isEmpty()) return coordinateBestList.get(0);//返回最优的第一个坐标
        //不存在最优坐标,不存在绕道坐标
        if (coordinateDetourList.isEmpty()) return null;
        //只有绕道坐标 多大概率绕道
        return DataCalc.isProbabilityTrigger(probability) ? coordinateDetourList.get(0) : null;
    }


    /**
     * 向目标方向移动
     *
     * @param targetX 坐标X
     * @param targetY 坐标Y
     */
    public void moveTargetLocation(int targetX, int targetY) {
        Map.Entry<Integer, Integer> coordinate = this.calcBestCoordinate(targetX, targetY, 0.1);//获取移动方案
        //是否可以移动
        if (coordinate != null) {//可以移动
            IActive iActive = new MoveImpl(this, this.getX(), this.getY(), coordinate.getKey(), coordinate.getValue());//记录当前角色的移动方案
            if (this.getGrid().proxyLeave(this)) return;//离开格子
            this.setCoordinate(coordinate.getKey(), coordinate.getValue());//设置角色的新坐标
            if (this.getGrid().proxyEnter(this)) return;//进入格子
            SocketServer.send(this.getCamp().getName(), iActive);//记录移动命令
        } else {//无法移动 丢失所有行动力
            this.setCurrentActive(0);
        }
    }

    /**
     * 随机移动
     */
    public void moveRandom() {
        log.info("不听指令 乱走");
        List<Map.Entry<Integer, Integer>> coordinateList = new ArrayList<>();//当前角色的周围四个坐标
        List<Map.Entry<Integer, Integer>> coordinateNewList = new ArrayList<>();//当前角色可以移动的坐标
        coordinateList.add(new AbstractMap.SimpleEntry<>(this.x - 1, this.y));
        coordinateList.add(new AbstractMap.SimpleEntry<>(this.x + 1, this.y));
        coordinateList.add(new AbstractMap.SimpleEntry<>(this.x, this.y - 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(this.x, this.y + 1));

        for (Map.Entry<Integer, Integer> coordinate : coordinateList) {
            Integer targetX = coordinate.getKey();
            Integer targetY = coordinate.getValue();
            if (this.mapModel.isCrossMap(targetX, targetY)) continue;//越界 非法坐标  不参与计算
            if (this.mapModel.getRoleModels()[targetX][targetY] != null) continue;//节点被占用  不参与计算
            if (this.mapModel.getGrids()[targetX][targetY].isEnterFailure(this)) continue;//行动力不足
            coordinateNewList.add(coordinate);
        }
        //是否可以移动
        if (!coordinateNewList.isEmpty()) {//可以移动
            Map.Entry<Integer, Integer> randomUnit = DataCalc.getRandomUnit(coordinateNewList);
            moveTargetLocation(randomUnit.getKey(), randomUnit.getValue());
        } else {//无法移动 丢失所有行动力
            this.setCurrentActive(0);
        }
    }


    /**
     * 获取距离目标最近的敌方角色
     *
     * @return 距离目标角色最近的角色
     */
    public RoleModel calcRecentlyEnemyRole() {
        int maxLength = this.mapModel.getX() + this.mapModel.getY() - 1;
        for (int distance = 1; distance < maxLength; distance++) {
            List<RoleModel> enemyRoleList = calcDistanceEnemyRoleList(distance);
            if (enemyRoleList.isEmpty()) continue;
            return DataCalc.getRandomUnit(enemyRoleList);
        }
        log.info("当前角色id: [{}], 没有找到敌方目标", this.getId());//没有目标
        return null;
    }


    /**
     * 获取指定距离的敌对角色列表
     *
     * @param distance 指定的距离
     * @return 返回敌对角色列表
     */
    public List<RoleModel> calcDistanceEnemyRoleList(int distance) {
        List<RoleModel> enemyRoleList = new ArrayList<>();//获取距离当前目标指定距离的所有敌方角色
        RoleModel[][] roleModels = this.getMapModel().getRoleModels();
        for (int currentX = -distance; currentX <= distance; currentX++) {
            int currentY = distance - Math.abs(currentX);
            if (currentY != 0) {
                if (calcEnemyRole(this.getX() + currentX, this.getY() - currentY))
                    enemyRoleList.add(roleModels[this.getX() + currentX][this.getY() - currentY]);
            }
            if (calcEnemyRole(this.getX() + currentX, this.getY() + currentY))
                enemyRoleList.add(roleModels[this.getX() + currentX][this.getY() + currentY]);
        }
        return enemyRoleList;
    }


    /**
     * 获取指定距离的队友角色列表
     *
     * @param distance 指定的距离
     * @return 返回敌对角色列表
     */
    public List<RoleModel> calcDistanceTeammateRoleList(int distance) {
        List<RoleModel> enemyRoleList = new ArrayList<>();//获取距离当前目标指定距离的所有敌方角色
        RoleModel[][] roleModels = this.getMapModel().getRoleModels();
        for (int currentX = -distance; currentX <= distance; currentX++) {
            int currentY = distance - Math.abs(currentX);
            if (currentY != 0) {
                if (calcTeammateRole(this.getX() + currentX, this.getY() - currentY))
                    enemyRoleList.add(roleModels[this.getX() + currentX][this.getY() - currentY]);
            }
            if (calcTeammateRole(this.getX() + currentX, this.getY() + currentY))
                enemyRoleList.add(roleModels[this.getX() + currentX][this.getY() + currentY]);
        }
        return enemyRoleList;
    }

    /**
     * 改变当前角色对应的阵容士气
     *
     * @param unityChange 士气变化
     */
    public void calcChangeRoleUnity(int unityChange) {
        this.getMapModel().getCampMemRole().values().stream().filter(unit -> unit.getCamp().equals(this.getCamp())).forEach(unit -> {
            unit.setUnity(unit.getUnity() + unityChange);
        });
    }


    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("command", command);
        result.put("x", x);
        result.put("y", y);
        result.put("camp", camp);
        result.put("roleType", roleType);
        result.put("weapon", weapon);
        result.put("grid", grid);
        result.put("currentHealth", currentHealth);
        result.put("cumulativeHealth", cumulativeHealth);
        result.put("attack", attack);
        result.put("trick", trick);
        result.put("defense", defense);
        result.put("exempt", exempt);
        result.put("hit", hit);
        result.put("dodge", dodge);
        result.put("unity", unity);
        result.put("currentActive", currentActive);
        result.put("cumulativeActive", cumulativeActive);
        return result;
    }

    @Override
    public String toString() {
        return toJSON().toJSONString();
    }
}