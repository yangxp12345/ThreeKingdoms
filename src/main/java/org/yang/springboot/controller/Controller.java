package org.yang.springboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.yang.springboot.init.file.FileRequestHandler;
import org.yang.springboot.util.ResponseUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/test")
@Component
@CrossOrigin
public class Controller {
    @Autowired
    private FileRequestHandler fileRequestHandler;//视频分段响应对象,构造方法会自动赋值


    /**
     * 简单测试请求
     *
     * @return 测试结果
     */
    @RequestMapping("/test")
    public Object test() {//自定义注解
        return "ok";
    }

    /**
     * 自定义参数解析请求
     *
     * @return 测试结果
     */
    @RequestMapping("/param")
    public Object param(String k1, String k2) {//自定义注解
        System.out.println(k1 + ":" + k2);
        return "param";
    }


    /**
     * 推消息
     *
     * @param response 封装参数
     */
    @RequestMapping(value = "/msg")
    public void msg(HttpServletResponse response) {
        ResponseUtil.respMsg(response, "1234");
    }

    /**
     * 推文件
     *
     * @param response 封装参数
     */
    @RequestMapping(value = "/file")
    public void file(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String filePath = "C:\\Users\\admin\\Desktop\\1\\1.mp4";
        ResponseUtil.respFile(request, response, fileRequestHandler, filePath);
    }
}