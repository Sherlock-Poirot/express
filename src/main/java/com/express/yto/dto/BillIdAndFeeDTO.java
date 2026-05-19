package com.express.yto.dto;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/5/8
 */
@Data
public class BillIdAndFeeDTO {

    private String billId;

    private BigDecimal fee;
}
