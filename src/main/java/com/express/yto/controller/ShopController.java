package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.service.ShopService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Detective
 * @date Created in 2025/10/14
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping("/batchInsert")
    @ApiOperation("批量插入店铺表（直营）")
    public RestResult<String> batchInsert(@RequestParam String readPath){
        shopService.batchInsert(readPath);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/export")
    @ApiOperation("导出")
    public RestResult<String> export(@RequestParam String fileName){
        shopService.export(fileName);
        return RestResult.ok("操作成功");
    }

    @GetMapping("/batchInsertEmp")
    @ApiOperation("批量插入店铺表（承包区）")
    public RestResult<String> batchInsertEmp(@RequestParam String readPath){
        shopService.batchInsertEmp(readPath);
        return RestResult.ok("操作成功");
    }
}
