package simpleframe.exception;

/**
 * 找不到 处理器、资源 异常
 * @blame MQPearth
 */
public class NoHandlerException extends Exception {

    public NoHandlerException() {
        super();
    }

    public NoHandlerException(String message) {
        super(message);
    }
}
