package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.service.DailyBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

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

    @PostMapping("/syncCustomerInfo")
    @ApiOperation("同步客户信息（从t_shop_emp更新t_daily_bill）")
    public RestResult<String> syncCustomerInfo(
            @RequestParam(value = "date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        String result = dailyBillService.syncCustomerInfoFromShopEmp(date);
        return RestResult.ok(result);
    }

    @PostMapping("/calculate")
    @ApiOperation("计算每日账单费用")
    public RestResult<String> calculateBill(
            @RequestParam(value = "date", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        String result = dailyBillService.calculateBill(date);
        return RestResult.ok(result);
    }

}