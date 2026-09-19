package com.express.yto.controller;

import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.dto.ValidationResultDTO;
import com.express.yto.dto.WaybillFlowBatchDTO;
import com.express.yto.model.SysTask;
import com.express.yto.service.WaybillDetailService;
import com.express.yto.service.WaybillFlowService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 运单明细控制器
 * 处理运单导入、清洗、校验、计算等操作
 * @author Detective
 * @date Created in 2026/5/6
 */
@RestController
@RequestMapping("/waybill")
public class WaybillController {

    @Autowired
    private WaybillDetailService waybillDetailService;

    @Autowired
    private WaybillFlowService waybillFlowService;

    /**
     * 导入运单数据（工作流IMPORT步骤）
     * 支持分多次导入多个文件，全部导入完成后需调用确认接口
     * @param file Excel文件
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @return 任务编号
     */
    @PostMapping("/import")
    public RestResult<String> importWaybill(@RequestParam("file") MultipartFile file,
                                            @RequestParam("billMonth") String billMonth) {
        return RestResult.ok(waybillDetailService.importWaybill(file, billMonth));
    }

    /**
     * 导入差异重量数据（工作流IMPORT_DIFF步骤，可选步骤，无差异可跳过）
     * @param file Excel文件
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @return 任务编号
     */
    @PostMapping("/import/diff")
    public RestResult<String> importWaybillDiff(@RequestParam("file") MultipartFile file,
                                                @RequestParam("billMonth") String billMonth) {
        return RestResult.ok(waybillDetailService.importWaybillDiff(file, billMonth));
    }

    /**
     * 查询导入任务状态
     * @param taskNo 任务编号
     * @return 任务信息
     */
    @GetMapping("/import/task")
    public RestResult<SysTask> getImportTask(@RequestParam("taskNo") String taskNo) {
        return RestResult.ok(waybillDetailService.getImportTask(taskNo));
    }

    /**
     * 清洗运单数据（工作流CLEAN步骤，异步执行）
     * 前置条件：IMPORT已人工确认且IMPORT_DIFF完成或跳过
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @return 操作结果（异步执行，步骤状态通过批次详情接口查询）
     */
    @PostMapping("/clean")
    public RestResult<String> cleanData(@RequestParam("billMonth") String billMonth){
        waybillDetailService.cleanData(billMonth);
        return RestResult.ok("操作成功，正在异步执行");
    }

    /**
     * 校验运单数据（工作流VALIDATE步骤，同步返回校验结果）
     * 前置条件：CLEAN已成功；校验完成后需人工核查并调用确认接口
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @return 校验结果
     */
    @PostMapping("/validate")
    public RestResult<ValidationResultDTO> validateData(@RequestParam("billMonth") String billMonth){
        ValidationResultDTO result = waybillDetailService.validateData(billMonth);
        return RestResult.ok(result);
    }

    /**
     * 计算运单费用（工作流CALCULATE步骤，异步执行）
     * 前置条件：VALIDATE已人工核查确认
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @return 操作结果（异步执行，步骤状态通过批次详情接口查询）
     */
    @PostMapping("/calculate")
    public RestResult<String> calculateBill(@RequestParam("billMonth") String billMonth){
        waybillDetailService.calculateBill(billMonth);
        return RestResult.ok("操作成功，正在异步执行");
    }

    /**
     * 归档运单明细（计算+人工核查完毕后执行）
     * 将 t_waybill_detail 数据迁移至 t_waybill_detail_copy 后清空原表
     * @param date 账单月份（格式：yyyy-MM），不传则归档全部数据
     * @return 归档条数
     */
    @PostMapping("/archive")
    public RestResult<Integer> archive(@RequestParam(value = "date", required = false) String date) {
        int count = waybillDetailService.archive(date);
        return RestResult.ok(count);
    }

    /**
     * 单条运单费用试算
     * 根据传入的运单信息（客户名称、重量、省份、扫描时间等）计算快递费
     * @param dto 运单信息
     * @return 计算后的运单信息（expense 字段为计算出的费用）
     */
    @PostMapping("/calculateSingle")
    public RestResult<ContractShopExcelDTO> calculateSingle(@RequestBody ContractShopExcelDTO dto) {
        return RestResult.ok(waybillDetailService.calculateSingleBill(dto));
    }

    /**
     * 查询账单工作流批次列表
     * 按账单月份倒序返回各批次及5个步骤的状态概览（IMPORT步骤状态由导入文件记录推导），不含导入文件明细
     * @param billMonth 账单月份（格式：yyyy-MM），不传则返回全部批次
     * @return 批次列表
     */
    @GetMapping("/flow/list")
    public RestResult<List<WaybillFlowBatchDTO>> listFlowBatches(
            @RequestParam(value = "billMonth", required = false) String billMonth) {
        return RestResult.ok(waybillFlowService.listFlowBatches(billMonth));
    }

    /**
     * 查询账单工作流批次详情
     * 返回该月份5个步骤状态及IMPORT步骤的导入文件清单（importFiles）
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @return 批次详情，批次不存在时 data 为 null
     */
    @GetMapping("/flow/{billMonth}")
    public RestResult<WaybillFlowBatchDTO> getFlowDetail(@PathVariable("billMonth") String billMonth) {
        return RestResult.ok(waybillFlowService.getFlowDetail(billMonth));
    }

    /**
     * 人工确认步骤完成（全部步骤均支持）
     * IMPORT要求所有文件导入成功，其余步骤要求已成功执行（SUCCESS）
     * 确认后步骤状态置为 PASSED，解锁下一步骤；重复确认幂等返回成功
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @param stepCode 步骤编码：IMPORT/IMPORT_DIFF/CLEAN/VALIDATE/CALCULATE
     * @return 操作结果
     */
    @PostMapping("/flow/{billMonth}/confirm/{stepCode}")
    public RestResult<String> confirmStep(@PathVariable("billMonth") String billMonth,
                                          @PathVariable("stepCode") String stepCode) {
        waybillFlowService.confirmStep(billMonth, stepCode);
        return RestResult.ok("操作成功");
    }

    /**
     * 跳过步骤（全部步骤均支持）
     * 仅待执行（WAITING）状态可跳过；IMPORT需无导入文件记录
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @param stepCode 步骤编码：IMPORT/IMPORT_DIFF/CLEAN/VALIDATE/CALCULATE
     * @return 操作结果
     */
    @PostMapping("/flow/{billMonth}/skip/{stepCode}")
    public RestResult<String> skipStep(@PathVariable("billMonth") String billMonth,
                                       @PathVariable("stepCode") String stepCode) {
        waybillFlowService.skipStep(billMonth, stepCode);
        return RestResult.ok("操作成功");
    }

    /**
     * 重置指定月份的账单工作流（整批作废重来）
     * 删除该月运单明细（t_waybill_detail）、原始导入数据（t_waybill_detail_original）及导入文件记录，
     * 并将5个步骤全部复位到待执行状态；存在执行中步骤时拒绝重置
     * @param billMonth 账单月份（格式：yyyy-MM）
     * @return 操作结果
     */
    @PostMapping("/flow/{billMonth}/reset")
    public RestResult<String> resetFlow(@PathVariable("billMonth") String billMonth) {
        waybillFlowService.resetFlow(billMonth);
        return RestResult.ok("重置成功");
    }

}