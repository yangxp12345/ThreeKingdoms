package org.yang.business.role;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
    private int currentHealth;//当前生命值
    private int cumulativeHealth;//累计生命值
    private int attack;//攻击力
    private int trick;//技巧 技巧远远大于防御力
    private int defense;//防御力
    private int exempt;//伤害最终减免
    private int hit;//命中   命中远远大于闪避
    private int dodge;//闪避
    private int unity;//胆魄
    private int currentAct;//当前剩余行动力
    private int cumulativeAct;//累计行动力


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
        this.currentAct = roleDataModel.getAct();
        this.cumulativeAct = roleDataModel.getAct();
    }


    /**
     * 判断指定位置是否是盟友
     *
     * @param currentX 横坐标
     * @param currentY 纵坐标
     * @return true:是盟友,false:不是盟友
     */
    public boolean isTeammateRole(int currentX, int currentY) {
        return (!this.getMapModel().isNullRole(currentX, currentY)) && this.getMapModel().getRoleModels()[currentX][currentY].getCamp() == this.getCamp();
    }

    /**
     * 判断指定位置是否是敌人
     *
     * @param currentX 横坐标
     * @param currentY 纵坐标
     * @return true:是盟友,false:不是盟友
     */
    public boolean isEnemyRole(int currentX, int currentY) {
        return (!this.getMapModel().isNullRole(currentX, currentY)) && this.getMapModel().getRoleModels()[currentX][currentY].getCamp() != this.getCamp();
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
            if (isTeammateRole(entry.getKey(), entry.getValue()))
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
            if (isEnemyRole(entry.getKey(), entry.getValue()))
                roleList.add(roleModels[entry.getKey()][entry.getValue()]);
        }
        return roleList;
    }
}