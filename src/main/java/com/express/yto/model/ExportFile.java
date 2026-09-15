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
 * 账单明细导出文件记录
 * 异步导出任务的文件落库信息，前端下载页展示用
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_export_file")
public class ExportFile {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务编号（雪花ID）
     */
    @TableField(value = "task_no")
    private String taskNo;

    /**
     * 账单月份 yyyy-MM
     */
    @TableField(value = "bill_month")
    private String billMonth;

    /**
     * 展示用文件名（如 2026-08_明细.zip）
     */
    @TableField(value = "file_name")
    private String fileName;

    /**
     * 服务器磁盘文件完整路径
     */
    @TableField(value = "file_path")
    private String filePath;

    /**
     * 状态：RUNNING 生成中 / SUCCESS 成功 / FAILED 失败
     */
    @TableField(value = "status")
    private String status;

    /**
     * 失败原因
     */
    @TableField(value = "error_msg")
    private String errorMsg;

    /**
     * 文件大小（字节）
     */
    @TableField(value = "file_size")
    private Long fileSize;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;
}
