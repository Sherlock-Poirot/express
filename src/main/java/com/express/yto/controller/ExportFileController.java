package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.enums.ImportStatus;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.ExportFile;
import com.express.yto.service.MonthlyBillService;
import cn.dev33.satoken.annotation.SaCheckPermission;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件下载页面的控制器
 * 展示后台异步生成的导出文件记录（t_export_file），并提供文件下载
 * 记录由 MonthlyBillController 的 /exportDetail/async 接口创建
 * @author Detective
 * @date Created in 2026/9/15
 */
@RestController
@RequestMapping("/exportFile")
@Slf4j
public class ExportFileController {

    @Autowired
    private MonthlyBillService monthlyBillService;

    /**
     * 导出记录列表（下载展示页数据源，最新50条）
     * @return 导出记录集合（含状态/文件大小/时间）
     */
    @GetMapping("/records")
    @SaCheckPermission("download:file")
    public RestResult<List<ExportFile>> records() {
        return RestResult.ok(monthlyBillService.listExportTasks());
    }

    /**
     * 按记录ID下载导出文件
     * 仅状态为SUCCESS的记录可下载
     * @param id 导出记录ID
     */
    @GetMapping("/download/{id}")
    @SaCheckPermission("download:file:download")
    public void download(@PathVariable("id") Long id, HttpServletResponse response) {
        ExportFile record = monthlyBillService.getExportFile(id);
        if (record == null) {
            throw new BusinessException("导出记录不存在");
        }
        if (!ImportStatus.SUCCESS.getCode().equals(record.getStatus())) {
            throw new BusinessException("文件尚未生成完成或生成失败，状态: " + record.getStatus());
        }
        Path filePath = Paths.get(record.getFilePath());
        if (!Files.exists(filePath)) {
            throw new BusinessException("服务器文件已不存在，请重新导出");
        }
        try {
            String encodedFileName = URLEncoder.encode(record.getFileName(), StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            response.reset();
            response.setContentType("application/zip");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName
                    + "\"; filename*=UTF-8''" + encodedFileName);
            response.setContentLengthLong(Files.size(filePath));
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Content-Transfer-Encoding", "binary");

            Files.copy(filePath, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("下载导出文件失败: id={}", id, e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "下载失败: " + e.getMessage());
            } catch (IOException ioException) {
                log.error("设置错误响应失败", ioException);
            }
        }
    }
}
