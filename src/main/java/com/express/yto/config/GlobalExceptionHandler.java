package com.express.yto.config;

import com.express.yto.dto.RestResult;
import com.express.yto.exception.BusinessException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
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
        return RestResult.fail(10000, e.getMessage());
    }

    /**
     * 未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RestResult<?> handleNotLoginException(NotLoginException e) {
        log.warn("未登录或Token无效：{}", e.getMessage());
        return RestResult.fail(401, "未登录或Token无效，请重新登录");
    }

    /**
     * 无权限异常
     */
    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public RestResult<?> handleNotPermissionException(NotPermissionException e) {
        log.warn("无权限访问：{}", e.getMessage());
        return RestResult.fail(403, "无权限访问");
    }

    /**
     * 角色不足异常
     */
    @ExceptionHandler(NotRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public RestResult<?> handleNotRoleException(NotRoleException e) {
        log.warn("角色不足：{}", e.getMessage());
        return RestResult.fail(403, "角色不足，无权限访问");
    }
}
