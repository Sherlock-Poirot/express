package com.express.yto.enums;

public enum PolicyType {
    BASE_REBATE(1, "基数返利"),
    FIXED_FEE(2, "固定收费"),
    DYNAMIC_REBATE(3, "动态返利");

    private final int code;
    private final String description;

    PolicyType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PolicyType fromCode(int code) {
        for (PolicyType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown policy type code: " + code);
    }
}
