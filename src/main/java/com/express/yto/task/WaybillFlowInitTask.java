package com.express.yto.task;

import com.express.yto.service.WaybillFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 账单工作流月度初始化任务
 * 每月1号凌晨1点为当月创建5个步骤记录（WAITING态）：
 * 导入原始账单 → 导入重量差异数据 → 清洗运单数据 → 校验运单数据 → 计算账单
 * 防重机制：依赖 t_waybill_flow_step 的 uk_bill_month_step 唯一键 + INSERT IGNORE，
 * 多实例并发/重复执行时由数据库唯一键保证幂等，无需额外分布式锁组件
 * @author Detective
 * @date Created in 2026/9/16
 */
@Slf4j
@Component
public class WaybillFlowInitTask {

    /** 账单月份格式 */
    private static final DateTimeFormatter BILL_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private WaybillFlowService waybillFlowService;

    /**
     * cron: 0 0 1 1 * ? 表示每月1号01:00:00执行（避开0点的数据清理任务）
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void initMonthlyFlow() {
        String billMonth = LocalDate.now().format(BILL_MONTH_FORMAT);
        log.info("======== 开始初始化账单工作流[{}] ========", billMonth);
        try {
            int count = waybillFlowService.initMonthlyFlow(billMonth);
            log.info("======== 账单工作流[{}]初始化完成，新增步骤记录 {} 条 ========", billMonth, count);
        } catch (Exception e) {
            log.error("======== 账单工作流[{}]初始化失败 ========", billMonth, e);
        }
    }
}
