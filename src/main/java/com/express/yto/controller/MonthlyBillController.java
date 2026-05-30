package com.express.yto.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.MonthlyBillSearchInput;
import com.express.yto.dto.RestResult;
import com.express.yto.model.MonthlyBill;
import com.express.yto.service.MonthlyBillService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monthlyBill")
@ApiOperation("月度账单功能")
public class MonthlyBillController {

    @Autowired
    private MonthlyBillService monthlyBillService;

    @ApiOperation("分页查询")
    @PostMapping("/search")
    public RestResult<IPage<MonthlyBill>> search(@RequestBody MonthlyBillSearchInput input) {
        return RestResult.ok(monthlyBillService.search(input));
    }

    @ApiOperation("编辑")
    @PostMapping("/update")
    public RestResult<String> update(@RequestBody MonthlyBill input) {
        monthlyBillService.updateBill(input);
        return RestResult.ok("操作成功");
    }

    @ApiOperation("详情")
    @GetMapping("/detail")
    public RestResult<MonthlyBill> getDetail(@RequestParam("id") Long id) {
        return RestResult.ok(monthlyBillService.getById(id));
    }
}