package com.express.yto.dto;

import java.util.List;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/10/21
 */
@Data
public class PriceExcelInput {

    private String exportPath;

    private List<String> kCodeList;
}
