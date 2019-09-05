package simpleframe.data;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * PostRequestData
 *
 * @blame MQPearth
 */
public class PostRequestData implements HandlerRequestData {
    @Override
    public Map<String, Object> handlerData(Method method, HttpServletRequest request) {
        return null;
    }
}
