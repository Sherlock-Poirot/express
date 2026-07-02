package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_collection_record")
public class CollectionRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("waybill_no")
    private String waybillNo;

    @TableField("expected_collection_time")
    private LocalDateTime expectedCollectionTime;

    @TableField("actual_collection_time")
    private LocalDateTime actualCollectionTime;

    @TableField("order_no")
    private String orderNo;

    @TableField("salesman_name")
    private String salesmanName;

    @TableField("collection_delay_minutes")
    private Integer collectionDelayMinutes;

    @TableField("is_delay")
    private Integer isDelay;

    @TableField("month")
    private String month;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}