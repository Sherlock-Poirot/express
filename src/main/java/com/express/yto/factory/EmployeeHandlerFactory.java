package com.express.yto.factory;

import com.express.yto.service.EmployeeBillHandler;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Detective
 * @date Created in 2026/3/20
 * 承包区账单处理工厂
 */
@Component
public class EmployeeHandlerFactory {

    @Autowired
    private List<EmployeeBillHandler> handlers;

    public EmployeeBillHandler getHandler(String companyId){
        for (EmployeeBillHandler handler : handlers) {
            if (handler.supports(companyId)) {
                return handler;
            }
        }
        throw new IllegalArgumentException("未找到处理器：" + companyId);
    }
}
