package com.express.yto.service;

import com.express.yto.enums.CashFlowPlatformEnum;
import com.express.yto.model.CashFlowDetail;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 资金流水导入处理器接口
 * 每种平台（微信/支付宝/各银行）的文件格式不同，由各自实现类负责解析，
 * 但解析结果必须统一为 CashFlowDetail 集合
 * @author Detective
 * @date Created in 2026/10/16
 */
public interface CashFlowImportHandler {

    /**
     * 当前处理器支持的平台类型（工厂分发依据）
     */
    CashFlowPlatformEnum getType();

    /**
     * 解析上传的流水文件为统一的资金流水明细集合
     * @param file 上传的Excel/CSV文件
     * @return 资金流水明细集合
     */
    List<CashFlowDetail> parse(MultipartFile file);
}
