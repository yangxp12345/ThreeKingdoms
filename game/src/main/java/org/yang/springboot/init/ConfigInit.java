package org.yang.springboot.init;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.yang.springboot.Config;
import org.yang.springboot.util.DirUtil;
import org.yang.springboot.util.IOUtil;

import java.io.File;

public class ConfigInit {


    public static void init() throws Exception {
        String configFilePath = DirUtil.getProjectFilePath("config.json", LogInit.class, true);
        File configFile = new File(configFilePath);
        String config = configFile.exists() ? IOUtil.readInputStream(configFile) : "{}";
        assignment(config);
    }

    /**
     * 解析配置参数
     *
     * @param config 配制文件内容
     */
    private static void assignment(String config) {
        JSONObject configJson = JSON.parseObject(config);
        Config.port = (int) configJson.getOrDefault("port", 80);
    }

    public static void main(String[] args) throws Exception {

        int index = 12;
        String[][][] arrayTwo = new String[100][100][100];
        for (int i = 0; i < arrayTwo[0][0].length; i++) {
            index += i;
            for (int j = 0; j < arrayTwo[0].length; j++) {
                for (int k = 0; k < arrayTwo.length; k++) {
                    //递归计算上层数据的索引对象
                    arrayTwo[k][j][i] = index + "";
                    System.out.println(arrayTwo[k][j][i]);
                    index += k;
                }
            }
        }
    }
}