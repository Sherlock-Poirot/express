package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.CustomerDetailDTO;
import com.express.yto.dto.CustomerInput;
import com.express.yto.dto.CustomerPriceDetailDTO;
import com.express.yto.dto.CustomerPriceInput;
import com.express.yto.dto.CustomerSearchInput;
import com.express.yto.dto.PriceDeleteInput;
import com.express.yto.model.Customer;
import java.util.List;

/**
 * @author Detective
 * @date Created in 2025/9/10
 */
public interface CustomerService extends IService<Customer> {


    void importByExcel(String filePath);

    void add(CustomerDetailDTO input);

    void delete(List<Long> ids);

    IPage<Customer> search(CustomerSearchInput input);

    List<CustomerPriceDetailDTO> getPrice(String kCode);

    void deletePrice(PriceDeleteInput input);

    void addPrice(List<CustomerPriceInput> input);

    CustomerDetailDTO getDetail(String code);

    void updateCustomer(CustomerDetailDTO input);
}
