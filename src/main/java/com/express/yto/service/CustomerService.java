package com.express.yto.service;

import com.express.yto.model.Customer;
import com.baomidou.mybatisplus.extension.service.IService;
    /**
 * @author Detective
 * @date Created in 2025/9/10
 */
public interface CustomerService extends IService<Customer>{


        void importByExcel(String filePath);
    }
