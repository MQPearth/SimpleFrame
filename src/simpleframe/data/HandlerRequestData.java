package simpleframe.data;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @blame MQPearh
 */
public interface HandlerRequestData {


    /**
     * key -- value   value的最大数量1 超过 1 的存为数组
     */
    int MAX_PARAMETER_VALUE_COUNT = 1;

    Pattern PATTERN = Pattern.compile("[{][a-zA-Z\\$_][a-zA-Z\\d_]*[}]");

    /**
     * 处理请求数据
     *
     * @param request
     * @return
     */
    Map<String, Object> handlerData(Method method, HttpServletRequest request);

    /**
     * 获取路径上的参数
     * 例：  /user/1   中的1
     *
     * @param method
     * @param request
     * @return
     */
    default Map<String, Object> getUrlParameter(Method method, HttpServletRequest request) {

        String requestURI = request.getRequestURI();


        return null;
    }
}
