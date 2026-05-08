package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.model.SysTask;
import com.express.yto.service.WaybillDetailService;
import io.swagger.annotations.Api;
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
    public RestResult<String> importWaybill(@RequestParam("file") MultipartFile file) {
        return RestResult.ok(waybillDetailService.importWaybill(file));
    }

    @GetMapping("/import/task")
    public RestResult<SysTask> getImportTask(@RequestParam("taskNo") String taskNo) {
        return RestResult.ok(waybillDetailService.getImportTask(taskNo));
    }
}
