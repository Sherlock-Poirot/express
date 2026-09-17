package com.express.yto.service;

import com.express.yto.dto.WaybillFlowBatchDTO;
import com.express.yto.model.WaybillFlowFile;

import java.util.List;

/**
 * 账单工作流服务
 * 管理每月账单处理流程（导入→清洗→校验→计算）的步骤状态
 * @author Detective
 * @date Created in 2026/9/16
 */
public interface WaybillFlowService {

    /**
     * 初始化指定月份的工作流步骤记录（5个步骤，WAITING态）
     * 幂等操作：依赖 uk_bill_month_step 唯一键，重复调用/多实例并发调用均不会产生重复数据
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @return 实际初始化的步骤数（已初始化过则返回0）
     */
    int initMonthlyFlow(String billMonth);

    /**
     * 查询工作流批次列表
     * 按账单月份倒序返回各批次及5个步骤的状态概览，不含导入文件明细
     * @return 批次列表
     */
    List<WaybillFlowBatchDTO> listFlowBatches();

    /**
     * 查询指定月份的工作流批次详情
     * 返回5个步骤状态及IMPORT步骤的导入文件清单
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @return 批次详情，批次不存在时返回null
     */
    WaybillFlowBatchDTO getFlowDetail(String billMonth);

    /**
     * 人工确认步骤完成
     * 仅支持IMPORT（导入完成确认，要求所有文件均导入成功）和VALIDATE（核查通过确认，要求校验已成功执行）
     * 确认后步骤状态置为PASSED，解锁下一步骤
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @param stepCode 步骤编码：IMPORT/VALIDATE
     */
    void confirmStep(String billMonth, String stepCode);

    /**
     * 跳过步骤
     * 仅支持IMPORT_DIFF（重量差异数据为可选步骤，无差异数据时跳过），仅WAITING状态可跳过
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @param stepCode 步骤编码：IMPORT_DIFF
     */
    void skipStep(String billMonth, String stepCode);

    /**
     * 校验IMPORT步骤是否允许继续导入文件
     * 人工确认（PASSED）后锁定，不允许再导入
     * @param billMonth 账单月份（格式：yyyy-MM）
     */
    void checkImportable(String billMonth);

    /**
     * 创建导入文件记录（RUNNING态）
     * 每次上传文件一条记录，导入完成后通过 finishImportFileRecord 更新结果
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @param fileName 原始文件名
     * @param taskNo 关联的导入任务编号
     * @return 文件记录
     */
    WaybillFlowFile createImportFileRecord(String billMonth, String fileName, String taskNo);

    /**
     * 导入文件记录完成回调（按task_no定位，仅RUNNING态可更新）
     * @param taskNo 导入任务编号
     * @param success 是否成功
     * @param rowCount 成功导入条数
     * @param errorMsg 失败原因（成功传null）
     */
    void finishImportFileRecord(String taskNo, boolean success, int rowCount, String errorMsg);

    /**
     * 启动步骤执行（校验前置条件后将状态置为RUNNING）
     * 前置规则：IMPORT_DIFF需IMPORT已确认；CLEAN需IMPORT已确认且IMPORT_DIFF完成或跳过；
     *          VALIDATE需CLEAN成功；CALCULATE需VALIDATE已确认
     * 可重复执行：CLEAN/VALIDATE成功或失败后可重跑；CALCULATE成功后不可重跑（防止覆盖人工核查成果）
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @param stepCode 步骤编码：IMPORT_DIFF/CLEAN/VALIDATE/CALCULATE
     */
    void startStep(String billMonth, String stepCode);

    /**
     * 步骤执行完成回调（异步任务结束时调用，仅RUNNING态可更新）
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @param stepCode 步骤编码
     * @param success 是否成功
     * @param errorMsg 失败原因（成功传null）
     */
    void finishStep(String billMonth, String stepCode, boolean success, String errorMsg);
}
