package com.express.yto.dto;

import lombok.Data;

@Data
public class CollectionRecordQueryDTO {

    private String startDate;

    private String endDate;

    private String salesmanName;

    private Integer isDelay;

    private String waybillNo;
}