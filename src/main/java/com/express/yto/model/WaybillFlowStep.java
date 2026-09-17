package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账单工作流步骤表
 * 每月账单处理流程各步骤的状态跟踪，每月1号由定时任务初始化
 * @author Detective
 * @date Created in 2026/9/16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_waybill_flow_step")
public class WaybillFlowStep {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账单月份 yyyy-MM
     */
    @TableField(value = "bill_month")
    private String billMonth;

    /**
     * 步骤编码：IMPORT/IMPORT_DIFF/CLEAN/VALIDATE/CALCULATE
     */
    @TableField(value = "step_code")
    private String stepCode;

    /**
     * 步骤名称
     */
    @TableField(value = "step_name")
    private String stepName;

    /**
     * 步骤顺序：1-5
     */
    @TableField(value = "step_order")
    private Integer stepOrder;

    /**
     * 状态：WAITING/RUNNING/SUCCESS/FAILED/PASSED/SKIPPED
     */
    @TableField(value = "status")
    private String status;

    /**
     * 步骤说明（该步骤做什么及人工注意事项）
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 关联sys_task.task_no（导入步骤进度查询复用）
     */
    @TableField(value = "task_no")
    private String taskNo;

    /**
     * 失败原因
     */
    @TableField(value = "error_msg")
    private String errorMsg;

    /**
     * 操作人
     */
    @TableField(value = "operator")
    private String operator;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private Date endTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    public static final String COL_ID = "id";

    public static final String COL_BILL_MONTH = "bill_month";

    public static final String COL_STEP_CODE = "step_code";

    public static final String COL_STEP_NAME = "step_name";

    public static final String COL_STEP_ORDER = "step_order";

    public static final String COL_STATUS = "status";

    public static final String COL_REMARK = "remark";

    public static final String COL_TASK_NO = "task_no";

    public static final String COL_ERROR_MSG = "error_msg";

    public static final String COL_OPERATOR = "operator";

    public static final String COL_START_TIME = "start_time";

    public static final String COL_END_TIME = "end_time";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}
