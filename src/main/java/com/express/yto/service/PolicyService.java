package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.model.Policy;

import java.util.List;

public interface PolicyService extends IService<Policy> {
    Policy createPolicy(Policy policy);

    Policy getPolicyById(Long id);

    List<Policy> listPolicies();

    Policy updatePolicy(Long id, Policy policy);

    boolean deletePolicy(Long id);
}
