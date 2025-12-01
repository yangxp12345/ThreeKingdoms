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
import org.yang.business.instruction.impl.StandByImpl;
import org.yang.business.map.MapModel;
import org.yang.business.role.RoleDataModel;
import org.yang.business.role.RoleModel;
import org.yang.business.weapon.impl.*;
import org.yang.springboot.init.annotation.MyRequestParam;
import org.yang.springboot.request.RequestParamModel;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/run")
@Component
@CrossOrigin
public class RunController {


    private MapModel mapModel;
    private boolean isInit = false;//是否初始化
    private boolean isRun = false;//是否在运行

    @RequestMapping("/sleep")
    public Object sleep(@MyRequestParam RequestParamModel paramModel) {
        String sleep = paramModel.getBody().get("sleep");
        log.info(sleep);
        ICommand.sleep = Integer.parseInt(sleep);
        return "{}";
    }

    /**
     * 简单测试请求
     *
     * @return 测试结果
     */
    @RequestMapping("/init")
    public synchronized Object init() {//自定义注解
        if (isInit) return mapModel.toString();//已经初始化了
        isInit = true;
        mapModel = new MapModel(50, 15);
        mapModel.getCampLocation().addCampLocation(WhiteImpl.class, 0, 7);//添加大本营
        mapModel.getCampLocation().addCampLocation(BlackImpl.class, 49, 7);


        mapModel.addRoleModel(RoleModel.create(1, WhiteImpl.class, GeneralImpl.class, ArrowImpl.class, RoleDataModel.getRoleDataModel()), 2, 7);

        mapModel.addRoleModel(RoleModel.create(2, BlackImpl.class, GeneralImpl.class, AxeImpl.class, RoleDataModel.getRoleDataModel()), 48, 11);

        mapModel.addRoleModel(RoleModel.create(3, WhiteImpl.class, DeputyImpl.class, CrossbowImpl.class, RoleDataModel.getRoleDataModel()), 7, 12);
        mapModel.addRoleModel(RoleModel.create(4, WhiteImpl.class, SoldierImpl.class, IronHammerImpl.class, RoleDataModel.getRoleDataModel()), 3, 3);
        mapModel.addRoleModel(RoleModel.create(5, WhiteImpl.class, SoldierImpl.class, LongKnifeImpl.class, RoleDataModel.getRoleDataModel()), 6, 6);
        mapModel.addRoleModel(RoleModel.create(6, WhiteImpl.class, SoldierImpl.class, LongSpearImpl.class, RoleDataModel.getRoleDataModel()), 4, 2);
        mapModel.addRoleModel(RoleModel.create(7, WhiteImpl.class, SoldierImpl.class, PreviouslyImpl.class, RoleDataModel.getRoleDataModel()), 5, 7);
        mapModel.addRoleModel(RoleModel.create(8, WhiteImpl.class, SoldierImpl.class, ShortKnifeImpl.class, RoleDataModel.getRoleDataModel()), 2, 8);
        mapModel.addRoleModel(RoleModel.create(9, WhiteImpl.class, SoldierImpl.class, SuctionBloodImpl.class, RoleDataModel.getRoleDataModel()), 1, 9);
        mapModel.addRoleModel(RoleModel.create(10, WhiteImpl.class, SoldierImpl.class, ThrowFireballsImpl.class, RoleDataModel.getRoleDataModel()), 5, 14);
        mapModel.addRoleModel(RoleModel.create(23, BlackImpl.class, SoldierImpl.class, TrebuchetImpl.class, RoleDataModel.getRoleDataModel()), 40, 1);
        mapModel.addRoleModel(RoleModel.create(24, BlackImpl.class, SoldierImpl.class, UnarmedImpl.class, RoleDataModel.getRoleDataModel()), 43, 3);
        mapModel.addRoleModel(RoleModel.create(25, BlackImpl.class, SoldierImpl.class, ArrowImpl.class, RoleDataModel.getRoleDataModel()), 43, 12);
        mapModel.addRoleModel(RoleModel.create(26, BlackImpl.class, SoldierImpl.class, AxeImpl.class, RoleDataModel.getRoleDataModel()), 43, 4);
        mapModel.addRoleModel(RoleModel.create(27, BlackImpl.class, SoldierImpl.class, CrossbowImpl.class, RoleDataModel.getRoleDataModel()), 43, 6);
        mapModel.addRoleModel(RoleModel.create(28, BlackImpl.class, SoldierImpl.class, IronHammerImpl.class, RoleDataModel.getRoleDataModel()), 43, 2);
        mapModel.addRoleModel(RoleModel.create(29, BlackImpl.class, SoldierImpl.class, LongKnifeImpl.class, RoleDataModel.getRoleDataModel()), 40, 7);
        mapModel.addRoleModel(RoleModel.create(30, BlackImpl.class, SoldierImpl.class, LongSpearImpl.class, RoleDataModel.getRoleDataModel()), 40, 8);
        mapModel.addRoleModel(RoleModel.create(31, BlackImpl.class, SoldierImpl.class, PreviouslyImpl.class, RoleDataModel.getRoleDataModel()), 40, 9);
        mapModel.updateCommand(BlackImpl.class, SoldierImpl.class, StandByImpl.class);
        mapModel.updateCommand(BlackImpl.class, DeputyImpl.class, StandByImpl.class);
        mapModel.updateCommand(BlackImpl.class, GeneralImpl.class, StandByImpl.class);
        mapModel.updateCommand(WhiteImpl.class, SoldierImpl.class, StandByImpl.class);
        mapModel.updateCommand(WhiteImpl.class, DeputyImpl.class, StandByImpl.class);
        mapModel.updateCommand(WhiteImpl.class, GeneralImpl.class, StandByImpl.class);
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
        String roleIdStr = body.get("roleId");
        if (roleIdStr == null || roleIdStr.isEmpty()) return DataCalc.toJson("error", "请选择角色");
        int roleId = Integer.parseInt(body.get("roleId"));
        ICommand instructionExample = ICommand.nameMap.get(instructionName);
        mapModel.getCampMemRole().get(roleId).setCommand(instructionExample);
        return "{}";
    }

    /**
     * 开始战斗
     */
    @RequestMapping("/start")
    public synchronized Object start() throws Exception {//自定义注解
        System.out.println(isRun);
        if (isRun) {
            return DataCalc.toJson("code", 200);//已经在运行//
        }
        isRun = true;
        if (!isInit) {
            init();
        }
        run();
        return DataCalc.toJson("code", 200);
    }

    /**
     * 查询某一个角色的详细信息
     */
    @RequestMapping("/getRoleIdMsg")
    public Object getRoleIdMsg(@MyRequestParam RequestParamModel paramModel) throws Exception {//自定义注解
        Integer roleId = Integer.parseInt(paramModel.getBody().get("roleId"));
        RoleModel roleModel = this.mapModel.getCampMemRole().get(roleId);
        return roleModel.toJSON();
    }


    private void run() {
        if (mapModel == null) return;
        new Thread(() -> {
            try {
                while (true) {
                    boolean run = mapModel.run();
                    if (run) break;
                }
                System.out.println("战斗结束 战斗结束 共" + mapModel.getRound() + "回合");
                isInit = false;
                isRun = false;
                mapModel = null;
            } catch (Exception e) {
                log.error("执行异常", e);
                throw new RuntimeException(e);
            }

        }).start();
    }
}