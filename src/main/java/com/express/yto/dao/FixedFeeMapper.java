package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.dto.CustomerIdAndTimeDTO;
import com.express.yto.model.FixedFee;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
@Mapper
public interface FixedFeeMapper extends BaseMapper<FixedFee> {

    void deleteBak();

    void insertBak();

    void updateBatch(@Param("list") List<CustomerIdAndTimeDTO> fixedUpdateList);
}