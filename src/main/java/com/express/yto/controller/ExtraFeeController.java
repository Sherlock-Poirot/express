package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.service.ExtraFeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Detective
 * @date Created in 2025/9/20
 */
@RestController
@RequestMapping("/extra")
@Api("地区加收功能")
public class ExtraFeeController {

    @Autowired
    private ExtraFeeService extraFeeService;

    @PostMapping("/import")
    @ApiOperation("导入excel")
    public RestResult<String> importByExcel(@RequestParam String path){
        extraFeeService.importByExcel(path);
        return RestResult.ok("操作成功");
    }

}
