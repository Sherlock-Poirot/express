package com.express.yto.aspect;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Detective
 * @date Created in 2026/5/6
 */
@Slf4j
@Aspect
@Component
public class ControllerLogAspect {
    /**
     * 切点：拦截所有Controller下所有方法
     * 改成你自己的controller包路径
     */
    @Pointcut("execution(* com.express.*.controller..*.*(..))")
    public void controllerPointCut() {
    }

    /**
     * 环绕通知：统计耗时 + 打印日志
     */
    @Around("controllerPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 请求信息
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String ip = request.getRemoteAddr();
        // 方法名
        String className = point.getSignature().getDeclaringTypeName();
        String methodName = point.getSignature().getName();
        // 请求参数
        Object[] args = point.getArgs();

        // 开始时间
        long startTime = System.currentTimeMillis();

        Object result = null;
        try {
            // 执行目标方法
            result = point.proceed();
        } finally {
            // 耗时
            long costTime = System.currentTimeMillis() - startTime;

            // 打印日志
            log.info("==================== Controller接口请求日志 ====================");
            log.info("请求地址: {}", url);
            log.info("请求方式: {}", method);
            log.info("请求IP: {}", ip);
            log.info("请求类方法: {}.{}", className, methodName);
            log.info("请求参数: {}", Arrays.toString(args));
            log.info("接口耗时: {} ms", costTime);
            log.info("================================================================");
        }
        return result;
    }
}
