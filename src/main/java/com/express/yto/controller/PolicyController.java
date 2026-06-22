package com.express.yto.controller;
import com.express.yto.dto.RestResult;
import com.express.yto.model.Policy;
import com.express.yto.service.PolicyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController
@RequestMapping("/policy")
public class PolicyController {
    @Autowired
    private PolicyService policyService;
    @PostMapping("/create")
    public RestResult<Policy> createPolicy(@RequestBody Policy policy) {
        Policy result = policyService.createPolicy(policy);
        return RestResult.ok(result);
    }
    @GetMapping("/{id}")
    public RestResult<Policy> getPolicyById(@PathVariable Long id) {
        Policy result = policyService.getPolicyById(id);
        if (result == null) {
            return RestResult.fail(404, "政策不存在");
        }
        return RestResult.ok(result);
    }
    @GetMapping("/list")
    public RestResult<List<Policy>> listPolicies() {
        List<Policy> result = policyService.listPolicies();
        return RestResult.ok(result);
    }
    @PutMapping("/{id}")
    public RestResult<Policy> updatePolicy(@PathVariable Long id, @RequestBody Policy policy) {
        Policy result = policyService.updatePolicy(id, policy);
        if (result == null) {
            return RestResult.fail(404, "政策不存在");
        }
        return RestResult.ok(result);
    }
    @DeleteMapping("/{id}")
    public RestResult<String> deletePolicy(@PathVariable Long id) {
        boolean success = policyService.deletePolicy(id);
        if (!success) {
            return RestResult.fail(404, "删除失败，政策不存在");
        }
        return RestResult.ok("删除成功");
    }
}
