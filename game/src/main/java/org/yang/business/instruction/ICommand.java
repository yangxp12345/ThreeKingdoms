package org.yang.business.instruction;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.role.RoleModel;
import org.yang.springboot.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 指令
 */
@Slf4j
@Data
public abstract class ICommand {

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
        log.info("阵容:{}, 级别:{}, 编号:{}, 坐标:({},{}) 指令:{}", role.getCamp().getName(), role.getRoleType().getName(), role.getId(), role.getX(), role.getY(), this.getName());
        while (role.getCurrentActive() > 0) {//最少行动一次
            this.run(role);
            role.getGrid().stay(role);//格子状态加成
        }
        role.setCurrentActive(role.getCumulativeActive());//恢复行动力下一次行动
    }

    /**
     * 执行指令
     *
     * @param role 角色
     */
    public abstract void run(RoleModel role);

}
