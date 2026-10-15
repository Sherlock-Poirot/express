package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.yto.dao.CashFlowDetailMapper;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.CashFlowDetail;
import com.express.yto.service.CashFlowDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 实收流水 Service 实现
 * @author Detective
 * @date Created in 2026/10/15
 */
@Slf4j
@Service
public class CashFlowDetailServiceImpl implements CashFlowDetailService {

    @Autowired
    private CashFlowDetailMapper cashFlowDetailMapper;

    @Override
    public IPage<CashFlowDetail> search(String custName, Integer platform, Integer actualType, Integer markType,
                                         Integer matchStatus, LocalDateTime startTime, LocalDateTime endTime,
                                         Integer pageNo, Integer pageSize) {
        QueryWrapper<CashFlowDetail> wrapper = new QueryWrapper<>();
        if (custName != null && !custName.trim().isEmpty()) {
            wrapper.like(CashFlowDetail.COL_CUST_NAME, custName.trim());
        }
        if (platform != null) {
            wrapper.eq(CashFlowDetail.COL_PLATFORM, platform);
        }
        if (actualType != null) {
            wrapper.eq(CashFlowDetail.COL_ACTUAL_TYPE, actualType);
        }
        if (markType != null) {
            wrapper.eq(CashFlowDetail.COL_MARK_TYPE, markType);
        }
        if (matchStatus != null) {
            wrapper.eq(CashFlowDetail.COL_MATCH_STATUS, matchStatus);
        }
        // 操作时间范围查询：左闭右闭
        if (startTime != null && endTime != null) {
            wrapper.ge(CashFlowDetail.COL_OPERATE_TIME, startTime)
                    .le(CashFlowDetail.COL_OPERATE_TIME, endTime);
        } else if (startTime != null) {
            wrapper.ge(CashFlowDetail.COL_OPERATE_TIME, startTime);
        } else if (endTime != null) {
            wrapper.le(CashFlowDetail.COL_OPERATE_TIME, endTime);
        }
        wrapper.orderByDesc(CashFlowDetail.COL_OPERATE_TIME);
        return cashFlowDetailMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
    }

    @Override
    public void add(CashFlowDetail detail) {
        validateDetail(detail);
        LocalDateTime now = LocalDateTime.now();
        detail.setCreateTime(now);
        detail.setUpdateTime(now);
        cashFlowDetailMapper.insert(detail);
        log.info("新增实收流水：客户={}，金额={}，实际收支类型={}，标记收支类型={}",
                detail.getCustName(), detail.getAmount(), detail.getActualType(), detail.getMarkType());
    }

    @Override
    public void update(CashFlowDetail detail) {
        if (detail.getId() == null) {
            throw new BusinessException("ID不能为空");
        }
        validateDetail(detail);
        CashFlowDetail existing = cashFlowDetailMapper.selectById(detail.getId());
        if (existing == null) {
            throw new BusinessException("流水记录不存在");
        }
        detail.setCreateTime(existing.getCreateTime());
        detail.setUpdateTime(LocalDateTime.now());
        cashFlowDetailMapper.updateById(detail);
        log.info("修改实收流水[{}]：客户={}，金额={}，实际收支类型={}，标记收支类型={}",
                detail.getId(), detail.getCustName(), detail.getAmount(),
                detail.getActualType(), detail.getMarkType());
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的记录");
        }
        int count = cashFlowDetailMapper.deleteByIds(ids);
        log.info("批量删除实收流水：删除{}条，ids={}", count, ids);
    }

    @Override
    public CashFlowDetail getById(Long id) {
        return cashFlowDetailMapper.selectById(id);
    }

    /**
     * 流水参数校验：客户名称 + 金额 + 收支类型
     */
    private void validateDetail(CashFlowDetail detail) {
        if (detail.getCustName() == null || detail.getCustName().trim().isEmpty()) {
            throw new BusinessException("客户名称不能为空");
        }
        if (detail.getAmount() == null) {
            throw new BusinessException("金额不能为空");
        }
        if (detail.getActualType() == null) {
            throw new BusinessException("实际收支类型不能为空");
        }
        if (detail.getMarkType() == null) {
            throw new BusinessException("标记收支类型不能为空");
        }
    }
}
