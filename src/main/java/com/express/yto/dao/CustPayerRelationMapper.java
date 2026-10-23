package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.model.CustPayerRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户-付款方映射 Mapper
 * @author Detective
 * @date Created in 2026/10/23
 */
@Mapper
public interface CustPayerRelationMapper extends BaseMapper<CustPayerRelation> {

    /**
     * 幂等upsert：按(cust_code, payer_name)存在则更新；accountId为空时保留原值
     */
    int upsertRelation(CustPayerRelation relation);

    /**
     * 按付款方名称（归一化后）精确查询，同一名称可能映射多个客户
     */
    List<CustPayerRelation> selectByPayerName(@Param("payerName") String payerName);

    /**
     * 按付款方名称关键词模糊查询（contains）
     */
    List<CustPayerRelation> selectByPayerNameKeyword(@Param("payerName") String payerName);

    /**
     * 按客户编码反查其全部付款方映射
     */
    List<CustPayerRelation> selectByCustCode(@Param("custCode") String custCode);
}
