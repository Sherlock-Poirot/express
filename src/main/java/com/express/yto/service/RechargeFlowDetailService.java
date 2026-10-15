package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.model.RechargeFlowDetail;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应收明细-面单流水明细 Service
 * @author Detective
 * @date Created in 2026/10/15
 */
public interface RechargeFlowDetailService {

    /**
     * 修改面单流水明细
     * @param detail 修改数据
     */
    void update(RechargeFlowDetail detail);

    /**
     * 列表查询（不分页）
     * @param fromCustName 来源客户名称（可选，模糊匹配）
     * @param fromCustCode 来源客户编码（可选，精确匹配）
     * @param toCustCode 目的客户编码（可选，精确匹配）
     * @param toCustName 目的客户名称（可选，模糊匹配）
     * @param operateType 操作类型（可选，精确匹配）
     * @param payType 支付方式（可选，精确匹配）
     * @param startTime 操作时间起始（可选，含）
     * @param endTime 操作时间结束（可选，含）
     * @param matchStatus 匹配状态（可选，0未匹配，1部分匹配，2已匹配）
     * @return 列表结果
     */
    List<RechargeFlowDetail> list(String fromCustName, String fromCustCode, String toCustCode, String toCustName,
                                   String operateType, String payType,
                                   LocalDateTime startTime, LocalDateTime endTime, Integer matchStatus);

    /**
     * 分页查询
     * @param fromCustName 来源客户名称（可选，模糊匹配）
     * @param fromCustCode 来源客户编码（可选，精确匹配）
     * @param toCustCode 目的客户编码（可选，精确匹配）
     * @param toCustName 目的客户名称（可选，模糊匹配）
     * @param operateType 操作类型（可选，精确匹配）
     * @param payType 支付方式（可选，精确匹配）
     * @param startTime 操作时间起始（可选，含）
     * @param endTime 操作时间结束（可选，含）
     * @param matchStatus 匹配状态（可选，0未匹配，1部分匹配，2已匹配）
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<RechargeFlowDetail> search(String fromCustName, String fromCustCode, String toCustCode, String toCustName,
                                      String operateType, String payType,
                                      LocalDateTime startTime, LocalDateTime endTime, Integer matchStatus,
                                      Integer pageNo, Integer pageSize);

    /**
     * 面单流水明细导入
     * 来源网点名称、目的网点按"|"切割拆分到对应字段
     * @param file Excel文件
     * @return 导入条数
     */
    Integer importExcel(MultipartFile file);
}
