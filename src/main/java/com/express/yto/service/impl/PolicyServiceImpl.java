package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.PolicyMapper;
import com.express.yto.model.Policy;
import com.express.yto.service.PolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyServiceImpl extends ServiceImpl<PolicyMapper, Policy> implements PolicyService {

    private static final Logger log = LoggerFactory.getLogger(PolicyServiceImpl.class);

    @Override
    public Policy createPolicy(Policy policy) {
        save(policy);
        return policy;
    }

    @Override
    public Policy getPolicyById(Long id) {
        return getById(id);
    }

    @Override
    public List<Policy> listPolicies() {
        return list();
    }

    @Override
    public Policy updatePolicy(Long id, Policy policy) {
        policy.setId(id);
        updateById(policy);
        return getById(id);
    }

    @Override
    public boolean deletePolicy(Long id) {
        return removeById(id);
    }
}
