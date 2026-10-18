package com.express.yto.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.PlatformOptionDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.model.CashAccount;
import com.express.yto.service.CashAccountService;
import com.express.yto.service.CashFlowDetailService;
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
 * 资金账户 Controller
 * 维护收款账户信息（同一平台可有多个账户，如私户、公户），流水按账户维度分类
 * @author Detective
 * @date Created in 2026/10/18
 */
@RestController
@RequestMapping("/cashAccount")
public class CashAccountController {

    @Autowired
    private CashAccountService cashAccountService;

    @Autowired
    private CashFlowDetailService cashFlowDetailService;

    /**
     * 获取资金流水可导入的平台列表（供前端导入下拉框使用）
     */
    @GetMapping("/platforms")
    public RestResult<List<PlatformOptionDTO>> platforms() {
        return RestResult.ok(cashFlowDetailService.listPlatformOptions());
    }

    /**
     * 账户列表查询（不分页）
     * @param accountName 账户名称（可选，模糊匹配）
     * @param platform 平台类型（可选，精确匹配）
     */
    @GetMapping("/list")
    public RestResult<List<CashAccount>> list(
            @RequestParam(value = "accountName", required = false) String accountName,
            @RequestParam(value = "platform", required = false) Integer platform) {
        return RestResult.ok(cashAccountService.list(accountName, platform));
    }

    /**
     * 账户分页查询
     * 筛选条件同列表接口
     * @param pageNo 页码（默认1）
     * @param pageSize 每页条数（默认10）
     */
    @GetMapping("/search")
    public RestResult<IPage<CashAccount>> search(
            @RequestParam(value = "accountName", required = false) String accountName,
            @RequestParam(value = "platform", required = false) Integer platform,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return RestResult.ok(cashAccountService.search(accountName, platform, pageNo, pageSize));
    }

    /**
     * 新增账户
     * 校验：账户名称、平台类型必填；同平台下账户名称不允许重复
     */
    @PostMapping("/add")
    public RestResult<String> add(@RequestBody CashAccount account) {
        cashAccountService.add(account);
        return RestResult.ok("新增成功");
    }

    /**
     * 修改账户
     */
    @PostMapping("/update")
    public RestResult<String> update(@RequestBody CashAccount account) {
        cashAccountService.update(account);
        return RestResult.ok("操作成功");
    }

    /**
     * 批量删除账户
     */
    @DeleteMapping("/batch")
    public RestResult<String> batchDelete(@RequestBody List<Long> ids) {
        cashAccountService.deleteByIds(ids);
        return RestResult.ok("删除成功");
    }

    /**
     * 账户详情
     */
    @GetMapping("/{id}")
    public RestResult<CashAccount> getById(@PathVariable Long id) {
        return RestResult.ok(cashAccountService.getById(id));
    }
}
