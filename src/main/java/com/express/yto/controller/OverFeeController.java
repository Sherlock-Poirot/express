package com.express.yto.controller;

import com.express.yto.dto.PriceExcelInput;
import com.express.yto.dto.RestResult;
import com.express.yto.service.OverFeeService;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/over")
public class OverFeeController {

    @Autowired
    private OverFeeService overFeeService;

    @PostMapping("/import")
    @ApiOperation("根据excel导入")
    public RestResult<String> importByExcel(@RequestParam String path){
        overFeeService.importByExcel(path);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/export")
    @ApiOperation("导出")
    public RestResult<String> export(@RequestBody PriceExcelInput input){
        overFeeService.export(input);
        return RestResult.ok("操作成功");
    }
}
