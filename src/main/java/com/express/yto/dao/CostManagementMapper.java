package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.dto.CostTypeSummaryDTO;
import com.express.yto.model.CostManagement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CostManagementMapper extends BaseMapper<CostManagement> {

    List<CostTypeSummaryDTO> sumGroupByCostType();

    List<CostTypeSummaryDTO> sumGroupByCostTypeAndMonth(@Param("month") String month);

    BigDecimal sumTotalByMonth(@Param("month") String month);
}
