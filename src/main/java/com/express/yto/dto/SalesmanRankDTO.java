package com.express.yto.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesmanRankDTO {

    private String salesmanName;

    private Integer totalCount;

    private Integer delayCount;

    private BigDecimal delayRate;

    private BigDecimal avgDelayMinutes;
}