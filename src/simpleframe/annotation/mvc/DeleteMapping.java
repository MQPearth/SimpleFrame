package simpleframe.annotation.mvc;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 处理一个DELETE请求
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMethod(RequestMethod.DELETE)
public @interface DeleteMapping {
    /**
     * 具体的映射路径
     *
     * @return
     */
    String value() default "";
}
