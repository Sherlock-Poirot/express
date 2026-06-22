package com.express.yto.dto;


import java.time.LocalDate;
import lombok.Data;

/**
 * 员工新增/编辑输入
 */
@Data
public class StaffInput {

    private Long id;

    private String contractName;

    private String realName;

    private String phone;

    private LocalDate entryDate;

    private Integer staffType;
}
