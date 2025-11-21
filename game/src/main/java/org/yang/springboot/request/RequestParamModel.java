package org.yang.springboot.request;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data
public class RequestParamModel {
    private String uri;//请求路径
    private String ip;//客户端ip
    private String methodType;//请求类型
    private Long timestamp = System.currentTimeMillis();//接受到请求的时间戳
    private Map<String, String> headers;//请求头
    private Map<String, String> body;//请求体



    public RequestParamModel() {
        headers = new HashMap<>();
        body = new HashMap<>();
    }

    /**
     * 添加请求头参数
     *
     * @param key   请求头key
     * @param value 请求头value
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * 添加请求体参数
     *
     * @param key   请求头key
     * @param value 请求头value
     */
    public void addBody(String key, String value) {
        body.put(key, value);
    }

    /**
     * 直接获取请求体或者请求头中的key,优先获取请求体中的值
     *
     * @param key 需要获取的key字段
     * @return 返回key对应的value
     */
    public Object getParam(String key) {
        if (body.containsKey(key)) return body.get(key);
        if (headers.containsKey(key)) return headers.get(key);
        return null;
    }
}
