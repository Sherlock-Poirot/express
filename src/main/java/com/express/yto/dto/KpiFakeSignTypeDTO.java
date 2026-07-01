package com.express.yto.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KpiFakeSignTypeDTO {

    private String fakeSignType;

    private Integer count;

    private BigDecimal percentage;
}