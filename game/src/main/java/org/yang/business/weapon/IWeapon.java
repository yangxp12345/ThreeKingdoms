package org.yang.business.weapon;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.active.impl.*;
import org.yang.business.calc.DataCalc;
import org.yang.business.map.MapModel;
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
    final private int act = 1;//行动消耗
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
     * @param enemyRole 敌方角色
     */
    final public void proxyAct(RoleModel role, RoleModel enemyRole) {
        if (role.getCurrentAct() < this.getAct()) {//力竭
            role.setCurrentAct(0);
            SocketServer.send(role.getCamp().getName(), new ExhaustionImpl(role));
            return;
        }
        role.setCurrentAct(role.getCurrentAct() - this.getAct());//更新行动力

        if (isFrighten(role, enemyRole)) {//被震慑
            SocketServer.send(role.getCamp().getName(), new FrightenImpl(role, enemyRole));
            return;
        }
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
    public abstract void calcHurt(RoleModel role, RoleModel enemyRole);


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
     * 是否被震慑
     * (周围队友提供胆+自己胆)/ (周围队友提供胆+自己胆+目标胆)
     *
     * @param role      当前角色
     * @param enemyRole 目标角色
     * @return 是否被震慑
     */
    private boolean isFrighten(RoleModel role, RoleModel enemyRole) {
        //根据当前角色周围的队友数量决定部分输出
        List<RoleModel> teammateRoleList = role.calcTeammateRoleList();//获取队友
        //求和
        int teammateUnitySum = teammateRoleList.stream().map(RoleModel::getUnity).mapToInt(Integer::intValue).sum();//周围队友提供胆魄
        if (DataCalc.isProbabilityTrigger((teammateUnitySum + role.getUnity()) * 1D / (teammateUnitySum + role.getUnity() + enemyRole.getUnity()))) {
            return false;//未被震慑
        }
        log.info("输出 阵容:{}, 编码:{}, 角色:{}, 坐标:({},{})  =>  阵容:{}, 编码:{}, 角色:{}, 坐标:({},{}) 被目标震慑,剩余生命:{}", role.getCamp().getName(), role.getId(), role.getRoleType().getName(), role.getX(), role.getY(), enemyRole.getCamp().getName(), enemyRole.getId(), enemyRole.getRoleType().getName(), enemyRole.getX(), enemyRole.getY(), enemyRole.getCurrentHealth());
        return true;//被震慑
    }


    /**
     * 伤害计算
     *
     * @param role      角色
     * @param enemyRole 目标角色
     * @param multiple  倍数
     */
    public void damageMultiplier(RoleModel role, RoleModel enemyRole, int multiple) {

        List<RoleModel> teammateRoleList = role.calcTeammateRoleList();//获取队友
        List<RoleModel> enemyRoleList = role.calcEnemyRoleList();//获取敌人

        //附近人数加成
        double peopleBonus = enemyRoleList.isEmpty() ? 1 : ((1D + teammateRoleList.size()) / (1D + teammateRoleList.size() + enemyRoleList.size()));
        //当前角色的技巧加成比例
        double proportionTrick = role.getTrick() * 1D / (role.getTrick() + enemyRole.getDefense());
        //当前角色的健康加成比例
        double proportionHealth = 1D * role.getCurrentHealth() / role.getCumulativeHealth();

        double valueDouble = multiple * role.getAttack() * peopleBonus * proportionTrick * proportionHealth - enemyRole.getExempt();
        int value = Math.max(DataCalc.toInt(valueDouble), 1);//最低伤害为1
        // 角色技能加成
        SocketServer.send(role.getCamp().getName(), new HarmImpl(enemyRole, value));
        enemyRole.setCurrentHealth(enemyRole.getCurrentHealth() - value);
        log.info("输出 阵容:{}, 编码:{}, 角色:{}, 坐标:({},{})  =>  阵容:{}, 编码:{}, 角色:{}, 坐标:({},{}) 伤害:{} ,剩余生命:{}", role.getCamp().getName(), role.getId(), role.getRoleType().getName(), role.getX(), role.getY(), enemyRole.getCamp().getName(), enemyRole.getId(), enemyRole.getRoleType().getName(), enemyRole.getX(), enemyRole.getY(), value, enemyRole.getCurrentHealth());
        if (enemyRole.getCurrentHealth() <= 0) {
            log.info("阵容:{}, 级别:{}, 编号:{}, 坐标:({},{}) 被击杀", enemyRole.getCamp().getName(), enemyRole.getRoleType().getName(), enemyRole.getId(), enemyRole.getX(), enemyRole.getY());
            role.getMapModel().killRole(enemyRole);
            SocketServer.send(role.getCamp().getName(), new KillImpl(enemyRole));
        }
    }

}