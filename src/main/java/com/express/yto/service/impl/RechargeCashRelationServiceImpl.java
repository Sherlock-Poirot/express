package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.yto.dao.CashAccountMapper;
import com.express.yto.dao.CashFlowDetailMapper;
import com.express.yto.dao.RechargeCashRelationMapper;
import com.express.yto.dao.RechargeFlowDetailMapper;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.CashAccount;
import com.express.yto.model.CashFlowDetail;
import com.express.yto.model.RechargeCashRelation;
import com.express.yto.model.RechargeFlowDetail;
import com.express.yto.service.CustPayerRelationService;
import com.express.yto.service.RechargeCashRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private CashAccountMapper cashAccountMapper;

    @Autowired
    private CustPayerRelationService custPayerRelationService;

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
        // 校验：已匹配的流水不允许再匹配
        Long matchedRechargeCount = rechargeFlowDetailMapper.selectCount(new QueryWrapper<RechargeFlowDetail>()
                .in(RechargeFlowDetail.COL_ID, rechargeIds)
                .eq(RechargeFlowDetail.COL_MATCH_STATUS, 1));
        if (matchedRechargeCount > 0) {
            throw new BusinessException("选中的应收流水中有 " + matchedRechargeCount + " 条已匹配，请先取消匹配再操作");
        }
        Long matchedCashCount = cashFlowDetailMapper.selectCount(new QueryWrapper<CashFlowDetail>()
                .in(CashFlowDetail.COL_ID, cashIds)
                .eq(CashFlowDetail.COL_MATCH_STATUS, 1));
        if (matchedCashCount > 0) {
            throw new BusinessException("选中的实收流水中有 " + matchedCashCount + " 条已匹配，请先取消匹配再操作");
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
        // 查实收流水关联的账户，得到账户名称（用于回写面单流水的支付方式）
        String accountName = resolveAccountName(cashIds);
        // 应收流水 match_status → 1（已匹配），支付方式标记为对应资金流水的账户名称
        UpdateWrapper<RechargeFlowDetail> rechargeUpdate = new UpdateWrapper<>();
        rechargeUpdate.in("id", rechargeIds).set("match_status", 1);
        if (accountName != null) {
            rechargeUpdate.set("pay_type", accountName);
        }
        rechargeFlowDetailMapper.update(null, rechargeUpdate);
        // 实收流水 match_status → 1（已匹配），类目标记为"面单费"
        UpdateWrapper<CashFlowDetail> cashUpdate = new UpdateWrapper<>();
        cashUpdate.in("id", cashIds)
                .set("match_status", 1)
                .set("category", "面单费");
        cashFlowDetailMapper.update(null, cashUpdate);
        log.info("匹配完成：应收{}条 × 实收{}条 = {}条关联记录，账户名称={}", rechargeIds.size(), cashIds.size(),
                rechargeIds.size() * cashIds.size(), accountName);
        // 匹配成功后沉淀客户-付款方映射（学习动作，失败不影响匹配结果）
        savePayerRelation(rechargeIds, cashIds);
    }

    /**
     * 匹配成功后沉淀客户-付款方映射，供后续自动匹配锁定客户：
     * 本次匹配涉及的所有应收流水客户编码唯一时，逐条实收流水沉淀映射；
     * 客户编码为空或存在多个（歧义）时跳过沉淀，避免学到错误关系
     */
    private void savePayerRelation(List<Long> rechargeIds, List<Long> cashIds) {
        try {
            Set<String> custCodes = rechargeFlowDetailMapper.selectList(new QueryWrapper<RechargeFlowDetail>()
                            .select(RechargeFlowDetail.COL_ID, RechargeFlowDetail.COL_TO_CUST_CODE)
                            .in(RechargeFlowDetail.COL_ID, rechargeIds))
                    .stream()
                    .map(RechargeFlowDetail::getToCustCode)
                    .filter(c -> c != null && !c.trim().isEmpty())
                    .map(String::trim)
                    .collect(Collectors.toSet());
            if (custCodes.size() != 1) {
                log.info("跳过沉淀客户-付款方映射：客户编码数={}（为空或多客户歧义），rechargeIds={}",
                        custCodes.size(), rechargeIds);
                return;
            }
            String custCode = custCodes.iterator().next();
            List<CashFlowDetail> cashList = cashFlowDetailMapper.selectList(new QueryWrapper<CashFlowDetail>()
                    .select(CashFlowDetail.COL_ID, CashFlowDetail.COL_CUST_NAME,
                            CashFlowDetail.COL_ACCOUNT_ID, CashFlowDetail.COL_PLATFORM)
                    .in(CashFlowDetail.COL_ID, cashIds));
            for (CashFlowDetail cash : cashList) {
                if (cash.getCustName() == null || cash.getCustName().trim().isEmpty()) {
                    continue;
                }
                custPayerRelationService.upsertRelation(custCode, cash.getCustName(),
                        cash.getAccountId(), cash.getPlatform());
            }
        } catch (Exception e) {
            log.warn("沉淀客户-付款方映射失败（不影响匹配结果）：rechargeIds={}，cashIds={}", rechargeIds, cashIds, e);
        }
    }

    /**
     * 取消匹配时同步删除本次关联对应对的客户-付款方映射（学习回退）：
     * 按本次实际删除的关联记录逐对定位(cust_code, payer_name)，避免多客户场景误删其他组合
     */
    private void removePayerRelations(List<RechargeCashRelation> relations) {
        try {
            Set<Long> rechargeIds = relations.stream()
                    .map(RechargeCashRelation::getRechargeId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            Set<Long> cashIds = relations.stream()
                    .map(RechargeCashRelation::getCashId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (rechargeIds.isEmpty() || cashIds.isEmpty()) {
                return;
            }
            Map<Long, String> custCodeMap = rechargeFlowDetailMapper.selectList(new QueryWrapper<RechargeFlowDetail>()
                            .select(RechargeFlowDetail.COL_ID, RechargeFlowDetail.COL_TO_CUST_CODE)
                            .in(RechargeFlowDetail.COL_ID, rechargeIds))
                    .stream()
                    .filter(r -> r.getToCustCode() != null && !r.getToCustCode().trim().isEmpty())
                    .collect(Collectors.toMap(RechargeFlowDetail::getId, r -> r.getToCustCode().trim(), (a, b) -> a));
            Map<Long, CashFlowDetail> cashMap = cashFlowDetailMapper.selectList(new QueryWrapper<CashFlowDetail>()
                            .select(CashFlowDetail.COL_ID, CashFlowDetail.COL_CUST_NAME,
                                    CashFlowDetail.COL_ACCOUNT_ID, CashFlowDetail.COL_PLATFORM)
                            .in(CashFlowDetail.COL_ID, cashIds))
                    .stream()
                    .collect(Collectors.toMap(CashFlowDetail::getId, c -> c, (a, b) -> a));
            for (RechargeCashRelation relation : relations) {
                String custCode = custCodeMap.get(relation.getRechargeId());
                CashFlowDetail cash = cashMap.get(relation.getCashId());
                if (custCode == null || cash == null
                        || cash.getCustName() == null || cash.getCustName().trim().isEmpty()) {
                    continue;
                }
                custPayerRelationService.deleteRelation(custCode, cash.getCustName(), cash.getPlatform());
            }
        } catch (Exception e) {
            log.warn("取消匹配时删除客户-付款方映射失败（不影响取消匹配结果）", e);
        }
    }

    /**
     * 取匹配的实收流水对应的账户名称，用于回写面单流水的支付方式
     * 多账户场景取第一个有账户名称的账户；无账户则返回null（不覆盖原支付方式）
     */
    private String resolveAccountName(List<Long> cashIds) {
        List<CashFlowDetail> cashList = cashFlowDetailMapper.selectList(new QueryWrapper<CashFlowDetail>()
                .select(CashFlowDetail.COL_ID, CashFlowDetail.COL_ACCOUNT_ID)
                .in(CashFlowDetail.COL_ID, cashIds));
        Set<Long> accountIds = cashList.stream()
                .map(CashFlowDetail::getAccountId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (accountIds.isEmpty()) {
            return null;
        }
        Map<Long, String> accountNameMap = cashAccountMapper.selectList(new QueryWrapper<CashAccount>()
                        .select(CashAccount.COL_ID, CashAccount.COL_ACCOUNT_NAME)
                        .in(CashAccount.COL_ID, accountIds))
                .stream()
                .collect(Collectors.toMap(CashAccount::getId, CashAccount::getAccountName, (a, b) -> a));
        return cashList.stream()
                .map(c -> accountNameMap.get(c.getAccountId()))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public void unmatch(List<Long> rechargeIds, List<Long> cashIds) {
        boolean hasRecharge = rechargeIds != null && !rechargeIds.isEmpty();
        boolean hasCash = cashIds != null && !cashIds.isEmpty();
        if (!hasRecharge && !hasCash) {
            throw new BusinessException("应收流水id和实收流水id至少传一个");
        }
        // 按传入条件查询关联记录，反查另一方的id
        QueryWrapper<RechargeCashRelation> wrapper = new QueryWrapper<>();
        if (hasRecharge) {
            wrapper.in(RechargeCashRelation.COL_RECHARGE_ID, rechargeIds);
        }
        if (hasCash) {
            wrapper.in(RechargeCashRelation.COL_CASH_ID, cashIds);
        }
        List<RechargeCashRelation> relations = rechargeCashRelationMapper.selectList(wrapper);
        if (relations.isEmpty()) {
            log.info("取消匹配：未查询到关联记录");
            return;
        }
        // 关联记录涉及的全部应收id、实收id（含反查出来的另一方）
        Set<Long> allRechargeIds = relations.stream()
                .map(RechargeCashRelation::getRechargeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> allCashIds = relations.stream()
                .map(RechargeCashRelation::getCashId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        // 删除本次涉及的关联记录
        int deleted = rechargeCashRelationMapper.delete(wrapper);
        // 同步删除本次关联对应对的客户-付款方映射（学习回退，失败不影响取消匹配）
        removePayerRelations(relations);
        // 检查应收流水是否还有剩余关联，没有则改回未匹配并清空支付方式
        for (Long rechargeId : allRechargeIds) {
            Long count = rechargeCashRelationMapper.selectCount(new QueryWrapper<RechargeCashRelation>()
                    .eq(RechargeCashRelation.COL_RECHARGE_ID, rechargeId));
            if (count == 0) {
                UpdateWrapper<RechargeFlowDetail> update = new UpdateWrapper<>();
                update.eq("id", rechargeId)
                        .set("match_status", 0)
                        .set("pay_type", null);
                rechargeFlowDetailMapper.update(null, update);
            }
        }
        // 检查实收流水是否还有剩余关联，没有则改回未匹配并清空类目
        for (Long cashId : allCashIds) {
            Long count = rechargeCashRelationMapper.selectCount(new QueryWrapper<RechargeCashRelation>()
                    .eq(RechargeCashRelation.COL_CASH_ID, cashId));
            if (count == 0) {
                UpdateWrapper<CashFlowDetail> update = new UpdateWrapper<>();
                update.eq("id", cashId)
                        .set("match_status", 0)
                        .set("category", null);
                cashFlowDetailMapper.update(null, update);
            }
        }
        log.info("取消匹配完成：删除{}条关联记录，涉及应收{}条、实收{}条",
                deleted, allRechargeIds.size(), allCashIds.size());
    }
}
