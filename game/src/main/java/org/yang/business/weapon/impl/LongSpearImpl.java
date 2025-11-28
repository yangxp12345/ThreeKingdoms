package org.yang.business.weapon.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.role.RoleModel;
import org.yang.business.weapon.IWeapon;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

/**
 * 长矛
 * 可以对周围4格进行单点输出
 */
@Slf4j
@Data
public class LongSpearImpl extends IWeapon {
    final private String name = "长矛";
    final private String msg = "可以对四周的某个位置近距离进行一次常规输出,有穿刺效果,可以对目标后侧进行衍射输出";
    final private int active = 2;//攻击行动消耗


    /**
     * 获取主要的攻击目标
     *
     * @return 任意攻击范围内的地方角色
     */
    @Override
    public List<RoleModel> calcEnemyRole(RoleModel role) {
        return role.calcDistanceEnemyRoleList(1);
    }

    /**
     * 使用武器造成输出
     *
     * @param role      角色
     * @param enemyRole 敌方角色
     */
    @Override
    public void calcHurt(RoleModel role, RoleModel enemyRole) {
        damageMultiplier(role, enemyRole, 1);//按照倍率进行输出伤害
        //获取身后的角色
        RoleModel enemyDiffractionRole = getEnemyDiffractionRole(role, enemyRole);
        if (enemyDiffractionRole != null) damageMultiplier(role, enemyDiffractionRole, 0.5);//按照倍率进行输出伤害 单倍输出

    }

    /**
     * 获取敌对角色身后的目标角色
     *
     * @param role      当前角色
     * @param enemyRole 敌对角色
     * @return 敌对角色两侧的角色
     */
    public static RoleModel getEnemyDiffractionRole(RoleModel role, RoleModel enemyRole) {
        RoleModel[][] roleModels = role.getMapModel().getRoleModels();
        Map.Entry<Integer, Integer> diffractionLocation = getEnemyDiffractionLocation(role, enemyRole);//获取衍射角色坐标
        if (role.calcEnemyRole(diffractionLocation.getKey(), diffractionLocation.getValue()))
            return roleModels[diffractionLocation.getKey()][diffractionLocation.getValue()];
        return null;
    }

    /**
     * 获取敌方角色身后的坐标
     *
     * @param role
     * @param enemyRole
     * @return
     */
    public static Map.Entry<Integer, Integer> getEnemyDiffractionLocation(RoleModel role, RoleModel enemyRole) {
        int currentX = role.getX();
        int currentY = role.getY();
        int targetX = enemyRole.getX();
        int targetY = enemyRole.getY();
        Map.Entry<Integer, Integer> location;
        if (currentX == targetX) {
            int enemyDiffractionY = targetY + ((currentY < targetY ? 1 : -1));
            location = new AbstractMap.SimpleEntry<>(targetX, enemyDiffractionY);
        } else {
            int enemyDiffractionX = targetX + ((currentX < targetX ? 1 : -1));
            location = new AbstractMap.SimpleEntry<>(enemyDiffractionX, targetY);

        }
        return location;
    }
}