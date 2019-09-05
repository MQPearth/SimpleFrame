package simpleframe.model;

import java.lang.reflect.Method;

/**
 * 异常处理 封装对象
 * @blame MQPearth
 */
public class ExceptionHandlerModel {

    /**
     * 实例
     */
    private Object instance;

    /**
     * 对应的处理方法
     */
    private Method method;

    public ExceptionHandlerModel() {
    }

    public ExceptionHandlerModel(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
