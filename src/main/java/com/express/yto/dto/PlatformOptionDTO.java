package com.express.yto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资金流水平台下拉选项
 * 用于前端导入资金流水时选择平台，value与导入接口platform参数一致
 * @author Detective
 * @date Created in 2026/10/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformOptionDTO {

    /** 平台编码（1微信，2支付宝，3台州银行，4工商银行，5农商银行） */
    private Integer value;

    /** 平台名称 */
    private String label;
}
