package com.express.yto.service.impl;

import com.express.yto.enums.CashFlowPlatformEnum;
import com.express.yto.model.CashFlowDetail;
import com.express.yto.service.CashFlowImportHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付宝资金流水导入处理器
 * @author Detective
 * @date Created in 2026/10/16
 */
@Component
public class AlipayCashFlowHandler implements CashFlowImportHandler {

    @Override
    public CashFlowPlatformEnum getType() {
        return CashFlowPlatformEnum.ALIPAY;
    }

    @Override
    public List<CashFlowDetail> parse(MultipartFile file) {
        // TODO 支付宝流水文件解析：等待具体文件格式后实现（表头行数、列映射、字段清洗）
        return new ArrayList<>();
    }
}
