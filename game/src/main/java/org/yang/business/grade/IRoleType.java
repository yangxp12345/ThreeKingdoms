package org.yang.business.grade;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;

import org.yang.springboot.util.ReflectionUtil;

import java.util.*;


/**
 * 身份
 */
@Data
@Slf4j
abstract public class IRoleType {
    final private static String name = "身份";


    public static Map<Class<? extends IRoleType>, IRoleType> classMap = new HashMap<>();//类型的唯一实例
    public static Map<String, IRoleType> nameMap = new HashMap<>();//名称的唯一实例

    static {
        try {
            Set<Class<? extends IRoleType>> classSet = ReflectionUtil.getClassSet(IRoleType.class);
            for (Class<? extends IRoleType> clazz : classSet) {
                IRoleType unit = clazz.newInstance();
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
    public String getName() {
        DataCalc.showHint(this);
        return name;
    }


}
