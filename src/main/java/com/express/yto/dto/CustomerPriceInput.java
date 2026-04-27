package com.express.yto.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/4/27
 */
@Data
public class CustomerPriceInput {

    /**
     * 客户编码
     */
    private String code;

    /**
     * 客户名称
     */
    private String name;

    /**
     * 开始时间
     */
    private LocalDate startTime;

    /**
     * 预付款
     */
    private BigDecimal prepayment;

    /**
     * 区域
     */
    private Integer area;

    /**
     * 首重价格
     */
    private BigDecimal firstFee;

    /**
     * 续重价格
     */
    private BigDecimal overFee;

    /**
     * 固定重量区间价格
     */
    List<FixedTinyDTO> fixedList;
}
