package org.yang.business.instruction.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.instruction.ICommand;
import org.yang.business.map.MapModel;
import org.yang.business.role.RoleModel;

/**
 * 往后面逃跑
 */
@Slf4j
@Data
public class EscapeImpl extends ICommand {
    final private String name = "撤退";


    @Override
    public void run(RoleModel role) {
        RoleModel retreatRole = getRetreatRole(role);//获取营地位置
        while (role.getCurrentAct() > 0) {//最少行动一次
            if (retreatRole == null) {
                log.info("阵容:{}, 级别:{}, 编号:{}, 坐标:({},{}) 指令:{} 不存在营地 无法撤退", role.getCamp().getName(),role.getRoleType().getName(), role.getId(), role.getX(), role.getY(), name);
                break;
            }
            boolean status = isRetreat(role, retreatRole);//是否可以撤退
            if (status) {//当前位置无法成功撤退 需要向营地移动
                activeActConsume(role);//目标撤退行动消耗
                role.getMapModel().moveDistance(role, retreatRole);//向指定目标方向移动一次
            } else {//可以成功撤退
                log.info("阵容:{}, 级别:{}, 编号:{}, 坐标:({},{}) 指令:{} 撤退成功", role.getCamp().getName(), role.getRoleType().getName(), role.getId(), role.getX(), role.getY(), name);
                role.getMapModel().retreatRole(role);//处理撤退的角色
                break;
            }
        }
    }

    /**
     * 当前位置可撤退行动消耗就散
     *
     * @param role 当前角色
     */
    private void activeActConsume(RoleModel role) {
        int sumAct = 0;//每次行动消耗累计
        int x = role.getX();
        int y = role.getY();
        sumAct += activeActConsumeUnit(role, x - 1, y - 1);
        sumAct += activeActConsumeUnit(role, x, y - 1);
        sumAct += activeActConsumeUnit(role, x + 1, y - 1);
        sumAct += activeActConsumeUnit(role, x - 1, y);
        sumAct += activeActConsumeUnit(role, x + 1, y);
        sumAct += activeActConsumeUnit(role, x - 1, y + 1);
        sumAct += activeActConsumeUnit(role, x, y + 1);
        sumAct += activeActConsumeUnit(role, x + 1, y + 1);
        role.setCurrentAct(role.getCurrentAct() - sumAct);//todo 行动减值需要优化
    }

    /**
     * 撤退行动消耗
     *
     * @param role 角色
     * @param x    x坐标
     * @param y    y坐标
     * @return 行动消耗
     */
    private static int activeActConsumeUnit(RoleModel role, int x, int y) {
        MapModel mapModel = role.getMapModel();
        if (!role.getMapModel().isCrossMap(x, y - 1)) {
            RoleModel targetRole = mapModel.getRoleModels()[x][y - 1];
            if (targetRole != null && targetRole.getCamp().equals(role.getCamp())) {//存在敌人
                return 1;
            }
        }
        return 0;
    }


    /**
     * 判断当前位置是否可以撤退
     *
     * @param role 当前角色
     * @return 是否可以撤退
     */
    private boolean isRetreat(RoleModel role, RoleModel retreatRole) {
        return Math.abs(retreatRole.getX() - role.getX() + retreatRole.getY() - role.getY()) == 1;//距离一个单位方可撤退
    }

    /**
     * 获取撤退营地位置
     *
     * @param role 当前角色
     * @return 营地位置
     */
    private RoleModel getRetreatRole(RoleModel role) {
        return role.getMapModel().getCampLocation().get(role.getCamp());
    }


}