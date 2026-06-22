package com.express.yto.controller;
import com.express.yto.dto.CostSummaryDTO;
import com.express.yto.dto.CostTypeSummaryDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.model.CostManagement;
import com.express.yto.service.CostManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
@RestController
@RequestMapping("/cost")
public class CostManagementController {
    @Autowired
    private CostManagementService costManagementService;
    @PostMapping("/create")
    public RestResult<CostManagement> createCost(@RequestBody CostManagement cost) {
        CostManagement result = costManagementService.createCost(cost);
        return RestResult.ok(result);
    }
    @GetMapping("/list")
    public RestResult<List<CostManagement>> listCosts(
            @RequestParam(value = "costType", required = false) Integer costType,
            @RequestParam(value = "month") String month) {
        List<CostManagement> result = costManagementService.listCosts(costType, month);
        return RestResult.ok(result);
    }
    @PutMapping("/{id}")
    public RestResult<CostManagement> updateCost(@PathVariable Long id, @RequestBody CostManagement cost) {
        CostManagement result = costManagementService.updateCost(id, cost);
        return RestResult.ok(result);
    }
    @DeleteMapping("/{id}")
    public RestResult<Boolean> deleteCost(@PathVariable Long id) {
        boolean result = costManagementService.deleteCost(id);
        return RestResult.ok(result);
    }
    @DeleteMapping("/batch/delete")
    public RestResult<Boolean> deleteCostBatch(@RequestBody List<Long> ids) {
        boolean result = costManagementService.deleteCostBatch(ids);
        return RestResult.ok(result);
    }
    @GetMapping("/summary/{month}")
    public RestResult<CostSummaryDTO> getCostSummary(
            @PathVariable String month) {
        CostSummaryDTO result = costManagementService.getCostSummary(month);
        return RestResult.ok(result);
    }
    @GetMapping("/sum/type/{month}")
    public RestResult<List<CostTypeSummaryDTO>> sumGroupByCostTypeByMonth(
            @PathVariable String month) {
        List<CostTypeSummaryDTO> result = costManagementService.sumGroupByCostTypeByMonth(month);
        return RestResult.ok(result);
    }
    @GetMapping("/sum/total/{month}")
    public RestResult<BigDecimal> sumAllCostsByMonth(
            @PathVariable String month) {
        BigDecimal result = costManagementService.sumAllCostsByMonth(month);
        return RestResult.ok(result);
    }
}