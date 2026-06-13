package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.model.Policy;
import com.express.yto.service.PolicyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "政策管理")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @PostMapping("/create")
    @ApiOperation("创建政策")
    public RestResult<Policy> createPolicy(@RequestBody Policy policy) {
        Policy result = policyService.createPolicy(policy);
        return RestResult.ok(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("查询政策详情")
    public RestResult<Policy> getPolicyById(@PathVariable Long id) {
        Policy result = policyService.getPolicyById(id);
        if (result == null) {
            return RestResult.fail(404, "政策不存在");
        }
        return RestResult.ok(result);
    }

    @GetMapping("/list")
    @ApiOperation("查询政策列表")
    public RestResult<List<Policy>> listPolicies() {
        List<Policy> result = policyService.listPolicies();
        return RestResult.ok(result);
    }

    @PutMapping("/{id}")
    @ApiOperation("更新政策")
    public RestResult<Policy> updatePolicy(@PathVariable Long id, @RequestBody Policy policy) {
        Policy result = policyService.updatePolicy(id, policy);
        if (result == null) {
            return RestResult.fail(404, "政策不存在");
        }
        return RestResult.ok(result);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除政策")
    public RestResult<String> deletePolicy(@PathVariable Long id) {
        boolean success = policyService.deletePolicy(id);
        if (!success) {
            return RestResult.fail(404, "删除失败，政策不存在");
        }
        return RestResult.ok("删除成功");
    }
}
