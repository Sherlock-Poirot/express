package com.express.yto.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.KpiCourierRankDTO;
import com.express.yto.dto.KpiFakeSignTypeDTO;
import com.express.yto.dto.KpiMonthlySummaryDTO;
import com.express.yto.dto.KpiSignRecordQueryDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.model.KpiSignRecord;
import com.express.yto.service.KpiSignRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kpi/sign-record")
@SaCheckLogin
public class KpiSignRecordController {

    @Autowired
    private KpiSignRecordService kpiSignRecordService;

    @PostMapping("/import")
    public RestResult<Map<String, Object>> importExcel(@RequestParam("file") MultipartFile file) {
        Integer count = kpiSignRecordService.importExcel(file);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", count);
        return RestResult.ok(result);
    }

    @GetMapping("/page")
    public RestResult<IPage<KpiSignRecord>> queryRecordPage(KpiSignRecordQueryDTO queryDTO,
                                                            @RequestParam(defaultValue = "1") Integer pageNum,
                                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<KpiSignRecord> page = kpiSignRecordService.queryRecordPage(queryDTO, pageNum, pageSize);
        return RestResult.ok(page);
    }

    @GetMapping("/summary/{month}")
    public RestResult<KpiMonthlySummaryDTO> getMonthlySummary(@PathVariable String month) {
        KpiMonthlySummaryDTO summary = kpiSignRecordService.getMonthlySummary(month);
        return RestResult.ok(summary);
    }

    @GetMapping("/courier-rank/{month}")
    public RestResult<List<KpiCourierRankDTO>> getCourierRank(@PathVariable String month) {
        List<KpiCourierRankDTO> rank = kpiSignRecordService.getCourierRank(month);
        return RestResult.ok(rank);
    }

    @GetMapping("/fake-sign-type/{month}")
    public RestResult<List<KpiFakeSignTypeDTO>> getFakeSignTypeStat(@PathVariable String month) {
        List<KpiFakeSignTypeDTO> stat = kpiSignRecordService.getFakeSignTypeStat(month);
        return RestResult.ok(stat);
    }

    @DeleteMapping("/{id}")
    public RestResult<String> deleteById(@PathVariable Long id) {
        kpiSignRecordService.deleteById(id);
        return RestResult.ok("删除成功");
    }

    @DeleteMapping("/month/{month}")
    public RestResult<String> deleteByMonth(@PathVariable String month) {
        kpiSignRecordService.deleteByMonth(month);
        return RestResult.ok("删除成功");
    }
}