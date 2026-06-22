package com.express.yto.dto;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/9/20
 */
@Data
public class FixedFeeInsertInput {

    /**
     * 客户编码
     */
    private String code;

    /**
     * 区域集合
     */
    private List<Integer> areas;

    /**
     * 重量上限
     */
    private BigDecimal weight;

    /**
     * 费用
     */
    private BigDecimal fee;

    /**
     * 开始时�?
     */
    private LocalDate startTime;

    /**
     * 结束时间
     */
    private LocalDate endTime;

}
