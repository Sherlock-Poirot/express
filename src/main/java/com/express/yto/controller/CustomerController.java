package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.service.CustomerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Detective
 * @date Created in 2025/9/18
 */
@RestController
@RequestMapping("/customer")
@ApiOperation("客户功能")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @ApiOperation("通过excel文件导入")
    @PostMapping
    public RestResult<String> importByExcel(@RequestParam String filePath){
        customerService.importByExcel(filePath);
        return RestResult.ok("操作成功");
    }
}
