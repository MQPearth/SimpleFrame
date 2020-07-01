package simpleframe.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 请求方法调用 封装对象
 *
 * @author MQPearth
 */
public class ServletMethodInvoke {
    /**
     * 响应
     */
    private HttpServletResponse response;
    /**
     * 请求
     */
    private HttpServletRequest request;
    /**
     * 目标方法
     */
    private Method method;

    /**
     * 调用方法的实例对象
     */
    private Object instance;

    public ServletMethodInvoke(HttpServletResponse response, HttpServletRequest request, Method method, Object instance) {
        this.response = response;
        this.request = request;
        this.method = method;
        this.instance = instance;
    }

    public ServletMethodInvoke(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }
}
