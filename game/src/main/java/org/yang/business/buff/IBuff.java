package org.yang.business.buff;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.role.RoleModel;

/**
 * 角色的状态增益
 */
@Slf4j
@Data
public abstract class IBuff {
    final static private String name = "状态名称";
    final private int sustainRound = 4;//状态持续回合
    private int startRound;//开始回合
    private int endRound;//结束回合

    /**
     * 代理添加增益
     *
     * @param roleModel 增益对应的角色
     */
    public void proxyAdd(RoleModel roleModel) {
        int currentRound = roleModel.getMapModel().getRound();//当前回合数
        this.setEndRound(this.getSustainRound() + currentRound);//初始化结束回合数
        this.setStartRound(currentRound);//初始化结束回合数
        this.add(roleModel);//添加增益
    }

    public void proxySustain(RoleModel roleModel) {
        if (roleModel.getMapModel().getRound() >= endRound) sub(roleModel);//是否结束
        this.sustain(roleModel);
    }

    /**
     * 叠加状态
     *
     */
    public void overlayAdd() {
        this.setEndRound(this.getEndRound() + this.getSustainRound());//叠加回合数
    }


    /**
     * 获取名称
     */
    public abstract String getName();

    /**
     * 获取名称
     */
    public abstract String toString();

    /**
     * 添加增益
     *
     * @param roleModel 增益对应的角色
     */
    protected abstract void add(RoleModel roleModel);


    /**
     * 维持增益
     *
     * @param roleModel 增益对应的角色
     */
    protected abstract void sustain(RoleModel roleModel);


    /**
     * 删除增益
     *
     * @param roleModel 增益对应的角色
     */
    protected abstract void sub(RoleModel roleModel);


}
