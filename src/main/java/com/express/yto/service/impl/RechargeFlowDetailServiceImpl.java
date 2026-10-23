package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.PrepaymentMapper;
import com.express.yto.dao.RechargeCashRelationMapper;
import com.express.yto.dao.RechargeFlowDetailMapper;
import com.express.yto.dao.ShopEmpMapper;
import com.express.yto.dto.RechargeFlowExcelDTO;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.Prepayment;
import com.express.yto.model.RechargeCashRelation;
import com.express.yto.model.RechargeFlowDetail;
import com.express.yto.model.ShopEmp;
import com.express.yto.service.RechargeFlowDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应收明细-面单流水明细 Service 实现
 * @author Detective
 * @date Created in 2026/10/15
 */
@Slf4j
@Service
public class RechargeFlowDetailServiceImpl extends ServiceImpl<RechargeFlowDetailMapper, RechargeFlowDetail>
        implements RechargeFlowDetailService {

    @Autowired
    private RechargeCashRelationMapper rechargeCashRelationMapper;

    @Autowired
    private ShopEmpMapper shopEmpMapper;

    @Autowired
    private PrepaymentMapper prepaymentMapper;

    @Override
    public void update(RechargeFlowDetail detail) {
        updateById(detail);
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的记录");
        }
        // 先删除关联关系表中该应收流水的匹配关系，避免产生孤儿关联数据
        QueryWrapper<RechargeCashRelation> relationWrapper = new QueryWrapper<>();
        relationWrapper.in(RechargeCashRelation.COL_RECHARGE_ID, ids);
        int relationCount = rechargeCashRelationMapper.delete(relationWrapper);
        int count = baseMapper.deleteByIds(ids);
        log.info("批量删除应收流水：删除{}条，清理关联关系{}条，ids={}", count, relationCount, ids);
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
        // 统一排序：按操作时间升序（时间小的在前、大的在后），避免导入文件本身排序不一致
        detailList.sort(Comparator.comparing(RechargeFlowDetail::getOperateTime,
                Comparator.nullsLast(Comparator.naturalOrder())));
        saveBatch(detailList);
        // 导入完成后执行自动匹配（入库/冻结/解冻/转单标已匹配；充值跨表计算单价金额）
        autoMatch();
        return detailList.size();
    }

    @Override
    @Transactional
    public void autoMatch() {
        // ===== 部分A：简单清洗 + 标记已匹配（match_status=1），仅处理未匹配记录 =====
        // 1. operate_type=入库 → pay_type=入库
        baseMapper.update(null, new UpdateWrapper<RechargeFlowDetail>()
                .eq(RechargeFlowDetail.COL_MATCH_STATUS, 0)
                .eq(RechargeFlowDetail.COL_OPERATE_TYPE, "入库")
                .set(RechargeFlowDetail.COL_PAY_TYPE, "入库")
                .set(RechargeFlowDetail.COL_MATCH_STATUS, 1));
        // 2. operate_type=冻结/解冻 → pay_type=转单
        baseMapper.update(null, new UpdateWrapper<RechargeFlowDetail>()
                .eq(RechargeFlowDetail.COL_MATCH_STATUS, 0)
                .in(RechargeFlowDetail.COL_OPERATE_TYPE, "冻结", "解冻")
                .set(RechargeFlowDetail.COL_PAY_TYPE, "转单")
                .set(RechargeFlowDetail.COL_MATCH_STATUS, 1));
        // 3. remark 包含"转移" → pay_type=转单
        baseMapper.update(null, new UpdateWrapper<RechargeFlowDetail>()
                .eq(RechargeFlowDetail.COL_MATCH_STATUS, 0)
                .like(RechargeFlowDetail.COL_REMARK, "转移")
                .set(RechargeFlowDetail.COL_PAY_TYPE, "转单")
                .set(RechargeFlowDetail.COL_MATCH_STATUS, 1));
        // 4. 在线支付自助充值：remark包含"在线支付" + operator="在线购买" + operate_type="充值" → pay_type="自助充值"，标记已匹配
        baseMapper.update(null, new UpdateWrapper<RechargeFlowDetail>()
                .eq(RechargeFlowDetail.COL_MATCH_STATUS, 0)
                .like(RechargeFlowDetail.COL_REMARK, "在线支付")
                .eq(RechargeFlowDetail.COL_OPERATOR, "在线购买")
                .eq(RechargeFlowDetail.COL_OPERATE_TYPE, "充值")
                .set(RechargeFlowDetail.COL_PAY_TYPE, "自助充值")
                .set(RechargeFlowDetail.COL_MATCH_STATUS, 1));

        // ===== 部分B：充值记录跨表匹配（to_shop_id → t_shop_emp.code → t_prepayment），匹配状态不变，仅处理未匹配记录 =====
        List<RechargeFlowDetail> rechargeList = baseMapper.selectList(new QueryWrapper<RechargeFlowDetail>()
                .eq(RechargeFlowDetail.COL_MATCH_STATUS, 0)
                .eq(RechargeFlowDetail.COL_OPERATE_TYPE, "充值")
                .isNotNull(RechargeFlowDetail.COL_TO_SHOP_ID)
                .ne(RechargeFlowDetail.COL_TO_SHOP_ID, ""));
        if (rechargeList.isEmpty()) {
            return;
        }
        // 收集所有 to_shop_id，批量查 t_shop_emp
        Set<String> shopIds = rechargeList.stream()
                .map(RechargeFlowDetail::getToShopId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<ShopEmp> shopEmpList = shopEmpMapper.selectList(new QueryWrapper<ShopEmp>()
                .in(ShopEmp.COL_SHOP_ID, shopIds));
        // shopId → prepayment查询code：emp_type=淘宝→yto_576017；限定→yto_576017_limit；其他→自身code
        Map<String, String> shopIdToCode = shopEmpList.stream()
                .filter(s -> s.getShopId() != null)
                .filter(s -> {
                    String empType = s.getEmpType();
                    return "淘宝".equals(empType) || "限定".equals(empType) || s.getCode() != null;
                })
                .collect(Collectors.toMap(ShopEmp::getShopId, this::resolvePrepaymentCode, (a, b) -> a));
        if (shopIdToCode.isEmpty()) {
            log.warn("自动匹配：充值记录的 to_shop_id 在 t_shop_emp 中均未找到对应 code");
            return;
        }
        // 批量查 t_prepayment，按 code 分组
        Set<String> codes = new HashSet<>(shopIdToCode.values());
        List<Prepayment> prepaymentList = prepaymentMapper.selectList(new QueryWrapper<Prepayment>()
                .in(Prepayment.COL_CODE, codes));
        Map<String, List<Prepayment>> prepayByCode = prepaymentList.stream()
                .filter(p -> p.getCode() != null)
                .collect(Collectors.groupingBy(Prepayment::getCode));

        // 逐条匹配 [start_time, end_time) 区间内的 prepayment，回写 unit_price 和 amount
        int matchedCount = 0;
        int noDepositCount = 0;
        for (RechargeFlowDetail detail : rechargeList) {
            String code = shopIdToCode.get(detail.getToShopId());
            if (code == null) {
                continue;
            }
            List<Prepayment> ranges = prepayByCode.get(code);
            if (ranges == null || ranges.isEmpty() || detail.getOperateTime() == null) {
                continue;
            }
            LocalDate opDate = detail.getOperateTime().toLocalDate();
            Prepayment matched = null;
            for (Prepayment p : ranges) {
                // [start_time, end_time) 左闭右开，与项目价格区间规则一致
                if (p.getStartTime() != null && !p.getStartTime().isAfter(opDate)
                        && p.getEndTime() != null && p.getEndTime().isAfter(opDate)) {
                    matched = p;
                    break;
                }
            }
            if (matched == null || matched.getPreFee() == null) {
                continue;
            }
            detail.setUnitPrice(matched.getPreFee());
            if (detail.getOperateQty() != null) {
                detail.setAmount(matched.getPreFee().multiply(BigDecimal.valueOf(detail.getOperateQty())));
            }
            // 预付款为0：无押金模式，支付方式标记"无押金"并视为已匹配（无实收流水对应，不再参与后续匹配）
            if (matched.getPreFee().compareTo(BigDecimal.ZERO) == 0) {
                detail.setPayType("无押金");
                detail.setMatchStatus(1);
                noDepositCount++;
            }
            baseMapper.updateById(detail);
            matchedCount++;
        }
        log.info("自动匹配完成：充值记录跨表匹配成功{}条（其中无押金{}条）", matchedCount, noDepositCount);
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
     * 根据店铺的 emp_type 决定查询 t_prepayment 时使用的 code
     * 淘宝 → yto_576017；限定 → yto_576017_limit；其他 → 店铺自身 code
     */
    private String resolvePrepaymentCode(ShopEmp shopEmp) {
        if (shopEmp == null) {
            return null;
        }
        String empType = shopEmp.getEmpType();
        if ("淘宝".equals(empType)) {
            return "yto_576017";
        } else if ("限定".equals(empType)) {
            return "yto_576017_limit";
        }
        return shopEmp.getCode();
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
        // 按操作时间正序（时间小的在前），NULL值排最后
        wrapper.orderByAsc(RechargeFlowDetail.COL_OPERATE_TIME + " IS NULL")
                .orderByAsc(RechargeFlowDetail.COL_OPERATE_TIME);
        return wrapper;
    }
}
