package com.express.yto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2024/6/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestResult<T> {

    private int code;

    private T data;

    private String message;

    public static <T> RestResult<T> ok(T data) {
        return new RestResult<>(200, data, "Success");
    }

    // Static method for error response
    public static <T> RestResult<T> fail(String message) {
        return new RestResult<>(500, null, message);
    }
}
