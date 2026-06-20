package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.PolicyMapper;
import com.express.yto.model.Policy;
import com.express.yto.service.PolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Override
    public BigDecimal getFixedPolicyTotalAmount() {
        QueryWrapper<Policy> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("policy_type", 2); // 2-固定收费
        List<Policy> fixedPolicies = list(queryWrapper);

        if (fixedPolicies == null || fixedPolicies.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return fixedPolicies.stream()
                .map(policy -> policy.getAmount() != null ? policy.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<Policy> getBaseRebatePolicies() {
        QueryWrapper<Policy> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("policy_type", 1); // 1-基数返利
        return list(queryWrapper);
    }

    @Override
    public List<Policy> getDynamicRebatePolicies() {
        QueryWrapper<Policy> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("policy_type", 3); // 3-动态返利
        return list(queryWrapper);
    }
}
