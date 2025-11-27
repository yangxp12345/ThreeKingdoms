package org.yang.business.weapon.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.role.RoleModel;
import org.yang.business.weapon.IWeapon;

import java.util.List;

/**
 * 饮血剑
 * 可以对周围4格进行单点输出
 */
@Slf4j
@Data
public class SuctionBloodImpl extends IWeapon {
    final private String name = "短刀";
    final private double suction = 0.01;//吸收量
    final private String msg = "可以对四周的某个位置近距离进行一次常规输出,根据伤害量恢复一定的生命值";
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
        long hurtValue = damageMultiplier(role, enemyRole, 1);//按照倍率进行输出伤害 单倍输出
        //吸血
        long currentHealth = DataCalc.toLong(role.getCurrentHealth() + hurtValue * suction);
        currentHealth = Math.min(currentHealth, role.getCumulativeHealth());
        role.setCurrentHealth(currentHealth);
    }

}