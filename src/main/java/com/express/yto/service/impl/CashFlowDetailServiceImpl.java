package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.CashAccountMapper;
import com.express.yto.dao.CashFlowDetailMapper;
import com.express.yto.dao.RechargeCashRelationMapper;
import com.express.yto.dto.PlatformOptionDTO;
import com.express.yto.enums.CashFlowPlatformEnum;
import com.express.yto.exception.BusinessException;
import com.express.yto.factory.CashFlowImportFactory;
import com.express.yto.model.CashAccount;
import com.express.yto.model.CashFlowDetail;
import com.express.yto.model.RechargeCashRelation;
import com.express.yto.model.SysDictItem;
import com.express.yto.service.CashFlowDetailService;
import com.express.yto.service.CashFlowImportHandler;
import com.express.yto.service.SysDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 实收流水 Service 实现
 * @author Detective
 * @date Created in 2026/10/15
 */
@Slf4j
@Service
public class CashFlowDetailServiceImpl extends ServiceImpl<CashFlowDetailMapper, CashFlowDetail>
        implements CashFlowDetailService {

    @Autowired
    private CashFlowDetailMapper cashFlowDetailMapper;

    @Autowired
    private RechargeCashRelationMapper rechargeCashRelationMapper;

    @Autowired
    private CashFlowImportFactory cashFlowImportFactory;

    @Autowired
    private SysDictService sysDictService;

    @Autowired
    private CashAccountMapper cashAccountMapper;

    @Override
    public List<PlatformOptionDTO> listPlatformOptions() {
        List<SysDictItem> items = sysDictService.getDictItems("platform");
        List<PlatformOptionDTO> options = new ArrayList<>(items.size());
        for (SysDictItem item : items) {
            options.add(PlatformOptionDTO.builder()
                    .value(Integer.valueOf(item.getDictValue()))
                    .label(item.getDictLabel())
                    .build());
        }
        return options;
    }

    @Override
    @Transactional
    public Integer importExcel(MultipartFile file, Long accountId) {
        // 按账户id查账户，用账户所属平台分发解析处理器
        if (accountId == null) {
            throw new BusinessException("账户id不能为空");
        }
        CashAccount account = cashAccountMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException("账户不存在：" + accountId);
        }
        Integer platform = account.getPlatform();
        // 工厂按平台类型分发到对应的解析处理器
        CashFlowImportHandler handler = cashFlowImportFactory.getHandler(platform);
        List<CashFlowDetail> detailList = handler.parse(file);
        if (detailList == null || detailList.isEmpty()) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        for (CashFlowDetail detail : detailList) {
            // 统一填充：流水归属该账户（及其平台），若字典值与枚举type不一致，去对应handler里单独处理
            detail.setAccountId(accountId);
            detail.setPlatform(platform);
            detail.setCreateTime(now);
            detail.setUpdateTime(now);
        }
        // 统一排序：按操作时间升序（时间小的在前、大的在后），避免各平台文件本身排序不一致
        detailList.sort(Comparator.comparing(CashFlowDetail::getOperateTime,
                Comparator.nullsLast(Comparator.naturalOrder())));
        saveBatch(detailList);
        log.info("资金流水导入完成：账户={}（{}，平台={}），导入{}条",
                accountId, account.getAccountName(), CashFlowPlatformEnum.of(platform).getName(), detailList.size());
        return detailList.size();
    }

    @Override
    public List<CashFlowDetail> list(String custName, Integer platform, Integer actualType, Integer markType,
                                     Integer matchStatus, BigDecimal minAmount, BigDecimal maxAmount,
                                     LocalDateTime startTime, LocalDateTime endTime) {
        return cashFlowDetailMapper.selectList(buildQueryWrapper(custName, platform, actualType, markType,
                matchStatus, minAmount, maxAmount, startTime, endTime));
    }

    @Override
    public IPage<CashFlowDetail> search(String custName, Integer platform, Integer actualType, Integer markType,
                                         Integer matchStatus, BigDecimal minAmount, BigDecimal maxAmount,
                                         LocalDateTime startTime, LocalDateTime endTime,
                                         Integer pageNo, Integer pageSize) {
        return cashFlowDetailMapper.selectPage(new Page<>(pageNo, pageSize),
                buildQueryWrapper(custName, platform, actualType, markType,
                        matchStatus, minAmount, maxAmount, startTime, endTime));
    }

    /**
     * 构建实收流水查询条件：所有条件可选，为空不参与过滤，按操作时间倒序
     */
    private QueryWrapper<CashFlowDetail> buildQueryWrapper(String custName, Integer platform, Integer actualType,
                                                            Integer markType, Integer matchStatus,
                                                            BigDecimal minAmount, BigDecimal maxAmount,
                                                            LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<CashFlowDetail> wrapper = new QueryWrapper<>();
        if (custName != null && !custName.trim().isEmpty()) {
            wrapper.like(CashFlowDetail.COL_CUST_NAME, custName.trim());
        }
        if (platform != null) {
            wrapper.eq(CashFlowDetail.COL_PLATFORM, platform);
        }
        if (actualType != null) {
            wrapper.eq(CashFlowDetail.COL_ACTUAL_TYPE, actualType);
        }
        if (markType != null) {
            wrapper.eq(CashFlowDetail.COL_MARK_TYPE, markType);
        }
        if (matchStatus != null) {
            wrapper.eq(CashFlowDetail.COL_MATCH_STATUS, matchStatus);
        }
        // 金额范围：左闭右闭
        if (minAmount != null) {
            wrapper.ge(CashFlowDetail.COL_AMOUNT, minAmount);
        }
        if (maxAmount != null) {
            wrapper.le(CashFlowDetail.COL_AMOUNT, maxAmount);
        }
        // 操作时间范围查询：左闭右闭
        if (startTime != null && endTime != null) {
            wrapper.ge(CashFlowDetail.COL_OPERATE_TIME, startTime)
                    .le(CashFlowDetail.COL_OPERATE_TIME, endTime);
        } else if (startTime != null) {
            wrapper.ge(CashFlowDetail.COL_OPERATE_TIME, startTime);
        } else if (endTime != null) {
            wrapper.le(CashFlowDetail.COL_OPERATE_TIME, endTime);
        }
        // 按操作时间正序（时间小的在前），NULL值排最后
        wrapper.orderByAsc(CashFlowDetail.COL_OPERATE_TIME + " IS NULL")
                .orderByAsc(CashFlowDetail.COL_OPERATE_TIME);
        return wrapper;
    }

    @Override
    public void add(CashFlowDetail detail) {
        validateDetail(detail);
        LocalDateTime now = LocalDateTime.now();
        detail.setCreateTime(now);
        detail.setUpdateTime(now);
        cashFlowDetailMapper.insert(detail);
        log.info("新增实收流水：客户={}，金额={}，实际收支类型={}，标记收支类型={}",
                detail.getCustName(), detail.getAmount(), detail.getActualType(), detail.getMarkType());
    }

    @Override
    public void update(CashFlowDetail detail) {
        if (detail.getId() == null) {
            throw new BusinessException("ID不能为空");
        }
        validateDetail(detail);
        CashFlowDetail existing = cashFlowDetailMapper.selectById(detail.getId());
        if (existing == null) {
            throw new BusinessException("流水记录不存在");
        }
        cashFlowDetailMapper.updateById(detail);
        log.info("修改实收流水[{}]：客户={}，金额={}，实际收支类型={}，标记收支类型={}",
                detail.getId(), detail.getCustName(), detail.getAmount(),
                detail.getActualType(), detail.getMarkType());
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的记录");
        }
        // 先删除关联关系表中该实收流水的匹配关系，避免产生孤儿关联数据
        QueryWrapper<RechargeCashRelation> relationWrapper = new QueryWrapper<>();
        relationWrapper.in(RechargeCashRelation.COL_CASH_ID, ids);
        int relationCount = rechargeCashRelationMapper.delete(relationWrapper);
        int count = cashFlowDetailMapper.deleteByIds(ids);
        log.info("批量删除实收流水：删除{}条，清理关联关系{}条，ids={}", count, relationCount, ids);
    }

    @Override
    public CashFlowDetail getById(Long id) {
        return cashFlowDetailMapper.selectById(id);
    }

    /**
     * 流水参数校验：客户名称 + 金额 + 实际收支类型；
     * 标记收支类型(markType)字段暂时废弃，数据库允许为空，不做校验
     */
    private void validateDetail(CashFlowDetail detail) {
        if (detail.getCustName() == null || detail.getCustName().trim().isEmpty()) {
            throw new BusinessException("客户名称不能为空");
        }
        if (detail.getAmount() == null) {
            throw new BusinessException("金额不能为空");
        }
        if (detail.getActualType() == null) {
            throw new BusinessException("实际收支类型不能为空");
        }
    }
}
