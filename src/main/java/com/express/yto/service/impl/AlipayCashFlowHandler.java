package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.express.yto.enums.CashFlowPlatformEnum;
import com.express.yto.exception.BusinessException;
import com.express.yto.listener.cashflow.AlipayCashFlowListener;
import com.express.yto.model.CashFlowDetail;
import com.express.yto.service.CashFlowImportHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 支付宝资金流水导入处理器
 * 只负责组装读取流程，具体的表头识别与行解析见 {@link AlipayCashFlowListener}
 * @author Detective
 * @date Created in 2026/10/16
 */
@Slf4j
@Component
public class AlipayCashFlowHandler implements CashFlowImportHandler {

    @Override
    public CashFlowPlatformEnum getType() {
        return CashFlowPlatformEnum.ALIPAY;
    }

    @Override
    public List<CashFlowDetail> parse(MultipartFile file) {
        AlipayCashFlowListener listener = new AlipayCashFlowListener();
        ExcelTypeEnum excelType = resolveExcelType(file.getOriginalFilename());
        try {
            EasyExcel.read(file.getInputStream(), listener)
                    .excelType(excelType)
                    // 支付宝导出的csv为GBK编码，EasyExcel默认按UTF-8解析会表头乱码导致无法识别，仅csv需要指定
                    .charset(excelType == ExcelTypeEnum.CSV ? Charset.forName("GBK") : null)
                    // 标题行由监听器动态识别（说明行数量不固定且csv空行会压缩逻辑行号），禁用EasyExcel默认表头机制
                    .headRowNumber(0)
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            log.error("读取支付宝流水文件失败", e);
            throw new BusinessException("读取文件失败");
        }
        return listener.getDetailList();
    }

    /**
     * 支付宝账单可能是.csv或转换后的.xlsx/.xls，按文件名指定解析类型；
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
