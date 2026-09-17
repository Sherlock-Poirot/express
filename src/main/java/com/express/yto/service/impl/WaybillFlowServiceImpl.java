package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.express.yto.dao.SysUserMapper;
import com.express.yto.dao.WaybillFlowFileMapper;
import com.express.yto.dao.WaybillFlowStepMapper;
import com.express.yto.dto.WaybillFlowBatchDTO;
import com.express.yto.dto.WaybillFlowFileDTO;
import com.express.yto.dto.WaybillFlowStepDTO;
import com.express.yto.enums.WaybillFlowStatus;
import com.express.yto.enums.WaybillFlowStepEnum;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.SysUser;
import com.express.yto.model.WaybillFlowFile;
import com.express.yto.model.WaybillFlowStep;
import com.express.yto.service.WaybillFlowService;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 账单工作流服务实现
 * @author Detective
 * @date Created in 2026/9/16
 */
@Slf4j
@Service
public class WaybillFlowServiceImpl implements WaybillFlowService {

    @Autowired
    private WaybillFlowStepMapper waybillFlowStepMapper;

    @Autowired
    private WaybillFlowFileMapper waybillFlowFileMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public int initMonthlyFlow(String billMonth) {
        List<WaybillFlowStep> steps = Arrays.stream(WaybillFlowStepEnum.values())
                .map(e -> WaybillFlowStep.builder()
                        .billMonth(billMonth)
                        .stepCode(e.getCode())
                        .stepName(e.getName())
                        .stepOrder(e.getOrder())
                        .status(WaybillFlowStatus.WAITING.getCode())
                        .remark(e.getRemark())
                        .build())
                .collect(Collectors.toList());

        // INSERT IGNORE + uk_bill_month_step 唯一键：多实例并发/重复执行时数据库层面保证幂等，无需额外分布式锁组件
        int count = waybillFlowStepMapper.insertIgnoreBatch(steps);
        log.info("账单工作流[{}]初始化完成，新增步骤记录 {} 条", billMonth, count);
        return count;
    }

    @Override
    public List<WaybillFlowBatchDTO> listFlowBatches() {
        List<WaybillFlowStep> steps = waybillFlowStepMapper.selectList(new QueryWrapper<WaybillFlowStep>()
                .select(WaybillFlowStep.COL_BILL_MONTH, WaybillFlowStep.COL_STEP_CODE, WaybillFlowStep.COL_STEP_NAME,
                        WaybillFlowStep.COL_STEP_ORDER, WaybillFlowStep.COL_STATUS, WaybillFlowStep.COL_REMARK,
                        WaybillFlowStep.COL_ERROR_MSG, WaybillFlowStep.COL_OPERATOR,
                        WaybillFlowStep.COL_START_TIME, WaybillFlowStep.COL_END_TIME)
                .orderByDesc(WaybillFlowStep.COL_BILL_MONTH)
                .orderByAsc(WaybillFlowStep.COL_STEP_ORDER));
        if (steps.isEmpty()) {
            return Collections.emptyList();
        }

        // 文件表数据量小（每月几个文件），一次全量查出用于推导IMPORT步骤状态，避免按月循环查询
        List<WaybillFlowFile> files = waybillFlowFileMapper.selectList(new QueryWrapper<WaybillFlowFile>()
                .select(WaybillFlowFile.COL_BILL_MONTH, WaybillFlowFile.COL_FILE_NAME, WaybillFlowFile.COL_STATUS,
                        WaybillFlowFile.COL_ROW_COUNT, WaybillFlowFile.COL_ERROR_MSG, WaybillFlowFile.COL_OPERATOR,
                        WaybillFlowFile.COL_CREATE_TIME));
        Map<String, List<WaybillFlowFile>> filesByMonth = files.stream()
                .collect(Collectors.groupingBy(WaybillFlowFile::getBillMonth));

        // groupingBy 到 LinkedHashMap 保持 billMonth 倒序
        Map<String, List<WaybillFlowStep>> stepsByMonth = steps.stream()
                .collect(Collectors.groupingBy(WaybillFlowStep::getBillMonth, LinkedHashMap::new, Collectors.toList()));

        return stepsByMonth.entrySet().stream()
                .map(e -> buildBatch(e.getKey(), e.getValue(), filesByMonth.get(e.getKey()), false))
                .collect(Collectors.toList());
    }

    @Override
    public WaybillFlowBatchDTO getFlowDetail(String billMonth) {
        List<WaybillFlowStep> steps = waybillFlowStepMapper.selectList(new QueryWrapper<WaybillFlowStep>()
                .eq(WaybillFlowStep.COL_BILL_MONTH, billMonth)
                .orderByAsc(WaybillFlowStep.COL_STEP_ORDER));
        if (steps.isEmpty()) {
            return null;
        }
        List<WaybillFlowFile> files = waybillFlowFileMapper.selectList(new QueryWrapper<WaybillFlowFile>()
                .eq(WaybillFlowFile.COL_BILL_MONTH, billMonth)
                .orderByAsc(WaybillFlowFile.COL_CREATE_TIME));
        return buildBatch(billMonth, steps, files, true);
    }

    /**
     * 组装批次DTO：步骤状态转换 + IMPORT步骤状态推导 + 当前步骤计算
     */
    private WaybillFlowBatchDTO buildBatch(String billMonth, List<WaybillFlowStep> steps,
                                           List<WaybillFlowFile> files, boolean withFiles) {
        List<WaybillFlowFile> monthFiles = files == null ? Collections.emptyList() : files;

        List<WaybillFlowStepDTO> stepDTOs = steps.stream().map(step -> {
            String displayStatus = resolveDisplayStatus(step, monthFiles);
            WaybillFlowStepDTO dto = new WaybillFlowStepDTO();
            dto.setStepCode(step.getStepCode());
            dto.setStepName(step.getStepName());
            dto.setStepOrder(step.getStepOrder());
            dto.setStatus(displayStatus);
            dto.setStatusDesc(statusDesc(displayStatus));
            dto.setRemark(step.getRemark());
            dto.setErrorMsg(step.getErrorMsg());
            dto.setOperator(step.getOperator());
            dto.setStartTime(step.getStartTime());
            dto.setEndTime(step.getEndTime());
            return dto;
        }).collect(Collectors.toList());

        WaybillFlowBatchDTO batch = new WaybillFlowBatchDTO();
        batch.setBillMonth(billMonth);
        batch.setSteps(stepDTOs);
        batch.setCurrentStepCode(resolveCurrentStepCode(stepDTOs));
        if (withFiles) {
            batch.setImportFiles(monthFiles.stream().map(f -> {
                WaybillFlowFileDTO dto = new WaybillFlowFileDTO();
                dto.setFileName(f.getFileName());
                dto.setStatus(f.getStatus());
                dto.setStatusDesc(statusDesc(f.getStatus()));
                dto.setRowCount(f.getRowCount());
                dto.setErrorMsg(f.getErrorMsg());
                dto.setOperator(f.getOperator());
                dto.setCreateTime(f.getCreateTime());
                return dto;
            }).collect(Collectors.toList()));
        }
        return batch;
    }

    /**
     * 推导步骤的展示状态
     * 非IMPORT步骤直接取落库状态；IMPORT步骤由文件记录推导（PASSED人工确认态优先）
     */
    private String resolveDisplayStatus(WaybillFlowStep step, List<WaybillFlowFile> files) {
        if (!WaybillFlowStepEnum.IMPORT.getCode().equals(step.getStepCode())) {
            return step.getStatus();
        }
        // 人工已确认"导入完成"，直接终态
        if (WaybillFlowStatus.PASSED.getCode().equals(step.getStatus())
                || WaybillFlowStatus.SKIPPED.getCode().equals(step.getStatus())) {
            return step.getStatus();
        }
        if (files.isEmpty()) {
            return step.getStatus();
        }
        boolean hasRunning = files.stream()
                .anyMatch(f -> WaybillFlowStatus.RUNNING.getCode().equals(f.getStatus()));
        if (hasRunning) {
            return WaybillFlowStatus.RUNNING.getCode();
        }
        boolean hasFailed = files.stream()
                .anyMatch(f -> WaybillFlowStatus.FAILED.getCode().equals(f.getStatus()));
        if (hasFailed) {
            return WaybillFlowStatus.FAILED.getCode();
        }
        // 至少一个文件且全部成功（尚未人工确认，可继续导入）
        return WaybillFlowStatus.SUCCESS.getCode();
    }

    /**
     * 当前步骤 = 第一个非 PASSED/SKIPPED 的步骤（含SUCCESS，如IMPORT已导完但未人工确认）
     * 全部完成时停在最后一步
     */
    private String resolveCurrentStepCode(List<WaybillFlowStepDTO> steps) {
        for (WaybillFlowStepDTO step : steps) {
            if (!WaybillFlowStatus.PASSED.getCode().equals(step.getStatus())
                    && !WaybillFlowStatus.SKIPPED.getCode().equals(step.getStatus())) {
                return step.getStepCode();
            }
        }
        return steps.isEmpty() ? null : steps.get(steps.size() - 1).getStepCode();
    }

    private String statusDesc(String status) {
        for (WaybillFlowStatus s : WaybillFlowStatus.values()) {
            if (s.getCode().equals(status)) {
                return s.getDesc();
            }
        }
        return status;
    }

    @Override
    public void confirmStep(String billMonth, String stepCode) {
        if (!WaybillFlowStepEnum.IMPORT.getCode().equals(stepCode)
                && !WaybillFlowStepEnum.VALIDATE.getCode().equals(stepCode)) {
            throw new BusinessException("仅导入原始账单、校验运单数据步骤支持人工确认");
        }
        WaybillFlowStep step = getStepOrThrow(billMonth, stepCode);
        // 已确认则幂等返回
        if (WaybillFlowStatus.PASSED.getCode().equals(step.getStatus())) {
            return;
        }

        if (WaybillFlowStepEnum.IMPORT.getCode().equals(stepCode)) {
            // IMPORT：要求所有导入文件均成功（状态由文件记录推导，落库状态可能仍为WAITING）
            List<WaybillFlowFile> files = waybillFlowFileMapper.selectList(new QueryWrapper<WaybillFlowFile>()
                    .eq(WaybillFlowFile.COL_BILL_MONTH, billMonth));
            if (files.isEmpty()) {
                throw new BusinessException("请先导入账单文件");
            }
            if (files.stream().anyMatch(f -> WaybillFlowStatus.RUNNING.getCode().equals(f.getStatus()))) {
                throw new BusinessException("存在导入中的文件，请稍后再确认");
            }
            if (files.stream().anyMatch(f -> WaybillFlowStatus.FAILED.getCode().equals(f.getStatus()))) {
                throw new BusinessException("存在导入失败的文件，请处理后再确认");
            }
            // 条件更新防并发：仅WAITING/SUCCESS可置为PASSED
            updateStepStatus(billMonth, stepCode, WaybillFlowStatus.PASSED,
                    Arrays.asList(WaybillFlowStatus.WAITING, WaybillFlowStatus.SUCCESS));
        } else {
            // VALIDATE：要求校验已成功执行
            if (!WaybillFlowStatus.SUCCESS.getCode().equals(step.getStatus())) {
                throw new BusinessException("请先执行校验且校验成功后再确认");
            }
            updateStepStatus(billMonth, stepCode, WaybillFlowStatus.PASSED,
                    Collections.singletonList(WaybillFlowStatus.SUCCESS));
        }
        log.info("账单工作流[{}]步骤[{}]人工确认通过，操作人：{}", billMonth, stepCode, currentOperator());
    }

    @Override
    public void skipStep(String billMonth, String stepCode) {
        if (!WaybillFlowStepEnum.IMPORT_DIFF.getCode().equals(stepCode)) {
            throw new BusinessException("仅导入重量差异数据步骤支持跳过");
        }
        getStepOrThrow(billMonth, stepCode);
        int updated = updateStepStatus(billMonth, stepCode, WaybillFlowStatus.SKIPPED,
                Collections.singletonList(WaybillFlowStatus.WAITING));
        if (updated == 0) {
            throw new BusinessException("仅待执行状态可跳过");
        }
        log.info("账单工作流[{}]步骤[{}]已跳过，操作人：{}", billMonth, stepCode, currentOperator());
    }

    /**
     * 查询步骤记录，不存在则抛出业务异常
     */
    private WaybillFlowStep getStepOrThrow(String billMonth, String stepCode) {
        WaybillFlowStep step = waybillFlowStepMapper.selectOne(new QueryWrapper<WaybillFlowStep>()
                .eq(WaybillFlowStep.COL_BILL_MONTH, billMonth)
                .eq(WaybillFlowStep.COL_STEP_CODE, stepCode));
        if (step == null) {
            throw new BusinessException("账单工作流[" + billMonth + "]步骤[" + stepCode + "]不存在");
        }
        return step;
    }

    /**
     * 条件更新步骤状态（仅当当前状态在allowFrom范围内时更新），防止并发下状态被覆盖
     * @return 影响行数
     */
    private int updateStepStatus(String billMonth, String stepCode, WaybillFlowStatus target,
                                 List<WaybillFlowStatus> allowFrom) {
        UpdateWrapper<WaybillFlowStep> uw = new UpdateWrapper<>();
        uw.eq(WaybillFlowStep.COL_BILL_MONTH, billMonth)
                .eq(WaybillFlowStep.COL_STEP_CODE, stepCode)
                .in(WaybillFlowStep.COL_STATUS, allowFrom.stream()
                        .map(WaybillFlowStatus::getCode).collect(Collectors.toList()))
                .set(WaybillFlowStep.COL_STATUS, target.getCode())
                .set(WaybillFlowStep.COL_OPERATOR, currentOperator())
                .set(WaybillFlowStep.COL_END_TIME, new Date());
        return waybillFlowStepMapper.update(null, uw);
    }

    /**
     * 当前登录人姓名（无姓名时取账号），用于操作人记录
     */
    private String currentOperator() {
        Long userId = Long.valueOf(StpUtil.getLoginId().toString());
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return String.valueOf(userId);
        }
        return user.getRealName() != null ? user.getRealName() : user.getUsername();
    }

    @Override
    public void checkImportable(String billMonth) {
        WaybillFlowStep step = getStepOrThrow(billMonth, WaybillFlowStepEnum.IMPORT.getCode());
        if (WaybillFlowStatus.PASSED.getCode().equals(step.getStatus())) {
            throw new BusinessException("[" + billMonth + "]导入原始账单已完成人工确认，如需重新导入请先重置批次");
        }
    }

    @Override
    public WaybillFlowFile createImportFileRecord(String billMonth, String fileName, String taskNo) {
        WaybillFlowFile record = WaybillFlowFile.builder()
                .billMonth(billMonth)
                .fileName(fileName)
                .taskNo(taskNo)
                .status(WaybillFlowStatus.RUNNING.getCode())
                .rowCount(0)
                .operator(currentOperator())
                .build();
        waybillFlowFileMapper.insert(record);
        return record;
    }

    @Override
    public void finishImportFileRecord(String taskNo, boolean success, int rowCount, String errorMsg) {
        UpdateWrapper<WaybillFlowFile> uw = new UpdateWrapper<>();
        uw.eq(WaybillFlowFile.COL_TASK_NO, taskNo)
                .eq(WaybillFlowFile.COL_STATUS, WaybillFlowStatus.RUNNING.getCode())
                .set(WaybillFlowFile.COL_STATUS, success ? WaybillFlowStatus.SUCCESS.getCode()
                        : WaybillFlowStatus.FAILED.getCode())
                .set(WaybillFlowFile.COL_ROW_COUNT, rowCount)
                .set(WaybillFlowFile.COL_ERROR_MSG, errorMsg);
        int updated = waybillFlowFileMapper.update(null, uw);
        if (updated == 0) {
            log.warn("导入文件记录完成回调未命中RUNNING记录，taskNo={}", taskNo);
        }
    }

    @Override
    public void startStep(String billMonth, String stepCode) {
        WaybillFlowStep step = getStepOrThrow(billMonth, stepCode);
        if (WaybillFlowStatus.RUNNING.getCode().equals(step.getStatus())) {
            throw new BusinessException("[" + step.getStepName() + "]正在执行中，请勿重复操作");
        }
        checkPrecondition(billMonth, stepCode);
        // 条件更新防并发：仅允许重复执行的状态可置RUNNING
        int updated = waybillFlowStepMapper.update(null, new UpdateWrapper<WaybillFlowStep>()
                .eq(WaybillFlowStep.COL_BILL_MONTH, billMonth)
                .eq(WaybillFlowStep.COL_STEP_CODE, stepCode)
                .in(WaybillFlowStep.COL_STATUS, startAllowFrom(stepCode))
                .set(WaybillFlowStep.COL_STATUS, WaybillFlowStatus.RUNNING.getCode())
                .set(WaybillFlowStep.COL_START_TIME, new Date())
                .set(WaybillFlowStep.COL_ERROR_MSG, null)
                .set(WaybillFlowStep.COL_OPERATOR, currentOperator()));
        if (updated == 0) {
            throw new BusinessException("[" + step.getStepName() + "]当前状态[" + step.getStatus() + "]不允许执行");
        }
    }

    @Override
    public void finishStep(String billMonth, String stepCode, boolean success, String errorMsg) {
        int updated = waybillFlowStepMapper.update(null, new UpdateWrapper<WaybillFlowStep>()
                .eq(WaybillFlowStep.COL_BILL_MONTH, billMonth)
                .eq(WaybillFlowStep.COL_STEP_CODE, stepCode)
                .eq(WaybillFlowStep.COL_STATUS, WaybillFlowStatus.RUNNING.getCode())
                .set(WaybillFlowStep.COL_STATUS, success ? WaybillFlowStatus.SUCCESS.getCode()
                        : WaybillFlowStatus.FAILED.getCode())
                .set(WaybillFlowStep.COL_END_TIME, new Date())
                .set(WaybillFlowStep.COL_ERROR_MSG, errorMsg));
        if (updated == 0) {
            log.warn("步骤完成回调未命中RUNNING记录，billMonth={}，stepCode={}，success={}", billMonth, stepCode, success);
        } else {
            log.info("账单工作流[{}]步骤[{}]执行{}", billMonth, stepCode, success ? "成功" : "失败：" + errorMsg);
        }
    }

    /**
     * 步骤前置条件校验：前置步骤未达标时抛出业务异常
     */
    private void checkPrecondition(String billMonth, String stepCode) {
        Map<String, WaybillFlowStep> byCode = waybillFlowStepMapper.selectList(
                        new QueryWrapper<WaybillFlowStep>().eq(WaybillFlowStep.COL_BILL_MONTH, billMonth))
                .stream()
                .collect(Collectors.toMap(WaybillFlowStep::getStepCode, s -> s));

        String importPassed = WaybillFlowStatus.PASSED.getCode();
        String success = WaybillFlowStatus.SUCCESS.getCode();
        String skipped = WaybillFlowStatus.SKIPPED.getCode();

        switch (stepCode) {
            case "IMPORT_DIFF":
                requireStepStatus(byCode, WaybillFlowStepEnum.IMPORT.getCode(), importPassed,
                        "请先完成[导入原始账单]（含人工确认）");
                break;
            case "CLEAN":
                requireStepStatus(byCode, WaybillFlowStepEnum.IMPORT.getCode(), importPassed,
                        "请先完成[导入原始账单]（含人工确认）");
                requireStepStatusAny(byCode, WaybillFlowStepEnum.IMPORT_DIFF.getCode(),
                        Arrays.asList(success, skipped), "请先完成[导入重量差异数据]或将其跳过");
                break;
            case "VALIDATE":
                requireStepStatus(byCode, WaybillFlowStepEnum.CLEAN.getCode(), success,
                        "请先完成[清洗运单数据]");
                break;
            case "CALCULATE":
                requireStepStatus(byCode, WaybillFlowStepEnum.VALIDATE.getCode(), importPassed,
                        "请先完成[校验运单数据]（含人工核查确认）");
                break;
            default:
                throw new BusinessException("步骤[" + stepCode + "]不支持通过工作流启动");
        }
    }

    /**
     * 启动步骤允许的来源状态
     */
    private List<WaybillFlowStatus> startAllowFrom(String stepCode) {
        switch (stepCode) {
            case "IMPORT_DIFF":
                // 重导差异：成功/失败/跳过后均可重新执行
                return Arrays.asList(WaybillFlowStatus.WAITING, WaybillFlowStatus.SUCCESS,
                        WaybillFlowStatus.FAILED, WaybillFlowStatus.SKIPPED);
            case "CALCULATE":
                // 成功后不可重跑（防止覆盖人工核查后的费用结果），失败可重试
                return Arrays.asList(WaybillFlowStatus.WAITING, WaybillFlowStatus.FAILED);
            default:
                // CLEAN/VALIDATE 支持重复执行
                return Arrays.asList(WaybillFlowStatus.WAITING, WaybillFlowStatus.SUCCESS,
                        WaybillFlowStatus.FAILED);
        }
    }

    private void requireStepStatus(Map<String, WaybillFlowStep> byCode, String stepCode,
                                   String requiredStatus, String errorMsg) {
        requireStepStatusAny(byCode, stepCode, Collections.singletonList(requiredStatus), errorMsg);
    }

    private void requireStepStatusAny(Map<String, WaybillFlowStep> byCode, String stepCode,
                                      List<String> requiredStatusList, String errorMsg) {
        WaybillFlowStep step = byCode.get(stepCode);
        if (step == null || !requiredStatusList.contains(step.getStatus())) {
            throw new BusinessException(errorMsg);
        }
    }
}
