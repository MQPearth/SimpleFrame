### 手写 Spring、SpringMVC 框架

#### 功能

##### 目前实现了以下注解

* @Autowired  从容器中自动注入
* @Bean 标记方法返回值加入容器管理
* @Component 标记此类加入容器管理
* @RestController 标记此类为Rest风格的控制器  结果转为json
* @RequestMapping 前置匹配路径
* @GetMapping 匹配一个Get请求
* @PutMapping 匹配一个Put请求
* @DeleteMapping 匹配一个Delete请求
* @PostMapping 匹配一个Post请求
* @PatchMapping 匹配一个Patch请求
* @ExceptionHandler 标记此方法处理一个异常
* @PathVariable 定义了但暂未实现



#### 使用

1.   Result.java

   ```java
    package com.test.model;

    public class Result {
        public static final int RESULT_OK = 200;

        private Integer code;
        private String message;

        private Result() {
        }
    
        public Integer getCode() {
            return code;
        }
    
        public void setCode(Integer code) {
            this.code = code;
        }
    
        public String getMessage() {
            return message;
        }
    
        public void setMessage(String message) {
            this.message = message;
        }
    
        public static Result resultFactory(int code, String message) {
            Result result = new Result();
            result.setCode(code);
            result.setMessage(message);
            return result;
        }

    }
   
   ```



2.   ApplicationConfig.java

   ```java
    package com.test.config;

    import simpleframe.annotation.bean.Bean;
    
    public class ApplicationConfig {
    
        @Bean
        public String getApplicationName() {
    
            return "Hello Simple Frame";
        }
    
    }
   
   ```

3. HelloController.java

   ```java
   package com.test.controller;
   
   
   import com.test.model.Result;
   import simpleframe.annotation.bean.Autowired;
   import simpleframe.annotation.mvc.GetMapping;
   import simpleframe.annotation.mvc.RequestMapping;
   import simpleframe.annotation.mvc.RestController;
   
   import java.util.Date;
   
   
   @RestController
   @RequestMapping("/simpleframe")
   public class HelloController {
   
       @Autowired
       private String applicationName;
   
       @GetMapping("/hello")
       public Result hello() {
           return Result.resultFactory(Result.RESULT_OK, applicationName);
       }
   }
   
   ```

   

4. 在web.xml 添加如下代码

    ```xml
    <servlet>
            <servlet-name>startupservlet</servlet-name>
            <servlet-class>simpleframe.servlet.StartUpServlet</servlet-class>
            <load-on-startup>0</load-on-startup>
        </servlet>

        <servlet-mapping>
            <servlet-name>startupservlet</servlet-name>
            <url-pattern>/*</url-pattern>
        </servlet-mapping>
    ```

5. 启动Server

6. 访问localhost:port/simple/hello 

   ```json
   {
       "code": "200",
       "message": "Hello Simple Frame"
   }
   ```

   
