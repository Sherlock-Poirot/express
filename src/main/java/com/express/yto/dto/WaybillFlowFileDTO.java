package com.express.yto.dto;

import lombok.Data;

import java.util.Date;

/**
 * 账单工作流导入文件DTO
 * IMPORT步骤的文件明细，作为人工确认"已导齐"的依据
 * @author Detective
 * @date Created in 2026/9/16
 */
@Data
public class WaybillFlowFileDTO {

    /** 原始文件名 */
    private String fileName;

    /** 状态：RUNNING导入中/SUCCESS成功/FAILED失败 */
    private String status;

    /** 状态中文名 */
    private String statusDesc;

    /** 成功导入条数 */
    private Integer rowCount;

    /** 失败原因 */
    private String errorMsg;

    /** 上传人 */
    private String operator;

    /** 上传时间 */
    private Date createTime;
}
