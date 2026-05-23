package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.model.SysTask;
import com.express.yto.service.WaybillDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Detective
 * @date Created in 2026/5/6
 */
@RestController
@RequestMapping("/waybill")
@Api(tags = "账单处理页面控制层")
public class WaybillController {

    @Autowired
    private WaybillDetailService waybillDetailService;

    @PostMapping("/import")
    @ApiOperation("导入账单")
    public RestResult<String> importWaybill(@RequestParam("file") MultipartFile file) {
        return RestResult.ok(waybillDetailService.importWaybill(file));
    }

    @PostMapping("/import/diff")
    @ApiOperation("导入差异")
    public RestResult<String> importWaybillDiff(@RequestParam("file") MultipartFile file) {
        return RestResult.ok(waybillDetailService.importWaybillDiff(file));
    }

    @GetMapping("/import/task")
    @ApiOperation("导入任务状态")
    public RestResult<SysTask> getImportTask(@RequestParam("taskNo") String taskNo) {
        return RestResult.ok(waybillDetailService.getImportTask(taskNo));
    }

    @PostMapping("/clean")
    @ApiOperation("清洗数据")
    public RestResult<String> cleanData(@RequestParam(value = "date",required = false) LocalDate date){
        // TODO 后期要改必须得是异步请求
        waybillDetailService.cleanData(date);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/calculate")
    @ApiOperation("计算")
    public RestResult<String> calculateBill(@RequestParam(value = "date",required = false) LocalDate date){
        // TODO 后期要改必须得是异步请求
        waybillDetailService.calculateBill(date);
        return RestResult.ok("操作成功");
    }


}
