package com.express.yto.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2025/9/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixedFeeMiniDTO {

    /**
     * 地区 1，2，3，4，5区
     */
    private Integer area;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 费用
     */
    private BigDecimal fee;
}
