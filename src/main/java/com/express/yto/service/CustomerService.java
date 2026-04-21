package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.CustomerInput;
import com.express.yto.dto.CustomerSearchInput;
import com.express.yto.model.Customer;
import java.util.List;

/**
 * @author Detective
 * @date Created in 2025/9/10
 */
public interface CustomerService extends IService<Customer> {


    void importByExcel(String filePath);

    void add(CustomerInput input);

    void delete(List<Integer> ids);

    IPage<Customer> search(CustomerSearchInput input);
}
