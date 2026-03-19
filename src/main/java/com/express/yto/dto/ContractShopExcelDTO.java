package com.express.yto.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2024/10/31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractShopExcelDTO {

    @ExcelProperty("运单号码")
    private String id;

    @ExcelProperty("扫描时间")
    private LocalDate scanDate;

    @ExcelProperty("计费重量（kg）")
    private BigDecimal weight;

    @ExcelProperty("计费省份")
    private String province;

    @ExcelProperty("计费目的地名称")
    private String destination;

    @ExcelProperty("物料业务员名称")
    private String employeeName;

    @ExcelProperty("物料发放客户")
    private String kCode;

    @ExcelProperty("物料发放客户名称")
    private String kName;

    @ExcelProperty("物料结算编码")
    private String shopId;

    @ExcelProperty("物料结算名称")
    private String shopName;

    @ExcelProperty("物料类型")
    private String shopType;

    @ExcelProperty("加收")
    private BigDecimal officeExtra;

    @ExcelProperty("快递费")
    private BigDecimal expense;

    @ExcelIgnore
    private Boolean overFlag = false;

    @ExcelIgnore
    private boolean processed;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContractShopExcelDTO that = (ContractShopExcelDTO) o;
        return id.equals(that.id) &&
                weight.equals(that.weight) &&
                province.equals(that.province);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weight, province);
    }
}
