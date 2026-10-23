package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.CashAccountMapper;
import com.express.yto.dao.CashFlowDetailMapper;
import com.express.yto.dao.PrepaymentMapper;
import com.express.yto.dao.RechargeCashRelationMapper;
import com.express.yto.dao.RechargeFlowDetailMapper;
import com.express.yto.dao.ShopEmpMapper;
import com.express.yto.dto.RechargeFlowExcelDTO;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.CashAccount;
import com.express.yto.model.CashFlowDetail;
import com.express.yto.model.CustPayerRelation;
import com.express.yto.model.Prepayment;
import com.express.yto.model.RechargeCashRelation;
import com.express.yto.model.RechargeFlowDetail;
import com.express.yto.model.ShopEmp;
import com.express.yto.service.CustPayerRelationService;
import com.express.yto.service.RechargeFlowDetailService;
import com.express.yto.util.PayerNameUtil;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
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

    @Autowired
    private CashFlowDetailMapper cashFlowDetailMapper;

    @Autowired
    private CashAccountMapper cashAccountMapper;

    @Autowired
    private CustPayerRelationService custPayerRelationService;

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

        // ===== 部分C：客户-付款方映射真匹配（三轮分层判定，唯一命中才自动匹配，其余留人工） =====
        autoMatchByRelation();
    }

    /**
     * 自动匹配时间窗口（天）：应收与实收操作时间允许的最大偏差。
     * 窗口仅用于收敛候选集，唯一性才是自动匹配的门槛
     */
    private static final int MATCH_WINDOW_DAYS = 3;

    /**
     * 部分C：客户-付款方映射真匹配，以每笔未匹配实收流水为锚，全量扫描未匹配池：
     * L1 单笔精确：映射锁定客户 + 金额相等 + 窗口内候选唯一 → 1对1
     * L2 全集求和：映射锁定客户 + 窗口内该客户全部未匹配应收求和=实收金额，且窗口内该客户实收唯一 → 1对N
     * L3 全局唯一：同金额+窗口内应收唯一，且同金额其他实收的窗口不覆盖该应收 → 1对1（客户由应收推断，同时沉淀新映射）
     * 多平台拆分转账（一笔应付多笔凑出）不做组合搜索，统一留人工
     */
    private void autoMatchByRelation() {
        // 1. 加载内存数据：未匹配实收（收入方向、有付款方名称）、未匹配应收（有客户编码、有金额）
        List<CashFlowDetail> cashList = cashFlowDetailMapper.selectList(new QueryWrapper<CashFlowDetail>()
                .eq(CashFlowDetail.COL_MATCH_STATUS, 0)
                .eq(CashFlowDetail.COL_ACTUAL_TYPE, 1)
                .isNotNull(CashFlowDetail.COL_CUST_NAME)
                .ne(CashFlowDetail.COL_CUST_NAME, "")
                .isNotNull(CashFlowDetail.COL_OPERATE_TIME)
                .isNotNull(CashFlowDetail.COL_AMOUNT));
        if (cashList.isEmpty()) {
            log.info("客户-付款方自动匹配：无未匹配实收流水，结束");
            return;
        }
        List<RechargeFlowDetail> rechargeList = baseMapper.selectList(new QueryWrapper<RechargeFlowDetail>()
                .eq(RechargeFlowDetail.COL_MATCH_STATUS, 0)
                .isNotNull(RechargeFlowDetail.COL_TO_CUST_CODE)
                .ne(RechargeFlowDetail.COL_TO_CUST_CODE, "")
                .isNotNull(RechargeFlowDetail.COL_OPERATE_TIME)
                .isNotNull(RechargeFlowDetail.COL_AMOUNT));
        if (rechargeList.isEmpty()) {
            log.info("客户-付款方自动匹配：无未匹配应收流水，结束");
            return;
        }
        // 付款方映射表：键→客户编码集合（同一键对应多个客户为歧义，锁定阶段不使用）
        List<CustPayerRelation> relations = custPayerRelationService.listAll();
        Map<String, Set<String>> payerToCustCodes = relations.stream()
                .filter(r -> r.getPayerName() != null && !r.getPayerName().trim().isEmpty()
                        && r.getCustCode() != null && !r.getCustCode().trim().isEmpty())
                .collect(Collectors.groupingBy(CustPayerRelation::getPayerName,
                        Collectors.mapping(r -> r.getCustCode().trim(), Collectors.toSet())));
        // 每笔实收预计算映射键并锁定客户（微信付款方名称先剥前缀再查映射；null=映射缺失或多客户歧义）
        Map<Long, String> cashIdToCustCode = new HashMap<>();
        for (CashFlowDetail cash : cashList) {
            String payerKey = PayerNameUtil.resolvePayerKey(cash.getCustName(), cash.getPlatform());
            Set<String> custCodes = payerToCustCodes.get(payerKey);
            if (custCodes != null && custCodes.size() == 1) {
                cashIdToCustCode.put(cash.getId(), custCodes.iterator().next());
            }
        }
        // 配对结果与占用集合（同一批跑批内应收不重复使用）
        Set<Long> usedRechargeIds = new HashSet<>();
        Map<Long, List<Long>> matchPairs = new LinkedHashMap<>();
        Map<Long, String> matchedCustCodes = new HashMap<>();
        int l1Count = 0;
        int l2Count = 0;
        int l3Count = 0;

        // 2. 第一轮 L1 单笔精确：映射锁定客户 + 金额相等 + 窗口内候选唯一 → 1对1
        for (CashFlowDetail cash : cashList) {
            if (matchPairs.containsKey(cash.getId())) {
                continue;
            }
            String custCode = cashIdToCustCode.get(cash.getId());
            if (custCode == null) {
                continue;
            }
            List<RechargeFlowDetail> candidates = filterCandidates(rechargeList, usedRechargeIds,
                    r -> custCode.equals(r.getToCustCode().trim())
                            && r.getAmount().compareTo(cash.getAmount()) == 0
                            && withinWindow(cash.getOperateTime(), r.getOperateTime()));
            if (candidates.size() == 1) {
                recordMatch(cash, candidates.get(0), custCode, matchPairs, matchedCustCodes, usedRechargeIds);
                l1Count++;
            }
        }

        // 3. 第二轮 L2 全集求和：映射锁定客户 + 窗口内该客户全部未匹配应收求和=实收金额，
        // 且窗口内锁到同一客户的其他未匹配实收不存在（全集切分唯一）→ 1对N
        for (CashFlowDetail cash : cashList) {
            if (matchPairs.containsKey(cash.getId())) {
                continue;
            }
            String custCode = cashIdToCustCode.get(cash.getId());
            if (custCode == null) {
                continue;
            }
            List<RechargeFlowDetail> windowAll = filterCandidates(rechargeList, usedRechargeIds,
                    r -> custCode.equals(r.getToCustCode().trim())
                            && withinWindow(cash.getOperateTime(), r.getOperateTime()));
            if (windowAll.isEmpty()) {
                continue;
            }
            BigDecimal sum = windowAll.stream()
                    .map(RechargeFlowDetail::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (sum.compareTo(cash.getAmount()) != 0) {
                continue;
            }
            boolean otherCashInWindow = cashList.stream()
                    .filter(c2 -> !c2.getId().equals(cash.getId()) && !matchPairs.containsKey(c2.getId()))
                    .anyMatch(c2 -> custCode.equals(cashIdToCustCode.get(c2.getId()))
                            && withinWindow(cash.getOperateTime(), c2.getOperateTime()));
            if (otherCashInWindow) {
                continue;
            }
            List<Long> rechargeIds = windowAll.stream().map(RechargeFlowDetail::getId).collect(Collectors.toList());
            matchPairs.put(cash.getId(), rechargeIds);
            matchedCustCodes.put(cash.getId(), custCode);
            usedRechargeIds.addAll(rechargeIds);
            l2Count++;
        }

        // 4. 第三轮 L3 全局唯一兜底（映射缺失/歧义的实收也可用）：
        // 同金额+窗口内未匹配应收唯一，且同金额其他实收的窗口不覆盖该应收（归属唯一）→ 1对1，客户由应收推断
        for (CashFlowDetail cash : cashList) {
            if (matchPairs.containsKey(cash.getId())) {
                continue;
            }
            List<RechargeFlowDetail> candidates = filterCandidates(rechargeList, usedRechargeIds,
                    r -> r.getAmount().compareTo(cash.getAmount()) == 0
                            && withinWindow(cash.getOperateTime(), r.getOperateTime()));
            if (candidates.size() != 1) {
                continue;
            }
            RechargeFlowDetail candidate = candidates.get(0);
            boolean ambiguous = cashList.stream()
                    .filter(c2 -> !c2.getId().equals(cash.getId()) && !matchPairs.containsKey(c2.getId()))
                    .filter(c2 -> c2.getAmount().compareTo(cash.getAmount()) == 0)
                    .anyMatch(c2 -> withinWindow(c2.getOperateTime(), candidate.getOperateTime()));
            if (ambiguous) {
                continue;
            }
            recordMatch(cash, candidate, candidate.getToCustCode().trim(),
                    matchPairs, matchedCustCodes, usedRechargeIds);
            l3Count++;
        }

        // 5. 落库：插关联、批量回写状态、沉淀映射
        if (matchPairs.isEmpty()) {
            log.info("客户-付款方自动匹配完成：无新增匹配，其余留人工");
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        Map<Long, CashFlowDetail> cashById = cashList.stream()
                .collect(Collectors.toMap(CashFlowDetail::getId, c -> c, (a, b) -> a));
        // 关联表逐对插入（与手动匹配一致）
        int relationCount = 0;
        for (Map.Entry<Long, List<Long>> entry : matchPairs.entrySet()) {
            for (Long rechargeId : entry.getValue()) {
                RechargeCashRelation relation = new RechargeCashRelation();
                relation.setRechargeId(rechargeId);
                relation.setCashId(entry.getKey());
                relation.setRelateTime(now);
                relation.setCreateTime(now);
                relation.setUpdateTime(now);
                rechargeCashRelationMapper.insert(relation);
                relationCount++;
            }
        }
        // 实收回写：match_status=1、category=面单费（一次性批量）
        UpdateWrapper<CashFlowDetail> cashUpdate = new UpdateWrapper<>();
        cashUpdate.in("id", matchPairs.keySet())
                .set("match_status", 1)
                .set("category", "面单费");
        cashFlowDetailMapper.update(null, cashUpdate);
        // 应收回写：match_status=1、pay_type=实收账户名称；按账户名称分组批量更新，无账户不覆盖支付方式
        Set<Long> accountIds = matchPairs.keySet().stream()
                .map(cashById::get)
                .map(CashFlowDetail::getAccountId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> accountNameMap = accountIds.isEmpty() ? Collections.emptyMap()
                : cashAccountMapper.selectList(new QueryWrapper<CashAccount>()
                        .select(CashAccount.COL_ID, CashAccount.COL_ACCOUNT_NAME)
                        .in(CashAccount.COL_ID, accountIds))
                .stream()
                .collect(Collectors.toMap(CashAccount::getId, CashAccount::getAccountName, (a, b) -> a));
        Map<String, List<Long>> payTypeGroups = new HashMap<>();
        for (Map.Entry<Long, List<Long>> entry : matchPairs.entrySet()) {
            String accountName = accountNameMap.get(cashById.get(entry.getKey()).getAccountId());
            payTypeGroups.computeIfAbsent(accountName, k -> new ArrayList<>()).addAll(entry.getValue());
        }
        for (Map.Entry<String, List<Long>> group : payTypeGroups.entrySet()) {
            UpdateWrapper<RechargeFlowDetail> rechargeUpdate = new UpdateWrapper<>();
            rechargeUpdate.in("id", group.getValue()).set("match_status", 1);
            if (group.getKey() != null) {
                rechargeUpdate.set("pay_type", group.getKey());
            }
            baseMapper.update(null, rechargeUpdate);
        }
        // 沉淀映射：配对成功的实收按推断出的客户编码学习（幂等；L3配对沉淀的是新知识）
        for (Map.Entry<Long, List<Long>> entry : matchPairs.entrySet()) {
            CashFlowDetail cash = cashById.get(entry.getKey());
            try {
                custPayerRelationService.upsertRelation(matchedCustCodes.get(entry.getKey()),
                        cash.getCustName(), cash.getAccountId(), cash.getPlatform());
            } catch (Exception e) {
                log.warn("自动匹配沉淀客户-付款方映射失败（不影响匹配结果）：cashId={}", entry.getKey(), e);
            }
        }
        log.info("客户-付款方自动匹配完成：L1单笔精确{}笔，L2全集求和{}笔，L3全局唯一{}笔，"
                + "新增关联{}条，其余留人工", l1Count, l2Count, l3Count, relationCount);
    }

    /**
     * 从未占用应收中按条件筛选候选
     */
    private List<RechargeFlowDetail> filterCandidates(List<RechargeFlowDetail> rechargeList,
                                                      Set<Long> usedRechargeIds,
                                                      Predicate<RechargeFlowDetail> condition) {
        return rechargeList.stream()
                .filter(r -> !usedRechargeIds.contains(r.getId()))
                .filter(condition)
                .collect(Collectors.toList());
    }

    /**
     * 应收操作时间是否落在以实收时间为基准的时间窗口内（±MATCH_WINDOW_DAYS 天）
     */
    private boolean withinWindow(LocalDateTime cashTime, LocalDateTime rechargeTime) {
        return !rechargeTime.isBefore(cashTime.minusDays(MATCH_WINDOW_DAYS))
                && !rechargeTime.isAfter(cashTime.plusDays(MATCH_WINDOW_DAYS));
    }

    /**
     * 记录一笔配对结果：实收id→应收id列表，占用该应收
     */
    private void recordMatch(CashFlowDetail cash, RechargeFlowDetail recharge, String custCode,
                             Map<Long, List<Long>> matchPairs, Map<Long, String> matchedCustCodes,
                             Set<Long> usedRechargeIds) {
        matchPairs.computeIfAbsent(cash.getId(), k -> new ArrayList<>()).add(recharge.getId());
        matchedCustCodes.put(cash.getId(), custCode);
        usedRechargeIds.add(recharge.getId());
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
