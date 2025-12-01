package org.yang.business.instruction.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.grade.IRoleType;
import org.yang.business.grade.impl.GeneralImpl;
import org.yang.business.instruction.ICommand;
import org.yang.business.role.RoleModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 包围敌方角色
 */
@Slf4j
@Data
public class EncircleImpl extends ICommand {
    final private String name = "包围";

    @Override
    public void run(RoleModel role) {

        List<RoleModel> enemyRoleList = role.getWeapon().calcEnemyRole(role);//攻击范围内的所有敌方角色
        List<RoleModel> targetRoleList = getMasterList(role);//需要被包围的角色列表

        filterEncircleRole(enemyRoleList, targetRoleList);//排除攻击范围内被包围的敌方角色 得到可以攻击的地方角色
        if (enemyRoleList.isEmpty()) {//没有可以攻击的敌方角色,找到需要包围的敌方角色并且移动过去
            if (targetRoleList.isEmpty()) {//不存在需要包围的目标
                role.setCurrentActive(0);
                return;
            }
            RoleModel targetRole = getRecentlyEncircleRole(role, targetRoleList);//选择距离当前角色最近的目标
            role.moveTargetLocation(targetRole.getX(),targetRole.getY());//向指定目标方向移动一次
        } else {//存在可以输出的敌方角色,任意选择一个开始输出
            RoleModel enemyRole = DataCalc.getRandomUnit(enemyRoleList);
            role.getWeapon().proxyAct(role, enemyRole);
        }
    }



    /**
     * 获取距离当前角色最近的目标
     *
     * @param role             参考角色
     * @param encircleRoleList 所有需要包围的目标
     * @return 返回最近的需要包围的目标
     */
    private RoleModel getRecentlyEncircleRole(RoleModel role, List<RoleModel> encircleRoleList) {
        RoleModel temp = null;
        int min = Math.max(role.getMapModel().getX(), role.getMapModel().getY());
        for (RoleModel encircleRole : encircleRoleList) {
            int encircleX = encircleRole.getX();
            int encircleY = encircleRole.getY();
            int x = role.getX();
            int y = role.getY();
            int max = Math.max(encircleX - x, encircleY - y);
            if (max < min) {
                min = max;
                temp = encircleRole;
            }
        }
        return temp;
    }

    /**
     * 根据id排除出需要包围的主将
     *
     * @param enemyRoleList    攻击范围内的敌方角色集合
     * @param encircleRoleList 需要包围的角色集合
     */
    private void filterEncircleRole(List<RoleModel> enemyRoleList, List<RoleModel> encircleRoleList) {
        //获取id列表
        Set<Integer> ids = encircleRoleList.stream().map(RoleModel::getId).collect(Collectors.toSet());
        for (int i = 0; i < enemyRoleList.size(); i++) {
            RoleModel roleModel = enemyRoleList.get(i);
            if (ids.contains(roleModel.getId())) {
                enemyRoleList.remove(i);
                i--;
            }
        }
    }

    /**
     * 获取需要包围的敌方主将角色信息
     * 目前只支持主将包围
     *
     * @param roleModel 当前角色
     * @return 返回需要包围的角色集合
     */
    private List<RoleModel> getMasterList(RoleModel roleModel) {
        List<RoleModel> allMaster = new ArrayList<>();
        for (Map.Entry<Integer, RoleModel> mapEntry : roleModel.getMapModel().campMemRole.entrySet()) {
            if (mapEntry.getValue().getCamp().equals(roleModel.getCamp())) continue;//相同阵容 跳过数据
            if (!roleModel.getRoleType().equals(IRoleType.classMap.get(GeneralImpl.class))) continue;//非主将
            allMaster.add(mapEntry.getValue());
        }
        return allMaster;
    }

    /**
     * 根据角色id列表获取被包围的角色信息
     *
     * @param roleModel   当前角色
     * @param encircleIds 被包围的角色id列表
     * @return 返回被包围的角色信息
     */
    private List<RoleModel> getRoleIdsList(RoleModel roleModel, Integer... encircleIds) {
        List<RoleModel> allEncircleRoleList = new ArrayList<>();
        for (Integer encircleId : encircleIds) {
            RoleModel encircleRole = roleModel.getMapModel().campMemRole.get(encircleId);
            if (encircleRole != null) allEncircleRoleList.add(encircleRole);
        }
        return allEncircleRoleList;
    }
}