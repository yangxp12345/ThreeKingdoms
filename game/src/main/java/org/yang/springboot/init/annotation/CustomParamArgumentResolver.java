package org.yang.springboot.init.annotation;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.yang.springboot.request.RequestParamModel;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义参数解析器
 */
@Slf4j
public class CustomParamArgumentResolver implements HandlerMethodArgumentResolver {


    private final boolean isExcludeOrReserve = true;//请求头关键字段是排除或者保留 true->排除 | false->保留
    private final Set<String> keyHeaderNames = new HashSet<>(Arrays.asList(//请求头关键字段
            "sec-fetch-mode",
            "sec-fetch-site",
            "accept-language",
            "sec-fetch-user",
            "accept",
            "sec-ch-ua",
            "sec-ch-ua-mobile",
            "sec-ch-ua-platform",
            "host",
            "upgrade-insecure-requests",
            "connection",
            "cache-control",
            "accept-encoding",
            "user-agent",
            "sec-fetch-dest"
    ));


    /**
     * 判断参数是否使用 @CustomParam 注解
     *
     * @param parameter 参数对象
     * @return 是否使用 @CustomParam 注解
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
//        if (parameter.getParameterType() != RequestParamModel.class)
//            throw new RuntimeException(String.format("参数类型异常 [%2s]->[%s(%s %s)],[%s]需要改为[%s]", parameter.getContainingClass().getName(), parameter.getMethod().getName(), parameter.getParameterType().getSimpleName(), parameter.getParameterName(), parameter.getParameterType().getSimpleName(), RequestParamModel.class.getSimpleName()));
        return true;
    }

    /**
     * 解析参数的具体逻辑 方法的形参只允许有一个 才允许使用
     *
     * @param parameter     参数对象
     * @param mavContainer  模型和视图对象
     * @param webRequest    请求对象
     * @param binderFactory 数据绑定工厂对象
     * @return 解析后的参数对象
     * @throws Exception 异常
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        RequestParamModel requestParamModel = new RequestParamModel();//自定义请求解析封装参数
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String methodType = httpServletRequest.getMethod();
        String requestURI = httpServletRequest.getRequestURI();
        String ipAddress = getIp(httpServletRequest);
        requestParamModel.setUri(requestURI);
        requestParamModel.setIp(ipAddress);
        requestParamModel.setMethodType(methodType);
        //解析请求头参数
        Iterator<String> headerNamesIter = webRequest.getHeaderNames();
        while (headerNamesIter.hasNext()) {
            String headerKey = headerNamesIter.next();
            String headerValue = webRequest.getParameter(headerKey);
            requestParamModel.addHeader(headerKey, headerValue);
            if (isExcludeOrReserve) {//排除不需要的key
                if (!keyHeaderNames.contains(headerKey)) requestParamModel.addHeader(headerKey, headerValue);
            } else {//保留
                if (keyHeaderNames.contains(headerKey)) requestParamModel.addHeader(headerKey, headerValue);
            }
        }
        //解析url后面跟着的参数
        Iterator<String> bodyNamesIter = webRequest.getParameterNames();
        while (bodyNamesIter.hasNext()) {
            String uriKey = bodyNamesIter.next();
            String uriValue = webRequest.getParameter(uriKey);
            requestParamModel.addBody(uriKey, uriValue);
        }
        //解析请求体的参数
        String jsonStr = readInputStream(httpServletRequest);
        Map<String, String> body = JSONObject.parseObject(jsonStr).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, unit -> unit.getValue().toString()));
        for (Map.Entry<String, String> bodyEntry : body.entrySet()) {
            requestParamModel.addBody(bodyEntry.getKey(), bodyEntry.getValue());
        }
        return requestParamModel;//形参为自定义参数
    }

    /**
     * 获取客户端IP 代理ip也会被处理
     *
     * @param httpServletRequest 请求对象
     * @return 返回客户端ip
     */
    private String getIp(HttpServletRequest httpServletRequest) {
        String ipAddress = httpServletRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = httpServletRequest.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = httpServletRequest.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = httpServletRequest.getRemoteAddr();
        }
        return ipAddress;
    }

    /**
     * 读取请求体流字符串
     *
     * @param httpServletRequest 请求对象
     * @return 返回请求体中的字符串
     * @throws Exception 异常
     */
    private String readInputStream(HttpServletRequest httpServletRequest) throws Exception {
        InputStreamReader read = null;
        BufferedReader bufferedReader = null;
        ServletInputStream inputStream = null;
        try {
            inputStream = httpServletRequest.getInputStream();
            StringBuilder sbr = new StringBuilder();
            read = new InputStreamReader(inputStream, StandardCharsets.UTF_8);//考虑到编码格式
            bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {//读取一行
                sbr.append(lineTxt).append("\n");
            }
            if (sbr.length() != 0) sbr.setLength(sbr.length() - 1);
            return sbr.toString();
        } catch (Exception e) {
            throw new Exception("解析请求体参数异常", e);
        } finally {
            close(read);
            close(bufferedReader);
            close(inputStream);
        }

    }

    /**
     * 关闭连接
     *
     * @param obj 连接对象
     */
    private void close(Closeable obj) {
        try {
            if (obj == null) return;
            obj.close();
        } catch (IOException e) {
            log.error("关闭流{}异常", obj.getClass().getSimpleName(), e);
        }
    }
}