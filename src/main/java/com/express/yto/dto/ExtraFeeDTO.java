package com.express.yto.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2026/4/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraFeeDTO {

    private String areaName;

    private BigDecimal fee;
}
