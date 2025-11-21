package org.yang.business.active.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yang.business.active.IActive;
import org.yang.business.role.RoleModel;

/**
 * 力竭
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExhaustionImpl extends IActive {
    final private static String name = "力竭";
    private int sourceX;//原X
    private int sourceY;//原Y

    public ExhaustionImpl(RoleModel sourceRole) {
        this.sourceX = sourceRole.getX();
        this.sourceY = sourceRole.getY();
    }
    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("sourceX", sourceX);
        result.put("sourceY", sourceY);
        return result.toJSONString();
    }
}

