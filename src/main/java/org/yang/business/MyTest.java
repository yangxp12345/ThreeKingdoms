package org.yang.business;

import com.alibaba.fastjson.serializer.SerializeConfig;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.camp.impl.BlackImpl;
import org.yang.business.camp.impl.WhiteImpl;
import org.yang.business.grade.impl.DeputyImpl;
import org.yang.business.grade.impl.GeneralImpl;
import org.yang.business.grade.impl.SoldierImpl;
import org.yang.business.instruction.impl.EncircleImpl;
import org.yang.business.instruction.impl.NearbyImpl;
import org.yang.business.map.MapModel;
import org.yang.business.role.RoleDataModel;
import org.yang.business.role.RoleModel;
import org.yang.business.weapon.impl.UnarmedImpl;

@Slf4j
public class MyTest {
    public static void main(String[] args) throws Exception {
        test();
    }

    public static void test() {
        SerializeConfig.getGlobalInstance().setAsmEnable(true);

        MapModel mapModel = new MapModel(50, 15);
        mapModel.addCampLocation(WhiteImpl.class, 0, 7);//添加大本营
        mapModel.addCampLocation(BlackImpl.class, 49, 7);

        mapModel.addRoleModel(RoleModel.create(1, WhiteImpl.class, GeneralImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 2, 7);

        mapModel.addRoleModel(RoleModel.create(2, BlackImpl.class, GeneralImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 48, 11);
        mapModel.addRoleModel(RoleModel.create(3, BlackImpl.class, DeputyImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 20, 12);
        mapModel.addRoleModel(RoleModel.create(4, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 20, 3);
        mapModel.addRoleModel(RoleModel.create(5, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 20, 6);
        mapModel.addRoleModel(RoleModel.create(6, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 20, 2);
        mapModel.addRoleModel(RoleModel.create(7, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 24, 7);
        mapModel.addRoleModel(RoleModel.create(8, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 24, 8);
        mapModel.addRoleModel(RoleModel.create(9, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 24, 9);
        mapModel.addRoleModel(RoleModel.create(10, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 24, 14);
        mapModel.addRoleModel(RoleModel.create(11, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 24, 1);
        mapModel.addRoleModel(RoleModel.create(12, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 30, 6);
        mapModel.addRoleModel(RoleModel.create(13, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 30, 2);
        mapModel.addRoleModel(RoleModel.create(14, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 30, 7);
        mapModel.addRoleModel(RoleModel.create(15, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 30, 8);
        mapModel.addRoleModel(RoleModel.create(16, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 35, 9);
        mapModel.addRoleModel(RoleModel.create(17, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 35, 11);
        mapModel.addRoleModel(RoleModel.create(18, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 35, 12);
        mapModel.addRoleModel(RoleModel.create(19, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 35, 1);
        mapModel.addRoleModel(RoleModel.create(20, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 35, 13);
        mapModel.addRoleModel(RoleModel.create(21, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 30, 14);
        mapModel.addRoleModel(RoleModel.create(22, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 30, 4);
        mapModel.addRoleModel(RoleModel.create(23, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 40, 1);
        mapModel.addRoleModel(RoleModel.create(24, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 43, 3);
        mapModel.addRoleModel(RoleModel.create(25, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 43, 12);
        mapModel.addRoleModel(RoleModel.create(26, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 43, 4);
        mapModel.addRoleModel(RoleModel.create(27, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 43, 6);
        mapModel.addRoleModel(RoleModel.create(28, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 43, 2);
        mapModel.addRoleModel(RoleModel.create(29, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 40, 7);
        mapModel.addRoleModel(RoleModel.create(30, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 40, 8);
        mapModel.addRoleModel(RoleModel.create(31, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 40, 9);
        System.out.println(mapModel);

        mapModel.updateCommand(BlackImpl.class, GeneralImpl.class, EncircleImpl.class);
        mapModel.updateCommand(BlackImpl.class, DeputyImpl.class, EncircleImpl.class);
        mapModel.updateCommand(BlackImpl.class, SoldierImpl.class, EncircleImpl.class);
        mapModel.updateCommand(WhiteImpl.class, GeneralImpl.class, NearbyImpl.class);
        mapModel.updateCommand(WhiteImpl.class, DeputyImpl.class, EncircleImpl.class);
        mapModel.updateCommand(WhiteImpl.class, SoldierImpl.class, EncircleImpl.class);
        System.out.println(mapModel);
        DataCalc.showMap(mapModel);//todo 测试用
        try {
            while (true) {
                boolean run = mapModel.run();
                DataCalc.showMap(mapModel);//todo 测试用
                if (run) break;
            }
        } catch (Exception e) {
            log.error("系统异常", e);
            throw new RuntimeException(e);
        }

        System.out.println("战斗结束 战斗结束 共" + mapModel.getRound() + "回合");
    }
}
