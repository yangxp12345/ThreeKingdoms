package org.yang.business.weapon.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.role.RoleModel;
import org.yang.business.weapon.IWeapon;

import java.util.List;
import java.util.Map;

/**
 * 斧
 * 可以对周围4格进行单点输出
 */
@Slf4j
@Data
public class AxeImpl extends IWeapon {
    final private String name = "斧";
    final private String msg = "可以对四周所有位置近距离进行一次弱化输出";
    final private int active = 3;//攻击行动消耗


    /**
     * 获取主要的攻击目标
     *
     * @return 任意攻击范围内的地方角色
     */
    @Override
    public List<RoleModel> calcEnemyRole(RoleModel role) {
        return role.calcAroundEnemyRoleList();
    }

    /**
     * 使用武器造成输出
     *
     * @param role      角色
     * @param enemyRole 敌方角色
     */
    @Override
    public void calcHurt(RoleModel role, RoleModel enemyRole) {
        List<RoleModel> enemyRoleList = role.calcAroundEnemyRoleList();
        for (RoleModel enemyRoleUnit : enemyRoleList) {
            damageMultiplier(role, enemyRoleUnit, 0.4);//按照倍率进行输出伤害
        }
    }
}