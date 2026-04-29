package com.express.yto.dto;

import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/4/25
 */
@Data
public class ShopEmpInput {

    private Long id;

    private String code;

    private String custName;

    private String shopId;

    private String shopName;

    private String platform;

    private String empName;

    private String empType;
}
