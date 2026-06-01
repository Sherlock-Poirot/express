package com.express.yto.annotation;

import java.lang.annotation.*;

/**
 * 数据脱敏注解
 * 用于标记需要进行数据脱敏处理的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataMask {

    /**
     * 模块编码，用于标识不同的业务模块
     */
    String module() default "";
}