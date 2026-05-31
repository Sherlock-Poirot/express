package com.express.yto.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MonthlyBillSearchInput extends PageInput {

    @NotNull(message = "type不能为空")
    private Integer type; // 必传参数：0-直营客户，1-直营业务员，2-合同散件/淘宝限定/特批

    private String dimensionType; // "time" - 时间维度，"customer" - 客户维度

    private String billMonth; // 时间维度时使用：查询月份

    private String customerName; // 客户维度时使用：客户名称

    private String sortField; // 排序字段：custName, receiveCount, receivableAmount

    private String sortOrder; // 排序方式：asc（升序）, desc（降序）
}