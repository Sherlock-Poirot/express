package com.express.yto.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.DailyBillMapper;
import com.express.yto.model.DailyBill;
import com.express.yto.service.DailyBillService;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class DailyBillServiceImpl extends ServiceImpl<DailyBillMapper, DailyBill> implements DailyBillService {

    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy/M/d H:mm:ss");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional
    public String uploadBill(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        log.info("开始上传每日账单: {}", fileName);

        List<DailyBill> dataList = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = createWorkbook(is, fileName)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();

            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    DailyBill bill = parseRow(row);
                    dataList.add(bill);
                    successCount++;

                    if (dataList.size() >= 1000) {
                        baseMapper.insertBatch(dataList);
                        dataList.clear();
                    }
                } catch (Exception e) {
                    failCount++;
                    log.warn("第{}行数据解析失败: {}", i + 1, e.getMessage());
                }
            }

            if (!dataList.isEmpty()) {
                baseMapper.insertBatch(dataList);
            }

        } catch (Exception e) {
            log.error("上传每日账单失败", e);
            return "上传失败: " + e.getMessage();
        }

        log.info("每日账单上传完成，成功{}条，失败{}条", successCount, failCount);
        return String.format("每日账单上传完成，成功%d条，失败%d条", successCount, failCount);
    }

    private Workbook createWorkbook(InputStream is, String fileName) throws Exception {
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(is);
        } else if (fileName.toLowerCase().endsWith(".xls")) {
            return new HSSFWorkbook(is);
        } else {
            throw new IllegalArgumentException("不支持的文件格式");
        }
    }

    private DailyBill parseRow(Row row) {
        DailyBill bill = new DailyBill();

        bill.setWaybillNo(getCellStringValue(row.getCell(0)));
        bill.setCustomerCode(getCellStringValue(row.getCell(1)));
        bill.setCustomerName(getCellStringValue(row.getCell(2)));
        bill.setChargeWeight(getCellDecimalValue(row.getCell(3)));
        bill.setTotalAmount(getCellDecimalValue(row.getCell(4)));
        bill.setMerchantCode(getCellStringValue(row.getCell(5)));
        bill.setMerchantName(getCellStringValue(row.getCell(6)));
        bill.setSettleProvince(getCellStringValue(row.getCell(7)));
        bill.setChargeDate(parseChargeDate(getCellStringValue(row.getCell(8))));
        bill.setRebateAmount(getCellDecimalValue(row.getCell(9)));
        bill.setCustomerFee(getCellDecimalValue(row.getCell(10)));

        if (bill.getChargeDate() != null) {
            bill.setBillMonth(bill.getChargeDate().format(DATE_FORMAT).substring(0, 7));
        }

        return bill;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return null;
        }
    }

    private BigDecimal getCellDecimalValue(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        switch (cell.getCellType()) {
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING:
                try {
                    return new BigDecimal(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return BigDecimal.ZERO;
                }
            default:
                return BigDecimal.ZERO;
        }
    }

    private LocalDate parseChargeDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateStr, DATE_FORMAT);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }
}