package com.express.yto.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.express.yto.dao.SysTaskMapper;
import com.express.yto.dao.WaybillDetailMapper;
import com.express.yto.dao.WaybillDetailOriginalMapper;
import com.express.yto.dto.IdAndWeightDTO;
import com.express.yto.enums.ImportStatus;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.SysTask;
import com.express.yto.model.WaybillDetail;
import com.express.yto.model.WaybillDetailOriginal;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
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

    @Async
    public void doImportAsync(byte[] fileBytes, String taskNo) {
        SysTask task = sysTaskMapper.selectOne(Wrappers.lambdaQuery(SysTask.class)
                .eq(SysTask::getTaskNo, taskNo));

        if (task == null) {
            log.error("任务不存在:{}", taskNo);
            return;
        }

        try {
            final int[] totalCount = {0};

            EasyExcel.read(new ByteArrayInputStream(fileBytes), WaybillDetail.class, new AnalysisEventListener<WaybillDetail>() {
                private final List<WaybillDetail> cacheList = new ArrayList<>(BATCH_SIZE);
                private final List<WaybillDetailOriginal> originalCacheList = new ArrayList<>(BATCH_SIZE);

                @Override
                public void invoke(WaybillDetail data, AnalysisContext context) {
                    data.setMaterialType(data.getMaterialType().replaceAll("电子面单",""));
                    data.setMaterialType(data.getMaterialType().replaceAll("新电子面单",""));
                    cacheList.add(data);

                    WaybillDetailOriginal original = WaybillDetailOriginal.builder()
                            .waybillNo(data.getWaybillNo())
                            .scanTime(data.getScanTime())
                            .weight(data.getWeight())
                            .province(data.getProvince())
                            .destination(data.getDestination())
                            .salesmanName(data.getSalesmanName())
                            .sendCustomer(data.getSendCustomer())
                            .sendCustomerName(data.getSendCustomerName())
                            .settleCode(data.getSettleCode())
                            .settleName(data.getSettleName())
                            .materialType(data.getMaterialType())
                            .extraFee(data.getExtraFee())
                            .expressFee(data.getExpressFee())
                            .billMonth(data.getBillMonth())
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

        } catch (Exception e) {
            log.error("Excel导入失败", e);
            task.setStatus(ImportStatus.FAILED.getCode());
            task.setMessage("原始账单导入失败：" + e.getMessage());
            sysTaskMapper.updateById(task);
        }
    }

//    @Async
    public void doImportDiffAsync(byte[] fileBytes, String taskNo) {
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

        } catch (Exception e) {
            log.error("Excel导入失败", e);
            task.setStatus(ImportStatus.FAILED.getCode());
            task.setMessage("差异重量导入失败：" + e.getMessage());
            sysTaskMapper.updateById(task);
        }
    }
}
