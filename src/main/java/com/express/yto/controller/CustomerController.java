package com.express.yto.controller;

import com.express.yto.dto.CustomerInput;
import com.express.yto.dto.RestResult;
import com.express.yto.service.CustomerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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


    @ApiOperation("新增")
    @PostMapping
    public RestResult<String> add(@RequestBody CustomerInput input){
        customerService.add(input);
        return RestResult.ok("操作成功");
    }

    @ApiOperation("批量删除")
    @PostMapping
    public RestResult<String> add(@RequestParam List<Integer> ids){
        customerService.delete(ids);
        return RestResult.ok("操作成功");
    }
}
