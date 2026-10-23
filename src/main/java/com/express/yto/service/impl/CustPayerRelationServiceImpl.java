package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.CustPayerRelationMapper;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.CustPayerRelation;
import com.express.yto.service.CustPayerRelationService;
import com.express.yto.util.PayerNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户-付款方映射 Service 实现
 * @author Detective
 * @date Created in 2026/10/23
 */
@Slf4j
@Service
public class CustPayerRelationServiceImpl extends ServiceImpl<CustPayerRelationMapper, CustPayerRelation>
        implements CustPayerRelationService {

    @Autowired
    private CustPayerRelationMapper custPayerRelationMapper;

    @Override
    public void upsertRelation(String custCode, String payerName, Long accountId, Integer platform) {
        if (custCode == null || custCode.trim().isEmpty()) {
            throw new BusinessException("客户编码不能为空");
        }
        if (payerName == null || payerName.trim().isEmpty()) {
            throw new BusinessException("付款方名称不能为空");
        }
        CustPayerRelation relation = CustPayerRelation.builder()
                .custCode(custCode.trim())
                .payerName(PayerNameUtil.resolvePayerKey(payerName, platform))
                .accountId(accountId)
                .build();
        custPayerRelationMapper.upsertRelation(relation);
        log.info("沉淀客户-付款方映射：custCode={}，payerName={}（原文={}），platform={}，accountId={}",
                custCode.trim(), relation.getPayerName(), payerName.trim(), platform, accountId);
    }

    @Override
    public int deleteRelation(String custCode, String payerName, Integer platform) {
        if (custCode == null || custCode.trim().isEmpty()) {
            return 0;
        }
        String key = PayerNameUtil.resolvePayerKey(payerName, platform);
        if (key.isEmpty()) {
            return 0;
        }
        QueryWrapper<CustPayerRelation> wrapper = new QueryWrapper<>();
        wrapper.eq(CustPayerRelation.COL_CUST_CODE, custCode.trim())
                .eq(CustPayerRelation.COL_PAYER_NAME, key);
        int deleted = custPayerRelationMapper.delete(wrapper);
        if (deleted > 0) {
            log.info("删除客户-付款方映射：custCode={}，payerName={}（原文={}），platform={}",
                    custCode.trim(), key, payerName.trim(), platform);
        }
        return deleted;
    }

    @Override
    public List<CustPayerRelation> listByNormalizedPayerName(String payerName) {
        String normalized = PayerNameUtil.normalizePayerName(payerName);
        if (normalized.isEmpty()) {
            return new ArrayList<>();
        }
        return custPayerRelationMapper.selectByPayerName(normalized);
    }

    @Override
    public List<CustPayerRelation> listByPayerNameKeyword(String payerName) {
        if (payerName == null || payerName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return custPayerRelationMapper.selectByPayerNameKeyword(payerName.trim());
    }

    @Override
    public List<CustPayerRelation> listByCustCode(String custCode) {
        if (custCode == null || custCode.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return custPayerRelationMapper.selectByCustCode(custCode.trim());
    }
}
