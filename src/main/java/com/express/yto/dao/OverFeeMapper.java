package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.dto.CustomerIdAndTimeDTO;
import com.express.yto.dto.OverFeeExcelMergeDTO;
import com.express.yto.dto.OverFeeExportDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.model.OverFee;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
@Mapper
public interface OverFeeMapper extends BaseMapper<OverFee> {


    List<OverFeeExportDTO> getListByCode(@Param("list") List<String> kCodeList);

    List<OverFeeExcelMergeDTO> getExcelByCode(@Param("list") List<String> kCodeList);

    void deleteBak();

    void insertBak();

    void updateBatch(@Param("list") List<CustomerIdAndTimeDTO> overUpdateList);

    void updateEndTime(@Param("code") String code, @Param("endTime") LocalDate endTime);
}