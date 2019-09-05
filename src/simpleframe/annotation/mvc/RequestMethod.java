package simpleframe.annotation.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMethod {

    String value();


    String POST = "POST";
    String PUT = "PUT";
    String DELETE = "DELETE";
    String GET = "GET";
    String PATCH = "PATCH";

}
