package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.RechargeFlowDetailMapper;
import com.express.yto.dto.RechargeFlowExcelDTO;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.RechargeFlowDetail;
import com.express.yto.service.RechargeFlowDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 应收明细-面单流水明细 Service 实现
 * @author Detective
 * @date Created in 2026/10/15
 */
@Slf4j
@Service
public class RechargeFlowDetailServiceImpl extends ServiceImpl<RechargeFlowDetailMapper, RechargeFlowDetail>
        implements RechargeFlowDetailService {

    @Override
    public void update(RechargeFlowDetail detail) {
        updateById(detail);
    }

    @Override
    public List<RechargeFlowDetail> list(String fromCustName, String fromCustCode, String toCustCode, String toCustName,
                                          String operateType, String payType,
                                          LocalDateTime startTime, LocalDateTime endTime, Integer matchStatus) {
        QueryWrapper<RechargeFlowDetail> wrapper = buildQueryWrapper(fromCustName, fromCustCode, toCustCode,
                toCustName, operateType, payType, startTime, endTime, matchStatus);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public IPage<RechargeFlowDetail> search(String fromCustName, String fromCustCode, String toCustCode, String toCustName,
                                             String operateType, String payType,
                                             LocalDateTime startTime, LocalDateTime endTime, Integer matchStatus,
                                             Integer pageNo, Integer pageSize) {
        QueryWrapper<RechargeFlowDetail> wrapper = buildQueryWrapper(fromCustName, fromCustCode, toCustCode,
                toCustName, operateType, payType, startTime, endTime, matchStatus);
        return baseMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
    }

    @Override
    @Transactional
    public Integer importExcel(MultipartFile file) {
        List<RechargeFlowExcelDTO> importList;
        try {
            importList = EasyExcel.read(file.getInputStream())
                    .head(RechargeFlowExcelDTO.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error("读取导入文件失败", e);
            throw new BusinessException("读取文件失败");
        }
        if (importList == null || importList.isEmpty()) {
            return 0;
        }
        List<RechargeFlowDetail> detailList = new ArrayList<>();
        for (RechargeFlowExcelDTO dto : importList) {
            RechargeFlowDetail detail = new RechargeFlowDetail();
            // 来源网点名称按"|"切割：1个元素=from_cust_name；2~4个元素依次=shop_id/shop_name/cust_code/cust_name
            parseFromShop(dto.getFromShopName(), detail);
            // 目的网点按"|"切割：1~4个元素依次=shop_id/shop_name/cust_code/cust_name
            parseToShop(dto.getToShopName(), detail);
            detail.setOperateType(dto.getOperateType());
            detail.setRechargePlatform(dto.getRechargePlatform());
            detail.setStock(dto.getStock());
            detail.setOperateQty(dto.getOperateQty());
            detail.setAfterStock(dto.getAfterStock());
            detail.setOperateTime(dto.getOperateTime());
            detail.setOperator(dto.getOperator());
            detail.setRemark(dto.getRemark());
            detail.setMatchStatus(0);
            detail.setCreateTime(LocalDateTime.now());
            detail.setUpdateTime(LocalDateTime.now());
            detailList.add(detail);
        }
        saveBatch(detailList);
        return detailList.size();
    }

    /**
     * 来源网点名称按"|"切割拆分
     * 1个元素 → from_cust_name
     * 2个元素 → from_shop_id, from_shop_name
     * 3个元素 → from_shop_id, from_shop_name, from_cust_code
     * 4个元素 → from_shop_id, from_shop_name, from_cust_code, from_cust_name
     */
    private void parseFromShop(String raw, RechargeFlowDetail detail) {
        if (raw == null || raw.trim().isEmpty()) {
            return;
        }
        String[] parts = raw.split("\\|");
        if (parts.length == 1) {
            detail.setFromCustName(parts[0].trim());
        } else {
            if (parts.length >= 1) detail.setFromShopId(parts[0].trim());
            if (parts.length >= 2) detail.setFromShopName(parts[1].trim());
            if (parts.length >= 3) detail.setFromCustCode(parts[2].trim());
            if (parts.length >= 4) detail.setFromCustName(parts[3].trim());
        }
    }

    /**
     * 目的网点按"|"切割拆分
     * 1个元素 → to_shop_id
     * 2个元素 → to_shop_id, to_shop_name
     * 3个元素 → to_shop_id, to_shop_name, to_cust_code
     * 4个元素 → to_shop_id, to_shop_name, to_cust_code, to_cust_name
     */
    private void parseToShop(String raw, RechargeFlowDetail detail) {
        if (raw == null || raw.trim().isEmpty()) {
            return;
        }
        String[] parts = raw.split("\\|");
        if (parts.length >= 1) detail.setToShopId(parts[0].trim());
        if (parts.length >= 2) detail.setToShopName(parts[1].trim());
        if (parts.length >= 3) detail.setToCustCode(parts[2].trim());
        if (parts.length >= 4) detail.setToCustName(parts[3].trim());
    }

    /**
     * 构建查询条件：客户名称模糊匹配，编码/类型精确匹配，操作时间左闭右闭，匹配状态精确
     */
    private QueryWrapper<RechargeFlowDetail> buildQueryWrapper(String fromCustName, String fromCustCode,
                                                                String toCustCode, String toCustName,
                                                                String operateType, String payType,
                                                                LocalDateTime startTime, LocalDateTime endTime,
                                                                Integer matchStatus) {
        QueryWrapper<RechargeFlowDetail> wrapper = new QueryWrapper<>();
        if (fromCustName != null && !fromCustName.trim().isEmpty()) {
            wrapper.like(RechargeFlowDetail.COL_FROM_CUST_NAME, fromCustName.trim());
        }
        if (fromCustCode != null && !fromCustCode.trim().isEmpty()) {
            wrapper.eq(RechargeFlowDetail.COL_FROM_CUST_CODE, fromCustCode.trim());
        }
        if (toCustCode != null && !toCustCode.trim().isEmpty()) {
            wrapper.eq(RechargeFlowDetail.COL_TO_CUST_CODE, toCustCode.trim());
        }
        if (toCustName != null && !toCustName.trim().isEmpty()) {
            wrapper.like(RechargeFlowDetail.COL_TO_CUST_NAME, toCustName.trim());
        }
        if (operateType != null && !operateType.trim().isEmpty()) {
            wrapper.eq(RechargeFlowDetail.COL_OPERATE_TYPE, operateType.trim());
        }
        if (payType != null && !payType.trim().isEmpty()) {
            wrapper.eq(RechargeFlowDetail.COL_PAY_TYPE, payType.trim());
        }
        // 操作时间范围查询：左闭右闭
        if (startTime != null && endTime != null) {
            wrapper.ge(RechargeFlowDetail.COL_OPERATE_TIME, startTime)
                    .le(RechargeFlowDetail.COL_OPERATE_TIME, endTime);
        } else if (startTime != null) {
            wrapper.ge(RechargeFlowDetail.COL_OPERATE_TIME, startTime);
        } else if (endTime != null) {
            wrapper.le(RechargeFlowDetail.COL_OPERATE_TIME, endTime);
        }
        if (matchStatus != null) {
            wrapper.eq(RechargeFlowDetail.COL_MATCH_STATUS, matchStatus);
        }
        wrapper.orderByDesc(RechargeFlowDetail.COL_OPERATE_TIME);
        return wrapper;
    }
}
