package com.express.yto.service;

import com.express.yto.dto.RegionDistributionSummaryDTO;
import java.time.LocalDate;

public interface HomeService {
    RegionDistributionSummaryDTO getRegionDistribution(LocalDate date);
}
