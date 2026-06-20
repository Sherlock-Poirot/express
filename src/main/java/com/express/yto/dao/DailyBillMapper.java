package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.model.DailyBill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
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
    List<com.express.yto.dto.RegionDistributionDTO> selectRegionDistribution(@Param("date") LocalDate date);

    @Select("SELECT SUM(total_amount) FROM t_daily_bill WHERE charge_date = #{date}")
    BigDecimal sumTotalAmountByDate(@Param("date") LocalDate date);

    @Select("SELECT COUNT(*) FROM t_daily_bill WHERE charge_date = #{date}")
    Long countByDate(@Param("date") LocalDate date);
}