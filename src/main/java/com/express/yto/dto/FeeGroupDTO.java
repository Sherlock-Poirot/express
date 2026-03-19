package com.express.yto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/9/16
 */
@Data
public class FeeGroupDTO {

    /**
     * 结束时间
     */
    private LocalDate endTime;

    /**
     * 开始时间
     */
    private LocalDate startTime;

    /**
     * 区域
     */
    private Integer area;

    /**
     * 重量段
     */
    private BigDecimal weight;

    /**
     * 费用
     */
    private BigDecimal fee;
}
