package com.express.yto.controller;

import com.express.yto.dto.CustomerStatisticsDTO;
import com.express.yto.dto.CustomerStatisticsSummaryDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.service.DailyBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dailyBill")
@Api(tags = "每日账单管理-量本利计算")
public class DailyBillController {

    @Autowired
    private DailyBillService dailyBillService;

    @PostMapping("/upload")
    @ApiOperation("上传每日账单")
    public RestResult<String> uploadBill(@RequestParam("file") MultipartFile file) {
        String result = dailyBillService.uploadBill(file);
        return RestResult.ok(result);
    }

    @PostMapping("/syncAndCalculate")
    @ApiOperation("同步客户信息并计算每日账单费用（合并操作）")
    public RestResult<String> syncAndCalculate(
            @RequestParam(value = "date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        String syncResult = dailyBillService.syncCustomerInfoFromShopEmp(date);
        String calculateResult = dailyBillService.calculateBill(date);
        return RestResult.ok(syncResult + " | " + calculateResult);
    }

    @GetMapping("/statistics")
    @ApiOperation("查询客户统计数据")
    public RestResult<List<CustomerStatisticsDTO>> getCustomerStatistics(
            @RequestParam(value = "date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<CustomerStatisticsDTO> result = dailyBillService.getCustomerStatistics(date);
        return RestResult.ok(result);
    }

    @GetMapping("/statistics/summary")
    @ApiOperation("获取客户统计汇总信息")
    public RestResult<CustomerStatisticsSummaryDTO> getCustomerStatisticsSummary(
            @RequestParam(value = "date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        CustomerStatisticsSummaryDTO summary = dailyBillService.getCustomerStatisticsSummary(date);
        return RestResult.ok(summary);
    }

}