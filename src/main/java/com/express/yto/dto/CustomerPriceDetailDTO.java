package com.express.yto.dto;

import com.express.yto.model.Area;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2026/4/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPriceDetailDTO {

    private String name;

    private String code;

    private String startTime;

    private String endTime;

    private BigDecimal prepayment;

    private String remark;

    private List<PriceDetailDTO> detail;

    List<Area> areas;
}
