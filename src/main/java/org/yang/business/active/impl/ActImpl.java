package org.yang.business.active.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yang.business.active.IActive;
import org.yang.business.role.RoleModel;

/**
 * 攻击
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActImpl extends IActive {
    final private static String name = "攻击";

    private int sourceX;//原X
    private int sourceY;//原Y

    private int targetX;//目标X
    private int targetY;//目标Y

    public ActImpl(RoleModel sourceRole, RoleModel targetRole) {
        this.sourceX = sourceRole.getX();
        this.sourceY = sourceRole.getY();
        this.targetX = targetRole.getX();
        this.targetY = targetRole.getY();
    }

    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("sourceX", sourceX);
        result.put("sourceY", sourceY);
        result.put("targetX", targetX);
        result.put("targetY", targetY);
        return result.toJSONString();
    }
}
