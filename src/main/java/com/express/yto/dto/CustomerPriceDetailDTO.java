package com.express.yto.dto;

import java.math.BigDecimal;
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
public class CustomerPriceDetailDTO {

    private String startTime;

    private String endTime;

    private BigDecimal prepayment;

    private List<PriceDetailDTO> detail;

}
