package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.ExportCustomerFeeInput;
import com.express.yto.dto.FixedFeeInsertInput;
import com.express.yto.model.FixedFee;
import java.util.List;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
public interface FixedFeeService extends IService<FixedFee> {


    /**
     * 插入固定重量区间价格
     */
    void insertByCustomer(List<FixedFeeInsertInput> list);

    /**
     * 导出固定重量区间价格
     */
    void exportByKCode(List<String> kCodeList);

    /**
     * 导出单个价格表
     */
    void exportSingle(ExportCustomerFeeInput input);
}
