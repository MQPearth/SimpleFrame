package simpleframe.util;

import simpleframe.exception.ConversationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * json转换工具
 *
 * @author MQPearth
 */
public class JsonUtils {

    /**
     * 可直接调用toString的类
     */
    private static final HashSet<Class> CLASS_SET = new HashSet<>();

    /**
     * 引号
     * ` " `
     */
    private static final String QUOTATION_MARKS = "\"";


    /**
     * ascii字符表中小写字母的起始值
     */
    private static final int ASCII_LOWER_CASE_LETTER_START = 97;

    /**
     * ascii字符表中小写字母的起始值
     */
    private static final int ASCII_LOWER_CASE_LETTER_END = 122;


    /**
     * ascii字符表中大写字母与小写字母的差值
     */
    private static final int ASCII_LOWER_UPPER_LETTER_SUB = 32;

    /**
     * json 元素间隔符
     */
    private static final String JSON_INTERVAL = ",";

    /**
     * 空字段 null
     */
    private static final String NULL_OBJECT = "null";

    /**
     * bean get 方法 的前缀
     */
    private static final String BEAN_GET_METHOD_PREFIX = "get";

    /**
     * json 字符串 起始字符 {
     */
    private static final String JSON_START = "{";

    /**
     * json 字符串 结束字符 }
     */
    private static final String JSON_END = "}";


    static {

        Class[] classes = new Class[]{int.class, Integer.class, double.class, Double.class,
                Long.class, long.class, Short.class, short.class, Byte.class, byte.class,
                Boolean.class, boolean.class, float.class, Float.class,
                Character.class, char.class, String.class, Date.class};

        CLASS_SET.addAll(Arrays.asList(classes));
    }

    /**
     * 对象转为json
     *
     * @param obj
     * @return
     */
    public static String toJSON(Object obj) throws ConversationException {
        try {
            HashSet<Object> serializationObjectSet = new HashSet<>();
            return toJson(obj, serializationObjectSet);

        } catch (Exception e) {
            throw new ConversationException("json转换异常");
        }
    }


    /**
     * @param obj                    待转换的对象
     * @param serializationObjectSet 已出现过的对象
     * @return json
     */
    private static String toJson(Object obj, HashSet<Object> serializationObjectSet) throws ConversationException {
        // 当前对象已出现过
        if (serializationObjectSet.contains(obj)) {
            return QUOTATION_MARKS + NULL_OBJECT + QUOTATION_MARKS;
        }

        if (CLASS_SET.contains(obj.getClass())) {
            return classesToString(obj);
        }


        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        StringBuilder buffer = new StringBuilder();
        buffer.append(JSON_START);
        for (Field field : fields) {

            field.setAccessible(true);
            String fieldName = field.getName();

            String methodName = BEAN_GET_METHOD_PREFIX + firstLetterToUpperCase(fieldName);
            // 字段值
            String fieldValue = null;

            boolean isObject = false;
            try {
                Method method = clazz.getMethod(methodName);
                // get方法返回值
                Object valueReturn = method.invoke(obj);
                if (valueReturn != null) {
                    if (CLASS_SET.contains(valueReturn.getClass())) {
                        fieldValue = classesToString(valueReturn);
                    } else {
                        // 是一个不在 [ classSet ] 内的对象
                        serializationObjectSet.add(obj);

                        fieldValue = toJson(valueReturn, serializationObjectSet);
                        isObject = true;
                    }
                } else {
                    fieldValue = NULL_OBJECT;
                }
            } catch (Exception e) {

                // realException 为null 不是反射方法出的异常
                if (null == e.getCause() && NoSuchMethodException.class.equals(e.getClass())) {
                    continue;
                }
                throw new ConversationException("json转换异常");


            }
            // "fieldName":"fieldValue",
            String hasQuotationMark = isObject ? "" : QUOTATION_MARKS;

            buffer.append(QUOTATION_MARKS).append(fieldName).append(QUOTATION_MARKS).append(":")
                    .append(hasQuotationMark).append(fieldValue).append(hasQuotationMark).append(",");

        }
        // 如果 ` ，` 在此buffer的最后，则删除最后的 ` ，`
        if (buffer.lastIndexOf(JSON_INTERVAL) == buffer.length() - 1) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        buffer.append(JSON_END);

        return buffer.toString();
    }


    /**
     * 字符串的首字母转大写
     *
     * @param str 字符串
     * @return
     */
    private static String firstLetterToUpperCase(String str) {
        char[] strArr = str.toCharArray();
        if (strArr[0] >= ASCII_LOWER_CASE_LETTER_START && strArr[0] <= ASCII_LOWER_CASE_LETTER_END) {
            strArr[0] = (char)(strArr[0] - ASCII_LOWER_UPPER_LETTER_SUB);
        }

        return new String(strArr);
    }

    /**
     * 为已存在于set中的类提供特定的转字符串方法
     *
     * @param obj
     * @return
     */
    private static String classesToString(Object obj) {
        // Date 类转时间戳
        if (Date.class.equals(obj.getClass())) {
            return ((Date)obj).getTime() + "";
        }


        return obj.toString();

    }
}
