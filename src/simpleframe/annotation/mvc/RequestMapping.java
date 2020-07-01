package simpleframe.annotation.mvc;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表明此类的所有方法以value为前缀
 *
 * @author MQPearth
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    /**
     * 前缀映射路径
     */
    String value();
}
