package org.yang.business.active;

import lombok.Data;

/**
 * 当前的动作接口
 */
@Data
public abstract class IActive {
    final private static String name = "动作";
    private int targetX;//目标X
    private int targetY;//目标Y


    @Override
    public String toString() {
        return "{}";
    }
}
