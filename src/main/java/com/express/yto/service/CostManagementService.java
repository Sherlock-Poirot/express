package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.CostSummaryDTO;
import com.express.yto.dto.CostTypeSummaryDTO;
import com.express.yto.model.CostManagement;

import java.math.BigDecimal;
import java.util.List;

public interface CostManagementService extends IService<CostManagement> {

    CostManagement createCost(CostManagement cost);

    List<CostManagement> listCosts(Integer costType, String month);

    CostManagement updateCost(Long id, CostManagement cost);

    boolean deleteCost(Long id);

    boolean deleteCostBatch(List<Long> ids);

    BigDecimal sumAllCostsByMonth(String month);

    List<CostTypeSummaryDTO> sumGroupByCostType();

    List<CostTypeSummaryDTO> sumGroupByCostTypeByMonth(String month);

    CostSummaryDTO getCostSummary(String month);
}