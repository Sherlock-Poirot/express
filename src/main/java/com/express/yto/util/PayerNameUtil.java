package com.express.yto.util;

import com.express.yto.enums.CashFlowPlatformEnum;

/**
 * 付款方名称工具
 * @author Detective
 * @date Created in 2026/10/23
 */
public final class PayerNameUtil {

    /** 微信备注预付款前缀：如 S2.45、s120（S+数字[.数字]），前缀随预付款变化，非稳定标识 */
    private static final String PREPAYMENT_PREFIX_REGEX = "(?i)^[a-z]\\d+(\\.\\d+)?";

    /** 归一化后小于该长度的付款方标识区分度不足，回退使用原文 */
    private static final int MIN_NORMALIZED_LENGTH = 2;

    private PayerNameUtil() {
    }

    /**
     * 付款方名称归一化：剥离微信备注里的预付款前缀，返回稳定客户标识；
     * 未命中前缀格式时原样返回（支付宝实名、银行户名不受影响）
     */
    public static String normalizePayerName(String payerName) {
        if (payerName == null || payerName.trim().isEmpty()) {
            return "";
        }
        return payerName.trim().replaceFirst(PREPAYMENT_PREFIX_REGEX, "").trim();
    }

    /**
     * 按平台计算付款方映射键：微信归一化剥离预付款前缀（剥离后为空或过短回退原文），
     * 其他平台直接返回原文。
     * 映射的沉淀、删除、匹配查找必须共用本方法，避免规则漂移
     */
    public static String resolvePayerKey(String payerName, Integer platform) {
        if (payerName == null || payerName.trim().isEmpty()) {
            return "";
        }
        if (platform != null && platform == CashFlowPlatformEnum.WECHAT.getType()) {
            String normalized = normalizePayerName(payerName);
            return (normalized.isEmpty() || normalized.length() < MIN_NORMALIZED_LENGTH)
                    ? payerName.trim() : normalized;
        }
        return payerName.trim();
    }
}
