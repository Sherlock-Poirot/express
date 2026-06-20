package com.express.yto.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("地区发货分布汇总DTO")
public class RegionDistributionSummaryDTO {
    
    @ApiModelProperty("查询日期，格式：yyyy-MM-dd")
    private String date;
    
    @ApiModelProperty("当日总单量")
    private Long totalBills;
    
    @ApiModelProperty("当日总金额")
    private BigDecimal totalAmount; 
    
    @ApiModelProperty("各地区发货分布列表")
    private List<RegionDistributionDTO> regionList;
}
