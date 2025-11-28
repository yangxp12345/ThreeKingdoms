package org.yang.business.buff.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.buff.IBuff;
import org.yang.business.instruction.ICommand;
import org.yang.business.instruction.impl.NearbyImpl;
import org.yang.business.role.RoleModel;
import org.yang.business.weapon.IWeapon;
import org.yang.business.weapon.impl.UnarmedImpl;

/**
 * 缴械
 */
@Data
@Slf4j
public class DisarmImpl extends IBuff {

    final static private String name = "缴械";
    final private int sustainRound = 4;//状态持续回合
    private int startRound;//开始回合
    private int endRound;//结束回合
    private IWeapon afterIWeapon;


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
        this.afterIWeapon = roleModel.getWeapon();//记录之前的武器
        roleModel.setWeapon(IWeapon.classMap.get(UnarmedImpl.class));//修改武器
    }

    @Override
    protected void sustain(RoleModel roleModel) {
        //效果持续期间的效果
    }

    @Override
    protected void sub(RoleModel roleModel) {
        //增益效果删除
        roleModel.setWeapon(afterIWeapon);//恢复武器
        roleModel.getIBuffMap().remove(this.getName());//删除当前增益效果
    }

}
