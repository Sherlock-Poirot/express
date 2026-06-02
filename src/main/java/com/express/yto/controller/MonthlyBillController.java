package com.express.yto.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.MonthlyBillSearchInput;
import com.express.yto.dto.RestResult;
import com.express.yto.model.MonthlyBill;
import com.express.yto.service.MonthlyBillService;
import io.swagger.annotations.ApiOperation;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monthlyBill")
@ApiOperation("月度账单功能")
@Validated
@Slf4j
public class MonthlyBillController {

    @Autowired
    private MonthlyBillService monthlyBillService;

    @ApiOperation("分页查询")
    @PostMapping("/search")
    public RestResult<IPage<MonthlyBill>> search(@RequestBody @Valid MonthlyBillSearchInput input) {
        return RestResult.ok(monthlyBillService.search(input));
    }

    @ApiOperation("编辑")
    @PostMapping("/update")
    public RestResult<String> update(@RequestBody MonthlyBill input) {
        monthlyBillService.updateBill(input);
        return RestResult.ok("操作成功");
    }

    @ApiOperation("详情")
    @GetMapping("/detail")
    public RestResult<MonthlyBill> getDetail(@RequestParam("id") Long id) {
        return RestResult.ok(monthlyBillService.getById(id));
    }

    @ApiOperation("生成汇总账单")
    @PostMapping("/generate")
    public RestResult<String> generate(@RequestParam("billMonth") String billMonth) {
        monthlyBillService.generateSummaryBill(billMonth);
        return RestResult.ok("操作成功");
    }

    @ApiOperation("汇总导出（简化版）")
    @GetMapping("/export")
    public void export(@RequestParam("billMonth") String billMonth, HttpServletResponse response) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            monthlyBillService.exportSummaryByBillMonth(billMonth, outputStream);

            byte[] data = outputStream.toByteArray();
            String fileName = billMonth + "月账单汇总.xlsx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");

            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
            response.setContentLength(data.length);
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Content-Transfer-Encoding", "binary");

            response.getOutputStream().write(data);
            response.getOutputStream().flush();

            log.info("导出账单成功: {}", billMonth);
        } catch (Exception e) {
            log.error("导出账单失败: {}", billMonth, e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败: " + e.getMessage());
            } catch (IOException ioException) {
                log.error("设置错误响应失败", ioException);
            }
        }
    }

    @ApiOperation("导出所有明细")
    @GetMapping("/exportDetail")
    public void exportDetail(
            @RequestParam("billMonth") String billMonth,
            HttpServletResponse response) {
        try {
            String fileName = billMonth + "_明细.zip";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");

            response.reset();
            response.setContentType("application/zip");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Content-Transfer-Encoding", "binary");

            monthlyBillService.exportAllDetail(billMonth, response.getOutputStream());
            response.getOutputStream().flush();

            log.info("导出所有明细成功: billMonth={}", billMonth);
        } catch (Exception e) {
            log.error("导出所有明细失败", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败：" + e.getMessage());
            } catch (IOException ioException) {
                log.error("设置错误响应失败", ioException);
            }
        }
    }
}
