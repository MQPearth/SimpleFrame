package simpleframe.annotation.mvc;


import java.lang.annotation.*;

/**
 * 处理一个get请求
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMethod(RequestMethod.POST)
public @interface PostMapping {
    /**
     * 具体的映射路径
     * @return
     */
    String value() default "";
}
