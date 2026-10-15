package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.RechargeFlowDetailMapper;
import com.express.yto.model.RechargeFlowDetail;
import com.express.yto.service.RechargeFlowDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 应收明细-面单流水明细服务实现类
 */
@Slf4j
@Service
public class RechargeFlowDetailServiceImpl extends ServiceImpl<RechargeFlowDetailMapper, RechargeFlowDetail>
        implements RechargeFlowDetailService {

    @Override
    public IPage<RechargeFlowDetail> queryPage(Integer pageNum, Integer pageSize, String fromShopId, String toShopId,
                                               String fromCustCode, String toCustCode, String operateType,
                                               String startTime, String endTime) {
        Page<RechargeFlowDetail> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectFlowPage(page, fromShopId, toShopId, fromCustCode, toCustCode,
                operateType, startTime, endTime);
    }
}
