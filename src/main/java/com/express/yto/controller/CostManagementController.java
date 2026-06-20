package com.express.yto.controller;

import com.express.yto.dto.CostSummaryDTO;
import com.express.yto.dto.CostTypeSummaryDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.model.CostManagement;
import com.express.yto.service.CostManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/cost")
@Api(tags = "成本管理")
public class CostManagementController {

    @Autowired
    private CostManagementService costManagementService;

    @PostMapping("/create")
    @ApiOperation("创建成本记录")
    public RestResult<CostManagement> createCost(@RequestBody CostManagement cost) {
        CostManagement result = costManagementService.createCost(cost);
        return RestResult.ok(result);
    }

    @GetMapping("/list")
    @ApiOperation("查询成本列表")
    public RestResult<List<CostManagement>> listCosts(
            @ApiParam("成本类型：1-场地成本，2-人工成本，3-操作成本，4-运能成本，5-折旧成本，6-其他")
            @RequestParam(value = "costType", required = false) Integer costType,
            @ApiParam(value = "月份，格式：yyyy-MM", required = true)
            @RequestParam(value = "month") String month) {
        List<CostManagement> result = costManagementService.listCosts(costType, month);
        return RestResult.ok(result);
    }

    @PutMapping("/{id}")
    @ApiOperation("更新成本记录")
    public RestResult<CostManagement> updateCost(@PathVariable Long id, @RequestBody CostManagement cost) {
        CostManagement result = costManagementService.updateCost(id, cost);
        return RestResult.ok(result);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除成本记录")
    public RestResult<Boolean> deleteCost(@PathVariable Long id) {
        boolean result = costManagementService.deleteCost(id);
        return RestResult.ok(result);
    }

    @DeleteMapping("/batch/delete")
    @ApiOperation("批量删除成本记录")
    public RestResult<Boolean> deleteCostBatch(@RequestBody List<Long> ids) {
        boolean result = costManagementService.deleteCostBatch(ids);
        return RestResult.ok(result);
    }

    @GetMapping("/summary/{month}")
    @ApiOperation("成本汇总（按月份）")
    public RestResult<CostSummaryDTO> getCostSummary(
            @ApiParam(value = "月份，格式：yyyy-MM", required = true)
            @PathVariable String month) {
        CostSummaryDTO result = costManagementService.getCostSummary(month);
        return RestResult.ok(result);
    }

    @GetMapping("/sum/type/{month}")
    @ApiOperation("按类型分类汇总（按月份）")
    public RestResult<List<CostTypeSummaryDTO>> sumGroupByCostTypeByMonth(
            @ApiParam(value = "月份，格式：yyyy-MM", required = true)
            @PathVariable String month) {
        List<CostTypeSummaryDTO> result = costManagementService.sumGroupByCostTypeByMonth(month);
        return RestResult.ok(result);
    }

    @GetMapping("/sum/total/{month}")
    @ApiOperation("总成本汇总（按月份）")
    public RestResult<BigDecimal> sumAllCostsByMonth(
            @ApiParam(value = "月份，格式：yyyy-MM", required = true)
            @PathVariable String month) {
        BigDecimal result = costManagementService.sumAllCostsByMonth(month);
        return RestResult.ok(result);
    }
}