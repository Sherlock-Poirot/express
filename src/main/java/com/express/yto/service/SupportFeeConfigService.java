package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.model.SupportFeeConfig;

import java.util.List;

/**
 * 扶持派费配置 Service
 * @author Detective
 * @date Created in 2026/9/23
 */
public interface SupportFeeConfigService {

    /**
     * 分页查询扶持派费配置
     * @param province 省份（可选，模糊匹配）
     * @param city 目的地市（可选，模糊匹配）
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<SupportFeeConfig> search(String province, String city, Integer pageNo, Integer pageSize);

    /**
     * 新增扶持派费配置
     */
    void add(SupportFeeConfig config);

    /**
     * 修改扶持派费配置
     */
    void update(SupportFeeConfig config);

    /**
     * 批量删除扶持派费配置
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据ID查询
     */
    SupportFeeConfig getById(Long id);
}
