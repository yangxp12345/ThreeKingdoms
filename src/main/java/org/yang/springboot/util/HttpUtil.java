package org.yang.springboot.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.Closeable;
import java.io.InputStream;
import java.util.Map;

/**
 * http发送工具类
 */
@Slf4j
public class HttpUtil {
    /**
     * 发送post请求
     *
     * @param url      请求渎职
     * @param headJson 请求头
     * @param bodyJson 请求体
     * @return 响应信息
     * @throws Exception 异常信息
     */
    public static String post(String url, JSONObject headJson, JSONObject bodyJson) throws Exception {
        CloseableHttpClient client = null;
        RequestConfig requestConfig = RequestConfig.custom().build();
        String response;
        InputStream content = null;
        try {
            client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);//支持get/post与put和delete - > HttpDelete
            post.setConfig(requestConfig);
            if (headJson != null) {//设置请求头
                for (Map.Entry<String, Object> head : headJson.entrySet()) {
                    post.addHeader(head.getKey(), head.getValue().toString());
                }
            }
            if (bodyJson == null) bodyJson = new JSONObject();
            StringEntity body = new StringEntity(bodyJson.toJSONString(), "utf-8");//设置请求体
            body.setContentEncoding("UTF-8");
            body.setContentType("application/json");//设置发送的请求体数据为json数据
            post.setEntity(body);
            HttpResponse res = client.execute(post);
            content = res.getEntity().getContent();
            StatusLine statusLine = res.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                String errorMsg = IOUtil.readInputStream(content);
                log.error("请求状态异常,状态码: {}, 错误信息: {}", statusCode, errorMsg);
                return null;
            } else {
                response = IOUtil.readInputStream(content);
            }
        } finally {
            close(client);
            close(content);
        }
        return response;
    }

    /**
     * 发送gei请求
     *
     * @param url 请求地址
     * @return 响应信息
     * @throws Exception 异常信息
     */
    public static String get(String url) throws Exception {
        return get(url, null);
    }

    /**
     * get请求
     *
     * @param url      请求地址
     * @param headJson 请求头信息
     * @return 响应数据
     * @throws Exception 异常信息
     */
    public static String get(String url, JSONObject headJson) throws Exception {
        return get(url, headJson, null);
    }

    /**
     * http的get请求
     *
     * @param url      请求地址
     * @param headJson 请求头信息
     * @param bodyJson 请求体信息,get请求的请求体会放在url后面
     * @return 请求结果
     * @throws Exception 异常提示
     */
    public static String get(String url, JSONObject headJson, JSONObject bodyJson) throws Exception {
        CloseableHttpClient client = null;
        String response;
        RequestConfig requestConfig = RequestConfig.custom().build();
        InputStream content = null;
        try {
            client = HttpClientBuilder.create().build();
            if (bodyJson != null) {
                StringBuilder sbr = new StringBuilder("?");
                for (Map.Entry<String, Object> entry : bodyJson.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    sbr.append(key).append("=").append(value).append("&");
                }
                if (!bodyJson.isEmpty()) {
                    sbr.setLength(sbr.length() - 1);
                    url = url + sbr;
                }
            }
            HttpGet get = new HttpGet(url);
            get.setConfig(requestConfig);
            if (headJson != null) {//设置请求头
                for (Map.Entry<String, Object> entry : headJson.entrySet()) {
                    get.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            HttpResponse res = client.execute(get);
            content = res.getEntity().getContent();
            StatusLine statusLine = res.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println(statusCode);
                String errorMsg = IOUtil.readInputStream(content);
                log.error("请求状态异常,状态码: {}, 错误信息: {}", statusCode, errorMsg);
                return null;
            }
            response = IOUtil.readInputStream(content);
        } finally {
            close(client);
            close(content);
        }
        return response;
    }


    private static void close(Closeable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (Exception e) {
                log.error("关闭{}对象失败", obj.getClass().getSimpleName(), e);
            }
        }

    }

    public static void main(String[] args) throws Exception {
        JSONObject headJson = new JSONObject();
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("k1", "v1");
        bodyJson.put("k2", "v2");
        bodyJson.put("k3", "v3");
        String post = post("http://127.0.0.1/test/param", headJson, bodyJson);
        System.out.println(post);
//        System.out.println("退出");

    }
}
