package com.express.yto.util;

import static com.express.yto.util.AreaUtil.isThisArea;

import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.model.OverFee;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * @author Detective
 * @date Created in 2026/3/20
 */
public class BillDealUtil {

    // 工具方法：根据日期快速匹配预付款（核心优化：O(1)匹配）
    public static BigDecimal getPrepaymentByDate(LocalDate targetDate, Map<LocalDateRange, BigDecimal> prepaymentRangeMap) {
        for (Map.Entry<LocalDateRange, BigDecimal> entry : prepaymentRangeMap.entrySet()) {
            LocalDateRange range = entry.getKey();
            if (isDateInRange(targetDate, range.getStart(), range.getEnd())) {
                return entry.getValue();
            }
        }
        return BigDecimal.ZERO;
    }

    // 工具方法：日期区间判断（封装后减少重复代码）
    public static boolean isDateInRange(LocalDate target, LocalDate start, LocalDate end) {
        return (target.isAfter(start) || target.isEqual(start)) && target.isBefore(end);
    }

    /**
     * 判断数据是否符合当前超重的数据
     *
     * @param dto
     * @param overFee
     * @param flagHaiNan
     * @return
     */
    public static boolean judgeOver(Map<String, Integer> areaMap, ContractShopExcelDTO dto, OverFee overFee, Boolean flagHaiNan) {
        return (dto.getScanDate().isAfter(overFee.getStartTime()) || dto.getScanDate().equals(overFee.getStartTime()))
                && dto.getScanDate().isBefore(overFee.getEndTime()) && isThisArea(areaMap, dto.getProvince(), overFee.getArea(),
                flagHaiNan);
    }
}
