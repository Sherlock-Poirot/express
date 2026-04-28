package com.express.yto.dto;

import com.express.yto.model.Customer;
import java.util.List;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/4/27
 */
@Data
public class CustomerDetailDTO extends Customer {

    List<ExtraFeeDTO> extra;
}
