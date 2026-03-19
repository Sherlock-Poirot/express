package com.express.yto.controller;

import com.express.yto.dto.DealDataInput;
import com.express.yto.dto.PreDealDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.service.DealDataService;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation("处理账单")
    public RestResult<String> deal(@RequestBody DealDataInput input) {
        log.info("开始时间，{}", LocalDateTime.now());
        dealDataService.doDeal(input.getReadPath(), input.getExportPath(),
                input.getSpringFestival(), input.getCompanyId());
        log.info("结束时间，{}", LocalDateTime.now());
        return RestResult.ok("操作成功");
    }

    @GetMapping("/preDeal")
    @ApiOperation("账单计算前置校验")
    public RestResult<PreDealDTO> preDeal(@RequestParam String path) {
        return dealDataService.preDeal(path);
    }

    @GetMapping("/count")
    @ApiOperation("校验账单拆分数量")
    public RestResult<String> count(@RequestParam String path) {
        dealDataService.count(path);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/fourRate")
    @ApiOperation("查看所有账单4区是否超比例")
    public RestResult<String> fourRate(@RequestBody DealDataInput input) {
        dealDataService.fourRate(input);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/compile")
    @ApiOperation("汇总记录")
    public RestResult<String> compile(@RequestBody DealDataInput input) {
        dealDataService.compile(input);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/backUp")
    @ApiOperation("备份数据")
    public RestResult<String> backUp() {
        dealDataService.backUp();
        return RestResult.ok("操作成功");
    }
}
