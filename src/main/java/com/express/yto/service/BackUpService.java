package com.express.yto.service;

/**
 * @author Detective
 * @date Created in 2026/3/13
 */
public interface BackUpService {

    void backUp();

    void updateInFirstDay();

    /**
     * 每月1号执行：先全量备份三张价格表到_bak表，再删除原表中end_time早于半年前的数据
     * 整个过程在同一事务内，备份或删除任一步失败则全部回滚
     */
    void monthlyBackupAndClean();
}
