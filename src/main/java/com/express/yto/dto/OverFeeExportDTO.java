package com.express.yto.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/10/21
 */
@Data
public class OverFeeExportDTO {

    private String kCode;

    private String kName;

    private List<OverFeeMiniDTO> overFeeList;
}
