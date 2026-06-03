package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.dto.MonthlyBillSummaryDTO;
import com.express.yto.model.MonthlyBill;
import com.express.yto.model.WaybillDetail;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MonthlyBillMapper extends BaseMapper<MonthlyBill> {

    List<MonthlyBillSummaryDTO> getDirectCustomerData();

    List<MonthlyBillSummaryDTO> getEmployeeData();

    List<MonthlyBillSummaryDTO> getContractLooseData();

    List<MonthlyBillSummaryDTO> getContractTaobaoLimitedData();

    List<MonthlyBillSummaryDTO> getContractSpecialData();

    void deleteByBillMonth(@Param("billMonth") String billMonth);

    List<WaybillDetail> getDirectCustomerDetailList(@Param("billMonth") String billMonth, @Param("customerName") String customerName);

    List<WaybillDetail> getEmployeeLooseDetailList(@Param("billMonth") String billMonth, @Param("customerName") String customerName);

    List<WaybillDetail> getContractLooseDetailList(@Param("billMonth") String billMonth, @Param("contractName") String contractName);

    List<WaybillDetail> getContractTaobaoDetailList(@Param("billMonth") String billMonth, @Param("empName") String empName);

    List<WaybillDetail> getContractLimitedDetailList(@Param("billMonth") String billMonth, @Param("empName") String empName);

    List<MonthlyBillSummaryDTO> getContractSpecialCustomerList();

    List<WaybillDetail> getContractSpecialDetailList(@Param("billMonth") String billMonth, @Param("custName") String custName);
}