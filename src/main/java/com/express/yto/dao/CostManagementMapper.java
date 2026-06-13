package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.dto.CostTypeSummaryDTO;
import com.express.yto.model.CostManagement;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CostManagementMapper extends BaseMapper<CostManagement> {

    BigDecimal sumByCostType(Integer costType);

    List<CostTypeSummaryDTO> sumGroupByCostType();
}