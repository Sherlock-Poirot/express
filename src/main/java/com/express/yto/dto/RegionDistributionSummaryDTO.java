package com.express.yto.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
@Data
public class RegionDistributionSummaryDTO {
    
    private String date;
    
    private Long totalBills;
    
    private BigDecimal totalAmount; 
    
    private List<RegionDistributionDTO> regionList;
}
