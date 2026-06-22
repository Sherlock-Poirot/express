package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.service.ExtraFeeService;


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
public class ExtraFeeController {

    @Autowired
    private ExtraFeeService extraFeeService;

    @PostMapping("/import")
    public RestResult<String> importByExcel(@RequestParam String path){
        extraFeeService.importByExcel(path);
        return RestResult.ok("操作成功");
    }

}
