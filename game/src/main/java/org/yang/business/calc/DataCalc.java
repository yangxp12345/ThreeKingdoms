package org.yang.business.calc;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.camp.ICamp;
import org.yang.business.camp.impl.WhiteImpl;
import org.yang.business.map.MapModel;
import org.yang.business.role.RoleModel;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DataCalc {


    /**
     * 记录未被实现的方法调用
     *
     * @param obj 当前实现类对象
     */
    public static void showHint(Object obj) {
        log.info("{}类未实现方法{}", obj.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    /**
     * 测试使用  答应地图信息
     *
     * @param mapModel 地图对象
     */
    public static void showMap(MapModel mapModel) {
        RoleModel[][] roleModels = mapModel.getRoleModels();
        StringBuilder sbr = new StringBuilder();
        sbr.append("  ");
        for (int i = 0; i < mapModel.getX(); i++) {
            sbr.append("- ");
        }
        sbr.append("\n");
        for (int y = mapModel.getY() - 1; y > 0; y--) {
            sbr.append(" |");
            for (int x = 0; x < mapModel.getX(); x++) {
                String value = roleModels[x][y] == null ? " " : (roleModels[x][y].getCamp().equals(ICamp.classMap.get(WhiteImpl.class)) ? "0" : "1");
                sbr.append(value).append(" ");
            }
            sbr.append("\n");
        }
        log.info("\n{}", sbr);
    }


    /**
     * 判断当前坐标是否是阵营位置
     *
     * @param campRole 阵营位置集合
     * @param x        坐标
     * @param y        坐标
     * @return 是否是阵营位置
     */
    private static boolean isCampLocation(Map<ICamp, RoleModel> campRole, int x, int y) {
        for (RoleModel roleModel : campRole.values()) {
            if (roleModel.getX() == x && roleModel.getY() == y) return true;
        }
        return false;
    }

    /**
     * 随机从集合中获取一个参数
     *
     * @param list 集合
     * @return 随机元素
     */
    public static <T> T getRandomUnit(List<T> list) {
        return list.get(getRandom(0, list.size() - 1));
    }

    /**
     * 获取范围内的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 返回区间内部的随机数 闭区间
     */
    public static int getRandom(int min, int max) {
        long round = Math.round(Math.random() * (max - min) + min);
        return (int) round;
    }

    /**
     * 概率触发
     *
     * @param threshold  触发阈值
     * @param upperLimit 概率上限
     * @return 是否触发
     */
    public static boolean isProbabilityTrigger(int threshold, int upperLimit) {
        return getRandom(1, upperLimit) <= threshold;
    }

    /**
     * 概率触发
     *
     * @param probability 概率
     * @return 是否触发
     */
    public static boolean isProbabilityTrigger(double probability) {
        return Math.random() <= probability;
    }

    /**
     * 取整数 四舍五入
     *
     * @param value 值
     * @return 整数
     */
    public static int toInt(double value) {
        long round = Math.round(value);
        return (int) round;
    }

    /**
     * 休眠
     *
     * @param millis 毫秒
     */
    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("休眠异常", e);
        }
    }


    /**
     * 获取二维数组转为二维List
     *
     * @param array 需要转化的二维数组
     * @param <T>   泛型
     * @return 返回二维List
     */
    public static <T> List<List<T>> toArray(T[][] array) {
        return Arrays.stream(array).map(row -> Arrays.stream(row).collect(Collectors.toList())).collect(Collectors.toList());
    }

    /**
     * 将参数转为json对象 根据kv键值对的顺序写入
     *
     * @param param 按照kv的顺序传入的数组
     * @return 返回json对象
     */
    public static JSONObject toJson(Object... param) {
        JSONObject result = new JSONObject();
        for (int i = 0; i < param.length; i += 2) {
            result.put(String.valueOf(param[i]), param[i + 1]);
        }
        return result;
    }

}
