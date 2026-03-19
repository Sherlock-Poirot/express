package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.FixedFeeMapper;
import com.express.yto.dto.ExportCustomerFeeInput;
import com.express.yto.dto.ExtraFeeExcelDTO;
import com.express.yto.dto.FixedFeeInsertInput;
import com.express.yto.dto.PrePaymentExcelDTO;
import com.express.yto.model.Customer;
import com.express.yto.model.ExtraFee;
import com.express.yto.model.FixedFee;
import com.express.yto.model.OverFee;
import com.express.yto.model.Prepayment;
import com.express.yto.service.CustomerService;
import com.express.yto.service.ExtraFeeService;
import com.express.yto.service.FixedFeeService;
import com.express.yto.service.OverFeeService;
import com.express.yto.service.PrepaymentService;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
@Service
@Slf4j
public class FixedFeeServiceImpl extends ServiceImpl<FixedFeeMapper, FixedFee> implements FixedFeeService {

    @Autowired
    private FixedFeeMapper mapper;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private FixedFeeService fixedFeeService;

    @Autowired
    private OverFeeService overFeeService;

    @Autowired
    private PrepaymentService prepaymentService;

    @Autowired
    private ExtraFeeService extraFeeService;

    @Override
    public void insertByCustomer(List<FixedFeeInsertInput> list) {
        List<FixedFee> models = new ArrayList<>();
        for (FixedFeeInsertInput input : list) {
            List<FixedFee> subModels = buildModels(input);
            models.addAll(subModels);
        }
        if (CollectionUtils.isNotEmpty(models)) {
            mapper.insert(models);
        }
    }

    @Override
    public void exportByKCode(List<String> kCodeList) {

    }

    @Override
    public void exportSingle(ExportCustomerFeeInput input) {
        String kCode = input.getKCode();

        // 客户信息
        QueryWrapper<Customer> cqWrapper = new QueryWrapper<>();
        cqWrapper.eq("k_code", kCode);
        Customer customer = customerService.getOne(cqWrapper);

        // 预付款
        QueryWrapper<Prepayment> pqWrapper = new QueryWrapper<>();
        pqWrapper.eq("k_code", kCode);
        List<Prepayment> prepaymentList = prepaymentService.list(pqWrapper);

        List<PrePaymentExcelDTO> preExcelList = new ArrayList<>(prepaymentList.size());
        for (Prepayment prepayment : prepaymentList) {
            PrePaymentExcelDTO dto = new PrePaymentExcelDTO();
            BeanUtils.copyProperties(prepayment, dto);
            dto.setKName(customer.getKName());
            preExcelList.add(dto);
        }

        // 固定重量区间价格
        QueryWrapper<FixedFee> fixWrapper = new QueryWrapper<>();
        fixWrapper.eq("k_code", kCode);
        List<FixedFee> fixedFeeList = fixedFeeService.list(fixWrapper);

        // 续重费用
        QueryWrapper<OverFee> overWrapper = new QueryWrapper<>();
        overWrapper.eq("k_code", kCode);
        List<OverFee> overFeeList = overFeeService.list(overWrapper);

        // 地区加收
        QueryWrapper<ExtraFee> eqWrapper = new QueryWrapper<>();
        eqWrapper.eq("k_code", kCode);
        List<ExtraFee> extraFeeList = extraFeeService.list(eqWrapper);

        List<ExtraFeeExcelDTO> extraFeeExcelList = new ArrayList<>(extraFeeList.size());
        for (ExtraFee extraFee : extraFeeList) {
            ExtraFeeExcelDTO dto = new ExtraFeeExcelDTO();
            dto.setKName(customer.getKName());
            BeanUtils.copyProperties(extraFee, dto);
            extraFeeExcelList.add(dto);
        }

        int count = 0;
        try (OutputStream out = new FileOutputStream(input.getExportPath())) {
            ExcelWriter excelWriter = EasyExcel.write(out).build();
            WriteSheet sheet = EasyExcel.writerSheet(customer.getKName()).needHead(false).build();
            WriteTable writeTable0 = EasyExcel.writerTable(0).needHead(Boolean.TRUE).head(PrePaymentExcelDTO.class).build();
            WriteTable writeTable1 = EasyExcel.writerTable(1).needHead(Boolean.TRUE).head(ExtraFeeExcelDTO.class).build();
            excelWriter.write(preExcelList, sheet, writeTable0);
            excelWriter.write(extraFeeExcelList, sheet, writeTable1);
            excelWriter.finish();
        } catch (Exception e) {
            log.error("导出失败:{}", e.getMessage());
        }
    }


    private List<FixedFee> buildModels(FixedFeeInsertInput input) {
        List<FixedFee> models = new ArrayList<>();
        for (Integer area : input.getAreas()) {
            FixedFee model = FixedFee.builder().kCode(input.getKCode()).area(area).weight(input.getWeight())
                    .fee(input.getFee()).startTime(input.getStartTime()).endTime(input.getEndTime()).build();
            models.add(model);
        }
        return models;
    }
}
