package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.express.yto.dao.FixedFeeMapper;
import com.express.yto.dao.OverFeeMapper;
import com.express.yto.dao.PrepaymentMapper;
import com.express.yto.dto.CustomerIdAndTimeDTO;
import com.express.yto.dto.FixedExcelDTO;
import com.express.yto.dto.OverExcelDTO;
import com.express.yto.dto.PrepayExcelDTO;
import com.express.yto.listener.FixedFeeListener;
import com.express.yto.listener.OverFeeListener;
import com.express.yto.listener.PrepayListener;
import com.express.yto.model.FixedFee;
import com.express.yto.model.OverFee;
import com.express.yto.model.Prepayment;
import com.express.yto.service.FixedOverPrepayService;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Detective
 * @date Created in 2026/3/17
 */
@Service
public class FixedOverPrepayServiceImpl implements FixedOverPrepayService {

    @Autowired
    private FixedFeeMapper fixedFeeMapper;

    @Autowired
    private OverFeeMapper overFeeMapper;

    @Autowired
    private PrepaymentMapper prepaymentMapper;

    @Override
    @Transactional
    public void importExcel(String filePath) {
        // 1. 初始化监听器
        FixedFeeListener fixedFeeListener = new FixedFeeListener();
        OverFeeListener overFeeListener = new OverFeeListener();
        PrepayListener prepayListener = new PrepayListener();

        // 2. 构建Excel读取器
        ExcelReader excelReader = EasyExcel.read(filePath).build();

        // 3. 读取Sheet1（固定重量价格，索引从0开始）
        ReadSheet readSheet1 = EasyExcel.readSheet(0)
                .head(FixedExcelDTO.class)
                .registerReadListener(fixedFeeListener)
                .build();

        // 4. 读取Sheet2（续重价格，索引1）
        ReadSheet readSheet2 = EasyExcel.readSheet(1)
                .head(OverExcelDTO.class)
                .registerReadListener(overFeeListener)
                .build();

        // 5. 读取Sheet3（预付款，索引2）
        ReadSheet readSheet3 = EasyExcel.readSheet(2)
                .head(PrepayExcelDTO.class)
                .registerReadListener(prepayListener)
                .build();

        // 6. 一次性执行所有 sheet 的读取
        excelReader.read(readSheet1, readSheet2, readSheet3);

        List<FixedExcelDTO> fixedExcelList = fixedFeeListener.getFixedFeeList();
        List<OverExcelDTO> overExcelList = overFeeListener.getOverExcelList();
        List<PrepayExcelDTO> prepayExcelList = prepayListener.getPrepayExcelList();

        // 7. 更新t_fixed_fee表
        List<CustomerIdAndTimeDTO> fixedUpdateList = new ArrayList<>();
        Map<String, LocalDate> fixedUpdateMap = new HashMap<>();
        List<FixedFee> fixedFeeList = new ArrayList<>();
        for (FixedExcelDTO dto : fixedExcelList) {
            fixedUpdateList.add(CustomerIdAndTimeDTO.builder().customerId(dto.getCode()).startTime(
                    fixedUpdateMap.computeIfAbsent(dto.getCode(), k -> dto.getStartTime())
            ).build());
            FixedFee fixedFee = new FixedFee();
            BeanUtils.copyProperties(dto, fixedFee);
            fixedFeeList.add(fixedFee);
        }
        fixedUpdateList = fixedUpdateList.stream().distinct().collect(Collectors.toList());
        fixedFeeMapper.updateBatch(fixedUpdateList);
        // 8. 新增t_fixed_fee表
        fixedFeeMapper.insert(fixedFeeList);

        // 9. 更新t_over_fee表
        List<CustomerIdAndTimeDTO> overUpdateList = new ArrayList<>();
        Map<String, LocalDate> overUpdateMap = new HashMap<>();
        List<OverFee> overFeeList = new ArrayList<>();
        for (OverExcelDTO dto : overExcelList) {
            overUpdateList.add(CustomerIdAndTimeDTO.builder().customerId(dto.getCode()).startTime(
                    overUpdateMap.computeIfAbsent(dto.getCode(), k -> dto.getStartTime())
            ).build());
            OverFee overFee = new OverFee();
            BeanUtils.copyProperties(dto, overFee);
            overFeeList.add(overFee);
        }
        overUpdateList = overUpdateList.stream().distinct().collect(Collectors.toList());
        overFeeMapper.updateBatch(overUpdateList);

        // 10. 新增t_over_fee表
        overFeeMapper.insert(overFeeList);

        // 11. 更新t_prepayment
        List<CustomerIdAndTimeDTO> preUpdateList = new ArrayList<>();
        Map<String, LocalDate> preUpdateMap = new HashMap<>();
        List<Prepayment> prepaymentList = new ArrayList<>();
        for (PrepayExcelDTO dto : prepayExcelList) {
            preUpdateList.add(CustomerIdAndTimeDTO.builder().customerId(dto.getCode()).startTime(
                    preUpdateMap.computeIfAbsent(dto.getCode(), k -> dto.getStartTime())
            ).build());
            Prepayment prepayment = new Prepayment();
            BeanUtils.copyProperties(dto, prepayment);
            prepaymentList.add(prepayment);
        }
        preUpdateList = preUpdateList.stream().distinct().collect(Collectors.toList());
        prepaymentMapper.updateBatch(preUpdateList);

        // 12. 新增t_prepayment
        prepaymentMapper.insert(prepaymentList);

    }
}
