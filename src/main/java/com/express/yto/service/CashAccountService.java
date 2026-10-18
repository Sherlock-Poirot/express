package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.model.CashAccount;

import java.util.List;

/**
 * 资金账户 Service
 * 同一平台（银行/微信/支付宝）可有多个账户（如私户、公户），流水按账户维度分类
 * @author Detective
 * @date Created in 2026/10/18
 */
public interface CashAccountService {

    /**
     * 查询账户列表（不分页）
     * 所有条件均可选，为空则不参与过滤
     * @param accountName 账户名称（模糊匹配）
     * @param platform 平台类型（精确匹配）
     * @return 按平台升序、id升序的账户列表
     */
    List<CashAccount> list(String accountName, Integer platform);

    /**
     * 分页查询账户
     * 筛选条件同 {@link #list}，所有条件均可选，为空则不参与过滤
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return 分页结果，按平台升序、id升序
     */
    IPage<CashAccount> search(String accountName, Integer platform, Integer pageNo, Integer pageSize);

    /**
     * 新增账户
     */
    void add(CashAccount account);

    /**
     * 修改账户
     */
    void update(CashAccount account);

    /**
     * 批量删除账户
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据ID查询
     */
    CashAccount getById(Long id);
}
