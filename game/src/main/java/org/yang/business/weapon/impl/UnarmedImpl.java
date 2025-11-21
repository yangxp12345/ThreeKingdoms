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
 * 赤手空拳
 * 可以对周围4格进行单点输出
 */
@Slf4j
@Data
public class UnarmedImpl extends IWeapon {
    final private String name = "赤手空拳";
    final private int active = 2;//行动消耗2 输出效率80


    /**
     * 获取主要的攻击目标
     *
     * @return 任意攻击范围内的地方角色
     */
    @Override
    public List<RoleModel> calcEnemyRole(RoleModel role) {
        int roleX = role.getX();//角色坐标
        int roleY = role.getY();//角色坐标
        List<RoleModel> enemyRoleList = new ArrayList<>();
        List<Map.Entry<Integer, Integer>> coordinateList = new ArrayList<>();
        coordinateList.add(new AbstractMap.SimpleEntry<>(roleX + 1, roleY));
        coordinateList.add(new AbstractMap.SimpleEntry<>(roleX, roleY - 1));
        coordinateList.add(new AbstractMap.SimpleEntry<>(roleX - 1, roleY));
        coordinateList.add(new AbstractMap.SimpleEntry<>(roleX, roleY + 1));
        for (Map.Entry<Integer, Integer> entry : coordinateList) {
            if (role.calcEnemyRole(entry.getKey(), entry.getValue()))
                enemyRoleList.add(role.getMapModel().getRoleModels()[entry.getKey()][entry.getValue()]);
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
        damageMultiplier(role, enemyRole, 1);//按照倍率进行输出伤害 单倍输出
        //是否存在衍射伤害
    }


}