package org.yang.springboot.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URLDecoder;

/**
 * 资源目录获取工具类
 */
@Slf4j
final public class DirUtil {


    /**
     * 获取资源的路径 获取的路径为根目录或者resource目录下的文件
     *
     * @param fileName 文件名称
     * @return 返回文件的绝对路径
     */
    public static String getProjectFilePath(String fileName) {
        return getProjectFilePath(fileName, false);
    }

    /**
     * @param fileName 文件名称
     * @param isSource 是否在resource目录下 jar启动不支持isSource获取
     * @return 返回文件的绝对路径
     */
    public static String getProjectFilePath(String fileName, boolean isSource) {
        return getProjectFilePath(fileName, DirUtil.class, isSource);

    }

    /**
     * @param fileName 目录名称
     * @param objClass 当前模块内部的任意类的class对象
     * @param isSource 是否在resource目录下
     * @return 返回目录的绝对路径
     */
    public static String getProjectFilePath(String fileName, Class<?> objClass, boolean isSource) {
        try {
            String tmpFile = URLDecoder.decode(objClass.getProtectionDomain().getCodeSource().getLocation().getPath(), "utf-8");
            boolean isJar = tmpFile.contains(".jar");//是否是jar启动
            if (tmpFile.startsWith("file:")) tmpFile = tmpFile.substring(5);
            tmpFile = tmpFile.split(".jar")[0];
            String home;
            if (isJar) {
                home = new File(tmpFile).getParent();
            } else {
                home = tmpFile.substring(0, tmpFile.length() - 16);
            }
            boolean isWin = System.getProperty("os.name").contains("dows");//是否是win
            if (isWin && home.startsWith("/")) home = home.substring(1);
            if (isSource && !isJar) {
                home = home + "/src/main/resources";
            }
            return home + "/" + fileName;
        } catch (Exception e) {
            log.error("获取文件路径异常", e);
            return "./" + fileName;
        }
    }
}