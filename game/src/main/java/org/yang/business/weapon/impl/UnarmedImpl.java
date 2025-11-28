package org.yang.business.weapon.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.role.RoleModel;
import org.yang.business.weapon.IWeapon;

import java.util.List;

/**
 * 赤手空拳
 * 可以对周围4格进行单点输出
 */
@Slf4j
@Data
public class UnarmedImpl extends IWeapon {
    final private String name = "赤手空拳";
    final private String msg = "可以对四周的某个位置近距离进行一次弱化的输出";
    final private int active = 1;//攻击行动消耗


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
        damageMultiplier(role, enemyRole, 0.3);//按照倍率进行输出伤害 单倍输出
        //是否存在衍射伤害
    }

}