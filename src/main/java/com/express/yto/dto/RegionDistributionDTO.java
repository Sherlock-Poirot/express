package com.express.yto.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
@ApiModel("地区发货分布DTO")
public class RegionDistributionDTO {
    
    @ApiModelProperty("省份/地区名称")
    private String province;
    
    @ApiModelProperty("该地区总单量")
    private Long totalCount;
    
    @ApiModelProperty("该地区总金额")
    private BigDecimal totalAmount;
    
    @ApiModelProperty("发货占比（百分比）")
    private BigDecimal percentage;
    
    @ApiModelProperty("排名（按单量从高到低）")
    private Long rank;
}
