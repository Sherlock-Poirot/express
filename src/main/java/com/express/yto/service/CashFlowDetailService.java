package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.PlatformOptionDTO;
import com.express.yto.model.CashFlowDetail;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 实收流水 Service
 * @author Detective
 * @date Created in 2026/10/15
 */
public interface CashFlowDetailService {

    /**
     * 查询可导入的资金流水平台下拉选项（来源：platform字典，仅启用项，按sort_order排序）
     * @return 平台选项列表，value为平台编码、label为平台名称
     */
    List<PlatformOptionDTO> listPlatformOptions();

    /**
     * 资金流水明细导入（按账户id查账户所属平台，工厂模式分发解析）
     * @param file 上传的流水文件
     * @param accountId 账户id（关联t_cash_account.id，导入的流水按账户归属）
     * @return 导入条数
     */
    Integer importExcel(MultipartFile file, Long accountId);

    /**
     * 查询实收流水列表（不分页）
     * 所有条件均可选，为空则不参与过滤
     * @param custName 客户名称（模糊匹配）
     * @param platform 平台（精确匹配，对应platform字典项）
     * @param actualType 实际收支类型（-1支出，1收入）
     * @param markType 标记收支类型（-1支出，0不计收支，1收入）
     * @param matchStatus 匹配状态（0未匹配，1部分匹配，2已匹配）
     * @param minAmount 金额下限（含）
     * @param maxAmount 金额上限（含）
     * @param startTime 操作时间起始（含）
     * @param endTime 操作时间结束（含）
     * @return 按操作时间倒序的流水列表
     */
    List<CashFlowDetail> list(String custName, Integer platform, Integer actualType, Integer markType,
                              Integer matchStatus, BigDecimal minAmount, BigDecimal maxAmount,
                              LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 分页查询实收流水
     * 筛选条件同 {@link #list}，所有条件均可选，为空则不参与过滤
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return 分页结果，按操作时间倒序
     */
    IPage<CashFlowDetail> search(String custName, Integer platform, Integer actualType, Integer markType,
                                 Integer matchStatus, BigDecimal minAmount, BigDecimal maxAmount,
                                 LocalDateTime startTime, LocalDateTime endTime,
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
