package com.express.yto.service;

import com.express.yto.model.CustPayerRelation;

import java.util.List;

/**
 * 客户-付款方映射 Service
 * 自动匹配时通过资金流水的付款方名称（cust_name）锁定系统客户
 * @author Detective
 * @date Created in 2026/10/23
 */
public interface CustPayerRelationService {

    /**
     * 手动匹配成功后沉淀映射：按(cust_code, payer_name)幂等upsert；
     * 微信平台的付款方名称归一化（剥离预付款前缀），其他平台直接存原文；
     * 归一化后为空或过短则回退存原文，保证精确匹配可用
     * @param custCode 客户编码（面单明细to_cust_code）
     * @param payerName 付款方名称（资金流水cust_name原文）
     * @param accountId 本次匹配的收款账户id，可为空（为空时保留已存值）
     * @param platform 资金流水所属平台（1微信），决定是否剥离前缀
     */
    void upsertRelation(String custCode, String payerName, Long accountId, Integer platform);

    /**
     * 按付款方名称（归一化后）精确查询映射；
     * 同一名称可能映射多个客户，调用方需处理多命中（多命中视为歧义，留人工）
     */
    List<CustPayerRelation> listByNormalizedPayerName(String payerName);

    /**
     * 按付款方名称关键词模糊查询映射（contains），同样可能多命中
     */
    List<CustPayerRelation> listByPayerNameKeyword(String payerName);

    /**
     * 取消匹配时回退学习：按平台计算映射键后删除该(cust_code, payer_name)映射；
     * 入参为空或算出的键为空时不做任何操作
     * @return 实际删除条数
     */
    int deleteRelation(String custCode, String payerName, Integer platform);

    /**
     * 按客户编码反查其全部付款方映射（管理与展示用）
     */
    List<CustPayerRelation> listByCustCode(String custCode);
}
