package com.express.yto.dto;

import java.time.LocalDate;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/4/26
 */
@Data
public class PriceDeleteInput {

    private String code;

    private LocalDate startTime;

    private LocalDate endTime;
}
