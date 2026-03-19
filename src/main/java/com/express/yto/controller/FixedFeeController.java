package com.express.yto.controller;

import com.express.yto.dto.ExportCustomerFeeInput;
import com.express.yto.dto.FixedFeeInsertInput;
import com.express.yto.dto.RestResult;
import com.express.yto.service.FixedFeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Detective
 * @date Created in 2025/9/20
 */
@RestController
@RequestMapping("/fixed")
@Api("固定重量区间")
public class FixedFeeController {

    @Autowired
    private FixedFeeService fixedFeeService;

    @PostMapping("/insertByCustomer")
    @ApiOperation("新增数据(客户版)")
    public RestResult<String> insertByCustomer(@RequestBody List<FixedFeeInsertInput> list) {
        fixedFeeService.insertByCustomer(list);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/exportBatch")
        @ApiOperation("导出固定重量区间价格表")
    public RestResult<String> exportBatch(@RequestParam List<String> kCodeList){
        fixedFeeService.exportByKCode(kCodeList);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/exportSingle")
    @ApiOperation("导出固定重量区间价格表")
    public RestResult<String> exportSingle(@RequestBody ExportCustomerFeeInput input){
        fixedFeeService.exportSingle(input);
        return RestResult.ok("操作成功");
    }

}
