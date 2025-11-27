package org.yang.business.weapon.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.map.MapModel;
import org.yang.business.role.RoleModel;
import org.yang.business.weapon.IWeapon;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 长刀
 * 可以对周围4格进行单点输出
 */
@Slf4j
@Data
public class LongKnifeImpl extends IWeapon {
    final private String name = "长刀";
    final private String msg = "可以对四周的某个位置近距离进行一次常规输出,有劈砍效果,可以对目标两侧衍射输出";
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
        List<RoleModel> enemyDiffractionRoleList = getEnemyDiffractionRoleList(role, enemyRole);
        for (RoleModel enemyDiffractionRole : enemyDiffractionRoleList) {//衍射伤害
            damageMultiplier(role, enemyDiffractionRole, 0.2);//按照倍率进行输出伤害 单倍输出
        }

    }


    /**
     * 获取敌对角色两侧的目标角色
     *
     * @param role      当前角色
     * @param enemyRole 敌对角色
     * @return 敌对角色两侧的角色
     */
    private List<RoleModel> getEnemyDiffractionRoleList(RoleModel role, RoleModel enemyRole) {
        List<RoleModel> enemyDiffractionRoleList = new ArrayList<>();//衍射目标
        RoleModel[][] roleModels = role.getMapModel().getRoleModels();
        int currentX = role.getX();
        int targetX = enemyRole.getX();
        int targetY = enemyRole.getY();
        if (currentX == targetX) {
            if (role.calcEnemyRole(targetX + 1, targetY))
                enemyDiffractionRoleList.add(roleModels[targetX + 1][targetY]);
            if (role.calcEnemyRole(targetX - 1, targetY))
                enemyDiffractionRoleList.add(roleModels[targetX - 1][targetY]);
        } else {
            if (role.calcEnemyRole(targetX, targetY + 1))
                enemyDiffractionRoleList.add(roleModels[targetX][targetY + 1]);
            if (role.calcEnemyRole(targetX, targetY - 1))
                enemyDiffractionRoleList.add(roleModels[targetX][targetY - 1]);
        }
        return enemyDiffractionRoleList;
    }

}