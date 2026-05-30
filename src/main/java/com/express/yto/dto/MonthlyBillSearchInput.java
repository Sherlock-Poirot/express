package com.express.yto.dto;

import lombok.Data;

@Data
public class MonthlyBillSearchInput extends PageInput {

    private String dimensionType; // "time" - 时间维度，"customer" - 客户维度

    private String billMonth; // 时间维度时使用：查询月份

    private String customerName; // 客户维度时使用：客户名称
}