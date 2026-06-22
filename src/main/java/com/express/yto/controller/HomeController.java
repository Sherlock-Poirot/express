package com.express.yto.controller;
import com.express.yto.dto.RegionDistributionSummaryDTO;
import com.express.yto.service.HomeService;
import com.express.yto.dto.RestResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.dev33.satoken.annotation.SaCheckPermission;
import java.time.LocalDate;
@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private HomeService homeService;
    @GetMapping("/region-distribution")
    public RestResult<RegionDistributionSummaryDTO> getRegionDistribution(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        RegionDistributionSummaryDTO result = homeService.getRegionDistribution(date);
        return RestResult.ok(result);
    }
}
