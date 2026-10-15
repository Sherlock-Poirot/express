package com.express.yto.dto;

import lombok.Data;

import java.util.List;

/**
 * 应收-实收多对多匹配入参
 * @author Detective
 * @date Created in 2026/10/15
 */
@Data
public class RechargeCashMatchInput {

    /** 应收流水id列表 */
    private List<Long> rechargeIds;

    /** 实收流水id列表 */
    private List<Long> cashIds;
}
