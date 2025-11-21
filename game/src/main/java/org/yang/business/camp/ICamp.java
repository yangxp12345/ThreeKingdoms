package org.yang.business.camp;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.springboot.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 所属阵营
 */
@Data
@Slf4j
public abstract class ICamp {
    private static final String name = "阵营";
    final static public Map<Class<? extends ICamp>, ICamp> classMap = new HashMap<>();
    final static public Map<String, ICamp> nameMap = new HashMap<>();


    static {
        try {
            Set<Class<? extends ICamp>> classSet = ReflectionUtil.getClassSet(ICamp.class);
            for (Class<? extends ICamp> clazz : classSet) {
                ICamp unit = clazz.newInstance();
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
