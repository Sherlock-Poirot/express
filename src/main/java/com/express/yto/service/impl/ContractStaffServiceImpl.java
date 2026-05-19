package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.ContractStaffMapper;
import com.express.yto.model.ContractStaff;
import com.express.yto.service.ContractStaffService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Detective
 * @date Created in 2026/5/9
 */
@Service
public class ContractStaffServiceImpl extends ServiceImpl<ContractStaffMapper, ContractStaff> implements
        ContractStaffService {

    @Autowired
    private ContractStaffMapper contractStaffMapper;

    @Override
    public IPage<ContractStaff> search(String staffName, String phone, Integer pageNo, Integer pageSize) {
        Page<ContractStaff> page = new Page<>(pageNo, pageSize);
        QueryWrapper<ContractStaff> qw = new QueryWrapper<>();
        if (StringUtils.isNotBlank(staffName)) {
            qw.like("real_name", staffName);
        }
        if (StringUtils.isNotBlank(phone)) {
            qw.or().like("phone", phone);
        }
        qw.orderByAsc("contract_name");
        return contractStaffMapper.selectPage(page, qw);
    }
}
