package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.dto.ValidationResultDTO;
import com.express.yto.model.SysTask;
import com.express.yto.service.WaybillDetailService;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 导入运单数据
     * @param file Excel文件
     * @return 任务编号
     */
    @PostMapping("/import")
    public RestResult<String> importWaybill(@RequestParam("file") MultipartFile file) {
        return RestResult.ok(waybillDetailService.importWaybill(file));
    }

    /**
     * 导入差异重量数据
     * @param file Excel文件
     * @return 任务编号
     */
    @PostMapping("/import/diff")
    public RestResult<String> importWaybillDiff(@RequestParam("file") MultipartFile file) {
        return RestResult.ok(waybillDetailService.importWaybillDiff(file));
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
     * 清洗运单数据
     * @param date 账单月份（格式：yyyy-MM）
     * @return 操作结果
     */
    @PostMapping("/clean")
    public RestResult<String> cleanData(@RequestParam(value = "date",required = false) String date){
        // TODO 后期要改必须得是异步请求
        waybillDetailService.cleanData(date);
        return RestResult.ok("操作成功");
    }

    /**
     * 校验运单数据（计算前校验）
     * @param date 账单月份（格式：yyyy-MM）
     * @return 校验结果
     */
    @PostMapping("/validate")
    public RestResult<ValidationResultDTO> validateData(@RequestParam(value = "date",required = false) String date){
        ValidationResultDTO result = waybillDetailService.validateData(date);
        return RestResult.ok(result);
    }

    /**
     * 计算运单费用
     * @param date 账单月份（格式：yyyy-MM）
     * @return 操作结果
     */
    @PostMapping("/calculate")
    public RestResult<String> calculateBill(@RequestParam(value = "date",required = false) String date){
        // TODO 后期要改必须得是异步请求
        waybillDetailService.calculateBill(date);
        return RestResult.ok("操作成功");
    }


}