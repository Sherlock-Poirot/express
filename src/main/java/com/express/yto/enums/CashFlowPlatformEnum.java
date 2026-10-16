package com.express.yto.enums;

/**
 * 资金流水导入平台枚举
 * @author Detective
 * @date Created in 2026/10/16
 */
public enum CashFlowPlatformEnum {

    /** 微信 */
    WECHAT(1, "微信"),

    /** 支付宝 */
    ALIPAY(2, "支付宝"),

    /** 台州银行 */
    TAIZHOU_BANK(3, "台州银行"),

    /** 工商银行 */
    ICBC(4, "工商银行"),

    /** 农商银行 */
    NONG_SHANG(5, "农商银行");

    private final int type;

    private final String name;

    CashFlowPlatformEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据type查找枚举，找不到返回null
     */
    public static CashFlowPlatformEnum of(Integer type) {
        if (type == null) {
            return null;
        }
        for (CashFlowPlatformEnum e : values()) {
            if (e.type == type) {
                return e;
            }
        }
        return null;
    }
}
