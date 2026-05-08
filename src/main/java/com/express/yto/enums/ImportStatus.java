package com.express.yto.enums;

/**
 * @author Detective
 * @date Created in 2026/5/6
 */
public enum ImportStatus {

    RUNNING("RUNNING", "执行中"),
    SUCCESS("SUCCESS", "已完成"),
    FAILED("FAILED", "执行失败");

    // 英文状态值
    private final String code;
    // 中文名称
    private final String desc;

    ImportStatus(String code, String desc) {
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
