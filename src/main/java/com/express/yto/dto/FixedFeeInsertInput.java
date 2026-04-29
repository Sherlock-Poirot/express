package com.express.yto.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/9/20
 */
@Data
@ApiModel("新增固定重量区间价格（客户版）")
public class FixedFeeInsertInput {

    /**
     * 客户编码
     */
    @ApiModelProperty("客户编码")
    private String code;

    /**
     * 区域集合
     */
    @ApiModelProperty("区域集合")
    private List<Integer> areas;

    /**
     * 重量上限
     */
    @ApiModelProperty("重量上限")
    private BigDecimal weight;

    /**
     * 费用
     */
    @ApiModelProperty("费用")
    private BigDecimal fee;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private LocalDate startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private LocalDate endTime;

}
