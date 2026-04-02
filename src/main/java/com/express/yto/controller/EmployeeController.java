package com.express.yto.controller;

import com.express.yto.dto.DealDataInput;
import com.express.yto.dto.RestResult;
import com.express.yto.service.DealDataService;
import com.express.yto.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Detective
 * @date Created in 2025/9/25
 */
@RestController
@RequestMapping("/employee")
@Api(tags = "承包区账单处理类")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PostMapping("/bill")
    @ApiOperation("处理承包区账单")
    public RestResult<String> employeeBill(@RequestBody DealDataInput input) {
        employeeService.dealEmployeeBill(input);
        return RestResult.ok("操作成功");
    }
}
