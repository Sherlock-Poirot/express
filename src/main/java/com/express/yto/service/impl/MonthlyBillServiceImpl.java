package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.MonthlyBillMapper;
import com.express.yto.dto.MonthlyBillSearchInput;
import com.express.yto.dto.MonthlyBillSummaryDTO;
import com.express.yto.model.MonthlyBill;
import com.express.yto.service.MonthlyBillService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MonthlyBillServiceImpl extends ServiceImpl<MonthlyBillMapper, MonthlyBill> implements MonthlyBillService {

    @Autowired
    private MonthlyBillMapper monthlyBillMapper;

    @Override
    public IPage<MonthlyBill> search(MonthlyBillSearchInput input) {
        Page<MonthlyBill> page = new Page<>(input.getPageNo(), input.getPageSize());
        QueryWrapper<MonthlyBill> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time");

        if ("time".equals(input.getDimensionType())) {
            if (StringUtils.isNotBlank(input.getBillMonth())) {
                qw.eq("bill_month", input.getBillMonth());
            }
        } else if ("customer".equals(input.getDimensionType())) {
            if (StringUtils.isNotBlank(input.getCustomerName())) {
                qw.like("cust_name", input.getCustomerName());
            }
        }

        return monthlyBillMapper.selectPage(page, qw);
    }

    @Override
    public void updateBill(MonthlyBill input) {
        input.setUpdateTime(LocalDateTime.now());
        monthlyBillMapper.updateById(input);
    }

    @Override
    @Transactional
    public void generateSummaryBill(String billMonth) {
        monthlyBillMapper.deleteByBillMonth(billMonth);

        List<MonthlyBill> billList = new ArrayList<>();

        List<MonthlyBillSummaryDTO> directCustomerList = monthlyBillMapper.getDirectCustomerData();
        for (MonthlyBillSummaryDTO dto : directCustomerList) {
            billList.add(MonthlyBill.builder()
                    .billMonth(billMonth)
                    .custName(dto.getCustName())
                    .code(dto.getCode())
                    .receiveCount(dto.getReceiveCount())
                    .avgWeight(dto.getAvgWeight())
                    .receivableAmount(dto.getReceivableAmount())
                    .type(0)
                    .build());
        }

        List<MonthlyBillSummaryDTO> employeeList = monthlyBillMapper.getEmployeeData();
        for (MonthlyBillSummaryDTO dto : employeeList) {
            billList.add(MonthlyBill.builder()
                    .billMonth(billMonth)
                    .custName(dto.getCustName())
                    .code(dto.getCode())
                    .receiveCount(dto.getReceiveCount())
                    .avgWeight(dto.getAvgWeight())
                    .receivableAmount(dto.getReceivableAmount())
                    .type(1)
                    .build());
        }

        List<MonthlyBillSummaryDTO> contractLooseList = monthlyBillMapper.getContractLooseData();
        for (MonthlyBillSummaryDTO dto : contractLooseList) {
            billList.add(MonthlyBill.builder()
                    .billMonth(billMonth)
                    .custName(dto.getCustName() + dto.getCustType())
                    .code(dto.getCode())
                    .receiveCount(dto.getReceiveCount())
                    .avgWeight(dto.getAvgWeight())
                    .receivableAmount(dto.getReceivableAmount())
                    .type(2)
                    .build());
        }

        List<MonthlyBillSummaryDTO> contractTaobaoLimitedList = monthlyBillMapper.getContractTaobaoLimitedData();
        for (MonthlyBillSummaryDTO dto : contractTaobaoLimitedList) {
            billList.add(MonthlyBill.builder()
                    .billMonth(billMonth)
                    .custName(dto.getCustName() + dto.getCustType())
                    .code(dto.getCode())
                    .receiveCount(dto.getReceiveCount())
                    .avgWeight(dto.getAvgWeight())
                    .receivableAmount(dto.getReceivableAmount())
                    .type(2)
                    .build());
        }

        List<MonthlyBillSummaryDTO> contractSpecialList = monthlyBillMapper.getContractSpecialData();
        for (MonthlyBillSummaryDTO dto : contractSpecialList) {
            billList.add(MonthlyBill.builder()
                    .billMonth(billMonth)
                    .custName(dto.getEmpName() + dto.getCustType() + "-" + dto.getCustName())
                    .code(dto.getCode())
                    .receiveCount(dto.getReceiveCount())
                    .avgWeight(dto.getAvgWeight())
                    .receivableAmount(dto.getReceivableAmount())
                    .type(2)
                    .build());
        }

        if (!billList.isEmpty()) {
            monthlyBillMapper.insert(billList);
        }
    }
}