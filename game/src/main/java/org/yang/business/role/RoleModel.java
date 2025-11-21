package org.yang.business.role;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.grid.IGrid;
import org.yang.business.instruction.ICommand;
import org.yang.business.instruction.impl.StandByImpl;
import org.yang.business.map.MapModel;
import org.yang.business.camp.ICamp;
import org.yang.business.grade.IRoleType;
import org.yang.business.weapon.IWeapon;

import java.util.*;

/**
 * 角色的指标值模型
 */
@Slf4j
@Data
public class RoleModel {
    private transient MapModel mapModel;//角色地图
    private int id;//角色的id
    private ICommand command;//指令 默认待命
    private int x;//角色所在的坐标
    private int y;//角色所在的坐标
    private ICamp camp;//所属阵营
    private IRoleType roleType; //身份类型
    private IWeapon weapon;//武器

    private IGrid grid;
    private int currentHealth;//当前生命值
    private int cumulativeHealth;//累计生命值
    private int attack;//攻击力
    private int trick;//技巧 技巧远远大于防御力
    private int defense;//防御力
    private int exempt;//伤害最终减免
    private int hit;//命中   命中远远大于闪避
    private int dodge;//闪避
    private int unity;//胆魄
    private int currentActive;//当前剩余行动力
    private int cumulativeActive;//累计行动力


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
        return roleModel;
    }

    public void load(MapModel mapModel, int x, int y) {
        this.mapModel = mapModel;
        this.x = x;
        this.y = y;
        this.setGrid(mapModel.getGrids()[x][y]);
    }


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
     * 判断指定位置是否是盟友
     *
     * @param currentX 横坐标
     * @param currentY 纵坐标
     * @return true:是盟友,false:不是盟友
     */
    public boolean calcTeammateRole(int currentX, int currentY) {
        if (this.getMapModel().isCrossMap(currentX, currentY)) return false;//越界
        if (this.getMapModel().getRoleModels()[currentX][currentY] == null) return false;//空节点
        return this.getMapModel().getRoleModels()[currentX][currentY].getCamp() == this.getCamp();//不同阵营
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
    public List<RoleModel> calcEnemyRoleList() {
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
     * 向敌方角色移动的最优选择方案
     *
     * @param enemyRole 敌方角色
     * @return 返回可以移动的坐标方案
     */
    public Map.Entry<Integer, Integer> calcBestCoordinate(RoleModel enemyRole) {
        int x = this.getX();
        int y = this.getY();
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
            if (!this.mapModel.isCrossMap(currentX, currentY) &&//没有越界,并且有空间 并且有移动多余的行动力气
                    this.mapModel.getRoleModels()[currentX][currentY] == null &&//目标位置有空间
                    this.mapModel.getGrids()[currentX][currentY].getAct() < this.getCurrentActive() //移动消耗行动力小于当前角色行动力
            ) {//移动角色
                //注意顺序 需要先设置行动后再设置坐标,否则行动坐标会被覆盖
                return new AbstractMap.SimpleEntry<>(currentX, currentY);
            }
        }
        return null;
    }

    public List<Map.Entry<Integer, Integer>> calcAllCoordinate() {
        List<Map.Entry<Integer, Integer>> allMoveCoordinateList = new ArrayList<>();
        List<Map.Entry<Integer, Integer>> allCoordinateList = new ArrayList<>();
        allCoordinateList.add(new AbstractMap.SimpleEntry<>(this.x - 1, y));
        allCoordinateList.add(new AbstractMap.SimpleEntry<>(this.x + 1, y));
        allCoordinateList.add(new AbstractMap.SimpleEntry<>(this.x, y - 1));
        allCoordinateList.add(new AbstractMap.SimpleEntry<>(this.x, y + 1));
        for (Map.Entry<Integer, Integer> entry : allCoordinateList) {
            if (!this.getMapModel().isCrossMap(entry.getKey(), entry.getValue()) && this.getMapModel().getRoleModels()[entry.getKey()][entry.getValue()] == null) {
                allMoveCoordinateList.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
            }
        }
        return allMoveCoordinateList;
    }
}