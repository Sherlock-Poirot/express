package com.express.yto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2025/10/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverFeeMiniDTO {

    /**
     * 地区
     */
    private Integer area;

    /**
     * 续重费用
     */
    private BigDecimal fee;


    /**
     * 首重上限
     */
    private BigDecimal firstWeight;

    /**
     * 首重费用
     */
    private BigDecimal firstFee;

    private LocalDate startTime;

    private LocalDate endTime;
}
