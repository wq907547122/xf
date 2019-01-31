package xifu.com.vo;

/**
 * 异常的返回包装类，与正常的异常的信息保存一致
 * @auth wq on 2019/1/4 9:17
 **/
public class ExceptionResult {
    private int code;
    private String message;
    private Long timestamp;

    public ExceptionResult() {
    }

    public ExceptionResult(int code, String message, Long timestamp) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
