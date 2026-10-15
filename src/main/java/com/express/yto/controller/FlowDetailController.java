package com.express.yto.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.RestResult;
import com.express.yto.dto.RechargeCashMatchInput;
import com.express.yto.model.CashFlowDetail;
import com.express.yto.model.RechargeFlowDetail;
import com.express.yto.service.CashFlowDetailService;
import com.express.yto.service.RechargeCashRelationService;
import com.express.yto.service.RechargeFlowDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流水明细 Controller（应收、实收等流水统一入口）
 * @author Detective
 * @date Created in 2026/10/15
 */
@RestController
@RequestMapping("/flowDetail")
public class FlowDetailController {

    @Autowired
    private RechargeFlowDetailService rechargeFlowDetailService;

    @Autowired
    private CashFlowDetailService cashFlowDetailService;

    @Autowired
    private RechargeCashRelationService rechargeCashRelationService;

    /**
     * 应收-实收多对多匹配
     * 传入应收id列表和实收id列表，笛卡尔积插入关联表，同时更新两张表 match_status 为1（已匹配）
     */
    @PostMapping("/match")
    public RestResult<String> match(@RequestBody RechargeCashMatchInput input) {
        rechargeCashRelationService.match(input.getRechargeIds(), input.getCashIds());
        return RestResult.ok("匹配成功");
    }

    /**
     * 取消应收-实收匹配
     * 删除关联记录，若无剩余关联则 match_status 改回0（未匹配）
     */
    @PostMapping("/unmatch")
    public RestResult<String> unmatch(@RequestBody RechargeCashMatchInput input) {
        rechargeCashRelationService.unmatch(input.getRechargeIds(), input.getCashIds());
        return RestResult.ok("取消匹配成功");
    }

    /**
     * 面单流水明细导入
     * 来源网点名称、目的网点按"|"切割拆分到对应字段
     */
    @PostMapping("/recharge/import")
    public RestResult<Integer> rechargeImport(@RequestParam("file") MultipartFile file) {
        return RestResult.ok(rechargeFlowDetailService.importExcel(file));
    }

    /**
     * 修改面单流水明细
     */
    @PostMapping("/recharge/update")
    public RestResult<String> rechargeUpdate(@RequestBody RechargeFlowDetail detail) {
        rechargeFlowDetailService.update(detail);
        return RestResult.ok("操作成功");
    }

    /**
     * 修改实收流水明细
     */
    @PostMapping("/cash/update")
    public RestResult<String> cashUpdate(@RequestBody CashFlowDetail detail) {
        cashFlowDetailService.update(detail);
        return RestResult.ok("操作成功");
    }

    /**
     * 应收流水列表查询（不分页）
     * @param fromCustName 来源客户名称（可选，模糊匹配）
     * @param fromCustCode 来源客户编码（可选，精确匹配）
     * @param toCustCode 目的客户编码（可选，精确匹配）
     * @param toCustName 目的客户名称（可选，模糊匹配）
     * @param operateType 操作类型（可选，精确匹配）
     * @param payType 支付方式（可选，精确匹配）
     * @param startTime 操作时间起始（可选，格式 yyyy-MM-dd HH:mm:ss）
     * @param endTime 操作时间结束（可选，格式 yyyy-MM-dd HH:mm:ss）
     * @param matchStatus 匹配状态（可选，0未匹配，1部分匹配，2已匹配）
     */
    @GetMapping("/recharge/list")
    public RestResult<List<RechargeFlowDetail>> rechargeList(
            @RequestParam(value = "fromCustName", required = false) String fromCustName,
            @RequestParam(value = "fromCustCode", required = false) String fromCustCode,
            @RequestParam(value = "toCustCode", required = false) String toCustCode,
            @RequestParam(value = "toCustName", required = false) String toCustName,
            @RequestParam(value = "operateType", required = false) String operateType,
            @RequestParam(value = "payType", required = false) String payType,
            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(value = "matchStatus", required = false) Integer matchStatus) {
        return RestResult.ok(rechargeFlowDetailService.list(fromCustName, fromCustCode, toCustCode,
                toCustName, operateType, payType, startTime, endTime, matchStatus));
    }

    /**
     * 应收流水分页查询
     * @param fromCustName 来源客户名称（可选，模糊匹配）
     * @param fromCustCode 来源客户编码（可选，精确匹配）
     * @param toCustCode 目的客户编码（可选，精确匹配）
     * @param toCustName 目的客户名称（可选，模糊匹配）
     * @param operateType 操作类型（可选，精确匹配）
     * @param payType 支付方式（可选，精确匹配）
     * @param startTime 操作时间起始（可选，格式 yyyy-MM-dd HH:mm:ss）
     * @param endTime 操作时间结束（可选，格式 yyyy-MM-dd HH:mm:ss）
     * @param matchStatus 匹配状态（可选，0未匹配，1部分匹配，2已匹配）
     * @param pageNo 页码（默认1）
     * @param pageSize 每页条数（默认10）
     */
    @GetMapping("/recharge/search")
    public RestResult<IPage<RechargeFlowDetail>> rechargeSearch(
            @RequestParam(value = "fromCustName", required = false) String fromCustName,
            @RequestParam(value = "fromCustCode", required = false) String fromCustCode,
            @RequestParam(value = "toCustCode", required = false) String toCustCode,
            @RequestParam(value = "toCustName", required = false) String toCustName,
            @RequestParam(value = "operateType", required = false) String operateType,
            @RequestParam(value = "payType", required = false) String payType,
            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(value = "matchStatus", required = false) Integer matchStatus,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return RestResult.ok(rechargeFlowDetailService.search(fromCustName, fromCustCode, toCustCode,
                toCustName, operateType, payType, startTime, endTime, matchStatus, pageNo, pageSize));
    }
}
