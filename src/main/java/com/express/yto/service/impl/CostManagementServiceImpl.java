package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.CostManagementMapper;
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
    public CostManagement getCostById(Long id) {
        return getById(id);
    }

    @Override
    public List<CostManagement> listCosts(Integer costType) {
        QueryWrapper<CostManagement> queryWrapper = new QueryWrapper<>();
        if (costType != null) {
            queryWrapper.eq("cost_type", costType);
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
    public BigDecimal sumByCostType(Integer costType) {
        BigDecimal sum = baseMapper.sumByCostType(costType);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumAllCosts() {
        List<CostManagement> list = list();
        return list.stream()
                .map(cost -> cost.getAmount() != null ? cost.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<Map<String, Object>> sumGroupByCostType() {
        return baseMapper.sumGroupByCostType();
    }
}