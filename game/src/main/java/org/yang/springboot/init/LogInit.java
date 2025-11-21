package org.yang.springboot.init;

import org.yang.springboot.util.DirUtil;

import java.io.File;

/**
 * 初始化日志
 */
public class LogInit {

    /**
     * 创建日志目录并设置存储位置
     */
    public static void init() {
        String logPath = DirUtil.getProjectFilePath("log", LogInit.class, false);
        File logDir = new File(logPath);
        if (!logDir.exists() || !logDir.isDirectory()) logDir.mkdirs();
        System.setProperty("log", logPath);//设置日志存储在当前项目的根目录
    }
}
