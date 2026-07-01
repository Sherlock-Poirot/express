package com.express.yto.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KpiMonthlySummaryDTO {

    private String month;

    private Integer totalCount;

    private Integer complaintCount;

    private Integer fakeSignCount;

    private Integer otherCount;

    private BigDecimal preSignCallRate;

    private BigDecimal qualifiedRate;

    private BigDecimal avgEfficiencyHours;
}