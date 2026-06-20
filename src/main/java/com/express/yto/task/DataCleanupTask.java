package com.express.yto.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.express.yto.dao.DailyBillMapper;
import com.express.yto.model.DailyBill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 数据清理定时任务
 * 用于定期清理历史数据，保持数据库性能
 */
@Slf4j
@Component
public class DataCleanupTask {

    @Autowired
    private DailyBillMapper dailyBillMapper;

    /**
     * 每天凌晨0点执行，删除一周前的运单数据
     * cron表达式: 0 0 0 * * ? 表示每天0点0分0秒执行
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupOldDailyBillData() {
        log.info("开始执行运单数据清理任务...");

        try {
            // 计算一周前的日期
            LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
            log.info("将删除 charge_date 早于 {} 的数据", oneWeekAgo);

            // 使用MyBatis Plus的条件构造器删除数据
            QueryWrapper<DailyBill> queryWrapper =new QueryWrapper<>();
            queryWrapper.lt("charge_date", oneWeekAgo);

            // 执行删除前先统计要删除的数据量
            Long count = dailyBillMapper.selectCount(queryWrapper);
            log.info("将删除 {} 条历史运单数据", count);

            if (count > 0) {
                // 执行删除
                int deletedCount = dailyBillMapper.delete(queryWrapper);
                log.info("运单数据清理任务完成，成功删除 {} 条数据", deletedCount);
            } else {
                log.info("没有需要清理的历史运单数据");
            }

        } catch (Exception e) {
            log.error("运单数据清理任务执行失败", e);
        }
    }
}
