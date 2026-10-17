package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.express.yto.enums.CashFlowPlatformEnum;
import com.express.yto.exception.BusinessException;
import com.express.yto.listener.cashflow.WeChatCashFlowListener;
import com.express.yto.model.CashFlowDetail;
import com.express.yto.service.CashFlowImportHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 微信资金流水导入处理器
 * 只负责组装读取流程，具体的表头识别与行解析见 {@link WeChatCashFlowListener}
 * @author Detective
 * @date Created in 2026/10/16
 */
@Slf4j
@Component
public class WeChatCashFlowHandler implements CashFlowImportHandler {

    @Override
    public CashFlowPlatformEnum getType() {
        return CashFlowPlatformEnum.WECHAT;
    }

    @Override
    public List<CashFlowDetail> parse(MultipartFile file) {
        WeChatCashFlowListener listener = new WeChatCashFlowListener();
        try {
            EasyExcel.read(file.getInputStream(), listener)
                    .excelType(resolveExcelType(file.getOriginalFilename()))
                    .sheet()
                    .headRowNumber(WeChatCashFlowListener.HEAD_ROW_NUMBER)
                    .doRead();
        } catch (IOException e) {
            log.error("读取微信流水文件失败", e);
            throw new BusinessException("读取文件失败");
        }
        return listener.getDetailList();
    }

    /**
     * 微信账单可能是.csv或转换后的.xlsx/.xls，按文件名指定解析类型；
     * 不传时EasyExcel按xlsx解析
     */
    private ExcelTypeEnum resolveExcelType(String fileName) {
        if (fileName == null) {
            return ExcelTypeEnum.XLSX;
        }
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".csv")) {
            return ExcelTypeEnum.CSV;
        }
        if (lower.endsWith(".xls")) {
            return ExcelTypeEnum.XLS;
        }
        return ExcelTypeEnum.XLSX;
    }
}
