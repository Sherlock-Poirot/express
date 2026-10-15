package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.model.RechargeFlowDetail;

/**
 * 应收明细-面单流水明细服务接口
 */
public interface RechargeFlowDetailService extends IService<RechargeFlowDetail> {

    /**
     * 分页查询面单流水明细
     */
    IPage<RechargeFlowDetail> queryPage(Integer pageNum, Integer pageSize, String fromShopId, String toShopId,
                                        String fromCustCode, String toCustCode, String operateType,
                                        String startTime, String endTime);
}
