package com.express.yto.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Detective
 * @date Created in 2026/3/17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerIdAndTimeDTO {

    private String customerId;

    private LocalDate startTime;
}
