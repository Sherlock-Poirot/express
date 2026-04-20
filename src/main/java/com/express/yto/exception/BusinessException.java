package com.express.yto.exception;

/**
 * @author Detective
 * @date Created in 2026/4/20
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
