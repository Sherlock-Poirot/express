package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.yto.dao.KpiSignRecordMapper;
import com.express.yto.dto.KpiCourierRankDTO;
import com.express.yto.dto.KpiFakeSignTypeDTO;
import com.express.yto.dto.KpiMonthlySummaryDTO;
import com.express.yto.dto.KpiSignRecordImportDTO;
import com.express.yto.dto.KpiSignRecordQueryDTO;
import com.express.yto.model.KpiSignRecord;
import com.express.yto.service.KpiSignRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class KpiSignRecordServiceImpl implements KpiSignRecordService {

    @Autowired
    private KpiSignRecordMapper kpiSignRecordMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    @Transactional
    public Integer importExcel(MultipartFile file) {
        List<KpiSignRecord> records = new ArrayList<>();

        try {
            EasyExcel.read(file.getInputStream(), KpiSignRecordImportDTO.class, new AnalysisEventListener<KpiSignRecordImportDTO>() {
                @Override
                public void invoke(KpiSignRecordImportDTO dto, AnalysisContext context) {
                    KpiSignRecord record = convertToEntity(dto);
                    if (record != null) {
                        records.add(record);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel解析完成，共{}条数据", records.size());
                }
            }).sheet().doRead();
        } catch (IOException e) {
            log.error("Excel读取失败", e);
            throw new RuntimeException("Excel读取失败", e);
        }

        if (!records.isEmpty()) {
            String month = records.get(0).getMonth();
            kpiSignRecordMapper.deleteByMonth(month);
            for (KpiSignRecord record : records) {
                kpiSignRecordMapper.insert(record);
            }
        }

        return records.size();
    }

    private KpiSignRecord convertToEntity(KpiSignRecordImportDTO dto) {
        if (dto.getWaybillNo() == null || dto.getWaybillNo().trim().isEmpty()) {
            return null;
        }

        LocalDate dataDate = parseDate(dto.getDataDate());
        String month = dataDate != null ? dataDate.format(DateTimeFormatter.ofPattern("yyyy-MM")) : null;

        KpiSignRecord record = KpiSignRecord.builder()
                .dataDate(dataDate)
                .waybillNo(dto.getWaybillNo())
                .reportTime(parseDateTime(dto.getReportTime()))
                .fakeSignType(dto.getFakeSignType())
                .signTime(parseDateTime(dto.getSignTime()))
                .precisionDelivery(parseBoolean(dto.getPrecisionDelivery()))
                .preSignCall(parseBoolean(dto.getPreSignCall()))
                .courierCode(dto.getCourierCode())
                .courierName(dto.getCourierName())
                .month(month)
                .build();

        calculateQualified(record);
        calculateEfficiency(record);

        return record;
    }

    private void calculateQualified(KpiSignRecord record) {
        if (record.getPreSignCall() != null && record.getPreSignCall() == 1) {
            record.setIsQualified(1);
        } else {
            record.setIsQualified(0);
        }
    }

    private void calculateEfficiency(KpiSignRecord record) {
        if (record.getSignTime() != null && record.getReportTime() != null) {
            long hours = java.time.Duration.between(record.getSignTime(), record.getReportTime()).toHours();
            long minutes = java.time.Duration.between(record.getSignTime(), record.getReportTime()).toMinutes() % 60;
            double efficiency = hours + minutes / 60.0;
            record.setEfficiencyHours(BigDecimal.valueOf(efficiency).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DATETIME_FORMATTER);
        } catch (Exception e) {
            try {
                LocalDate date = LocalDate.parse(dateTimeStr.trim(), DATE_FORMATTER);
                return date.atStartOfDay();
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private Integer parseBoolean(String boolStr) {
        if (boolStr == null || boolStr.trim().isEmpty()) {
            return 0;
        }
        String str = boolStr.trim();
        if ("是".equals(str) || "1".equals(str) || "true".equalsIgnoreCase(str)) {
            return 1;
        }
        return 0;
    }

    @Override
    public IPage<KpiSignRecord> queryRecordPage(KpiSignRecordQueryDTO queryDTO, Integer pageNum, Integer pageSize) {
        Page<KpiSignRecord> page = new Page<>(pageNum, pageSize);
        return kpiSignRecordMapper.selectRecordPage(page,
                queryDTO.getStartDate(),
                queryDTO.getEndDate(),
                queryDTO.getCourierName(),
                queryDTO.getIsQualified(),
                queryDTO.getWaybillNo(),
                queryDTO.getFakeSignType());
    }

    @Override
    public KpiMonthlySummaryDTO getMonthlySummary(String month) {
        return kpiSignRecordMapper.selectMonthlySummary(month);
    }

    @Override
    public List<KpiCourierRankDTO> getCourierRank(String month) {
        return kpiSignRecordMapper.selectCourierRank(month);
    }

    @Override
    public List<KpiFakeSignTypeDTO> getFakeSignTypeStat(String month) {
        return kpiSignRecordMapper.selectFakeSignTypeStat(month);
    }

    @Override
    public void deleteById(Long id) {
        kpiSignRecordMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByMonth(String month) {
        kpiSignRecordMapper.deleteByMonth(month);
    }
}