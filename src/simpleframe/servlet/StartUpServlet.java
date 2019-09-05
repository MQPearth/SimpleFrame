package simpleframe.servlet;

import simpleframe.annotation.bean.Autowired;
import simpleframe.annotation.bean.Bean;
import simpleframe.annotation.bean.Component;
import simpleframe.annotation.mvc.*;
import simpleframe.data.GetRequestData;
import simpleframe.data.HandlerRequestData;
import simpleframe.exception.ConversationException;
import simpleframe.exception.NoHandlerException;
import simpleframe.model.ExceptionHandlerModel;
import simpleframe.model.RequestModel;
import simpleframe.model.ServletMethodInvoke;
import simpleframe.model.ServletModel;
import simpleframe.proxy.AbstractHttpServletInvocation;

import simpleframe.proxy.HttpServletRequestInvocation;
import simpleframe.proxy.HttpServletResponseInvocation;
import simpleframe.util.JsonUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * 用于扫描类 配置类 分发请求 异常处理
 *
 * @blame MQPearth
 */
public class StartUpServlet implements Servlet {

    /**
     * 控制层方法 映射容器
     */
    private static final HashMap<String, RequestModel> CONTROLLER_MAP = new HashMap<>(20);


    /**
     * 加入容器管理的bean
     */
    private static final HashMap<String, Object> BEAN_MAP = new HashMap<>(20);


    /**
     * 处理自定义异常的类
     */
    private static final HashMap<Class, ExceptionHandlerModel> EXCEPTION_HANDLER_MAP = new HashMap<>(20);

    /**
     * 处理请求携带数据
     */
    private static final HashMap<String, HandlerRequestData> HANDLER_REQUEST_DATA_MAP = new HashMap<>(5);


    static {

        // 提前初始化 request response
        Class requestClass = HttpServletRequestWrapper.class;
        Class responseClass = HttpServletResponseWrapper.class;


        HttpServletRequestInvocation requestInvocation = new HttpServletRequestInvocation();
        HttpServletRequest requestInstance = (HttpServletRequest)Proxy.newProxyInstance(requestClass.getClassLoader(), requestClass.getInterfaces(), requestInvocation);

        // jdk 动态代理
        HttpServletResponseInvocation responseInvocation = new HttpServletResponseInvocation();
        HttpServletResponse responseInstance = (HttpServletResponse)Proxy.newProxyInstance(responseClass.getClassLoader(), responseClass.getInterfaces(), responseInvocation);

        BEAN_MAP.put(HttpServletRequest.class.getName(), requestInstance);
        BEAN_MAP.put(HttpServletResponse.class.getName(), responseInstance);


        HANDLER_REQUEST_DATA_MAP.put("GET", new GetRequestData());
        HANDLER_REQUEST_DATA_MAP.put("POST", new GetRequestData());
        HANDLER_REQUEST_DATA_MAP.put("DELETE", new GetRequestData());
        HANDLER_REQUEST_DATA_MAP.put("PUT", new GetRequestData());
        HANDLER_REQUEST_DATA_MAP.put("PATCH", new GetRequestData());

    }

    /**
     * classpath 路径
     */
    private String classpath = null;


    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        //获取类文件 集合
        LinkedList<File> classFiles = scanPackage();
        try {
            analysisClass(classFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据注解分析类
     *
     * @param classFiles
     */
    private void analysisClass(LinkedList<File> classFiles) throws Exception {

        initBean(classFiles);

        initController();

    }


    /**
     * 初始化bean容器
     * 扫描controller层
     * 扫描exceptionHandler
     *
     * @param classFiles
     * @throws Exception
     */
    private void initBean(LinkedList<File> classFiles) throws Exception {

        ClassLoader classLoader = this.getClass().getClassLoader();


        for (File classFile : classFiles) {
            // 加载 类文件
            Class<?> clazz = classLoader.loadClass(StartUpServlet.getClasspath(classFile, this.classpath));
            // 获取类上的注解
            Annotation[] classAnnotations = clazz.getAnnotations();
            for (Annotation annotation : classAnnotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                //判断该类是否需要加入容器
                if (Component.class.equals(annotationType) ||
                        RestController.class.equals(annotationType)) {
                    Object instance = clazz.newInstance();
                    BEAN_MAP.put(clazz.getName(), instance);
                }
            }
            // 获取类的公共方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {

                Annotation[] methodAnnotations = method.getAnnotations();
                for (Annotation annotation : methodAnnotations) {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    if (Bean.class.equals(annotationType)) {
                        //实例化
                        Object instance = clazz.newInstance();
                        //调用方法
                        Object bean = method.invoke(instance);
                        BEAN_MAP.put(method.getReturnType().getName(), bean);
                    }

                    if (ExceptionHandler.class.equals(annotationType)) {
                        ExceptionHandler exceptionHandler = (ExceptionHandler)annotation;
                        // 加入容器管理
                        Object instance = clazz.newInstance();
                        BEAN_MAP.put(clazz.getName(), instance);

                        ExceptionHandlerModel model = new ExceptionHandlerModel(instance, method);

                        EXCEPTION_HANDLER_MAP.put(exceptionHandler.value(), model);
                    }
                }
            }

        }

        autowiredInstance(classLoader);

    }

    /**
     * 为类属性注入实例
     *
     * @param classLoader 当前类的类加载器
     */
    private void autowiredInstance(ClassLoader classLoader) {

        BEAN_MAP.forEach((className, instanceClass) -> {
            try {
                //加载类
                Class<?> clazz = classLoader.loadClass(className);
                //获取所有字段
                Field[] declaredFields = clazz.getDeclaredFields();

                for (Field field : declaredFields) {
                    //访问私有属性
                    field.setAccessible(true);
                    Annotation[] fieldAnnotations = field.getAnnotations();
                    for (Annotation annotation : fieldAnnotations) {
                        //此字段需要自动注入
                        if (Autowired.class.equals(annotation.annotationType())) {
                            Type type = field.getGenericType();
                            //从bean容器中获取实例
                            Object instanceBean = BEAN_MAP.get(type.getTypeName());
                            if (null == instanceBean) {

                                System.err.println("找不到名为" + type.getTypeName() + "的Bean");
                            }
                            //注入
                            field.set(instanceClass, instanceBean);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }


    /**
     * 初始化控制层路径映射
     */
    private void initController() throws Exception {
        BEAN_MAP.forEach((className, instanceBean) -> {

            Class<?> clazz = instanceBean.getClass();
            RestController restController = clazz.getAnnotation(RestController.class);

            //控制层
            if (null != restController) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                //前置映射路径
                String prefixPath = requestMapping.value();
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    String methodPath = "";
                    String requestMethod = "";
                    boolean hasMapping = false;
                    Annotation[] methodAnnotations = method.getAnnotations();
                    for (Annotation annotation : methodAnnotations) {
                        try {
                            ClassLoader classLoader = StartUpServlet.class.getClassLoader();
                            Class<? extends Annotation> annotationClass =
                                    (Class<? extends Annotation>)classLoader.loadClass(annotation.annotationType().getName());
                            RequestMethod requestMethodAnnotation = annotationClass.getAnnotation(RequestMethod.class);
                            if (null == requestMethodAnnotation) {
                                continue;
                            }
                            Method valueMethod = annotationClass.getMethod("value");

                            //执行注解的value方法
                            Object valueReturn = valueMethod.invoke(annotation);

                            if (null != valueReturn) {
                                // 有value方法 且 注解上有RequestMethod注解
                                hasMapping = true;
                                methodPath = valueReturn.toString();
                                requestMethod = requestMethodAnnotation.value();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (hasMapping) {
                        String completePath = requestMethod + RequestModel.MIDDLE_CHAR + prefixPath + methodPath;
                        RequestModel requestModel = new RequestModel(method, completePath, instanceBean);
                        CONTROLLER_MAP.put(completePath, requestModel);
                    }

                }
            }
        });
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }


    /**
     * 分发请求
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {


        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;


        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        // completePath   例：GET#/user
        String completePath = requestMethod + RequestModel.MIDDLE_CHAR + requestURI;

        execute(request, response, completePath);

    }

    /**
     * 执行controller方法
     */
    private void execute(HttpServletRequest request, HttpServletResponse response, String completePath) {
        ServletMethodInvoke servletMethod = new ServletMethodInvoke(request, response);

        try {
            RequestModel requestModel = CONTROLLER_MAP.get(completePath);
            if (null == requestModel) {
                throw new NoHandlerException("找不到处理器~");
            }

            Method controllerMethod = requestModel.getMethod();

            servletMethod.setInstance(requestModel.getInstance());
            servletMethod.setMethod(controllerMethod);

            doInvoke(servletMethod);

        } catch (Exception e) {
            Throwable realException = e.getCause();
            Class<? extends Throwable> realExceptionClass = null;
            if (null == realException) {
                realExceptionClass = e.getClass();
                realException = e;
            } else {
                realExceptionClass = e.getCause().getClass();
            }

            ExceptionHandlerModel model = EXCEPTION_HANDLER_MAP.get(realExceptionClass);

            if (null != model) {
                // 异常处理器map中有此异常处理的方法
                try {
                    servletMethod.setMethod(model.getMethod());
                    servletMethod.setInstance(model.getInstance());

                    doInvoke(servletMethod, realException);
                } catch (Exception ex) {
                    //返回500
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    ex.printStackTrace();
                }
            } else {
                //返回500
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }

        } finally {
            AbstractHttpServletInvocation.remove();
        }
    }

    /**
     * 调用方法
     *
     * @param args 目标方法的参数
     * @throws Exception 可能抛出的异常
     */
    private void doInvoke(ServletMethodInvoke servletMethod, Object... args) throws Exception {

        HttpServletRequest request = servletMethod.getRequest();
        HttpServletResponse response = servletMethod.getResponse();

        ServletModel servletModel = new ServletModel(request, response);
        // 为ThreadLocal 注入 request response
        AbstractHttpServletInvocation.setThreadLocal(servletModel);

        Method method = servletMethod.getMethod();
        Object instance = servletMethod.getInstance();


        // next version
//        Map<String, Object> parameter = getData(method, request);
//
//        parameter.forEach((key, values) -> {
//
//            System.out.println(key + "--" + values);
//        });
//        method = settingMethodParameter(parameter);

        Object valueReturn = method.invoke(instance, args);
        if (null != valueReturn) {
            writerResponse(response, valueReturn);
        }


    }

    /**
     * 自动注入方法上的参数
     *
     * @param parameter 参数map
     * @return
     */
    private Method settingMethodParameter(Map<String, Object> parameter) {


        return null;
    }

    /**
     * 提取请求携带的数据
     *
     * @param request
     * @return
     */
    private Map<String, Object> getData(Method method, HttpServletRequest request) {

        return HANDLER_REQUEST_DATA_MAP.get(request.getMethod()).handlerData(method, request);
    }

    /**
     * 将方法返回值转为 json 写入 响应体
     *
     * @param response 响应对象
     * @param value    方法返回值
     */
    private void writerResponse(HttpServletResponse response, Object value) throws IOException, ConversationException {
        response.setContentType("application/json;charset=UTF-8");
        String json = JsonUtils.toJSON(value);
        response.getWriter().println(json);
    }


    @Override
    public String getServletInfo() {
        return this.getServletConfig().getServletName();
    }

    @Override
    public void destroy() {
        //
    }


    /**
     * 扫描classpath路径下所有的class文件
     */
    private LinkedList<File> scanPackage() {
        //类加载器
        String classpath = StartUpServlet.class.getResource("/").getPath();
        this.classpath = classpath;
        File file = new File(classpath);
        //包 集合
        ConcurrentLinkedQueue<File> packageList = new ConcurrentLinkedQueue<>(Arrays.asList(file.listFiles()));
        //类 集合
        LinkedList<File> classList = new LinkedList<>();

        Iterator<File> iterator = packageList.iterator();

        while (packageList.size() > 0) {
            if (!iterator.hasNext()) {
                break;
            }
            File fileTemp = iterator.next();
            iterator.remove();
            //是 文件夹
            if (fileTemp.isDirectory()) {
                List<File> fileList = Arrays.asList(fileTemp.listFiles());
                packageList.addAll(fileList);
            } else {
                if (fileTemp.getName().endsWith(".class")) {
                    classList.add(fileTemp);
                }
            }
            iterator = packageList.iterator();


        }


        return classList;
    }

    /**
     * 从容器中取出bean
     *
     * @param key
     * @return
     */
    public static Object getBean(String key) {

        return BEAN_MAP.get(key);
    }


    /**
     * 获取classpath式的路径格式
     *
     * @param file
     * @param rootPath
     * @return
     */
    private static String getClasspath(File file, String rootPath) {
        File rootFolder = new File(rootPath);
        String absolutePath = file.getAbsolutePath();
        rootPath = rootFolder.getAbsolutePath();
        String path = absolutePath.replace(rootPath, "");
        String substring = path.substring(1);
        String replaceAll = substring.replaceAll("\\\\", ".").replaceAll(".class", "");

        return replaceAll;
    }
}
