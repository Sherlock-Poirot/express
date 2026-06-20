package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.CustomerStatisticsDTO;
import com.express.yto.dto.CustomerStatisticsSummaryDTO;
import com.express.yto.dto.RegionDistributionDTO;
import com.express.yto.model.DailyBill;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DailyBillService extends IService<DailyBill> {
    String uploadBill(MultipartFile file);
    
    String syncCustomerInfoFromShopEmp(LocalDate date);
    
    String calculateBill(LocalDate date);
    
    List<CustomerStatisticsDTO> getCustomerStatistics(LocalDate date);
    
    CustomerStatisticsSummaryDTO getCustomerStatisticsSummary(LocalDate date);

    List<RegionDistributionDTO> getRegionDistributionByDate(LocalDate date);

    BigDecimal sumTotalAmountByDate(LocalDate date);

    Long countByDate(LocalDate date);
}