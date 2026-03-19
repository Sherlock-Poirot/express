package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.dto.CustomerAllPriceDTO;
import com.express.yto.model.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Detective
 * @date Created in 2025/9/10
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

    CustomerAllPriceDTO existByName(@Param("kName") String fileName);

}