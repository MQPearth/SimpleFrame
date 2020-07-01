package simpleframe.proxy;

import simpleframe.model.ServletModel;

import java.lang.reflect.InvocationHandler;

/**
 * 实现invocationHandler 子类需实现invoke方法进行代理
 *
 * @author MQPearth
 */
public abstract class AbstractHttpServletInvocation implements InvocationHandler {

    /**
     * 本地线程变量 request & response
     */
    private static final ThreadLocal<ServletModel> THREAD_LOCAL = new ThreadLocal<>();

    public static void setThreadLocal(ServletModel servletModel) {
        THREAD_LOCAL.set(servletModel);
    }

    public static ThreadLocal<ServletModel> getThreadLocal() {
        return THREAD_LOCAL;
    }


    public static void remove() {
        THREAD_LOCAL.remove();
    }

}
