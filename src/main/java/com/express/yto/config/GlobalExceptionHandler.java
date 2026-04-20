package com.express.yto.config;

import com.express.yto.dto.RestResult;
import com.express.yto.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Detective
 * @date Created in 2026/4/20
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常（你自己抛的）
     */
    @ExceptionHandler(BusinessException.class)
    public RestResult<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return RestResult.fail(e.getMessage());
    }
}
