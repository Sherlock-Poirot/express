package com.express.yto.dto;

import lombok.Data;

@Data
public class KpiSignRecordQueryDTO {

    private String startDate;

    private String endDate;

    private String month;

    private String courierName;

    private Integer isQualified;

    private String waybillNo;

    private String fakeSignType;
}