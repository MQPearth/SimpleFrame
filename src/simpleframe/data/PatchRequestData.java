package simpleframe.data;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * PatchRequestData
 *
 * @blame MQPearth
 */
public class PatchRequestData implements HandlerRequestData {

    @Override
    public Map<String, Object> handlerData(Method method, HttpServletRequest request) {
        return null;
    }
}
