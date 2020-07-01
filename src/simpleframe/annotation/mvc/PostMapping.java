package simpleframe.annotation.mvc;


import java.lang.annotation.*;

/**
 * 匹配一个post请求
 *
 * @author MQPearth
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMethod(RequestMethod.POST)
public @interface PostMapping {
    /**
     * 具体的映射路径
     */
    String value() default "";
}
