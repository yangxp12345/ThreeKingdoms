package org.yang.business.grade.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.calc.DataCalc;
import org.yang.business.camp.ICamp;
import org.yang.business.grade.IRoleType;
import org.yang.business.instruction.ICommand;
import org.yang.business.role.RoleModel;
import org.yang.business.weapon.IWeapon;



/**
 * 主将
 */
@Slf4j
@Data
public class GeneralImpl extends IRoleType {
    final private String name = "主将";



    public GeneralImpl() {

    }



}
