package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.CollectionRecordQueryDTO;
import com.express.yto.dto.SalesmanRankDTO;
import com.express.yto.model.CollectionRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CollectionRecordService {

    Integer importExcel(MultipartFile file);

    IPage<CollectionRecord> queryRecordPage(CollectionRecordQueryDTO queryDTO, Integer pageNum, Integer pageSize);

    List<SalesmanRankDTO> getSalesmanRank(String startDate, String endDate);
}