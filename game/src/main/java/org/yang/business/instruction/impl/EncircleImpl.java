package org.yang.business.instruction.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.camp.ICamp;
import org.yang.business.grade.IRoleType;
import org.yang.business.grade.impl.GeneralImpl;
import org.yang.business.instruction.ICommand;
import org.yang.business.map.MapModel;
import org.yang.business.role.RoleModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 找最近的并且需要被包围的敌方角色
 */
@Slf4j
@Data
public class EncircleImpl extends ICommand {
    final private String name = "包围";

    @Override
    public void run(RoleModel role) {
        while (role.getCurrentAct() > 0) {//最少行动一次
            List<RoleModel> enemyRoleList = role.getWeapon().calcEnemyRole( role);//攻击范围内的所有敌方角色
            List<RoleModel> encircleRoleList = getMasterList(role);//需要被包围的角色列表
            filterMaster(enemyRoleList, encircleRoleList);//得到攻击范围内不被包围的敌方角色
            if (enemyRoleList.isEmpty()) {//没有可以攻击的敌方角色,找到需要包围的敌方角色并且移动过去
                if (encircleRoleList.isEmpty()) return;//不存在需要包围的目标
                RoleModel encircleRole = getRecentlyEncircleRole(role, encircleRoleList);//选择距离当前角色最近的目标
                role.getMapModel().moveDistance(role, encircleRole);//向指定目标方向移动一次
                //如果当前角色距离包围角色只有一个格,30%概率只会移动一次就跳出
                if (Math.abs(role.getX() - encircleRole.getX()) <= 1 && Math.abs(role.getY() - encircleRole.getY()) <= 1 && DataCalc.isProbabilityTrigger(0.3))
                    break;
            } else {//存在可以输出的敌方角色,任意选择一个开始输出
                RoleModel enemyRole = DataCalc.getRandomUnit(enemyRoleList);
                role.getWeapon().proxyAct(role, enemyRole);
            }
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
    private void filterMaster(List<RoleModel> enemyRoleList, List<RoleModel> encircleRoleList) {
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
     * 获取需要包围的角色信息
     * 目前只支持主将包围
     *
     * @param roleModel 当前角色
     * @return 返回需要包围的角色集合
     */
    private List<RoleModel> getMasterList(RoleModel roleModel) {
        List<RoleModel> allMaster = new ArrayList<>();
        for (Map.Entry<ICamp, Map<Class<? extends IRoleType>, List<RoleModel>>> mapEntry : roleModel.getMapModel().campMemRole.entrySet()) {
            if (mapEntry.getKey().equals(roleModel.getCamp())) continue;//相同阵容 跳过数据
            List<RoleModel> roleModels = mapEntry.getValue().get(GeneralImpl.class);
            allMaster.addAll(roleModels);
        }
        return allMaster;
    }
}