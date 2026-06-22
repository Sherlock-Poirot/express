package com.express.yto.dto;

import lombok.Data;
import java.math.BigDecimal;
@Data
public class RegionDistributionDTO {
    
    private String province;
    
    private Long totalCount;
    
    private BigDecimal totalAmount;
    
    private BigDecimal percentage;
    
    private Long rank;
}
