package com.express.yto.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.CustomerDetailDTO;
import com.express.yto.dto.CustomerInput;
import com.express.yto.dto.CustomerPriceDetailDTO;
import com.express.yto.dto.CustomerPriceInput;
import com.express.yto.dto.CustomerSearchInput;
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

}
