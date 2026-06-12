package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.model.DailyBill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DailyBillMapper extends BaseMapper<DailyBill> {
    void insertBatch(@Param("list") List<DailyBill> list);
    
    List<DailyBill> selectDistinctMerchantCustomer();
    
    List<DailyBill> selectDistinctMerchantCustomerByDate(@Param("date") LocalDate date);
    
    int updateCustomerInfoByMerchant(@Param("merchantCode") String merchantCode, 
                                      @Param("oldCustomerCode") String oldCustomerCode,
                                      @Param("newCustomerCode") String newCustomerCode,
                                      @Param("newCustomerName") String newCustomerName);
    
    void batchUpdateCustomerInfo(@Param("list") List<DailyBill> list);
}