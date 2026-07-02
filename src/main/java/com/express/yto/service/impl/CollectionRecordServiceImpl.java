package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.CollectionRecordMapper;
import com.express.yto.dto.CollectionRecordImportDTO;
import com.express.yto.dto.CollectionRecordQueryDTO;
import com.express.yto.dto.SalesmanRankDTO;
import com.express.yto.model.CollectionRecord;
import com.express.yto.service.CollectionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CollectionRecordServiceImpl extends ServiceImpl<CollectionRecordMapper, CollectionRecord> implements CollectionRecordService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    @Transactional
    public Integer importExcel(MultipartFile file) {
        try {
            List<CollectionRecordImportDTO> importList = EasyExcel.read(file.getInputStream())
                    .head(CollectionRecordImportDTO.class)
                    .sheet()
                    .doReadSync();

            if (importList == null || importList.isEmpty()) {
                return 0;
            }

            Map<String, List<CollectionRecordImportDTO>> monthGroup = importList.stream()
                    .filter(item -> item.getExpectedCollectionTime() != null)
                    .collect(Collectors.groupingBy(item -> item.getExpectedCollectionTime().format(MONTH_FORMATTER)));

            for (Map.Entry<String, List<CollectionRecordImportDTO>> entry : monthGroup.entrySet()) {
                String month = entry.getKey();
                baseMapper.deleteByMonth(month);
            }

            List<CollectionRecord> records = new ArrayList<>();
            for (CollectionRecordImportDTO dto : importList) {
                CollectionRecord record = new CollectionRecord();
                record.setWaybillNo(dto.getWaybillNo());
                record.setExpectedCollectionTime(dto.getExpectedCollectionTime());
                record.setActualCollectionTime(dto.getActualCollectionTime());
                record.setOrderNo(dto.getOrderNo());
                record.setSalesmanName(dto.getSalesmanName());

                if (dto.getExpectedCollectionTime() != null) {
                    record.setMonth(dto.getExpectedCollectionTime().format(MONTH_FORMATTER));

                    if (dto.getActualCollectionTime() != null) {
                        Duration duration = Duration.between(dto.getExpectedCollectionTime(), dto.getActualCollectionTime());
                        int delayMinutes = (int) duration.toMinutes();
                        record.setCollectionDelayMinutes(delayMinutes);
                        record.setIsDelay(delayMinutes > 0 ? 1 : 0);
                    }
                }

                records.add(record);
            }

            if (!records.isEmpty()) {
                baseMapper.insertBatch(records);
            }

            log.info("揽收记录导入完成，共{}条", records.size());
            return records.size();

        } catch (IOException e) {
            log.error("揽收记录导入失败", e);
            throw new RuntimeException("导入失败：" + e.getMessage());
        }
    }

    @Override
    public IPage<CollectionRecord> queryRecordPage(CollectionRecordQueryDTO queryDTO, Integer pageNum, Integer pageSize) {
        Page<CollectionRecord> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectRecordPage(page,
                queryDTO.getStartDate(),
                queryDTO.getEndDate(),
                queryDTO.getSalesmanName(),
                queryDTO.getIsDelay(),
                queryDTO.getWaybillNo());
    }

    @Override
    public List<SalesmanRankDTO> getSalesmanRank(String startDate, String endDate) {
        return baseMapper.selectSalesmanRank(startDate, endDate);
    }
}