package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.yto.dto.SalesmanRankDTO;
import com.express.yto.model.CollectionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CollectionRecordMapper extends BaseMapper<CollectionRecord> {

    IPage<CollectionRecord> selectRecordPage(Page<CollectionRecord> page,
                                             @Param("startDate") String startDate,
                                             @Param("endDate") String endDate,
                                             @Param("salesmanName") String salesmanName,
                                             @Param("isDelay") Integer isDelay,
                                             @Param("waybillNo") String waybillNo);

    List<SalesmanRankDTO> selectSalesmanRank(@Param("startDate") String startDate, @Param("endDate") String endDate);

    void deleteByMonth(@Param("month") String month);

    void insertBatch(@Param("list") List<CollectionRecord> list);
}