package com.express.yto.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.express.yto.dao.SysTaskMapper;
import com.express.yto.dao.WaybillDetailMapper;
import com.express.yto.dao.WaybillDetailOriginalMapper;
import com.express.yto.dto.IdAndWeightDTO;
import com.express.yto.enums.ImportStatus;
import com.express.yto.enums.WaybillFlowStatus;
import com.express.yto.enums.WaybillFlowStepEnum;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.SysTask;
import com.express.yto.model.WaybillDetail;
import com.express.yto.model.WaybillDetailOriginal;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Detective
 * @date Created in 2026/5/6
 */
@Service
@Slf4j
public class WaybillAsyncService {

    private static final int BATCH_SIZE = 5000;

    @Autowired
    private WaybillDetailMapper waybillDetailMapper;

    @Autowired
    private WaybillDetailOriginalMapper waybillDetailOriginalMapper;

    @Autowired
    private SysTaskMapper sysTaskMapper;

    @Autowired
    private WaybillFlowService waybillFlowService;

    @Async("asyncExecutor")
    public void doImportAsync(byte[] fileBytes, String taskNo) {
        SysTask task = sysTaskMapper.selectOne(Wrappers.lambdaQuery(SysTask.class)
                .eq(SysTask::getTaskNo, taskNo));

        if (task == null) {
            log.error("任务不存在:{}", taskNo);
            return;
        }

        try {
            final int[] totalCount = {0};

            EasyExcel.read(new ByteArrayInputStream(fileBytes), new AnalysisEventListener<Map<Integer, String>>() {
                private final List<WaybillDetail> cacheList = new ArrayList<>(BATCH_SIZE);
                private final List<WaybillDetailOriginal> originalCacheList = new ArrayList<>(BATCH_SIZE);
                private Map<String, Integer> headMapping = new HashMap<>();

                @Override
                public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                    for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                        String value = entry.getValue().getStringValue().trim();
                        headMapping.put(value, entry.getKey());
                    }
                }

                @Override
                public void invoke(Map<Integer, String> data, AnalysisContext context) {
                    WaybillDetail detail = new WaybillDetail();
                    detail.setWaybillNo(data.get(headMapping.get("运单号码")));
                    detail.setScanTime(parseDate(data.get(headMapping.get("扫描时间"))));
                    detail.setWeight(parseBigDecimal(data.get(headMapping.get("计费重量（kg）"))));
                    detail.setProvince(data.get(headMapping.get("计费省份")));
                    detail.setDestination(data.get(headMapping.get("计费目的地名称")));
                    detail.setSalesmanName(data.get(headMapping.get("物料业务员名称")));
                    detail.setSendCustomer(data.get(headMapping.get("物料发放客户")));
                    detail.setSendCustomerName(data.get(headMapping.get("物料发放客户名称")));
                    detail.setSettleCode(data.get(headMapping.get("物料结算编码")));
                    detail.setSettleName(data.get(headMapping.get("物料结算名称")));
                    String materialType = data.get(headMapping.get("物料类型"));
                    if (materialType != null) {
                        materialType = materialType.replaceAll("新电子面单", "").replaceAll("电子面单", "");
                    }
                    detail.setMaterialType(materialType);
                    detail.setExtraFee(parseBigDecimal(data.get(headMapping.get("加收"))));
                    detail.setExpressFee(parseBigDecimal(data.get(headMapping.get("快递费"))));

                    if (totalCount[0] == 0) {
                        log.info("第一行数据 - materialType: [{}], waybillNo: [{}]", 
                                materialType, detail.getWaybillNo());
                    }

                    cacheList.add(detail);

                    WaybillDetailOriginal original = WaybillDetailOriginal.builder()
                            .waybillNo(detail.getWaybillNo())
                            .scanTime(detail.getScanTime())
                            .weight(detail.getWeight())
                            .province(detail.getProvince())
                            .destination(detail.getDestination())
                            .salesmanName(detail.getSalesmanName())
                            .sendCustomer(detail.getSendCustomer())
                            .sendCustomerName(detail.getSendCustomerName())
                            .settleCode(detail.getSettleCode())
                            .settleName(detail.getSettleName())
                            .materialType(detail.getMaterialType())
                            .extraFee(detail.getExtraFee())
                            .expressFee(detail.getExpressFee())
                            .billMonth(detail.getBillMonth())
                            .build();
                    originalCacheList.add(original);

                    totalCount[0]++;

                    if (cacheList.size() >= BATCH_SIZE) {
                        waybillDetailMapper.insertBatch(cacheList);
                        waybillDetailOriginalMapper.insertBatch(originalCacheList);
                        cacheList.clear();
                        originalCacheList.clear();
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    if (!cacheList.isEmpty()) {
                        waybillDetailMapper.insertBatch(cacheList);
                        waybillDetailOriginalMapper.insertBatch(originalCacheList);
                        cacheList.clear();
                        originalCacheList.clear();
                    }
                }
            }).sheet().doRead();

            task.setTotal(totalCount[0]);
            task.setStatus(ImportStatus.SUCCESS.getCode());
            task.setMessage("原始账单成功导入" + totalCount[0] + "条");
            sysTaskMapper.updateById(task);
            // 工作流：更新导入文件记录为成功
            waybillFlowService.finishImportFileRecord(taskNo, true, totalCount[0], null);

        } catch (Exception e) {
            log.error("Excel导入失败", e);
            task.setStatus(ImportStatus.FAILED.getCode());
            task.setMessage("原始账单导入失败：" + e.getMessage());
            sysTaskMapper.updateById(task);
            // 工作流：更新导入文件记录为失败
            waybillFlowService.finishImportFileRecord(taskNo, false, 0, e.getMessage());
        }
    }

    @Async("asyncExecutor")
    public void doImportDiffAsync(byte[] fileBytes, String taskNo, String billMonth) {
        SysTask task = sysTaskMapper.selectOne(Wrappers.lambdaQuery(SysTask.class)
                .eq(SysTask::getTaskNo, taskNo));

        if (task == null) {
            log.error("任务不存在:{}", taskNo);
            return;
        }

        try {
            final int[] totalCount = {0};

            // 🔥 用 ByteArrayInputStream 包装字节数组
            EasyExcel.read(new ByteArrayInputStream(fileBytes), IdAndWeightDTO.class, new AnalysisEventListener<IdAndWeightDTO>() {
                private final List<IdAndWeightDTO> cacheList = new ArrayList<>(BATCH_SIZE);

                @Override
                public void invoke(IdAndWeightDTO data, AnalysisContext context) {
                    cacheList.add(data);
                    totalCount[0]++;

                    if (cacheList.size() >= BATCH_SIZE) {
                        waybillDetailMapper.updateWeight(cacheList);
                        cacheList.clear();
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    if (!cacheList.isEmpty()) {
                        waybillDetailMapper.updateWeight(cacheList);
                        cacheList.clear();
                    }
                }
            }).sheet().doRead();

            // 成功更新
            task.setTotal(totalCount[0]);
            task.setStatus(ImportStatus.SUCCESS.getCode());
            task.setMessage("差异重量成功导入" + totalCount[0] + "条");
            sysTaskMapper.updateById(task);
            // 工作流：导入重量差异步骤完成
            waybillFlowService.finishStep(billMonth, WaybillFlowStepEnum.IMPORT_DIFF.getCode(), true, null);

        } catch (Exception e) {
            log.error("Excel导入失败", e);
            task.setStatus(ImportStatus.FAILED.getCode());
            task.setMessage("差异重量导入失败：" + e.getMessage());
            sysTaskMapper.updateById(task);
            // 工作流：导入重量差异步骤失败
            waybillFlowService.finishStep(billMonth, WaybillFlowStepEnum.IMPORT_DIFF.getCode(), false, e.getMessage());
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
