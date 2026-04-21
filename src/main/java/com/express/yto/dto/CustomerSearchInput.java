package com.express.yto.dto;

import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/4/21
 */
@Data
public class CustomerSearchInput extends PageInput {

    private String kName;

    private String kCode;
}
