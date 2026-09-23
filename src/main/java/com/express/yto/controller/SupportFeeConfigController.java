package com.express.yto.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.RestResult;
import com.express.yto.model.SupportFeeConfig;
import com.express.yto.service.SupportFeeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 扶持派费配置 Controller
 * @author Detective
 * @date Created in 2026/9/23
 */
@RestController
@RequestMapping("/supportFeeConfig")
public class SupportFeeConfigController {

    @Autowired
    private SupportFeeConfigService supportFeeConfigService;

    /**
     * 分页查询扶持派费配置
     * @param province 省份（可选，模糊匹配）
     * @param city 目的地市（可选，模糊匹配）
     * @param pageNo 页码（默认1）
     * @param pageSize 每页条数（默认10）
     */
    @GetMapping("/search")
    public RestResult<IPage<SupportFeeConfig>> search(
            @RequestParam(value = "province", required = false) String province,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return RestResult.ok(supportFeeConfigService.search(province, city, pageNo, pageSize));
    }

    /**
     * 根据ID查询单条配置（编辑回显用）
     */
    @GetMapping("/{id}")
    public RestResult<SupportFeeConfig> getById(@PathVariable("id") Long id) {
        return RestResult.ok(supportFeeConfigService.getById(id));
    }

    /**
     * 新增扶持派费配置
     */
    @PostMapping("/add")
    public RestResult<String> add(@RequestBody SupportFeeConfig config) {
        supportFeeConfigService.add(config);
        return RestResult.ok("操作成功");
    }

    /**
     * 修改扶持派费配置
     */
    @PostMapping("/update")
    public RestResult<String> update(@RequestBody SupportFeeConfig config) {
        supportFeeConfigService.update(config);
        return RestResult.ok("操作成功");
    }

    /**
     * 批量删除扶持派费配置
     */
    @DeleteMapping("/batch")
    public RestResult<String> batchDelete(@RequestBody List<Long> ids) {
        supportFeeConfigService.deleteByIds(ids);
        return RestResult.ok("操作成功");
    }
}
