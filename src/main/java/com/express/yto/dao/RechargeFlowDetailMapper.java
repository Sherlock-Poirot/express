package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.yto.model.RechargeFlowDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 应收明细-面单流水明细 Mapper
 */
@Mapper
public interface RechargeFlowDetailMapper extends BaseMapper<RechargeFlowDetail> {

    IPage<RechargeFlowDetail> selectFlowPage(Page<RechargeFlowDetail> page,
                                             @Param("fromShopId") String fromShopId,
                                             @Param("toShopId") String toShopId,
                                             @Param("fromCustCode") String fromCustCode,
                                             @Param("toCustCode") String toCustCode,
                                             @Param("operateType") String operateType,
                                             @Param("startTime") String startTime,
                                             @Param("endTime") String endTime);
}
