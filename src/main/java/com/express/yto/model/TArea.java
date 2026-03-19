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
public class TArea {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公司id
     */
    @TableField(value = "company_id")
    private String companyId;

    /**
     * 区域字典
     */
    @TableField(value = "area_num")
    private Integer areaNum;

    /**
     * 区域城市
     */
    @TableField(value = "area_city")
    private String areaCity;

    public static final String COL_ID = "id";

    public static final String COL_COMPANY_ID = "company_id";

    public static final String COL_AREA_NUM = "area_num";

    public static final String COL_AREA_CITY = "area_city";
}