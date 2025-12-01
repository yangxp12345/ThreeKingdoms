package org.yang.business.active.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yang.business.active.IActive;
import org.yang.business.role.RoleModel;

/**
 * 伤害
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HarmImpl extends IActive {
    final private static String name = "伤害";
    private int targetX;//目标X
    private int targetY;//目标Y
    private long harm;

    public HarmImpl(RoleModel targetRole, long harm) {

        this.targetX = targetRole.getX();
        this.targetY = targetRole.getY();
        this.harm = harm;
    }
    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("targetX", targetX);
        result.put("targetY", targetY);
        return result.toJSONString();
    }
}
