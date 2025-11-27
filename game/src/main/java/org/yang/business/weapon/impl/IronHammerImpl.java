package org.yang.business.weapon.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.role.RoleModel;
import org.yang.business.weapon.IWeapon;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 铁锤
 * 可以对周围4格进行单点输出
 */
@Slf4j
@Data
public class IronHammerImpl extends IWeapon {
    final private String name = "铁锤";
    final private String msg = "可以对四周的某个位置近距离进行一次常规输出,有击退效果,如果目标后面有角色 无法击退,目标会受到2倍伤害";
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
        Map.Entry<Integer, Integer> diffractionLocation = LongSpearImpl.getEnemyDiffractionLocation(role, enemyRole);//获取衍射角色坐标
        if (role.getMapModel().isCrossMap(diffractionLocation.getKey(), diffractionLocation.getValue())) {//越界相当于存在衍射目标
            damageMultiplier(role, enemyRole, 2);//按照倍率进行输出伤害
            return;
        }
        if (role.calcEnemyRole(diffractionLocation.getKey(), diffractionLocation.getValue())) {//衍射目标存在
            damageMultiplier(role, enemyRole, 2);//按照倍率进行输出伤害
            return;
        }
        //不存在衍射坐标
        damageMultiplier(role, enemyRole, 1);//按照倍率进行输出伤害
        enemyRole.moveTargetLocation(diffractionLocation.getKey(), diffractionLocation.getValue());//击退
        log.info("击退位置:{}", diffractionLocation);
    }
}