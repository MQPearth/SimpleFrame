package simpleframe.annotation.mvc;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 匹配一个get请求
 *
 * @author MQPearth
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMethod(RequestMethod.GET)
public @interface GetMapping {
    /**
     * 具体的映射路径
     */
    String value() default "";
}
