package org.yang.business.buff.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.buff.IBuff;
import org.yang.business.role.RoleModel;

/**
 * 中毒
 */
@Data
@Slf4j
public class PoisonImpl extends IBuff {

    final static private String name = "中毒";
    final private int sustainRound = 4;//状态持续回合
    private int startRound;//开始回合
    private int endRound;//结束回合


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("sustainRound", sustainRound);
        result.put("startRound", startRound);
        result.put("endRound", endRound);
        return result.toJSONString();
    }

    @Override
    public void add(RoleModel roleModel) {
        //增益效果
    }

    @Override
    protected void sustain(RoleModel roleModel) {
        //效果持续期间的效果
        int currentRound = roleModel.getMapModel().getRound();
        roleModel.setCurrentHealth(roleModel.getCurrentHealth() - currentRound);
        if (roleModel.getCurrentHealth() <= 0) roleModel.getMapModel().killRole(roleModel);

    }

    @Override
    protected void sub(RoleModel roleModel) {
        roleModel.getIBuffMap().remove(this.getName());//删除当前增益效果
    }

}
