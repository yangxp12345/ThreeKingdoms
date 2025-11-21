package org.yang.springboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yang.business.calc.DataCalc;
import org.yang.business.camp.ICamp;
import org.yang.business.camp.impl.BlackImpl;
import org.yang.business.camp.impl.WhiteImpl;
import org.yang.business.grade.IRoleType;
import org.yang.business.grade.impl.DeputyImpl;
import org.yang.business.grade.impl.GeneralImpl;
import org.yang.business.grade.impl.SoldierImpl;
import org.yang.business.instruction.ICommand;
import org.yang.business.instruction.impl.NearbyImpl;
import org.yang.business.map.MapModel;
import org.yang.business.role.RoleDataModel;
import org.yang.business.role.RoleModel;
import org.yang.business.weapon.impl.UnarmedImpl;
import org.yang.springboot.init.annotation.MyRequestParam;
import org.yang.springboot.request.RequestParamModel;
import org.yang.springboot.socket.SocketServer;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/run")
@Component
@CrossOrigin
public class RunController {

    private boolean isInit = false;//地图是否初始化
    private boolean start = false;//是否已经启动白兵战

    private MapModel mapModel;

    @RequestMapping("/sleep")
    public Object sleep(@MyRequestParam RequestParamModel paramModel) {
        String sleep = paramModel.getBody().get("sleep");
        log.info(sleep);
        SocketServer.sleep = Integer.parseInt(sleep);
        return "{}";
    }

    /**
     * 简单测试请求
     *
     * @return 测试结果
     */
    @RequestMapping("/init")
    public Object init() {//自定义注解
        mapModel = new MapModel(50, 15);
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
        mapModel.updateCommand(BlackImpl.class, SoldierImpl.class, NearbyImpl.class);
        mapModel.updateCommand(WhiteImpl.class, GeneralImpl.class, NearbyImpl.class);
        isInit = true;
        start = false;
        return mapModel.toString();
    }

    /**
     * 修改角色指令
     *
     * @param paramModel 请求参数封装
     * @return 返回成功
     */
    @RequestMapping("/instructionBatch")
    public Object instructionBatch(@MyRequestParam RequestParamModel paramModel) {//自定义注解
        Map<String, String> body = paramModel.getBody();
        String campName = body.get("campName");
        String roleTypeName = body.get("roleTypeName");
        String instructionName = body.get("instructionName");
        ICamp campExample = ICamp.nameMap.get(campName);
        IRoleType roleTypeExample = IRoleType.nameMap.get(roleTypeName);
        ICommand instructionExample = ICommand.nameMap.get(instructionName);
        mapModel.updateCommand(campExample.getClass(), roleTypeExample.getClass(), instructionExample.getClass());
        return "{}";
    }

    /**
     * 修改角色指令
     */
    @RequestMapping("/instruction")
    public Object instruction(@MyRequestParam RequestParamModel paramModel) throws Exception {//自定义注解
        Map<String, String> body = paramModel.getBody();
        String instructionName = body.get("instructionName");
        int roleId = Integer.parseInt(body.get("roleId"));
        ICommand instructionExample = ICommand.nameMap.get(instructionName);
        mapModel.updateCommand(roleId, instructionExample.getClass());
        return "{}";
    }

    /**
     * 开始战斗
     */
    @RequestMapping("/start")
    public Object start() throws Exception {//自定义注解
        if (!isInit) return DataCalc.toJson("error", "请先初始化地图");
        if (start) return DataCalc.toJson("error", "请勿重复开始");
        start = true;
        new Thread(() -> {
            try {
                while (true) {
                    boolean run = mapModel.run();
                    DataCalc.showMap(mapModel);//todo 测试用
                    if (run) break;
                }
                System.out.println("战斗结束 战斗结束 共" + mapModel.getRound() + "回合");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        return DataCalc.toJson("code", 200);
    }
}