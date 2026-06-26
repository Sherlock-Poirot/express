package com.express.yto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorDTO {

    private String customerName;

    private String customerCode;

    private String errorType;
}
