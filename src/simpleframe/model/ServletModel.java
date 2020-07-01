package simpleframe.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * servlet 封装对象 用于threadLocal
 *
 * @author MQPearth
 */
public class ServletModel {

    private HttpServletRequest request;

    private HttpServletResponse response;

    public ServletModel() {
    }

    public ServletModel(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
