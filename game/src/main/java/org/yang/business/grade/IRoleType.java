package org.yang.business.grade;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.camp.ICamp;

import org.yang.business.grade.impl.GeneralImpl;
import org.yang.business.instruction.ICommand;
import org.yang.business.map.MapModel;
import org.yang.business.role.RoleModel;
import org.yang.springboot.util.ReflectionUtil;

import java.util.*;


/**
 * 身份
 */
@Data
@Slf4j
abstract public class IRoleType {
    final private static String name = "身份";


    public static Map<Class<? extends IRoleType>, IRoleType> classMap = new HashMap<>();//类型的唯一实例
    public static Map<String, IRoleType> nameMap = new HashMap<>();//名称的唯一实例

    static {
        try {
            Set<Class<? extends IRoleType>> classSet = ReflectionUtil.getClassSet(IRoleType.class);
            for (Class<? extends IRoleType> clazz : classSet) {
                IRoleType unit = clazz.newInstance();
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
     * 新增
     *
     * @param roleModel 角色对象
     */
    public void addRoleModel(RoleModel roleModel) {
        if (!roleModel.getMapModel().campMemRole.containsKey(roleModel.getCamp()))
            roleModel.getMapModel().campMemRole.put(roleModel.getCamp(), new HashMap<>());
        Map<Class<? extends IRoleType>, List<RoleModel>> roleTypeRoleListMap = roleModel.getMapModel().campMemRole.get(roleModel.getCamp());//父类的成员变量
        if (!roleTypeRoleListMap.containsKey(this.getClass()))
            roleTypeRoleListMap.put(this.getClass(), new ArrayList<>());
        roleTypeRoleListMap.get(this.getClass()).add(roleModel);
        roleModel.getMapModel().allCampSet.add(roleModel.getCamp());

    }


    /**
     * 击杀
     *
     * @param roleModel 被删除的角色信息
     */
    public void killRole(RoleModel roleModel) {
        removeCampRole(roleModel);//移除战场阵容内存角色归类
        if (!roleModel.getMapModel().campKillRole.containsKey(roleModel.getCamp()))//添加击杀数据
            roleModel.getMapModel().campKillRole.put(roleModel.getCamp(), new HashMap<>());//添加阵容标签
        Map<Class<? extends IRoleType>, List<RoleModel>> roleTypeListMap = roleModel.getMapModel().campKillRole.get(roleModel.getCamp());
        if (!roleTypeListMap.containsKey(this.getClass()))
            roleTypeListMap.put(this.getClass(), new ArrayList<>());
        roleTypeListMap.get(this.getClass()).add(roleModel);
    }

    /**
     * 撤退
     *
     * @param roleModel 被删除的角色信息
     */
    public void retreatRole(RoleModel roleModel) {
        removeCampRole(roleModel);//移除战场阵容内存角色归类
        //添加撤退数据
        if (!roleModel.getMapModel().campRetreatRole.containsKey(roleModel.getCamp()))
            roleModel.getMapModel().campRetreatRole.put(roleModel.getCamp(), new HashMap<>());//添加阵容标签
        Map<Class<? extends IRoleType>, List<RoleModel>> roleTypeListMap = roleModel.getMapModel().campRetreatRole.get(roleModel.getCamp());
        if (!roleTypeListMap.containsKey(this.getClass()))
            roleTypeListMap.put(this.getClass(), new ArrayList<>());
        roleTypeListMap.get(this.getClass()).add(roleModel);
    }

    /**
     * 移除战场内存角色
     *
     * @param roleModel 需要被移除的角色
     */
    private void removeCampRole(RoleModel roleModel) {
        //移除战场阵容内存角色归类
        roleModel.getMapModel().campMemRole.get(roleModel.getCamp()).get(roleModel.getRoleType().getClass()).remove(roleModel);
        if (roleModel.getMapModel().campMemRole.get(roleModel.getCamp()).get(roleModel.getRoleType().getClass()).isEmpty())//角色类型为空
            roleModel.getMapModel().campMemRole.get(roleModel.getCamp()).remove(roleModel.getRoleType().getClass());//删除角色类型
        if (roleModel.getMapModel().campMemRole.get(roleModel.getCamp()).isEmpty()) //阵营为空
            roleModel.getMapModel().campMemRole.remove(roleModel.getCamp());//删除阵营
    }

    /**
     * 更新指令
     *
     * @param campClass     阵营类型
     * @param roleTypeClass 角色类型
     * @param command       指令对象
     */
    public static void updateCommand(Class<? extends ICamp> campClass, Class<? extends IRoleType> roleTypeClass, Class<? extends ICommand> command, MapModel mapModel) {
        Map<Class<? extends IRoleType>, List<RoleModel>> roleTypeListMap = mapModel.campMemRole.get(ICamp.classMap.get(campClass));
        if (roleTypeListMap == null) return;
        IRoleType roleType = IRoleType.classMap.get(roleTypeClass);
        List<RoleModel> roleRoleList = roleTypeListMap.get(roleType.getClass());
        if (roleRoleList == null) return;
        for (RoleModel roleModel : roleRoleList) {
            roleModel.setCommand(ICommand.classMap.get(command));
        }
    }

    /**
     * 更新指令
     *
     * @param roleId  角色id
     * @param command 指令对象
     */
    public static void updateCommand(Integer roleId, Class<? extends ICommand> command, MapModel mapModel) {
        RoleModel roleModel = mapModel.getRoleIdMap().get(roleId);
        roleModel.setCommand(ICommand.classMap.get(command));
    }

}
