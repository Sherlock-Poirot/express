package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.model.RechargeCashRelation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应收-实收关联关系 Service
 * @author Detective
 * @date Created in 2026/10/15
 */
public interface RechargeCashRelationService {

    /**
     * 分页查询关联关系
     * @param rechargeId 应收流水id（可选）
     * @param cashId 实收流水id（可选）
     * @param operator 操作人（可选，模糊匹配）
     * @param startTime 关联时间起始（可选，含）
     * @param endTime 关联时间结束（可选，含）
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<RechargeCashRelation> search(Long rechargeId, Long cashId, String operator,
                                       LocalDateTime startTime, LocalDateTime endTime,
                                       Integer pageNo, Integer pageSize);

    /**
     * 新增关联关系
     */
    void add(RechargeCashRelation relation);

    /**
     * 修改关联关系
     */
    void update(RechargeCashRelation relation);

    /**
     * 批量删除关联关系
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据ID查询
     */
    RechargeCashRelation getById(Long id);

    /**
     * 根据应收流水id查询所有关联关系（用于查看某笔应收被哪些实收核销）
     */
    List<RechargeCashRelation> listByRechargeId(Long rechargeId);

    /**
     * 根据实收流水id查询所有关联关系（用于查看某笔实收核销了哪些应收）
     */
    List<RechargeCashRelation> listByCashId(Long cashId);
}
