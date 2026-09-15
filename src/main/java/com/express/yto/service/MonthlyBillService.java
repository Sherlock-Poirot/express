package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.MonthlyBillSearchInput;
import com.express.yto.model.ExportFile;
import com.express.yto.model.MonthlyBill;
import java.io.OutputStream;
import java.util.List;

public interface MonthlyBillService extends IService<MonthlyBill> {

    IPage<MonthlyBill> search(MonthlyBillSearchInput input);

    void updateBill(MonthlyBill input);

    void generateSummaryBill(String billMonth);

    void exportSummary(MonthlyBillSearchInput input, OutputStream outputStream);

    void exportSummaryByBillMonth(String billMonth, OutputStream outputStream);

    void exportDetail(String billMonth, String customerName, Integer type, OutputStream outputStream);

    void exportAllDetail(String billMonth, OutputStream outputStream);

    /**
     * 创建异步导出任务记录（状态RUNNING），返回记录ID
     */
    Long createExportTask(String billMonth);

    /**
     * 查询导出记录列表（下载展示页用，最新50条倒序）
     */
    List<ExportFile> listExportTasks();

    /**
     * 根据ID查询导出记录
     */
    ExportFile getExportFile(Long id);
}