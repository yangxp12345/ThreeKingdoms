package org.yang.springboot.init.file;


import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * 响应文件
 */
@Component
public class FileRequestHandler extends ResourceHttpRequestHandler {
    public final static String file_key = "my_file";

    @Override
    protected Resource getResource(HttpServletRequest request) {
        String filePath = (String) request.getAttribute(file_key);
        return new FileSystemResource(filePath);
    }
}