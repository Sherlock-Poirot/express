package com.express.yto.dto;

import java.util.List;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/4/23
 */
@Data
public class AreaDTO {

    private String companyId;

    private List<AreaListDTO> areaList;
}
