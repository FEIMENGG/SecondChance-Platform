package com.group3.handle;

import com.group3.error.BaseException;
import com.group3.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * @author group3
 * @date 2024/04
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Global unified exception return information
     */
    public static final String DEFAULT_ERROR_MESSAGE = "System maintenance, please visit later";

    /**
     * 500 exceptions will be caught by this method
     *
     * @param req request
     * @param e   exception
     * @return output
     * @throws Exception unknown exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public BaseResponse handleAllError(HttpServletRequest req, Exception e) {
        log.error("System internal exception", e);
        return BaseResponse.fail();
    }

    /**
     * 500 transaction exceptions are caught by this method
     *
     * @param req request
     * @param e   exception
     * @return output
     * @throws Exception unknown exception
     */
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public BaseResponse handleBizError(HttpServletRequest req, BaseException e) {
        log.error("transaction exception", e);
        return BaseResponse.fail(e.getMsg());
    }
}
