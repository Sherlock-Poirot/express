package com.express.yto.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyBillSummaryDTO {

    private String empName;

    private String custName;

    private String code;

    private Integer receiveCount;

    private BigDecimal avgWeight;

    private BigDecimal receivableAmount;

    private String custType;

    private Integer type;
}