package com.express.yto.controller;

import com.express.yto.dto.ExpenseSummaryDTO;
import com.express.yto.dto.ExpenseTypeSummaryDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.model.Expense;
import com.express.yto.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 报销管理控制器
 * 财务管理模块 - 报销管理相关接口
 */
@RestController
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    /**
     * 创建报销记录
     * @param expense 报销信息
     * @return 报销记录
     */
    @PostMapping("/create")
    public RestResult<Expense> createExpense(@RequestBody Expense expense) {
        Expense result = expenseService.createExpense(expense);
        return RestResult.ok(result);
    }

    /**
     * 查询报销列表（支持多条件筛选）
     * @param expenseType 报销类型（可选）
     * @param month 月份（可选）
     * @param status 审核状态（可选）
     * @param applicant 申请人（可选，模糊匹配）
     * @param transferType 转账方式（可选）
     * @return 报销列表
     */
    @GetMapping("/list")
    public RestResult<List<Expense>> listExpenses(
            @RequestParam(value = "expenseType", required = false) Integer expenseType,
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "applicant", required = false) String applicant,
            @RequestParam(value = "transferType", required = false) Integer transferType) {
        List<Expense> result = expenseService.listExpenses(expenseType, month, status, applicant, transferType);
        return RestResult.ok(result);
    }

    /**
     * 更新报销记录
     * @param id 报销ID
     * @param expense 报销信息
     * @return 更新后的报销记录
     */
    @PutMapping("/{id}")
    public RestResult<Expense> updateExpense(@PathVariable Long id, @RequestBody Expense expense) {
        Expense result = expenseService.updateExpense(id, expense);
        return RestResult.ok(result);
    }

    /**
     * 删除报销记录
     * @param id 报销ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public RestResult<Boolean> deleteExpense(@PathVariable Long id) {
        boolean result = expenseService.deleteExpense(id);
        return RestResult.ok(result);
    }

    /**
     * 批量删除报销记录
     * @param ids 报销ID列表
     * @return 是否成功
     */
    @DeleteMapping("/batch/delete")
    public RestResult<Boolean> deleteExpenseBatch(@RequestBody List<Long> ids) {
        boolean result = expenseService.deleteExpenseBatch(ids);
        return RestResult.ok(result);
    }

    /**
     * 审核报销
     * @param id 报销ID
     * @param status 审核状态（1-通过，2-拒绝）
     * @return 是否成功
     */
    @PostMapping("/audit/{id}")
    public RestResult<Boolean> auditExpense(@PathVariable Long id, @RequestParam Integer status) {
        boolean result = expenseService.auditExpense(id, status);
        return RestResult.ok(result);
    }

    /**
     * 确认转账（自动记录转账时间）
     * @param id 报销ID
     * @param transferType 转账方式（关联字典transfer_type）
     * @return 是否成功
     */
    @PostMapping("/transfer/{id}")
    public RestResult<Boolean> confirmTransfer(@PathVariable Long id, @RequestParam Integer transferType) {
        boolean result = expenseService.confirmTransfer(id, transferType);
        return RestResult.ok(result);
    }

    /**
     * 获取报销汇总信息
     * @param month 月份
     * @return 汇总信息（包含总金额和类型统计）
     */
    @GetMapping("/summary/{month}")
    public RestResult<ExpenseSummaryDTO> getExpenseSummary(@PathVariable String month) {
        ExpenseSummaryDTO result = expenseService.getExpenseSummary(month);
        return RestResult.ok(result);
    }

    /**
     * 按报销类型分组统计
     * @param month 月份
     * @return 各类型报销金额统计
     */
    @GetMapping("/sum/type/{month}")
    public RestResult<List<ExpenseTypeSummaryDTO>> sumGroupByExpenseTypeByMonth(@PathVariable String month) {
        List<ExpenseTypeSummaryDTO> result = expenseService.sumGroupByExpenseTypeByMonth(month);
        return RestResult.ok(result);
    }

    /**
     * 统计指定月份报销总金额
     * @param month 月份
     * @return 总金额
     */
    @GetMapping("/sum/total/{month}")
    public RestResult<BigDecimal> sumAllExpensesByMonth(@PathVariable String month) {
        BigDecimal result = expenseService.sumAllExpensesByMonth(month);
        return RestResult.ok(result);
    }

    /**
     * 导出报销数据（支持多条件筛选）
     * @param expenseType 报销类型（可选）
     * @param month 月份（可选）
     * @param status 审核状态（可选）
     * @param applicant 申请人（可选）
     * @param transferType 转账方式（可选）
     * @param response HTTP响应对象
     */
    @GetMapping("/export")
    public void exportExpenses(
            @RequestParam(value = "expenseType", required = false) Integer expenseType,
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "applicant", required = false) String applicant,
            @RequestParam(value = "transferType", required = false) Integer transferType,
            HttpServletResponse response) {
        try {
            String fileName = "报销数据_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");

            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Content-Transfer-Encoding", "binary");

            OutputStream outputStream = response.getOutputStream();
            expenseService.exportExpenses(expenseType, month, status, applicant, transferType, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败: " + e.getMessage());
            } catch (IOException ioException) {
                // ignore
            }
        }
    }
}
