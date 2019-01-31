package xifu.com.exception;



/**
 * 自定义异常
 * @auth wq on 2019/1/3 16:08
 **/

public class XiFuException extends RuntimeException{
    private ExceptionEnum exceptionEnum;

    public XiFuException(ExceptionEnum exceptionEnum){
        super();
        this.exceptionEnum = exceptionEnum;
    }

    public ExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }

    public void setExceptionEnum(ExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }
}
