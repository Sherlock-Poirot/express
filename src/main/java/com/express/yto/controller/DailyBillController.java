package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.service.DailyBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
}