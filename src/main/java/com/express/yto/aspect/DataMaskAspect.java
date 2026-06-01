package com.express.yto.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.express.yto.annotation.DataMask;
import com.express.yto.dto.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 数据脱敏AOP切面
 * 根据用户角色对敏感数据进行脱敏处理
 */
@Slf4j
@Aspect
@Component
public class DataMaskAspect {

    /**
     * 拦截带@DataMask注解的方法，进行数据脱敏处理
     */
    @Around("@annotation(dataMask)")
    public Object around(ProceedingJoinPoint joinPoint, DataMask dataMask) throws Throwable {
        // 1. 执行原方法
        Object result = joinPoint.proceed();

        // 2. 获取当前用户角色
        List<String> roles = StpUtil.getRoleList();

        // 3. 判断是否需要脱敏（财务文员需要脱敏）
        boolean needMask = !roles.contains("ADMIN") && !roles.contains("FINANCE_MANAGER");

        if (needMask && result instanceof RestResult) {
            RestResult<?> restResult = (RestResult<?>) result;
            Object data = restResult.getData();

            if (data != null) {
                // 根据模块进行脱敏处理
                String module = dataMask.module();
                switch (module) {
                    case "report:profit":
                        maskProfitData(data);
                        break;
                    // 可以扩展其他模块的脱敏逻辑
                    default:
                        break;
                }
            }
        }

        return result;
    }

    /**
     * 量本利报表数据脱敏
     * 财务文员只能看到发货量，看不到成本和利润
     */
    private void maskProfitData(Object data) {
        try {
            Class<?> clazz = data.getClass();

            // 移除成本字段
            maskField(clazz, data, "cost");
            // 移除利润字段
            maskField(clazz, data, "profit");
            // 移除利润率字段
            maskField(clazz, data, "profitRate");
            maskField(clazz, data, "profit_rate");

        } catch (Exception e) {
            log.warn("数据脱敏失败", e);
        }
    }

    /**
     * 对指定字段进行脱敏（设置为null）
     */
    private void maskField(Class<?> clazz, Object data, String fieldName) throws Exception {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(data, null);
        } catch (NoSuchFieldException e) {
            // 字段不存在，忽略
        }
    }
}