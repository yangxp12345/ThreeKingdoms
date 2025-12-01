package org.yang;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.springframework.boot.web.servlet.ServletComponentScan;
import org.yang.springboot.Config;
import org.yang.springboot.init.Initialization;

import java.util.Collections;


@Slf4j
@SpringBootApplication
@ServletComponentScan
public class Application {
    public static void main(String[] args) throws Exception {
        Initialization.init();//初始化配置
        SpringApplication app = new SpringApplication(Application.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", Config.port));
        app.run(args);
        log.info("启动完成");
    }
}