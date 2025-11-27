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
        List<RoleModel> targetRoleList = role.getWeapon().calcEnemyRole(role);//攻击范围内的所有敌方角色
        if (targetRoleList.isEmpty()) {//没有可以攻击的敌方角色,找到最近的敌方角色并且移动一步过去
            log.info("没有可以攻击的敌方角色,找到最近的敌方角色并且移动一步过去");
            RoleModel targetRole = role.calcRecentlyEnemyRole();
            log.info("阵营{} 当前角色坐标 ({},{})最近的敌方角色:{}",role.getCamp().getName(), role.getX(), role.getY(), targetRole == null);
            if (targetRole == null) {//战场上找不到地方目标
                role.setCurrentActive(0);//不行动
                return;
            }
            role.moveTargetLocation(targetRole.getX(), targetRole.getY());//向指定目标方向移动一次
        } else {//存在可以输出的敌方角色,直接输出
            log.info("存在可以输出的敌方角色,直接输出");
            RoleModel enemyRole = DataCalc.getRandomUnit(targetRoleList);
            role.getWeapon().proxyAct(role, enemyRole);
        }
    }
}