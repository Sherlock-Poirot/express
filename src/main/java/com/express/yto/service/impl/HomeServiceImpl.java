package com.express.yto.service.impl;

import com.express.yto.dto.RegionDistributionDTO;
import com.express.yto.dto.RegionDistributionSummaryDTO;
import com.express.yto.service.DailyBillService;
import com.express.yto.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private DailyBillService dailyBillService;

    @Override
    public RegionDistributionSummaryDTO getRegionDistribution(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        List<RegionDistributionDTO> rawList = dailyBillService.getRegionDistributionByDate(date);
        Long totalBills = dailyBillService.countByDate(date);
        BigDecimal totalAmount = dailyBillService.sumTotalAmountByDate(date);

        if (totalAmount == null) totalAmount = BigDecimal.ZERO;

        List<RegionDistributionDTO> regionList = rawList.stream()
                .map(item -> {
                    BigDecimal percentage = BigDecimal.ZERO;
                    if (totalBills > 0) {
                        percentage = BigDecimal.valueOf(item.getTotalCount())
                                .multiply(BigDecimal.valueOf(100))
                                .divide(BigDecimal.valueOf(totalBills), 2, RoundingMode.HALF_UP);
                    }
                    item.setPercentage(percentage);
                    return item;
                })
                .sorted((a, b) -> b.getTotalCount().compareTo(a.getTotalCount()))
                .collect(Collectors.toList());

        for (int i = 0; i < regionList.size(); i++) {
            regionList.get(i).setRank((long) (i + 1));
        }

        RegionDistributionSummaryDTO result = new RegionDistributionSummaryDTO();
        result.setDate(date.toString());
        result.setTotalBills(totalBills);
        result.setTotalAmount(totalAmount);
        result.setRegionList(regionList);
        return result;
    }
}
