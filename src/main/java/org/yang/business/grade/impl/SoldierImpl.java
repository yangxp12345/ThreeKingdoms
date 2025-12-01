package org.yang.business.grade.impl;

import lombok.Data;
import org.yang.business.calc.DataCalc;
import org.yang.business.camp.ICamp;
import org.yang.business.grade.IRoleType;
import org.yang.business.instruction.ICommand;

import org.yang.business.role.RoleModel;
import org.yang.business.weapon.IWeapon;

import java.util.List;
import java.util.Map;

/**
 * 小兵
 */
@Data
public class SoldierImpl extends IRoleType {
    final private String name = "小兵";



    public SoldierImpl() {
    }


}
