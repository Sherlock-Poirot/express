package com.express.yto.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Detective
 * @date Created in 2026/3/22
 * <p>
 * 超重算法的工具类
 */
public class CalculateUtil {

    /**
     * 续重模式
     */
    public static final Integer ADDITIONAL = 1;

    /**
     * 凑整模式
     */
    public static final Integer ROUNDUP = 2;

    /**
     * 实际模式
     */
    public static final Integer ACTUAL = 3;

    /**
     * 1.续重重量计算模式 （凑整重量-1） * 续重费用 + 面单费 - 预付款
     * <p>
     * 2.凑整整理计算模式  凑整整理 * 续重费用 + 面单费 - 预付款
     * <p>
     * 3.实际重量计算模式  实际重量 * 续重费用 + 面单费 - 预付款
     *
     * @param weight 重量
     * @param fee    续重费用
     * @param label  面单费
     * @param model  模式
     * @return 运单快递费
     */
    public static BigDecimal calculate(BigDecimal prepay, BigDecimal weight, BigDecimal fee, BigDecimal label,
            Integer model) {
        BigDecimal cw;
        if (model.equals(ADDITIONAL)) {
            // 续重模式
            cw = weight.setScale(0, RoundingMode.CEILING).subtract(BigDecimal.ONE);
        } else if (model.equals(ROUNDUP)) {
            // 凑整模式
            cw = weight.setScale(0, RoundingMode.CEILING);
        } else {
            // 实际模式
            cw = weight;
        }
        return cw.multiply(fee).add(label).subtract(prepay);
    }
}
