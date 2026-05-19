package com.express.yto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2026/5/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCodeAndNameDTO {

    private String customerName;

    private String customerCode;
}
