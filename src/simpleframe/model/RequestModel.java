package simpleframe.model;

import java.lang.reflect.Method;

/**
 * 请求处理 封装模型
 * @blame MQPearth
 */
public class RequestModel {

    /**
     * 目标方法
     */
    private Method method;

    /**
     * 请求路径
     * GET#/user
     */
    private String requestUrl;

    /**
     * 调用方法的实例对象
     */
    private Object instance;


    public static final String MIDDLE_CHAR = "#";

    public RequestModel() {
    }

    public RequestModel(Method method, String requestUrl, Object instance) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.instance = instance;

    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

}
