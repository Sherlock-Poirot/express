package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.dto.CustomerIdAndTimeDTO;
import com.express.yto.model.Prepayment;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
@Mapper
public interface PrepaymentMapper extends BaseMapper<Prepayment> {

    void deleteBak();

    void insertBak();

    void updateBatch(@Param("list") List<CustomerIdAndTimeDTO> preUpdateList);

    void updateEndTime(@Param("code") String code, @Param("endTime") LocalDate endTime);

    /**
     * 价格覆盖校验：按客户编码批量查询价格区间（只取判定所需的 code/start_time/end_time 三列，
     * 且过滤掉区间不完整的记录）
     */
    List<Prepayment> selectPriceRangesByCodes(@Param("codes") Set<String> codes);
}