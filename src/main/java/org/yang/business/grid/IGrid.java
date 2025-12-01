package org.yang.business.grid;

import lombok.Data;

import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.role.RoleModel;
import org.yang.springboot.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 地图的不同类型
 */
@Data
@Slf4j
public abstract class IGrid {
    private static final String name = "格子";
    final private int act = 1;//进入需要消耗的行动力
    final static public Map<Class<? extends IGrid>, IGrid> classMap = new HashMap<>();
    final static public Map<String, IGrid> nameMap = new HashMap<>();

    static {
        try {
            Set<Class<?>> classSet = ReflectionUtil.getClassSet(IGrid.class);
            for (Class<?> clazz : classSet) {
                IGrid unit = (IGrid) clazz.newInstance();
                classMap.put((Class<? extends IGrid>) clazz, unit);
                nameMap.put(unit.getName(), unit);
            }
        } catch (Exception e) {
            log.error("获取{}异常", name, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取名称
     */
    public String getName() {
        DataCalc.showHint(this);
        return name;
    }


    /**
     * 代理离开格子
     *
     * @param role 当前角色
     * @return 返回是否存活
     */
    final public boolean proxyLeave(RoleModel role) {
        RoleModel[][] roleModels = role.getMapModel().getRoleModels();
        roleModels[role.getX()][role.getY()] = null;//删除地图上的角色数据
        this.leave(role);//调用离开方法
        return false;
    }

    /**
     * 代理进入格子
     *
     * @param role 当前角色
     * @return 返回是否存活
     */
    final public boolean proxyEnter(RoleModel role) {
        role.getMapModel().getRoleModels()[role.getX()][role.getY()] = role;//重新设置地图上的角色数据
        role.setGrid(this);//设置角色的格子
        this.enter(role);//调用进入方法
        return false;
    }


    /**
     * 进入
     *
     * @param role 当前角色
     * @return 是否阵亡
     */
    protected boolean enter(RoleModel role) {
        DataCalc.showHint(this);
        return false;
    }


    /**
     * 停留
     *
     * @param role 当前角色
     * @return 是否阵亡
     */
    public boolean stay(RoleModel role) {
        DataCalc.showHint(this);
        return false;
    }


    /**
     * 离开
     *
     * @param role 当前角色
     * @return 是否阵亡
     */
    protected abstract boolean leave(RoleModel role);


    /**
     * 是否没有行动力进入当前格子
     *
     * @param role 当前角色
     * @return 是否能够进入
     */
    final public boolean isEnterFailure(RoleModel role) {
        return role.getCurrentActive() < this.getAct();
    }
}
