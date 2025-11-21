package org.yang.business.instruction.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.instruction.ICommand;
import org.yang.business.role.RoleModel;

import java.util.List;

/**
 * 找最近的敌方角色
 */
@Slf4j
@Data
public class NearbyImpl extends ICommand {
    final private String name = "进攻";

    @Override
    public void run(RoleModel role) {
        List<RoleModel> enemyRoleList = role.getWeapon().calcEnemyRole(role);//攻击范围内的所有敌方角色
        if (enemyRoleList.isEmpty()) {//没有可以攻击的敌方角色,找到最近的敌方角色并且移动一步过去
            RoleModel enemyRole = role.getMapModel().getRecentlyEnemyRole(role);
            if (enemyRole == null) {//没有敌方目标
                role.setCurrentActive(0);//不行动
                return;
            }
            role.getMapModel().moveDistance(role, enemyRole);//向指定目标方向移动一次
        } else {//存在可以输出的敌方角色,直接输出
            RoleModel enemyRole = DataCalc.getRandomUnit(enemyRoleList);
            role.getWeapon().proxyAct(role, enemyRole);
        }

    }
}
