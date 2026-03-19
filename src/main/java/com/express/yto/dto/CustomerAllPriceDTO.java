package com.express.yto.dto;

import com.express.yto.model.ExtraFee;
import com.express.yto.model.FixedFee;
import com.express.yto.model.OverFee;
import com.express.yto.model.Prepayment;
import java.util.List;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2025/10/5
 */
@Data
public class CustomerAllPriceDTO {

    private String kCode;

    private String kName;

    private List<ExtraFee> extraFeeList;

    private List<FixedFee> fixedFeeList;

    private List<OverFee> overFeeList;

    private List<Prepayment> prepaymentList;
}
