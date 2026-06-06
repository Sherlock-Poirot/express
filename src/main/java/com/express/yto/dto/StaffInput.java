package com.express.yto.dto;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.Data;

/**
 * 员工新增/编辑输入
 */
@Data
public class StaffInput {

    @ApiModelProperty("主键ID（编辑时必填）")
    private Long id;

    @ApiModelProperty("承包区名称")
    private String contractName;

    @ApiModelProperty("姓名")
    private String realName;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("入职日期")
    private LocalDate entryDate;

    @ApiModelProperty("职员类型0.承包区，1.业务员")
    private Integer staffType;
}
