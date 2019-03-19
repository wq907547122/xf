package xifu.com.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.vo.ExceptionResult;

/**
 * 全局异常处理类
 * @auth wq on 2019/1/4 9:14
 **/
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 统一处理的XiFuException的异常
     * @param e
     * @return
     */
    @ExceptionHandler(XiFuException.class)
    public ResponseEntity<ExceptionResult> errorHandler(XiFuException e){
        ExceptionEnum exceptionEnum = e.getExceptionEnum();
        ExceptionResult er = new ExceptionResult(exceptionEnum.getCode(), exceptionEnum.getMsg(), System.currentTimeMillis());
        return ResponseEntity.status(er.getCode()).body(er);
    }
}
