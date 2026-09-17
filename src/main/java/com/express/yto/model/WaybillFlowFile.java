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
 * 账单工作流导入文件记录表
 * 记录每个账单月份导入的Excel文件（文件数量不固定），作为导入步骤人工确认"已导齐"的依据
 * @author Detective
 * @date Created in 2026/9/16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_waybill_flow_file")
public class WaybillFlowFile {
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
     * 原始文件名
     */
    @TableField(value = "file_name")
    private String fileName;

    /**
     * 文件MD5（防重复导入）
     */
    @TableField(value = "file_md5")
    private String fileMd5;

    /**
     * 关联sys_task.task_no
     */
    @TableField(value = "task_no")
    private String taskNo;

    /**
     * 状态：RUNNING导入中/SUCCESS成功/FAILED失败
     */
    @TableField(value = "status")
    private String status;

    /**
     * 成功导入条数
     */
    @TableField(value = "row_count")
    private Integer rowCount;

    /**
     * 失败原因
     */
    @TableField(value = "error_msg")
    private String errorMsg;

    /**
     * 上传人
     */
    @TableField(value = "operator")
    private String operator;

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

    public static final String COL_FILE_NAME = "file_name";

    public static final String COL_FILE_MD5 = "file_md5";

    public static final String COL_TASK_NO = "task_no";

    public static final String COL_STATUS = "status";

    public static final String COL_ROW_COUNT = "row_count";

    public static final String COL_ERROR_MSG = "error_msg";

    public static final String COL_OPERATOR = "operator";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}
