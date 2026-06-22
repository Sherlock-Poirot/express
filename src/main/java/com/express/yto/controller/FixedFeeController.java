package com.express.yto.controller;

import com.express.yto.dto.ExportCustomerFeeInput;
import com.express.yto.dto.FixedFeeInsertInput;
import com.express.yto.dto.RestResult;
import com.express.yto.service.FixedFeeService;


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
public class FixedFeeController {

    @Autowired
    private FixedFeeService fixedFeeService;

    @PostMapping("/insertByCustomer")
    public RestResult<String> insertByCustomer(@RequestBody List<FixedFeeInsertInput> list) {
        fixedFeeService.insertByCustomer(list);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/exportBatch")
        public RestResult<String> exportBatch(@RequestParam List<String> kCodeList){
        fixedFeeService.exportByKCode(kCodeList);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/exportSingle")
    public RestResult<String> exportSingle(@RequestBody ExportCustomerFeeInput input){
        fixedFeeService.exportSingle(input);
        return RestResult.ok("操作成功");
    }

}
