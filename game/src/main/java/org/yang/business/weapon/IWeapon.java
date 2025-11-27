package org.yang.business.weapon;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.active.impl.*;
import org.yang.business.calc.DataCalc;
import org.yang.business.role.RoleModel;
import org.yang.springboot.socket.SocketServer;
import org.yang.springboot.util.ReflectionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 士兵武器
 */
@Slf4j
@Data
public abstract class IWeapon {

    final static private String name = "士兵武器";
    final private int active = 0;//行动消耗
    final static public Map<Class<? extends IWeapon>, IWeapon> classMap = new HashMap<>();
    final static public Map<String, IWeapon> nameMap = new HashMap<>();

    static {
        try {
            Set<Class<? extends IWeapon>> classSet = ReflectionUtil.getClassSet(IWeapon.class);
            for (Class<? extends IWeapon> clazz : classSet) {
                IWeapon unit = clazz.newInstance();
                classMap.put(clazz, unit);
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
     * 代理行动
     *
     * @param role      角色
     * @param enemyRole 主要的敌方角色
     */
    final public void proxyAct(RoleModel role, RoleModel enemyRole) {
        if (role.getCurrentActive() < this.getActive()) {
            role.setCurrentActive(0);
            return;
        }
        role.setCurrentActive(role.getCurrentActive() - this.getActive());//允许攻击 更新行动力

        if (!isHit(role, enemyRole)) {//未命中
            SocketServer.send(role.getCamp().getName(), new MissImpl(enemyRole));
            return;
        }
        //命中开始进入上伤害计算
        calcHurt(role, enemyRole);//开始

    }

    /**
     * 计算攻击对象 根据攻击距离和武器计算当前主要的攻击目标
     *
     * @param role 角色
     * @return 攻击对象
     */
    public abstract List<RoleModel> calcEnemyRole(RoleModel role);

    /**
     * 使用武器造成输出,不同的武器输出方式不一样,例如: 贯穿/衍射/单点/连击
     *
     * @param role      角色
     * @param enemyRole 敌方角色
     */
    protected abstract void calcHurt(RoleModel role, RoleModel enemyRole);


    /**
     * 判断是否命中
     *
     * @param role      角色
     * @param enemyRole 敌方角色
     * @return 是否命中失败
     */
    private boolean isHit(RoleModel role, RoleModel enemyRole) {
        SocketServer.send(role.getCamp().getName(), new ActImpl(role, enemyRole));
        if (!DataCalc.isProbabilityTrigger(role.getHit() * 1D / (role.getHit() + enemyRole.getDodge()))) {
            log.info("输出 阵容:{}, 编码:{}, 角色:{}, 坐标:({},{})  =>  阵容:{}, 编码:{}, 角色:{}, 坐标:({},{}) 未命中,剩余生命:{}", role.getCamp().getName(), role.getId(), role.getRoleType().getName(), role.getX(), role.getY(), enemyRole.getCamp().getName(), enemyRole.getId(), enemyRole.getRoleType().getName(), enemyRole.getX(), enemyRole.getY(), enemyRole.getCurrentHealth());
            return false;//未命中 跳出
        }
        return true;
    }


    /**
     * 伤害计算
     *
     * @param role      角色
     * @param enemyRole 目标角色
     * @param multiple  倍数
     */
    protected long damageMultiplier(RoleModel role, RoleModel enemyRole, double multiple) {
        List<RoleModel> teammateRoleList = role.calcTeammateRoleList();//获取队友
        List<RoleModel> enemyRoleList = role.calcAroundEnemyRoleList();//获取敌人
        //主将伤害加成
        double generalAdd = role.calcGeneralRoleModel().stream().map(RoleModel::getAttack).mapToLong(x -> x).sum() * 0.05;
        //副将伤害加成
        double deputyAdd = role.calcDeputyRoleModel().stream().map(RoleModel::getAttack).mapToLong(x -> x).sum() * 0.01;
        //附近人数加成[1/8 ,1] 周围队友人数/周围总人数=加成()周围包含自己
        double peopleBonus = enemyRoleList.isEmpty() ? 1 : ((1D + teammateRoleList.size()) / (1D + teammateRoleList.size() + enemyRoleList.size()));
        //当前角色的技巧加成比例  自己的技巧/(自己的技巧+目标的技巧)
        double proportionTrick = role.getTrick() * 1D / (role.getTrick() + enemyRole.getDefense());
        //当前角色的健康加成比例   自己的生命/生命上限
        double proportionHealth = 1D * role.getCurrentHealth() / role.getCumulativeHealth();
        double roleAtkAdd = role.getAttack() + generalAdd + deputyAdd;//计算经过主将副将加成后的攻击力
        double valueDouble = multiple * roleAtkAdd * peopleBonus * proportionTrick * proportionHealth - enemyRole.getExempt();
        long value = Math.max(DataCalc.toLong(valueDouble), 1);//最低伤害为1
        // 角色技能加成
        SocketServer.send(role.getCamp().getName(), new HarmImpl(enemyRole, value));
        enemyRole.setCurrentHealth(enemyRole.getCurrentHealth() - value);
        log.info("输出 阵容:{}, 编码:{}, 角色:{}, 坐标:({},{})  =>  阵容:{}, 编码:{}, 角色:{}, 坐标:({},{}) 伤害:{} ,剩余生命:{}", role.getCamp().getName(), role.getId(), role.getRoleType().getName(), role.getX(), role.getY(), enemyRole.getCamp().getName(), enemyRole.getId(), enemyRole.getRoleType().getName(), enemyRole.getX(), enemyRole.getY(), value, enemyRole.getCurrentHealth());
        if (enemyRole.getCurrentHealth() <= 0) {
            log.info("阵容:{}, 级别:{}, 编号:{}, 坐标:({},{}) 被击杀", enemyRole.getCamp().getName(), enemyRole.getRoleType().getName(), enemyRole.getId(), enemyRole.getX(), enemyRole.getY());
            role.getMapModel().killRole(enemyRole);//击杀角色
            SocketServer.send(role.getCamp().getName(), new KillImpl(enemyRole));
        }
        return value;
    }

}