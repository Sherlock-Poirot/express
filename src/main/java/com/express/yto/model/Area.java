package com.express.yto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2026/3/16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_area")
public class Area {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "company_id")
    private String companyId;

    @TableField(value = "area_num")
    private Integer areaNum;

    @TableField(value = "area_city")
    private String areaCity;

}
