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
 * @author Detective
 * @date Created in 2026/5/6
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_task")
public class SysTask {
    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务编号 UUID
     */
    @TableField(value = "task_no")
    private String taskNo;

    /**
     * 任务类型：import 导入 / calculate 计算 / summary 汇总
     */
    @TableField(value = "task_type")
    private String taskType;

    /**
     * 状态：RUNNING 执行中 SUCCESS 成功 FAILED 失败
     */
    @TableField(value = "status")
    private String status;

    /**
     * 总数量/成功数
     */
    @TableField(value = "total")
    private Integer total;

    /**
     * 描述信息
     */
    @TableField(value = "message")
    private String message;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

    public static final String COL_ID = "id";

    public static final String COL_TASK_NO = "task_no";

    public static final String COL_TASK_TYPE = "task_type";

    public static final String COL_STATUS = "status";

    public static final String COL_TOTAL = "total";

    public static final String COL_MESSAGE = "message";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}