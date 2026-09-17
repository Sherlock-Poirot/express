package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.ValidationResultDTO;
import com.express.yto.model.SysTask;
import com.express.yto.model.WaybillDetail;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Detective
 * @date Created in 2026/5/6
 */
public interface WaybillDetailService extends IService<WaybillDetail> {


    /**
     * 导入原始账单（工作流IMPORT步骤）
     * 校验步骤可导入后，创建导入文件记录并异步执行导入
     * @param file Excel文件
     * @param billMonth 账单月份（yyyy-MM）
     * @return 任务编号（前端轮询 /waybill/import/task 获取进度）
     */
    String importWaybill(MultipartFile file, String billMonth);

    SysTask getImportTask(String taskNo);

    /**
     * 清洗运单数据（工作流CLEAN步骤，异步执行）
     * 校验前置条件（IMPORT已确认、IMPORT_DIFF完成或跳过）后提交异步任务
     * @param billMonth 账单月份（yyyy-MM）
     */
    void cleanData(String billMonth);

    /**
     * 计算运单费用（工作流CALCULATE步骤，异步执行）
     * 校验前置条件（VALIDATE已人工确认）后提交异步任务
     * @param billMonth 账单月份（yyyy-MM）
     */
    void calculateBill(String billMonth);

    /**
     * 执行清洗业务逻辑（供异步任务调用，含事务）
     * @param billMonth 账单月份（yyyy-MM）
     */
    void executeClean(String billMonth);

    /**
     * 执行计算业务逻辑（供异步任务调用）
     * @param billMonth 账单月份（yyyy-MM）
     */
    void executeCalculate(String billMonth);

    /**
     * 导入差异重量数据（工作流IMPORT_DIFF步骤，异步执行）
     * 校验前置条件（IMPORT已确认）后创建任务并异步执行
     * @param file Excel文件
     * @param billMonth 账单月份（yyyy-MM）
     * @return 任务编号
     */
    String importWaybillDiff(MultipartFile file, String billMonth);

    /**
     * 校验运单数据（工作流VALIDATE步骤，同步执行返回结果）
     * 校验前置条件（CLEAN成功）后执行校验，完成后等人工核查确认
     * @param billMonth 账单月份（yyyy-MM）
     * @return 校验结果
     */
    ValidationResultDTO validateData(String billMonth);

    /**
     * 运单明细归档：将 t_waybill_detail 数据迁移到 t_waybill_detail_copy 后清空原表
     * @param billMonth 账单月份（yyyy-MM），为 null 或空时归档全部
     * @return 归档条数
     */
    int archive(String billMonth);

    /**
     * 单条运单费用试算：按客户名称路由到对应的 ExcelFileHandler 计算费用
     * @param dto 运单信息（需包含客户名称、重量、省份、扫描时间等）
     * @return 计算后的运单信息（expense 字段已填充费用）
     */
    ContractShopExcelDTO calculateSingleBill(com.express.yto.dto.ContractShopExcelDTO dto);
}
