package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.MonthlyBillSearchInput;
import com.express.yto.model.MonthlyBill;
import java.io.OutputStream;

public interface MonthlyBillService extends IService<MonthlyBill> {

    IPage<MonthlyBill> search(MonthlyBillSearchInput input);

    void updateBill(MonthlyBill input);

    void generateSummaryBill(String billMonth);

    void exportSummary(MonthlyBillSearchInput input, OutputStream outputStream);
    
    void exportSummaryByBillMonth(String billMonth, OutputStream outputStream);

    void exportDetail(String billMonth, String customerName, Integer type, OutputStream outputStream);

    void exportAllDetail(String billMonth, OutputStream outputStream);
}