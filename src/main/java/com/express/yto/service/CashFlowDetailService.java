package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.model.CashFlowDetail;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 实收流水 Service
 * @author Detective
 * @date Created in 2026/10/15
 */
public interface CashFlowDetailService {

    /**
     * 资金流水明细导入（工厂模式按平台类型分发解析）
     * @param file 上传的流水文件
     * @param platform 平台类型（1微信，2支付宝，3台州银行，4工商银行，5农商银行）
     * @return 导入条数
     */
    Integer importExcel(MultipartFile file, Integer platform);

    /**
     * 分页查询实收流水
     * @param custName 客户名称（可选，模糊匹配）
     * @param platform 平台（可选，精确匹配）
     * @param actualType 实际收支类型（可选）
     * @param markType 标记收支类型（可选）
     * @param matchStatus 匹配状态（可选，0未匹配，1部分匹配，2已匹配）
     * @param startTime 操作时间起始（可选，含当天）
     * @param endTime 操作时间结束（可选，含当天）
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<CashFlowDetail> search(String custName, Integer platform, Integer actualType, Integer markType,
                                 Integer matchStatus, LocalDateTime startTime, LocalDateTime endTime,
                                 Integer pageNo, Integer pageSize);

    /**
     * 新增实收流水
     */
    void add(CashFlowDetail detail);

    /**
     * 修改实收流水
     */
    void update(CashFlowDetail detail);

    /**
     * 批量删除实收流水
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据ID查询
     */
    CashFlowDetail getById(Long id);
}
