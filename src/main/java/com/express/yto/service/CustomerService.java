package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.CustomerCodeAndNameDTO;
import com.express.yto.dto.CustomerDetailDTO;
import com.express.yto.dto.CustomerPriceDetailDTO;
import com.express.yto.dto.CustomerPriceInput;
import com.express.yto.dto.CustomerSearchInput;
import com.express.yto.dto.PriceBatchAdjustInput;
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

    /**
     * 批量调整客户价格
     * 对每个客户 end_time=2999-12-31 的当前生效价格：旧数据截断至开始时间（改end_time），
     * 再新增调整后的价格数据（fixed_fee改fee、over_fee改first_fee、prepayment改pre_fee，均为加法）
     * 五区开关=false时area=5的固定费/续重费保持不变
     * @param inputs 调价参数集合
     */
    void batchAdjustPrice(List<PriceBatchAdjustInput> inputs);

    /**
     * 查询所有有当前生效价格（end_time=2999-12-31）的客户编码和名称，支持客户名称模糊匹配
     * @param name 客户名称（可选，模糊匹配）
     * @return 客户编码名称列表（按编码升序）
     */
    List<CustomerCodeAndNameDTO> listCustomersWithPrice(String name);
}
