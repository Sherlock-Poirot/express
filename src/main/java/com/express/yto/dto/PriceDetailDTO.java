package com.express.yto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2026/4/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceDetailDTO {

    private LocalDate startTime;

    private LocalDate endTime;

    private String area;

    private BigDecimal firstFee;

    private BigDecimal overFee;

    private List<FixedTinyDTO> fixedFee;
}
