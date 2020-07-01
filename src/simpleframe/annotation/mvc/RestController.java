package simpleframe.annotation.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 只有标明了Controller或RestController的类才会进行RequestMapping 映射
 * 表明此类为控制层
 * 表明此类或此方法以json返回数据
 *
 * @author MQPearth
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestController {

}
