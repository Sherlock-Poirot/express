package com.express.yto.task;

import com.express.yto.service.BackUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 价格表月度备份与清理任务
 * 每月1号早上6点：t_fixed_fee/t_over_fee/t_prepayment 全量备份到对应_bak表，
 * 并删除原表中end_time早于半年前的历史数据
 */
@Slf4j
@Component
public class PriceBackupTask {

    @Autowired
    private BackUpService backUpService;

    /**
     * cron: 0 0 6 1 * ? 表示每月1号06:00:00执行
     */
    @Scheduled(cron = "0 0 6 1 * ?")
    public void monthlyBackupAndClean() {
        log.info("======== 开始执行价格表月度备份与清理任务 ========");
        try {
            backUpService.monthlyBackupAndClean();
            log.info("======== 价格表月度备份与清理任务执行成功 ========");
        } catch (Exception e) {
            // 事务已整体回滚（bak表与原表均保持任务前状态），记录日志等待人工处理
            log.error("======== 价格表月度备份与清理任务执行失败，事务已回滚 ========", e);
        }
    }
}
