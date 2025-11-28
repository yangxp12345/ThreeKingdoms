package org.yang.business.buff.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.buff.IBuff;
import org.yang.business.role.RoleModel;

/**
 * 虚弱
 */
@Data
@Slf4j
public class WeaknessImpl extends IBuff {

    final static private String name = "虚弱";
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
        roleModel.setCumulativeActive(roleModel.getCumulativeActive() - 1);//行动力下降1
        roleModel.setCurrentActive(roleModel.getCurrentActive() - 1);
    }

    @Override
    protected void sustain(RoleModel roleModel) {
        //效果持续期间的效果
    }

    @Override
    protected void sub(RoleModel roleModel) {
        //增益效果删除 恢复行动力上限
        roleModel.setCumulativeActive(roleModel.getCumulativeActive() + 1);
        roleModel.setCurrentActive(roleModel.getCurrentActive() + 1);
        roleModel.getIBuffMap().remove(this.getName());//删除当前增益效果
    }

}
