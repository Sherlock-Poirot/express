package com.express.yto.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 客户运单扫描时间范围DTO（价格覆盖校验用）
 * @author Detective
 * @date Created in 2026/10/9
 */
@Data
public class CustomerScanRangeDTO {

    /** 客户编码（send_customer，已trim） */
    private String code;

    /** 客户名称（send_customer_name） */
    private String customerName;

    /** 当月最早扫描日期 */
    private LocalDate minScanTime;

    /** 当月最晚扫描日期 */
    private LocalDate maxScanTime;
}
