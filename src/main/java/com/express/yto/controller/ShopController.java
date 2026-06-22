package com.express.yto.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.CustomerCodeAndNameDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.dto.ShopEmpInput;
import com.express.yto.model.ShopEmp;
import com.express.yto.service.CustomerService;
import com.express.yto.service.ShopService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Detective
 * @date Created in 2025/10/14
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/search")
    public RestResult<IPage<ShopEmp>> search(@RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "empName", required = false) String empName,
            @RequestParam(value = "shopName", required = false) String shopName,
            @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {
        return RestResult.ok(shopService.search(code, name, empName, shopName, pageNo, pageSize));
    }

    @PostMapping("/batchDelete")
    public RestResult<String> batchDelete(@RequestBody List<Integer> ids) {
        shopService.batchDelete(ids);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/add")
    public RestResult<String> add(@RequestBody ShopEmpInput input) {
        shopService.add(input);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/update")
    public RestResult<String> update(@RequestBody ShopEmpInput input) {
        shopService.update(input);
        return RestResult.ok("操作成功");
    }

    @PostMapping("/import")
    public RestResult<Integer> importShop(@RequestParam("file") MultipartFile file) {
        int count = shopService.importShop(file);
        return RestResult.ok(count);
    }

    @GetMapping("/fuzzyMatch")
    public RestResult<List<CustomerCodeAndNameDTO>> fuzzyMatch(
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "name", required = false) String name) {
        return RestResult.ok(customerService.fuzzyMatch(code, name));
    }

}
