package org.yang.business.map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.buff.IBuff;
import org.yang.business.calc.DataCalc;
import org.yang.business.camp.ICamp;
import org.yang.business.grade.IRoleType;
import org.yang.business.grid.ExampleGrid;
import org.yang.business.grid.IGrid;
import org.yang.business.instruction.ICommand;
import org.yang.business.role.RoleModel;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 小沙场
 */

@Slf4j
@Data
public class MapModel {

    public final Map<Integer, RoleModel> campMemRole;//战场阵容内存角色
    private transient IterData<RoleModel> iterData;//迭代对象
    public final List<RoleModel> killRoleList;//被击杀的角色
    public final List<RoleModel> retreatRoleList;//撤退的角色
    private final CampLocationModel campLocation;//大本营


    final private IGrid[][] grids;//格子
    final transient private RoleModel[][] roleModels;//地图上的角色坐标记录

    private int X;//地图坐标上限
    private int Y;
    private transient int round;//回合数
    private transient int unityAddRound = 10;//多少个回合增加士气


    public MapModel() {
        this(50, 15);//设置默认值
    }

    public MapModel(int x, int y) {
        this.X = x;
        this.Y = y;
        this.grids = new IGrid[this.X][this.Y];//地图范围
        this.roleModels = new RoleModel[this.X][this.Y];//角色范围
        ExampleGrid.defaultGrid(grids);//生成默认地图
        this.round = 0;
        this.campMemRole = new HashMap<>();//战场阵容内存角色归类
        this.campLocation = new CampLocationModel();//大本营位置
        this.killRoleList = new ArrayList<>();//被击杀的角色
        this.retreatRoleList = new ArrayList<>();//撤退的角色
    }


    /**
     * 运行一回合
     *
     * @return 是否战斗结束
     */
    public boolean run() throws Exception {
        this.round++;//增加一回合
        log.info("第[{}]回合", this.round);
        //buff持续作用
        List<RoleModel> allBuffList = this.campMemRole.values().stream().sorted((before, after) -> {//根据角色的trick(技巧) 从大到小排序  如果二者大小一样,便使用随机排序
            if (before.getTrick() == after.getTrick()) return new Random().nextInt(2) - 1;
            return before.getTrick() > after.getTrick() ? -1 : 1;
        }).collect(Collectors.toList());
        if (allBuffList.isEmpty()) return true;//没有可以控制的角色 战斗结束
        IterData<RoleModel> buffRoleIter = new IterData<>(allBuffList);//复制对象,迭代处理
        while (buffRoleIter.hasNext()) {//遍历角色
            RoleModel roleModel = buffRoleIter.next();
            List<IBuff> iBuffs = new ArrayList<>(roleModel.getIBuffMap().values());
            IterData<IBuff> buffIter = new IterData<>(iBuffs);//复制对象,迭代处理
            while (buffIter.hasNext()) {//遍历一个角色的buff
                IBuff iBuff = buffIter.next();
                iBuff.proxySustain(roleModel);
            }
        }


        //清空上一次的行动记录
        int campSize = this.campMemRole.values().stream().map(RoleModel::getCamp).collect(Collectors.toSet()).size();//存活阵容数量
        if (campSize == 1) return true;//只剩下一个阵容 战斗结束
        List<RoleModel> allRoleList = this.campMemRole.values().stream().sorted((before, after) -> {//根据角色的trick(技巧) 从大到小排序  如果二者大小一样,便使用随机排序
            if (before.getTrick() == after.getTrick()) return new Random().nextInt(2) - 1;
            return before.getTrick() > after.getTrick() ? -1 : 1;
        }).collect(Collectors.toList());
        if (allRoleList.isEmpty()) return true;//没有可以控制的角色 战斗结束
        this.iterData = new IterData<>(allRoleList);//复制对象,迭代处理
        while (this.iterData.hasNext()) {
            RoleModel roleModel = this.iterData.next();
            roleModel.getCommand().proxyRun(roleModel);
        }
        //每一回合胆魄加1
        if (this.round % unityAddRound == 0) {
            this.campMemRole.values().forEach(unit -> unit.setUnity(unit.getUnity() + 1));
        }
        return false;//战斗未结束
    }


    /**
     * 添加大本营
     *
     * @param campClass 阵营类型
     * @param x         坐标
     * @param y         坐标
     */
    public void addCampLocation(Class<? extends ICamp> campClass, int x, int y) {
        if (isCrossMap(x, y))
            throw new RuntimeException(String.format("坐标(%d,%d)越界, 规定范围是([0-%d],[0-%d])", x, y, X - 1, Y - 1));
        this.campLocation.addCampLocation(campClass, x, y);//添加阵营
    }

    /**
     * 添加角色到战场
     *
     * @param roleModel 角色
     * @param x         坐标
     * @param y         坐标
     */
    public synchronized void addRoleModel(RoleModel roleModel, int x, int y) {
        if (isCrossMap(x, y))
            throw new RuntimeException(String.format("坐标(%d,%d)越界, 规定范围是([0-%d],[0-%d])", x, y, X - 1, Y - 1));
        if (this.roleModels[x][y] != null)
            throw new RuntimeException(String.format("当前坐标(%d,%d)已经有角色%s", x, y, this.roleModels[x][y].getRoleType().getName()));
        roleModel.load(this, x, y);//加载数据
        this.campMemRole.put(roleModel.getId(), roleModel);//写入阵容
        this.roleModels[x][y] = roleModel;//写入地图
    }


    /**
     * 撤退角色清理数据
     *
     * @param roleModel 需要撤退的对象
     */
    public void retreatRole(RoleModel roleModel) {
        MapModel mapModel = roleModel.getMapModel();
        mapModel.removeRole(roleModel);//清理地图数据
        mapModel.getRetreatRoleList().add(roleModel);//添加撤退数据
    }


    /**
     * 角色击杀
     *
     * @param roleModel 被击杀的角色
     */
    public void killRole(RoleModel roleModel) {
        MapModel mapModel = roleModel.getMapModel();
        mapModel.removeRole(roleModel);//清理地图数据
        mapModel.getKillRoleList().add(roleModel);//添加击杀数据

    }


    /**
     * 移除角色数据
     *
     * @param roleModel 需要移除的角色
     */
    private void removeRole(RoleModel roleModel) {
        this.campMemRole.remove(roleModel.getId());//移除内存
        this.iterData.remove(roleModel);//移除迭代器
        this.roleModels[roleModel.getX()][roleModel.getY()] = null;//移除地图
    }


    /**
     * 更新指令
     *
     * @param campClass        阵营编号
     * @param roleTypeClass    角色类型
     * @param commandTypeClass 指令
     */
    public void updateCommand(Class<? extends ICamp> campClass, Class<? extends IRoleType> roleTypeClass, Class<? extends ICommand> commandTypeClass) {
        ICamp iCamp = ICamp.classMap.get(campClass);
        IRoleType iRoleType = IRoleType.classMap.get(roleTypeClass);
        ICommand iCommand = ICommand.classMap.get(commandTypeClass);
        for (RoleModel roleModel : this.getCampMemRole().values()) {
            if (!roleModel.getCamp().equals(iCamp)) continue;//非相同阵营
            if (!roleModel.getRoleType().equals(iRoleType)) continue;///非指定类型的角色
            roleModel.setCommand(iCommand);//修改指令
        }
    }

    /**
     * 是否越界
     *
     * @param currentX 地图坐标
     * @param currentY 地图坐标
     * @return 是否越界
     */
    public boolean isCrossMap(int currentX, int currentY) {
        return 0 > currentX || currentX >= this.X || currentY < 0 || currentY >= this.Y;
    }

    @Override
    public String toString() {
        JSONObject result = new JSONObject();
        result.put("X", X);
        result.put("Y", Y);
        result.put("grids", DataCalc.toArray(grids));
        result.put("roleModels", campMemRole.values().stream().map(RoleModel::toJSON).collect(Collectors.toList()));//toString会自动调用get方法导致数据递归
        result.put("campLocation", new ArrayList<>(campLocation.getCampLocationMap().values()));
        return JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
    }
}