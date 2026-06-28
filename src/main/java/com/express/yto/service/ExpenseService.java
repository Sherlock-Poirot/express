package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.ExpenseSummaryDTO;
import com.express.yto.dto.ExpenseTypeSummaryDTO;
import com.express.yto.model.Expense;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * 报销管理服务接口
 */
public interface ExpenseService extends IService<Expense> {

    Expense createExpense(Expense expense);

    List<Expense> listExpenses(Integer expenseType, String month, Integer status, String applicant, Integer transferType);

    Expense updateExpense(Long id, Expense expense);

    boolean deleteExpense(Long id);

    boolean deleteExpenseBatch(List<Long> ids);

    boolean auditExpense(Long id, Integer status);

    boolean confirmTransfer(Long id, Integer transferType);

    BigDecimal sumAllExpensesByMonth(String month);

    List<ExpenseTypeSummaryDTO> sumGroupByExpenseType();

    List<ExpenseTypeSummaryDTO> sumGroupByExpenseTypeByMonth(String month);

    ExpenseSummaryDTO getExpenseSummary(String month);

    void exportExpenses(Integer expenseType, String month, Integer status, String applicant, Integer transferType, OutputStream outputStream);
}
