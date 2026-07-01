package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.KpiCourierRankDTO;
import com.express.yto.dto.KpiFakeSignTypeDTO;
import com.express.yto.dto.KpiMonthlySummaryDTO;
import com.express.yto.dto.KpiSignRecordQueryDTO;
import com.express.yto.model.KpiSignRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface KpiSignRecordService {

    Integer importExcel(MultipartFile file);

    IPage<KpiSignRecord> queryRecordPage(KpiSignRecordQueryDTO queryDTO, Integer pageNum, Integer pageSize);

    KpiMonthlySummaryDTO getMonthlySummary(String month);

    List<KpiCourierRankDTO> getCourierRank(String month);

    List<KpiFakeSignTypeDTO> getFakeSignTypeStat(String month);

    void deleteById(Long id);

    void deleteByMonth(String month);
}