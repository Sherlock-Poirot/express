package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.CostManagementMapper;
import com.express.yto.dto.CostSummaryDTO;
import com.express.yto.dto.CostTypeSummaryDTO;
import com.express.yto.model.CostManagement;
import com.express.yto.service.CostManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class CostManagementServiceImpl extends ServiceImpl<CostManagementMapper, CostManagement> implements CostManagementService {

    @Override
    public CostManagement createCost(CostManagement cost) {
        log.info("创建成本记录: {}", cost.getCostName());
        save(cost);
        return cost;
    }

    @Override
    public List<CostManagement> listCosts(Integer costType, String month) {
        QueryWrapper<CostManagement> queryWrapper = new QueryWrapper<>();
        if (costType != null) {
            queryWrapper.eq("cost_type", costType);
        }
        if (month != null && !month.isEmpty()) {
            queryWrapper.eq("month", month);
        }
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Override
    public CostManagement updateCost(Long id, CostManagement cost) {
        log.info("更新成本记录: id={}", id);
        cost.setId(id);
        updateById(cost);
        return getById(id);
    }

    @Override
    public boolean deleteCost(Long id) {
        log.info("删除成本记录: id={}", id);
        return removeById(id);
    }

    @Override
    public boolean deleteCostBatch(List<Long> ids) {
        log.info("批量删除成本记录: ids={}", ids);
        if (ids == null || ids.isEmpty()) {
            log.warn("批量删除失败：ID列表为空");
            return false;
        }
        return removeByIds(ids);
    }

    @Override
    public BigDecimal sumAllCostsByMonth(String month) {
        BigDecimal sum = baseMapper.sumTotalByMonth(month);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public List<CostTypeSummaryDTO> sumGroupByCostType() {
        return baseMapper.sumGroupByCostType();
    }

    @Override
    public List<CostTypeSummaryDTO> sumGroupByCostTypeByMonth(String month) {
        return baseMapper.sumGroupByCostTypeAndMonth(month);
    }

    @Override
    public CostSummaryDTO getCostSummary(String month) {
        CostSummaryDTO summary = new CostSummaryDTO();
        summary.setMonth(month);

        // 获取总成本
        BigDecimal totalAmount = sumAllCostsByMonth(month);
        summary.setTotalAmount(totalAmount);

        // 获取按类型分类汇总
        List<CostTypeSummaryDTO> typeSummaryList = sumGroupByCostTypeByMonth(month);
        summary.setTypeSummaryList(typeSummaryList);

        log.info("成本汇总: 月份={}, 总成本={}, 类型数量={}", month, totalAmount, typeSummaryList.size());

        return summary;
    }
}
