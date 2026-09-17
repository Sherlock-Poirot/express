package com.express.yto.enums;

/**
 * 账单工作流步骤状态枚举
 * WAITING → RUNNING → SUCCESS/PASSED 或 FAILED（FAILED修复后可重试）
 * @author Detective
 * @date Created in 2026/9/16
 */
public enum WaybillFlowStatus {

    WAITING("WAITING", "待执行"),
    RUNNING("RUNNING", "执行中"),
    SUCCESS("SUCCESS", "已完成"),
    PASSED("PASSED", "人工确认通过"),
    FAILED("FAILED", "执行失败"),
    SKIPPED("SKIPPED", "已跳过");

    /** 英文状态值 */
    private final String code;
    /** 中文名称 */
    private final String desc;

    WaybillFlowStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
