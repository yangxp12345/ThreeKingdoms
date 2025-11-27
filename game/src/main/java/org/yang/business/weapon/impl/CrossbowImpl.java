package org.yang.business.weapon.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.role.RoleModel;
import org.yang.business.weapon.IWeapon;

import java.util.ArrayList;
import java.util.List;

/**
 * 弓弩
 * 可以对周围4格进行单点输出
 */
@Slf4j
@Data
public class CrossbowImpl extends IWeapon {
    final private String name = "弓弩";
    final private String msg = "可以对四周的某个方位进行一次远距离常规输出";
    final private int active = 3;//攻击行动消耗
    final private int atkMaxDistance = 13;//攻击最大距离
    final private int atkMinDistance = 5;//攻击最小距离


    /**
     * 获取主要的攻击目标
     *
     * @return 任意攻击范围内的地方角色
     */
    @Override
    public List<RoleModel> calcEnemyRole(RoleModel role) {
        List<RoleModel> shortSoldierEnemyRoleList = role.calcShortSoldierEnemyRoleList();
        if (!shortSoldierEnemyRoleList.isEmpty()) {//远程输出存在短兵敌人
            role.setWeapon(IWeapon.classMap.get(UnarmedImpl.class));
            log.info("武器更变为徒手");
            return shortSoldierEnemyRoleList;
        }



        List<RoleModel> enemyRoleList = new ArrayList<>();
        for (int distance = atkMinDistance; distance <= atkMaxDistance; distance++) {
            List<RoleModel> enemyRoleDistanceList = role.calcDistanceEnemyRoleList(distance);//获取指定距离的敌对角色
            enemyRoleList.addAll(enemyRoleDistanceList);
        }
        return enemyRoleList;
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
    }


}