package com.express.yto.dto;

import io.swagger.annotations.ApiModelProperty;
import java.time.YearMonth;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
@Data
public class DealDataInput {

    /**
     * 文件读取路径
     */
    private String readPath;

    /**
     * 导出路径
     */
    private String exportPath;

    /**
     * 是否是春节
     */
    @ApiModelProperty(value = "是否春节", example = "false", dataType = "boolean")
    private Boolean springFestival = false;

    /**
     * 公司id
     */
    private String companyId;

    /**
     * 月份
     */
    @ApiModelProperty(value = "月份")
    private String month;


}
