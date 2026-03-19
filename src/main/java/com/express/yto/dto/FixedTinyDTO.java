package com.express.yto.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/10/30
 */
@Data
@Builder
public class FixedTinyDTO {

    private BigDecimal weight;

    private BigDecimal fee;
}
