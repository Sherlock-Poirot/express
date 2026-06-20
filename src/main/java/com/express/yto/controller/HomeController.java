package com.express.yto.controller;

import com.express.yto.dto.RegionDistributionSummaryDTO;
import com.express.yto.service.HomeService;
import com.express.yto.dto.RestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/home")
@Api(tags = "首页统计接口")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/region-distribution")
    @ApiOperation("根据日期查询各地区发货占比")
    public RestResult<RegionDistributionSummaryDTO> getRegionDistribution(
            @ApiParam(value = "日期，格式：yyyy-MM-dd", example = "2024-01-01")
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        RegionDistributionSummaryDTO result = homeService.getRegionDistribution(date);
        return RestResult.ok(result);
    }
}
