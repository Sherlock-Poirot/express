package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.CostTypeSummaryDTO;
import com.express.yto.model.CostManagement;

import java.math.BigDecimal;
import java.util.List;

public interface CostManagementService extends IService<CostManagement> {

    CostManagement createCost(CostManagement cost);

    CostManagement getCostById(Long id);

    List<CostManagement> listCosts(Integer costType);

    CostManagement updateCost(Long id, CostManagement cost);

    boolean deleteCost(Long id);

    BigDecimal sumByCostType(Integer costType);

    BigDecimal sumAllCosts();

    List<CostTypeSummaryDTO> sumGroupByCostType();
}