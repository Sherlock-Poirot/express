package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.CashAccountMapper;
import com.express.yto.enums.CashFlowPlatformEnum;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.CashAccount;
import com.express.yto.service.CashAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资金账户 Service 实现
 * @author Detective
 * @date Created in 2026/10/18
 */
@Slf4j
@Service
public class CashAccountServiceImpl extends ServiceImpl<CashAccountMapper, CashAccount>
        implements CashAccountService {

    @Autowired
    private CashAccountMapper cashAccountMapper;

    @Override
    public List<CashAccount> list(String accountName, Integer platform) {
        return cashAccountMapper.selectList(buildQueryWrapper(accountName, platform));
    }

    @Override
    public IPage<CashAccount> search(String accountName, Integer platform, Integer pageNo, Integer pageSize) {
        return cashAccountMapper.selectPage(new Page<>(pageNo, pageSize),
                buildQueryWrapper(accountName, platform));
    }

    /**
     * 构建账户查询条件：所有条件可选，为空不参与过滤，按平台升序、id升序
     */
    private QueryWrapper<CashAccount> buildQueryWrapper(String accountName, Integer platform) {
        QueryWrapper<CashAccount> wrapper = new QueryWrapper<>();
        if (accountName != null && !accountName.trim().isEmpty()) {
            wrapper.like(CashAccount.COL_ACCOUNT_NAME, accountName.trim());
        }
        if (platform != null) {
            wrapper.eq(CashAccount.COL_PLATFORM, platform);
        }
        wrapper.orderByAsc(CashAccount.COL_PLATFORM, CashAccount.COL_ID);
        return wrapper;
    }

    @Override
    public void add(CashAccount account) {
        // 校验必填：账户名称、平台类型
        if (account.getAccountName() == null || account.getAccountName().trim().isEmpty()) {
            throw new BusinessException("账户名称不能为空");
        }
        if (account.getPlatform() == null) {
            throw new BusinessException("平台类型不能为空");
        }
        if (CashFlowPlatformEnum.of(account.getPlatform()) == null) {
            throw new BusinessException("无效的平台类型：" + account.getPlatform());
        }
        // 同平台下账户名称不允许重复
        QueryWrapper<CashAccount> qw = new QueryWrapper<>();
        qw.eq(CashAccount.COL_PLATFORM, account.getPlatform())
                .eq(CashAccount.COL_ACCOUNT_NAME, account.getAccountName().trim());
        if (cashAccountMapper.selectCount(qw) > 0) {
            throw new BusinessException("该平台下已存在同名账户：" + account.getAccountName().trim());
        }
        LocalDateTime now = LocalDateTime.now();
        account.setCreateTime(now);
        account.setUpdateTime(now);
        cashAccountMapper.insert(account);
    }

    @Override
    public void update(CashAccount account) {
        // 刷新更新时间，按主键更新非空字段
        account.setUpdateTime(LocalDateTime.now());
        cashAccountMapper.updateById(account);
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        cashAccountMapper.deleteByIds(ids);
    }

    @Override
    public CashAccount getById(Long id) {
        return cashAccountMapper.selectById(id);
    }
}
