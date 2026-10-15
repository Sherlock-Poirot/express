package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.yto.dao.CashFlowDetailMapper;
import com.express.yto.dao.RechargeCashRelationMapper;
import com.express.yto.dao.RechargeFlowDetailMapper;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.CashFlowDetail;
import com.express.yto.model.RechargeCashRelation;
import com.express.yto.model.RechargeFlowDetail;
import com.express.yto.service.RechargeCashRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应收-实收关联关系 Service 实现
 * @author Detective
 * @date Created in 2026/10/15
 */
@Slf4j
@Service
public class RechargeCashRelationServiceImpl implements RechargeCashRelationService {

    @Autowired
    private RechargeCashRelationMapper rechargeCashRelationMapper;

    @Autowired
    private RechargeFlowDetailMapper rechargeFlowDetailMapper;

    @Autowired
    private CashFlowDetailMapper cashFlowDetailMapper;

    @Override
    public IPage<RechargeCashRelation> search(Long rechargeId, Long cashId, String operator,
                                                LocalDateTime startTime, LocalDateTime endTime,
                                                Integer pageNo, Integer pageSize) {
        QueryWrapper<RechargeCashRelation> wrapper = new QueryWrapper<>();
        if (rechargeId != null) {
            wrapper.eq(RechargeCashRelation.COL_RECHARGE_ID, rechargeId);
        }
        if (cashId != null) {
            wrapper.eq(RechargeCashRelation.COL_CASH_ID, cashId);
        }
        if (operator != null && !operator.trim().isEmpty()) {
            wrapper.like(RechargeCashRelation.COL_OPERATOR, operator.trim());
        }
        // 关联时间范围查询：左闭右闭
        if (startTime != null && endTime != null) {
            wrapper.ge(RechargeCashRelation.COL_RELATE_TIME, startTime)
                    .le(RechargeCashRelation.COL_RELATE_TIME, endTime);
        } else if (startTime != null) {
            wrapper.ge(RechargeCashRelation.COL_RELATE_TIME, startTime);
        } else if (endTime != null) {
            wrapper.le(RechargeCashRelation.COL_RELATE_TIME, endTime);
        }
        wrapper.orderByDesc(RechargeCashRelation.COL_RELATE_TIME);
        return rechargeCashRelationMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
    }

    @Override
    public void add(RechargeCashRelation relation) {
        validateRelation(relation);
        LocalDateTime now = LocalDateTime.now();
        relation.setCreateTime(now);
        relation.setUpdateTime(now);
        if (relation.getRelateTime() == null) {
            relation.setRelateTime(now);
        }
        rechargeCashRelationMapper.insert(relation);
        log.info("新增应收-实收关联：应收id={}，实收id={}，关联金额={}，操作人={}",
                relation.getRechargeId(), relation.getCashId(),
                relation.getRelateAmount(), relation.getOperator());
    }

    @Override
    public void update(RechargeCashRelation relation) {
        if (relation.getId() == null) {
            throw new BusinessException("ID不能为空");
        }
        validateRelation(relation);
        RechargeCashRelation existing = rechargeCashRelationMapper.selectById(relation.getId());
        if (existing == null) {
            throw new BusinessException("关联关系记录不存在");
        }
        relation.setCreateTime(existing.getCreateTime());
        relation.setUpdateTime(LocalDateTime.now());
        rechargeCashRelationMapper.updateById(relation);
        log.info("修改应收-实收关联[{}]：应收id={}，实收id={}，关联金额={}",
                relation.getId(), relation.getRechargeId(),
                relation.getCashId(), relation.getRelateAmount());
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的记录");
        }
        int count = rechargeCashRelationMapper.deleteByIds(ids);
        log.info("批量删除应收-实收关联：删除{}条，ids={}", count, ids);
    }

    @Override
    public RechargeCashRelation getById(Long id) {
        return rechargeCashRelationMapper.selectById(id);
    }

    @Override
    public List<RechargeCashRelation> listByRechargeId(Long rechargeId) {
        QueryWrapper<RechargeCashRelation> wrapper = new QueryWrapper<>();
        wrapper.eq(RechargeCashRelation.COL_RECHARGE_ID, rechargeId)
                .orderByDesc(RechargeCashRelation.COL_RELATE_TIME);
        return rechargeCashRelationMapper.selectList(wrapper);
    }

    @Override
    public List<RechargeCashRelation> listByCashId(Long cashId) {
        QueryWrapper<RechargeCashRelation> wrapper = new QueryWrapper<>();
        wrapper.eq(RechargeCashRelation.COL_CASH_ID, cashId)
                .orderByDesc(RechargeCashRelation.COL_RELATE_TIME);
        return rechargeCashRelationMapper.selectList(wrapper);
    }

    /**
     * 关联关系参数校验：应收id + 实收id + 关联金额
     */
    private void validateRelation(RechargeCashRelation relation) {
        if (relation.getRechargeId() == null) {
            throw new BusinessException("应收流水id不能为空");
        }
        if (relation.getCashId() == null) {
            throw new BusinessException("实收流水id不能为空");
        }
        if (relation.getRelateAmount() == null) {
            throw new BusinessException("关联金额不能为空");
        }
    }

    @Override
    @Transactional
    public void match(List<Long> rechargeIds, List<Long> cashIds) {
        if (rechargeIds == null || rechargeIds.isEmpty()) {
            throw new BusinessException("应收流水id列表不能为空");
        }
        if (cashIds == null || cashIds.isEmpty()) {
            throw new BusinessException("实收流水id列表不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        // 笛卡尔积逐对插入关联表
        for (Long rechargeId : rechargeIds) {
            for (Long cashId : cashIds) {
                RechargeCashRelation relation = new RechargeCashRelation();
                relation.setRechargeId(rechargeId);
                relation.setCashId(cashId);
                relation.setRelateTime(now);
                relation.setCreateTime(now);
                relation.setUpdateTime(now);
                rechargeCashRelationMapper.insert(relation);
            }
        }
        // 应收流水 match_status → 1（已匹配）
        UpdateWrapper<RechargeFlowDetail> rechargeUpdate = new UpdateWrapper<>();
        rechargeUpdate.in("id", rechargeIds).set("match_status", 1);
        rechargeFlowDetailMapper.update(null, rechargeUpdate);
        // 实收流水 match_status → 1（已匹配）
        UpdateWrapper<CashFlowDetail> cashUpdate = new UpdateWrapper<>();
        cashUpdate.in("id", cashIds).set("match_status", 1);
        cashFlowDetailMapper.update(null, cashUpdate);
        log.info("匹配完成：应收{}条 × 实收{}条 = {}条关联记录", rechargeIds.size(), cashIds.size(),
                rechargeIds.size() * cashIds.size());
    }

    @Override
    @Transactional
    public void unmatch(List<Long> rechargeIds, List<Long> cashIds) {
        if (rechargeIds == null || rechargeIds.isEmpty()) {
            throw new BusinessException("应收流水id列表不能为空");
        }
        if (cashIds == null || cashIds.isEmpty()) {
            throw new BusinessException("实收流水id列表不能为空");
        }
        // 删除传入应收与实收之间的关联记录
        QueryWrapper<RechargeCashRelation> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.in(RechargeCashRelation.COL_RECHARGE_ID, rechargeIds)
                .in(RechargeCashRelation.COL_CASH_ID, cashIds);
        int deleted = rechargeCashRelationMapper.delete(deleteWrapper);
        // 检查应收流水是否还有剩余关联，没有则改回未匹配
        for (Long rechargeId : rechargeIds) {
            Long count = rechargeCashRelationMapper.selectCount(new QueryWrapper<RechargeCashRelation>()
                    .eq(RechargeCashRelation.COL_RECHARGE_ID, rechargeId));
            if (count == 0) {
                UpdateWrapper<RechargeFlowDetail> update = new UpdateWrapper<>();
                update.eq("id", rechargeId).set("match_status", 0);
                rechargeFlowDetailMapper.update(null, update);
            }
        }
        // 检查实收流水是否还有剩余关联，没有则改回未匹配
        for (Long cashId : cashIds) {
            Long count = rechargeCashRelationMapper.selectCount(new QueryWrapper<RechargeCashRelation>()
                    .eq(RechargeCashRelation.COL_CASH_ID, cashId));
            if (count == 0) {
                UpdateWrapper<CashFlowDetail> update = new UpdateWrapper<>();
                update.eq("id", cashId).set("match_status", 0);
                cashFlowDetailMapper.update(null, update);
            }
        }
        log.info("取消匹配完成：删除{}条关联记录", deleted);
    }
}
