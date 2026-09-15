package com.express.yto.service;

import com.express.yto.dao.ExportFileMapper;
import com.express.yto.enums.ImportStatus;
import com.express.yto.model.ExportFile;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 账单明细异步导出服务
 * 后台线程生成zip文件到服务器磁盘，完成后回写t_export_file记录状态，
 * 前端通过下载展示页从记录列表取链接下载，避免同步导出HTTP超时
 */
@Slf4j
@Service
public class MonthlyBillExportAsyncService {

    @Autowired
    private MonthlyBillService monthlyBillService;

    @Autowired
    private ExportFileMapper exportFileMapper;

    /**
     * 异步生成账单明细zip
     * @param recordId t_export_file记录ID（创建记录时已写入billMonth/filePath）
     */
    @Async("asyncExecutor")
    public void doExportDetailAsync(Long recordId) {
        ExportFile record = exportFileMapper.selectById(recordId);
        if (record == null) {
            log.error("导出任务记录不存在: {}", recordId);
            return;
        }
        long start = System.currentTimeMillis();
        try {
            Path filePath = Paths.get(record.getFilePath());
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            // 复用现有导出逻辑：把原本写HTTP响应流的zip改为写入磁盘文件
            try (OutputStream out = new FileOutputStream(filePath.toFile())) {
                monthlyBillService.exportAllDetail(record.getBillMonth(), out);
            }

            record.setStatus(ImportStatus.SUCCESS.getCode());
            record.setFileSize(Files.size(filePath));
            record.setUpdateTime(new Date());
            exportFileMapper.updateById(record);
            log.info("账单明细导出完成: {} -> {}，耗时{}ms，大小{}字节",
                    record.getBillMonth(), record.getFilePath(),
                    System.currentTimeMillis() - start, record.getFileSize());
        } catch (Exception e) {
            log.error("账单明细导出失败: {}", record.getBillMonth(), e);
            // 清理可能产生的半截文件，避免下载页拿到损坏的zip
            try {
                Files.deleteIfExists(Paths.get(record.getFilePath()));
            } catch (Exception ignored) {
                // 删除失败不影响状态回写
            }
            record.setStatus(ImportStatus.FAILED.getCode());
            record.setErrorMsg(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            record.setUpdateTime(new Date());
            exportFileMapper.updateById(record);
        }
    }
}
