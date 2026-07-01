package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.yto.dto.KpiCourierRankDTO;
import com.express.yto.dto.KpiFakeSignTypeDTO;
import com.express.yto.dto.KpiMonthlySummaryDTO;
import com.express.yto.model.KpiSignRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KpiSignRecordMapper extends BaseMapper<KpiSignRecord> {

    IPage<KpiSignRecord> selectRecordPage(Page<KpiSignRecord> page,
                                          @Param("startDate") String startDate,
                                          @Param("endDate") String endDate,
                                          @Param("courierName") String courierName,
                                          @Param("isQualified") Integer isQualified,
                                          @Param("waybillNo") String waybillNo,
                                          @Param("fakeSignType") String fakeSignType);

    KpiMonthlySummaryDTO selectMonthlySummary(@Param("month") String month);

    List<KpiCourierRankDTO> selectCourierRank(@Param("month") String month);

    List<KpiFakeSignTypeDTO> selectFakeSignTypeStat(@Param("month") String month);

    void deleteByMonth(@Param("month") String month);
}