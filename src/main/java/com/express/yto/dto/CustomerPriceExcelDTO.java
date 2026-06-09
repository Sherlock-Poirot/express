package com.express.yto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPriceExcelDTO {

    private String startTime;

    private String endTime;

    private String areaOne;

    private String areaTwo;

    private String areaThree;

    private String areaFour;

    private String areaFive;

    private String fixedPrice;

    private String remark;
}
