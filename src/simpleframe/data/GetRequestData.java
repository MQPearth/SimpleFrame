package simpleframe.data;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理get请求携带的数据
 *
 * @blame MQPearth
 */
public class GetRequestData implements HandlerRequestData {


    @Override
    public Map<String, Object> handlerData(Method method, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> {

            // value 只有
            if (value.length <= HandlerRequestData.MAX_PARAMETER_VALUE_COUNT) {
                // 存一个值
                map.put(key, value[0]);
            } else {
                // 存数组
                map.put(key, value);

            }
        });

        return map;
    }
}
