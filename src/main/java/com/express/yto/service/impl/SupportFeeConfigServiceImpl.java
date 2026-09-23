package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.yto.dao.SupportFeeConfigMapper;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.SupportFeeConfig;
import com.express.yto.service.SupportFeeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 扶持派费配置 Service 实现
 * @author Detective
 * @date Created in 2026/9/23
 */
@Slf4j
@Service
public class SupportFeeConfigServiceImpl implements SupportFeeConfigService {

    @Autowired
    private SupportFeeConfigMapper supportFeeConfigMapper;

    @Override
    public IPage<SupportFeeConfig> search(String province, String city, Integer pageNo, Integer pageSize) {
        QueryWrapper<SupportFeeConfig> wrapper = new QueryWrapper<>();
        if (province != null && !province.trim().isEmpty()) {
            wrapper.like(SupportFeeConfig.COL_PROVINCE, province.trim());
        }
        if (city != null && !city.trim().isEmpty()) {
            wrapper.like(SupportFeeConfig.COL_CITY, city.trim());
        }
        wrapper.orderByDesc(SupportFeeConfig.COL_CREATE_TIME);
        return supportFeeConfigMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
    }

    @Override
    public void add(SupportFeeConfig config) {
        validateConfig(config);
        config.setCreateTime(LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());
        supportFeeConfigMapper.insert(config);
        log.info("新增扶持派费配置：省={}，市={}，金额={}，区间=[{},{})",
                config.getProvince(), config.getCity(), config.getExtraFee(),
                config.getStartTime(), config.getEndTime());
    }

    @Override
    public void update(SupportFeeConfig config) {
        if (config.getId() == null) {
            throw new BusinessException("ID不能为空");
        }
        validateConfig(config);
        SupportFeeConfig existing = supportFeeConfigMapper.selectById(config.getId());
        if (existing == null) {
            throw new BusinessException("配置不存在");
        }
        config.setCreateTime(existing.getCreateTime());
        config.setUpdateTime(LocalDateTime.now());
        supportFeeConfigMapper.updateById(config);
        log.info("修改扶持派费配置[{}]：省={}，市={}，金额={}，区间=[{},{})",
                config.getId(), config.getProvince(), config.getCity(), config.getExtraFee(),
                config.getStartTime(), config.getEndTime());
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的记录");
        }
        int count = supportFeeConfigMapper.deleteBatchIds(ids);
        log.info("批量删除扶持派费配置：删除{}条，ids={}", count, ids);
    }

    @Override
    public SupportFeeConfig getById(Long id) {
        return supportFeeConfigMapper.selectById(id);
    }

    /**
     * 配置参数校验：必填项 + 日期区间 + 金额
     */
    private void validateConfig(SupportFeeConfig config) {
        if (config.getProvince() == null || config.getProvince().trim().isEmpty()) {
            throw new BusinessException("省份不能为空");
        }
        if (config.getExtraFee() == null) {
            throw new BusinessException("加收金额不能为空");
        }
        if (config.getStartTime() == null || config.getEndTime() == null) {
            throw new BusinessException("生效开始和结束日期不能为空");
        }
        if (!config.getStartTime().isBefore(config.getEndTime())) {
            throw new BusinessException("生效开始日期必须早于结束日期");
        }
    }
}
