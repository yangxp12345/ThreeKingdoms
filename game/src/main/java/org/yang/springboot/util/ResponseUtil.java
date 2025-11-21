package org.yang.springboot.util;

import lombok.extern.slf4j.Slf4j;
import org.yang.springboot.init.file.FileRequestHandler;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ResponseUtil {


    /**
     * 响应数据
     *
     * @param response 响应对象
     * @param result   响应数据
     */
    public static void respMsg(HttpServletResponse response, String result) {
        try {
            response.setHeader("content-type", "application/json;charset=utf-8;");
            ServletOutputStream outputStream = response.getOutputStream();
            if (result == null) result = "";
            byte[] bytes = result.getBytes(StandardCharsets.UTF_8);
            outputStream.write(bytes);
        } catch (IOException e) {
            log.error("响应异常", e);
        }
    }
    /**
     * 响应文件
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param filePath 文件路径
     * @throws Exception 异常
     */
    public static void respFile(HttpServletRequest request, HttpServletResponse response, FileRequestHandler fileRequestHandler, String filePath) throws Exception {
        Path videoPath = Paths.get(filePath);
        if (!Files.exists(videoPath)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        }
        String contentType = Files.probeContentType(videoPath);
        if (contentType != null && !contentType.isEmpty()) {//设置文件的响应类型
            response.setContentType(contentType);
        }
        request.setAttribute(FileRequestHandler.file_key, filePath);//将视频的地址传递给自定义的资源处理器
        fileRequestHandler.handleRequest(request, response);
    }
}
