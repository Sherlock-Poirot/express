package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.dto.BillIdAndFeeDTO;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.CustomerCodeAndNameDTO;
import com.express.yto.dto.IdAndWeightDTO;
import com.express.yto.dto.ShopCustomerNameDTO;
import com.express.yto.model.WaybillDetail;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Detective
 * @date Created in 2026/5/6
 */
@Mapper
public interface WaybillDetailMapper extends BaseMapper<WaybillDetail> {

    void insertBatch(@Param("list") List<WaybillDetail> cacheList);

    void updateExtraFee(LocalDate date);

    List<ShopCustomerNameDTO> getShopCustomer();

    void updateCustomerByShopName(@Param("shopName") String shopName, @Param("materialType") String materialType, @Param("customerName") String customerName, @Param("customerCode") String customerCode);

    List<CustomerCodeAndNameDTO> getCustomerNameAndCode();

    void updateCode(@Param("name") String customerName, @Param("code") String customerCode);

    void updateEmpInfo(@Param("custName") String customerName, @Param("empName") String empName,
            @Param("empType") String empType);

    void updateDiscrete();

    List<CustomerCodeAndNameDTO> getDirectCustomer();

    void updateFeeBatch(@Param("list") List<BillIdAndFeeDTO> idAndFeeList);

    List<ContractShopExcelDTO> getEmpAliLoose();

    List<ContractShopExcelDTO> getEmpLimit();

    List<ContractShopExcelDTO> getSpecialEmpBill();

    List<ContractShopExcelDTO> getCompanyLoose();

    void updateWeight(@Param("list") List<IdAndWeightDTO> cacheList);

    void updateEmpType(LocalDate date);

    void updateExpressFee(LocalDate date);
}