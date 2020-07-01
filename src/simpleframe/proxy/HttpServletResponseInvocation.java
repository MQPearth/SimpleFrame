package simpleframe.proxy;

import simpleframe.annotation.bean.Component;
import simpleframe.model.ServletModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * HttpServletResponse代理对象
 *
 * @author MQPearth
 */
public class HttpServletResponseInvocation extends AbstractHttpServletInvocation {


    public HttpServletResponseInvocation() {

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 线程本地 变量


        HttpServletResponse response = AbstractHttpServletInvocation.getThreadLocal().get().getResponse();


        return method.invoke(response, args);

    }
}
