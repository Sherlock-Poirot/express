package com.express.yto.dto;

import lombok.Data;

import java.util.Date;

/**
 * 账单工作流步骤DTO
 * status为前端展示用的推导后状态（IMPORT步骤由导入文件记录推导）
 * @author Detective
 * @date Created in 2026/9/16
 */
@Data
public class WaybillFlowStepDTO {

    /** 步骤编码：IMPORT/IMPORT_DIFF/CLEAN/VALIDATE/CALCULATE */
    private String stepCode;

    /** 步骤名称 */
    private String stepName;

    /** 步骤顺序：1-5 */
    private Integer stepOrder;

    /** 展示状态：WAITING/RUNNING/SUCCESS/FAILED/PASSED/SKIPPED */
    private String status;

    /** 状态中文名 */
    private String statusDesc;

    /** 步骤说明（该步骤做什么及人工注意事项） */
    private String remark;

    /** 失败原因 */
    private String errorMsg;

    /** 操作人 */
    private String operator;

    /** 开始时间 */
    private Date startTime;

    /** 结束时间 */
    private Date endTime;
}
