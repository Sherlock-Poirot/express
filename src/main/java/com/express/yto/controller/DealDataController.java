package com.express.yto.controller;

import com.express.yto.dto.DealDataInput;
import com.express.yto.dto.PreDealDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.service.DealDataService;


import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
@RestController
@RequestMapping("/dealData")
@Slf4j
public class DealDataController {

    @Autowired
    private DealDataService dealDataService;

    @PostMapping("/deal")
    public RestResult<String> deal(@RequestBody DealDataInput input) {
        log.info("开始时间，{}", LocalDateTime.now());
        dealDataService.doDeal(input.getReadPath(), input.getExportPath(),
                input.getSpringFestival(), input.getCompanyId(), input.getMonth());
        log.info("结束时间，{}", LocalDateTime.now());
        return RestResult.ok("操作成功");
    }

    @GetMapping("/preDeal")
    public RestResult<PreDealDTO> preDeal(@RequestParam String path) {
        return dealDataService.preDeal(path);
    }

    @GetMapping("/count")
    public RestResult<String> count(@RequestParam String path) {
        dealDataService.count(path);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/fourRate")
    public RestResult<String> fourRate(@RequestBody DealDataInput input) {
        dealDataService.fourRate(input);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/compile")
    public RestResult<String> compile(@RequestBody DealDataInput input) {
        dealDataService.compile(input);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/backUp")
    public RestResult<String> backUp() {
        dealDataService.backUp();
        return RestResult.ok("操作成功");
    }
}
