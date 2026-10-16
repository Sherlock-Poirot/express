package com.express.yto.factory;

import com.express.yto.enums.CashFlowPlatformEnum;
import com.express.yto.exception.BusinessException;
import com.express.yto.service.CashFlowImportHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 资金流水导入处理器工厂
 * Spring自动收集所有 CashFlowImportHandler 实现，按平台类型分发
 * @author Detective
 * @date Created in 2026/10/16
 */
@Component
public class CashFlowImportFactory {

    // 注入所有实现了CashFlowImportHandler接口的Bean（Spring自动收集）
    @Autowired
    private List<CashFlowImportHandler> handlers;

    /**
     * 根据平台类型获取对应的导入处理器
     * @param type 平台类型（1微信，2支付宝，3台州银行，4工商银行，5农商银行）
     */
    public CashFlowImportHandler getHandler(Integer type) {
        CashFlowPlatformEnum platform = CashFlowPlatformEnum.of(type);
        if (platform == null) {
            throw new BusinessException("不支持的导入类型：" + type);
        }
        for (CashFlowImportHandler handler : handlers) {
            if (handler.getType() == platform) {
                return handler;
            }
        }
        throw new BusinessException("平台[" + platform.getName() + "]的导入处理器未注册");
    }
}
