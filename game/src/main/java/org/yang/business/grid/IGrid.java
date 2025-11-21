package org.yang.business.grid;

import lombok.Data;
import lombok.SneakyThrows;

import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.role.RoleModel;
import org.yang.springboot.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 地图的不同类型
 */
@Data
@Slf4j
public abstract class IGrid {
    private static final String name = "格子";
    final private int act = 1;//进入需要消耗的行动力
    final static public Map<Class<? extends IGrid>, IGrid> classMap = new HashMap<>();
    final static public Map<String, IGrid> nameMap = new HashMap<>();

    static {
        try {
            Set<Class<?>> classSet = ReflectionUtil.getClassSet(IGrid.class);
            for (Class<?> clazz : classSet) {
                IGrid unit = (IGrid) clazz.newInstance();
                classMap.put((Class<? extends IGrid>) clazz, unit);
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
    public String getName() {
        DataCalc.showHint(this);
        return name;
    }








    /**
     * 进入
     */
    public void enter(RoleModel role) {
        DataCalc.showHint(this);
    }


    /**
     * 停留
     */
    public void stay(RoleModel role) {
        DataCalc.showHint(this);
    }


    /**
     * 离开
     */
    public void leave(RoleModel role) {
        DataCalc.showHint(this);
    }


    @Override
    public String toString() {
        return "";
    }
}
