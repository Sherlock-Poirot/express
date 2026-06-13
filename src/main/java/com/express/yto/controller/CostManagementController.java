package com.express.yto.controller;

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

    @PostMapping
    @ApiOperation("创建成本记录")
    public RestResult<CostManagement> createCost(@RequestBody CostManagement cost) {
        CostManagement result = costManagementService.createCost(cost);
        return RestResult.ok(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询成本记录")
    public RestResult<CostManagement> getCostById(@PathVariable Long id) {
        CostManagement result = costManagementService.getCostById(id);
        return RestResult.ok(result);
    }

    @GetMapping
    @ApiOperation("查询成本列表")
    public RestResult<List<CostManagement>> listCosts(
            @ApiParam("成本类型：1-场地成本，2-人工成本，3-操作成本，4-运能成本，5-折旧成本")
            @RequestParam(value = "costType", required = false) Integer costType) {
        List<CostManagement> result = costManagementService.listCosts(costType);
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

    @GetMapping("/sum")
    @ApiOperation("按类型统计成本")
    public RestResult<List<CostTypeSummaryDTO>> sumGroupByCostType() {
        List<CostTypeSummaryDTO> result = costManagementService.sumGroupByCostType();
        return RestResult.ok(result);
    }

    @GetMapping("/sum/total")
    @ApiOperation("统计总成本")
    public RestResult<BigDecimal> sumAllCosts() {
        BigDecimal result = costManagementService.sumAllCosts();
        return RestResult.ok(result);
    }

    @GetMapping("/sum/type/{costType}")
    @ApiOperation("统计指定类型成本")
    public RestResult<BigDecimal> sumByCostType(@PathVariable Integer costType) {
        BigDecimal result = costManagementService.sumByCostType(costType);
        return RestResult.ok(result);
    }
}