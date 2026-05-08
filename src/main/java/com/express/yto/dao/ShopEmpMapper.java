package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.dto.EmpBillInfoDTO;
import com.express.yto.dto.ShopCustomerNameDTO;
import com.express.yto.model.ShopEmp;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Detective
 * @date Created in 2025/10/31
 */
@Mapper
public interface ShopEmpMapper extends BaseMapper<ShopEmp> {

    List<ShopCustomerNameDTO> getShopCustomer();

    List<EmpBillInfoDTO> getEmpInfo();
}