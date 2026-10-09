package com.express.yto.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.CustomerCodeAndNameDTO;
import com.express.yto.dto.CustomerDetailDTO;
import com.express.yto.dto.CustomerInput;
import com.express.yto.dto.CustomerPriceDetailDTO;
import com.express.yto.dto.CustomerPriceInput;
import com.express.yto.dto.CustomerSearchInput;
import com.express.yto.dto.PriceBatchAdjustInput;
import com.express.yto.dto.PriceDeleteInput;
import com.express.yto.dto.RestResult;
import com.express.yto.model.Customer;
import com.express.yto.service.CustomerService;


import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Detective
 * @date Created in 2025/9/18
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/importByExcel")
    public RestResult<String> importByExcel(@RequestParam String filePath){
        customerService.importByExcel(filePath);
        return RestResult.ok("操作成功");
    }


    @PostMapping("/add")
    public RestResult<String> add(@RequestBody CustomerDetailDTO input){
        customerService.add(input);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/batchDelete")
    public RestResult<String> add(@RequestBody List<Long> ids){
        customerService.delete(ids);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/search")
    public RestResult<IPage<Customer>> search(@RequestBody CustomerSearchInput input){
        return RestResult.ok(customerService.search(input));
    }

    @GetMapping("/detail")
    public RestResult<CustomerDetailDTO> getDetail(@RequestParam("code") String code){
        return RestResult.ok(customerService.getDetail(code));
    }

    @PostMapping("/update")
    public RestResult<String> updateCustomer(@RequestBody CustomerDetailDTO input){
        customerService.updateCustomer(input);
        return RestResult.ok("操作成功");
    }

    @GetMapping("/price")
    public RestResult<List<CustomerPriceDetailDTO>> getPrice(@RequestParam String kCode){
        return RestResult.ok(customerService.getPrice(kCode));
    }

    @PostMapping("/deletePrice")
    public RestResult<String> deletePrice(@RequestBody PriceDeleteInput input){
        customerService.deletePrice(input);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/addPrice")
    public RestResult<String> addPrice(@RequestBody List<CustomerPriceInput> input){
        customerService.addPrice(input);
        return RestResult.ok("操作成功");
    }

    /**
     * 导出所有客户价格表
     * 每个客户一个sheet页（以客户名称命名），无价格明细的客户跳过
     * 表头与价格详情弹窗一致：开始/结束日期、预付款、区域、固定重量档、首重、续重
     * @param response HTTP响应（xlsx文件流）
     */
    @GetMapping("/exportAllPrice")
    public void exportAllPrice(HttpServletResponse response){
        customerService.exportAllPrice(response);
    }

    /**
     * 批量调整客户价格
     * 对每个客户 end_time=2999-12-31 的当前生效价格：旧数据截断至开始时间，新增调整后数据（保留历史轨迹）
     * priceDelta 作用于固定费fee与首重first_fee（正涨负降），prepayDelta 作用于预付款 pre_fee
     * fiveAreaFlag=false 时五区（area=5）的固定费/首重费保持不变
     * @param inputs 调价参数集合（整体事务，任一客户失败全部回滚）
     * @return 操作结果
     */
    @PostMapping("/batchAdjustPrice")
    public RestResult<String> batchAdjustPrice(@RequestBody List<PriceBatchAdjustInput> inputs){
        customerService.batchAdjustPrice(inputs);
        return RestResult.ok("操作成功");
    }

    /**
     * 查询所有有当前生效价格（end_time=2999-12-31）的客户编码和名称
     * 用于批量调价等页面的客户列表展示，支持客户名称模糊匹配
     * @param name 客户名称（可选，模糊匹配）
     * @return 客户编码名称列表（按编码升序）
     */
    @GetMapping("/listWithPrice")
    public RestResult<List<CustomerCodeAndNameDTO>> listWithPrice(
            @RequestParam(value = "name", required = false) String name){
        return RestResult.ok(customerService.listCustomersWithPrice(name));
    }

}
