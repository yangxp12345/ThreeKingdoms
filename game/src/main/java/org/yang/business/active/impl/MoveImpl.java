package org.yang.business.active.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yang.business.active.IActive;
import org.yang.business.role.RoleModel;

/**
 * 移动
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoveImpl extends IActive {
    final private static String name = "移动";
    private int sourceX;//原X
    private int sourceY;//原Y

    private int targetX;//目标X
    private int targetY;//目标Y

    public MoveImpl(RoleModel roleModel,int sourceX, int sourceY,int targetX, int targetY){
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.targetX = targetX;
        this.targetY = targetY;
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
