package com.express.yto.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KpiCourierRankDTO {

    private String courierCode;

    private String courierName;

    private Integer totalCount;

    private Integer qualifiedCount;

    private BigDecimal qualifiedRate;

    private BigDecimal preSignCallRate;

    private BigDecimal avgEfficiencyHours;
}