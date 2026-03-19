package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.PriceExcelInput;
import com.express.yto.model.OverFee;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
public interface OverFeeService extends IService<OverFee> {


    void importByExcel(String path);

    void export(PriceExcelInput input);
}
