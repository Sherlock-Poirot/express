package com.express.yto.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.CustomerInput;
import com.express.yto.dto.CustomerPriceDetailDTO;
import com.express.yto.dto.CustomerSearchInput;
import com.express.yto.dto.RestResult;
import com.express.yto.model.Customer;
import com.express.yto.service.CustomerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import java.util.List;
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
@ApiOperation("客户功能")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @ApiOperation("通过excel文件导入")
    @PostMapping("/importByExcel")
    public RestResult<String> importByExcel(@RequestParam String filePath){
        customerService.importByExcel(filePath);
        return RestResult.ok("操作成功");
    }


    @ApiOperation("新增")
    @PostMapping("add")
    public RestResult<String> add(@RequestBody CustomerInput input){
        customerService.add(input);
        return RestResult.ok("操作成功");
    }

    @ApiOperation("批量删除")
    @PostMapping("batchRemove")
    public RestResult<String> add(@RequestParam List<Integer> ids){
        customerService.delete(ids);
        return RestResult.ok("操作成功");
    }

    @ApiOperation("分页查询")
    @PostMapping("search")
    public RestResult<IPage<Customer>> search(@RequestBody CustomerSearchInput input){
        return RestResult.ok(customerService.search(input));
    }

    @ApiOperation("客户价格详情")
    @GetMapping("price")
    public RestResult<List<CustomerPriceDetailDTO>> getPrice(@RequestParam String kCode){
        return RestResult.ok(customerService.getPrice(kCode));
    }
}
