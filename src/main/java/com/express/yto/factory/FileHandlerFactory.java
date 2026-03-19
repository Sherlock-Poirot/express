package com.express.yto.factory;

import com.express.yto.service.ExcelFileHandler;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Detective
 * @date Created in 2025/9/23
 */
@Component
public class FileHandlerFactory {

    // 注入所有实现了FileHandler接口的Bean（Spring自动收集）
    @Autowired
    private List<ExcelFileHandler> handlers;

    // 根据文件获取处理器
    public ExcelFileHandler getHandler(String fileName) {
        for (ExcelFileHandler handler : handlers) {
            if (handler.supports(fileName)) {
                return handler;
            }
        }
        throw new IllegalArgumentException("未找到处理器：" + fileName);
    }
}
