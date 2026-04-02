package com.express.yto.service;

import com.express.yto.dto.DealDataInput;
import com.express.yto.dto.PreDealDTO;
import com.express.yto.dto.RestResult;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
public interface DealDataService {

    void doDeal(String readPath, String exportPath, Boolean springFestival, String companyId, String month);

    RestResult<PreDealDTO> preDeal(String path);

    void count(String path);

    void fourRate(DealDataInput input);

    void compile(DealDataInput input);

    void backUp();
}
