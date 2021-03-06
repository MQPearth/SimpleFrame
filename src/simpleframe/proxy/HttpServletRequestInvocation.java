package simpleframe.proxy;

import simpleframe.annotation.bean.Component;
import simpleframe.model.RequestModel;
import simpleframe.model.ServletModel;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * HttpServletRequest 代理对象
 * @author MQPearth
 */
public class HttpServletRequestInvocation extends AbstractHttpServletInvocation {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 线程本地 变量
        HttpServletRequest request = AbstractHttpServletInvocation.getThreadLocal().get().getRequest();
        // 使用事先设置的对象去调用方法
        return method.invoke(request, args);

    }
}
