package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.DealDataInput;
import com.express.yto.dto.FixedFeeMiniDTO;
import com.express.yto.factory.EmployeeHandlerFactory;
import com.express.yto.service.EmployeeService;
import com.express.yto.util.AreaUtil;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Detective
 * @date Created in 2025/9/25
 */
@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeHandlerFactory employeeFactory;

    @Override
    public void dealEmployeeBill(DealDataInput input) {
        employeeFactory.getHandler(input.getCompanyId()).handle(input);
    }
}
