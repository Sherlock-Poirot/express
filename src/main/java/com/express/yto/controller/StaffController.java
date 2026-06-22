package com.express.yto.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.RestResult;
import com.express.yto.dto.StaffInput;
import com.express.yto.model.ContractStaff;
import com.express.yto.service.ContractStaffService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Detective
 * @date Created in 2026/5/9
 */
@RestController
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private ContractStaffService contractStaffService;

    @GetMapping("/page")
    public RestResult<IPage<ContractStaff>> page(@RequestParam(required = false) String staffName,
            @RequestParam(required = false) String phone,
            @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {
        return RestResult.ok(contractStaffService.search(staffName, phone, pageNo, pageSize));
    }

    @PostMapping("/add")
    public RestResult<String> add(@RequestBody StaffInput input) {
        contractStaffService.add(input);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/update")
    public RestResult<String> update(@RequestBody StaffInput input) {
        contractStaffService.update(input);
        return RestResult.ok("操作成功");
    }
}
