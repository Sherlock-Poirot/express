package com.express.yto.service.impl;

import static com.express.yto.util.AreaUtil.isThisArea;
import static com.express.yto.util.BillDealUtil.isDateInRange;
import static com.express.yto.util.BillDealUtil.judgeOver;
import static com.express.yto.util.ExcelUtil.getBorderStyle;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.DealDataInput;
import com.express.yto.dto.FeeGroupDTO;
import com.express.yto.listener.BillExcelListener;
import com.express.yto.model.Area;
import com.express.yto.model.ExtraFee;
import com.express.yto.model.FixedFee;
import com.express.yto.model.OverFee;
import com.express.yto.service.AreaService;
import com.express.yto.service.EmployeeBillHandler;
import com.express.yto.service.EmployeeService;
import org.springframework.context.annotation.Lazy;
import com.express.yto.service.ExtraFeeService;
import com.express.yto.service.FixedFeeService;
import com.express.yto.service.OverFeeService;
import com.express.yto.service.PrepaymentService;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Detective
 * @date Created in 2026/3/20
 */
@Component
@Slf4j
public class NormalEmployeeHandler implements EmployeeBillHandler {

    @Autowired
    @Lazy
    private EmployeeService employeeService;

    private static String EXCEL_EXTENSIONS = ".xlsx";

    @Override
    public void handle(DealDataInput input) {
        File folder = new File(input.getReadPath());
        File[] files = folder.listFiles();
        assert files != null;
        for (File file : files) {
            log.info("读取到excel【{}】", file.getName());
            if (!file.getName().contains(EXCEL_EXTENSIONS)) {
                continue;
            }
            // 处理文件计算账单导出文件
            dealBill(input, file);
        }
    }

    @Override
    public boolean supports(String companyId) {
        // TODO 返回默认的通用算法，如果以后不适配通用的改为台州圆通和中通算法
        return true;
    }

    private void dealBill(DealDataInput input, File file) {
        List<ContractShopExcelDTO> aliList = new ArrayList<>();
        List<ContractShopExcelDTO> looseList = new ArrayList<>();
        List<ContractShopExcelDTO> limitedList = new ArrayList<>();
        List<ReadSheet> sheets;
        try (ExcelReader excelReader = EasyExcel.read(file).build()) {
            sheets = excelReader.excelExecutor().sheetList();
        }
        for (ReadSheet sheet : sheets) {
            try (ExcelReader sheetReader = EasyExcel.read(file).build()) {
                BillExcelListener listener = new BillExcelListener();
                ReadSheet readSheet = EasyExcel.readSheet(sheet.getSheetNo())
                        .head(ContractShopExcelDTO.class)  // 指定数据模型类
                        .registerReadListener(listener)  // 注册监听器
                        .build();
                sheetReader.read(readSheet);
                List<ContractShopExcelDTO> list = listener.getList();
                if (sheet.getSheetName().contains("淘宝")) {
                    aliList = employeeService.aliAndLoose(list, input.getCompanyId(), false);
                }
                if (sheet.getSheetName().contains("散件")) {
                    looseList = employeeService.aliAndLoose(list, input.getCompanyId(), false);
                }
                if (sheet.getSheetName().contains("限定")) {
                    limitedList = employeeService.aliAndLoose(list, input.getCompanyId() + "_limit", true);
                }
            }
        }

        appendSum(aliList);
        appendSum(looseList);
        appendSum(limitedList);
        ExcelWriter writer = EasyExcel.write(input.getExportPath() + "/" + input.getMonth() + file.getName())
                .registerWriteHandler(getBorderStyle()).build();
        WriteSheet ali = EasyExcel.writerSheet(0, "淘宝").head(ContractShopExcelDTO.class).build();
        WriteSheet loose = EasyExcel.writerSheet(1, "散件").head(ContractShopExcelDTO.class).build();
        WriteSheet limit = EasyExcel.writerSheet(2, "限定").head(ContractShopExcelDTO.class).build();
        writer.write(aliList, ali);
        writer.write(looseList, loose);
        writer.write(limitedList, limit);
        writer.finish();
        log.info("{}导出完成", file.getName());
    }

    private void appendSum(List<ContractShopExcelDTO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 增加一行总计
        BigDecimal amount = list.stream().map(ContractShopExcelDTO::getExpense)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal label = BigDecimal.valueOf(list.size() * 3);
        list.add(ContractShopExcelDTO.builder().expense(amount).build());
        list.add(ContractShopExcelDTO.builder().shopType("面单预付").expense(label).build());
        list.add(ContractShopExcelDTO.builder().shopType("应付合计").expense(amount.subtract(label)).build());
    }

}
