package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.CustomerCodeAndNameDTO;
import com.express.yto.dto.CustomerDetailDTO;
import com.express.yto.dto.CustomerPriceDetailDTO;
import com.express.yto.dto.CustomerPriceInput;
import com.express.yto.dto.CustomerSearchInput;
import com.express.yto.dto.PriceDeleteInput;
import com.express.yto.model.Customer;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

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

    List<CustomerCodeAndNameDTO> fuzzyMatch(String code, String name);

    /**
     * 导出所有客户的价格表
     * 每个客户一个sheet页（以客户名称命名），无价格明细的客户跳过
     * @param response HTTP响应（文件流式输出）
     */
    void exportAllPrice(HttpServletResponse response);
}
