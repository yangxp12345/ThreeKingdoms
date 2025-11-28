package org.yang.business.instruction;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.grade.impl.SoldierImpl;
import org.yang.business.role.RoleModel;
import org.yang.springboot.util.ReflectionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 指令
 */
@Slf4j
@Data
public abstract class ICommand {
    public static int sleep = 1;

    final static private String name = "指令";
    final static public Map<Class<? extends ICommand>, ICommand> classMap = new HashMap<>();
    final static public Map<String, ICommand> nameMap = new HashMap<>();


    static {
        try {
            Set<Class<? extends ICommand>> classSet = ReflectionUtil.getClassSet(ICommand.class);
            for (Class<? extends ICommand> clazz : classSet) {
                ICommand unit = clazz.newInstance();
                classMap.put(clazz, unit);
                nameMap.put(unit.getName(), unit);
            }
        } catch (Exception e) {
            log.error("获取{}异常", name, e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取名称
     */
    public abstract String getName();


    /**
     * 执行代理指令
     *
     * @param role 角色
     */
    final public void proxyRun(RoleModel role) {
        log.info("阵容:{}, 武器:{}, 编号:{}, 坐标:({},{}) 指令:{}", role.getCamp().getName(), role.getWeapon().getName(), role.getId(), role.getX(), role.getY(), this.getName());
        while (role.getCurrentActive() > 0) {//有行动力就行动
            DataCalc.sleep(ICommand.sleep);
            if (role.getGrid().stay(role)) return;//格子停留后触发的效果
            //根据统帅能力计算是否听令
            if (!isListenInstruction(role)) return;
            this.run(role);//执行指令
        }
        role.setCurrentActive(role.getCumulativeActive());//恢复行动力下一次行动
    }

    /**
     * 执行指令
     *
     * @param role 角色
     */
    public abstract void run(RoleModel role);


    /**
     * 是否听指令 不停指令变会乱走
     *
     * @param role 当前角色
     * @return 是否听令
     */
    private boolean isListenInstruction(RoleModel role) {
        if (!(role.getRoleType() instanceof SoldierImpl)) {
            return DataCalc.isProbabilityTrigger(role.getUnity(), 100);
        }
        ;
        List<RoleModel> generalRoleList = role.calcGeneralRoleModel();//主将
        List<RoleModel> deputyRoleList = role.calcDeputyRoleModel();//副将
        List<RoleModel> soldierRoleList = role.calcSoldierRoleModel();//士兵角色
        long generalCommander = generalRoleList.stream().map(RoleModel::getCommander).mapToLong(x -> x).sum() * 100;
        long deputyCommander = deputyRoleList.stream().map(RoleModel::getCommander).mapToLong(x -> x).sum() * 100;
        long unityAddValue = (generalCommander + deputyCommander) / soldierRoleList.size();//每个士兵的胆魄加值
        //累计平均分配统帅+胆魄
        if (DataCalc.isProbabilityTrigger(role.getUnity() + unityAddValue, 100)) return true;
        role.moveRandom();//不听指令 乱走
        return false;

    }

}
