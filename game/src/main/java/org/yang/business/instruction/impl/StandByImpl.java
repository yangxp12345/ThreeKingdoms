package org.yang.business.instruction.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.instruction.ICommand;
import org.yang.business.map.MapModel;
import org.yang.business.role.RoleModel;

import java.util.List;

/**
 * 原地待命
 */
@Slf4j
@Data
public class StandByImpl extends ICommand {
    final private String name = "待命";

    @Override
    public void run(RoleModel role) {
        while (role.getCurrentAct() > 0) {//最少行动一次
            List<RoleModel> enemyRoleList = role.getWeapon().calcEnemyRole(role);//攻击范围内的所有敌方角色
            if (enemyRoleList.isEmpty()) {
                break;
            }
            RoleModel enemyRole = DataCalc.getRandomUnit(enemyRoleList);//任意攻击一个敌方角色
            role.getWeapon().proxyAct(role, enemyRole);
        }
    }

}
