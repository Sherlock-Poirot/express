package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 面单流水明细导入 DTO
 * 来源网点名称、目的网点为原始合并字段，导入时需按"|"切割拆分
 * @author Detective
 * @date Created in 2026/10/15
 */
@Data
public class RechargeFlowExcelDTO {

    @ExcelProperty("来源网点名称")
    private String fromShopName;

    @ExcelProperty("目的网点")
    private String toShopName;

    @ExcelProperty("操作类型")
    private String operateType;

    @ExcelProperty("充值平台")
    private String rechargePlatform;

    @ExcelProperty("库存")
    private Integer stock;

    @ExcelProperty("操作数量")
    private Integer operateQty;

    @ExcelProperty("操作后库存")
    private Integer afterStock;

    @ExcelProperty("操作时间")
    private LocalDateTime operateTime;

    @ExcelProperty("操作人")
    private String operator;

    @ExcelProperty("备注")
    private String remark;
}
