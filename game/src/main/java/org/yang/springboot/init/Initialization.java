package org.yang.springboot.init;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统初始化
 */
@Slf4j
public class Initialization {
    public static void init() throws Exception {
        log.info("初始化系统日志");
        LogInit.init();
        log.info("加载配置文件");
        ConfigInit.init();

    }


}
