package org.yang.business.grid.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.grid.IGrid;
import org.yang.business.role.RoleModel;

@Slf4j
@Data
public class PlainImpl extends IGrid {
    final private String name = "平原";
    final private int act = 2;//进入需要消耗Act


    @Override
    public boolean enter(RoleModel role) {
        role.setCurrentActive(role.getCurrentActive() - this.act);
        return false;
    }

    @Override
    public boolean stay(RoleModel role) {
        return false;
    }

    @Override
    public boolean leave(RoleModel role) {
        return false;
    }

    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        result.put("act", this.act);
        result.put("name", this.name);
        return result.toJSONString();
    }
}
