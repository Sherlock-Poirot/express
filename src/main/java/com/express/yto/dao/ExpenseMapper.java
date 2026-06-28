package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.dto.ExpenseTypeSummaryDTO;
import com.express.yto.model.Expense;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ExpenseMapper extends BaseMapper<Expense> {

    List<ExpenseTypeSummaryDTO> sumGroupByExpenseType();

    List<ExpenseTypeSummaryDTO> sumGroupByExpenseTypeAndMonth(@Param("month") String month);

    BigDecimal sumTotalByMonth(@Param("month") String month);
}
