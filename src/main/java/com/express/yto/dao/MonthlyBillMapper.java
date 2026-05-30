package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.dto.MonthlyBillSummaryDTO;
import com.express.yto.model.MonthlyBill;
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
}