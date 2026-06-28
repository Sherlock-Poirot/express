package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.ExpenseMapper;
import com.express.yto.dto.ExpenseExportDTO;
import com.express.yto.dto.ExpenseSummaryDTO;
import com.express.yto.dto.ExpenseTypeSummaryDTO;
import com.express.yto.model.Expense;
import com.express.yto.model.SysDictItem;
import com.express.yto.service.ExpenseService;
import com.express.yto.service.SysDictItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报销管理服务实现类
 */
@Slf4j
@Service
public class ExpenseServiceImpl extends ServiceImpl<ExpenseMapper, Expense> implements ExpenseService {

    @Autowired
    private SysDictItemService dictItemService;

    @Override
    public Expense createExpense(Expense expense) {
        log.info("创建报销记录: {}", expense.getExpenseName());
        save(expense);
        return expense;
    }

    @Override
    public List<Expense> listExpenses(Integer expenseType, String month, Integer status, String applicant, Integer transferType) {
        QueryWrapper<Expense> queryWrapper = new QueryWrapper<>();
        if (expenseType != null) {
            queryWrapper.eq("expense_type", expenseType);
        }
        if (month != null && !month.isEmpty()) {
            queryWrapper.eq("month", month);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (applicant != null && !applicant.isEmpty()) {
            queryWrapper.like("applicant", applicant);
        }
        if (transferType != null) {
            queryWrapper.eq("transfer_type", transferType);
        }
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Override
    public Expense updateExpense(Long id, Expense expense) {
        log.info("更新报销记录: id={}", id);
        expense.setId(id);
        updateById(expense);
        return getById(id);
    }

    @Override
    public boolean deleteExpense(Long id) {
        log.info("删除报销记录: id={}", id);
        return removeById(id);
    }

    @Override
    public boolean deleteExpenseBatch(List<Long> ids) {
        log.info("批量删除报销记录: ids={}", ids);
        if (ids == null || ids.isEmpty()) {
            log.warn("批量删除失败：ID列表为空");
            return false;
        }
        return removeByIds(ids);
    }

    @Override
    public boolean auditExpense(Long id, Integer status) {
        log.info("审核报销记录: id={}, status={}", id, status);
        Expense expense = new Expense();
        expense.setId(id);
        expense.setStatus(status);
        return updateById(expense);
    }

    @Override
    public boolean confirmTransfer(Long id, Integer transferType) {
        log.info("确认转账: id={}, transferType={}", id, transferType);
        Expense expense = new Expense();
        expense.setId(id);
        expense.setTransferType(transferType);
        expense.setTransferTime(LocalDate.now());
        return updateById(expense);
    }

    @Override
    public BigDecimal sumAllExpensesByMonth(String month) {
        BigDecimal sum = baseMapper.sumTotalByMonth(month);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public List<ExpenseTypeSummaryDTO> sumGroupByExpenseType() {
        return baseMapper.sumGroupByExpenseType();
    }

    @Override
    public List<ExpenseTypeSummaryDTO> sumGroupByExpenseTypeByMonth(String month) {
        return baseMapper.sumGroupByExpenseTypeAndMonth(month);
    }

    @Override
    public ExpenseSummaryDTO getExpenseSummary(String month) {
        ExpenseSummaryDTO summary = new ExpenseSummaryDTO();
        summary.setMonth(month);

        BigDecimal totalAmount = sumAllExpensesByMonth(month);
        summary.setTotalAmount(totalAmount);

        List<ExpenseTypeSummaryDTO> typeSummaryList = sumGroupByExpenseTypeByMonth(month);
        summary.setTypeSummaryList(typeSummaryList);

        log.info("报销汇总: 月份={}, 总金额={}, 类型数量={}", month, totalAmount, typeSummaryList.size());

        return summary;
    }

    @Override
    public void exportExpenses(Integer expenseType, String month, Integer status, String applicant, Integer transferType, OutputStream outputStream) {
        log.info("导出报销数据: expenseType={}, month={}, status={}, applicant={}, transferType={}",
                expenseType, month, status, applicant, transferType);

        // 查询数据
        List<Expense> expenses = listExpenses(expenseType, month, status, applicant, transferType);

        // 获取字典映射
        Map<Integer, String> expenseTypeMap = getDictItemMap("expense_type");
        Map<Integer, String> statusMap = getDictItemMap("audit_status");
        Map<Integer, String> transferTypeMap = getDictItemMap("transfer_type");

        // 转换为导出DTO
        List<ExpenseExportDTO> exportList = expenses.stream()
                .map(expense -> ExpenseExportDTO.builder()
                        .expenseTypeName(expenseTypeMap.get(expense.getExpenseType()))
                        .expenseName(expense.getExpenseName())
                        .amount(expense.getAmount())
                        .applicant(expense.getApplicant())
                        .expenseDate(expense.getExpenseDate())
                        .month(expense.getMonth())
                        .statusName(statusMap.get(expense.getStatus()))
                        .transferTime(expense.getTransferTime())
                        .transferTypeName(transferTypeMap.get(expense.getTransferType()))
                        .remark(expense.getRemark())
                        .build())
                .collect(Collectors.toList());

        // 使用EasyExcel导出
        EasyExcel.write(outputStream, ExpenseExportDTO.class)
                .sheet("报销数据")
                .doWrite(exportList);

        log.info("导出报销数据完成，共{}条", exportList.size());
    }

    /**
     * 获取字典项映射（值->标签）
     * @param dictCode 字典编码
     * @return 映射Map
     */
    private Map<Integer, String> getDictItemMap(String dictCode) {
        Map<Integer, String> map = new HashMap<>();
        List<SysDictItem> items = dictItemService.getDictItemsByCode(dictCode);
        for (SysDictItem item : items) {
            try {
                map.put(Integer.parseInt(item.getDictValue()), item.getDictLabel());
            } catch (NumberFormatException e) {
                log.warn("字典值转换失败: dictCode={}, dictValue={}", dictCode, item.getDictValue());
            }
        }
        return map;
    }
}
