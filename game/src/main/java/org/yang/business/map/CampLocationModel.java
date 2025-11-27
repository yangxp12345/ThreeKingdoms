package org.yang.business.map;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yang.business.camp.ICamp;

import java.util.HashMap;
import java.util.Map;

/**
 * 大本营
 */
@Slf4j
@Data
public class CampLocationModel {

    private final Map<ICamp, CampLocation> campLocationMap;

    @Data
    public static class CampLocation {
        private int x;//坐标
        private int y;
        private ICamp camp;//所属阵营

        protected CampLocation(ICamp camp, int x, int y) {
            this.camp = camp;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            JSONObject result = new JSONObject();
            result.put("x", x);
            result.put("y", y);
            result.put("camp", camp);
            return result.toString();
        }
    }

    public CampLocationModel() {
        campLocationMap = new HashMap<>();
    }

    /**
     * 添加大本营
     */
    public void addCampLocation(Class<? extends ICamp> campClass, int x, int y) {
        ICamp camp = ICamp.classMap.get(campClass);
        if (campLocationMap.containsKey(camp))
            throw new RuntimeException(String.format("阵营(%s)已存在,坐标: (%d,%d)", camp.getName(), x, y));
        campLocationMap.put(camp, new CampLocation(camp, x, y));
    }

}