package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2026/5/9
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_contract_staff")
public class ContractStaff {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 承包区名称
     */
    @TableField(value = "contract_name")
    private String contractName;

    /**
     * 姓名
     */
    @TableField(value = "real_name")
    private String realName;

    /**
     * 联系电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 入职日期
     */
    @TableField(value = "entry_date")
    private LocalDate entryDate;

    /**
     * 职员类型0.承包区，1.业务员
     */
    @TableField(value = "staff_type")
    private Integer staffType;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    public static final String COL_ID = "id";

    public static final String COL_CONTRACT_NAME = "contract_name";

    public static final String COL_REAL_NAME = "real_name";

    public static final String COL_PHONE = "phone";

    public static final String COL_ENTRY_DATE = "entry_date";

    public static final String COL_STAFF_TYPE = "staff_type";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}