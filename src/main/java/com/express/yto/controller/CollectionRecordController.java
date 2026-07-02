package com.express.yto.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.yto.dto.CollectionRecordQueryDTO;
import com.express.yto.dto.SalesmanRankDTO;
import com.express.yto.model.CollectionRecord;
import com.express.yto.service.CollectionRecordService;
import com.express.yto.dto.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/collection/record")
public class CollectionRecordController {

    @Autowired
    private CollectionRecordService collectionRecordService;

    @PostMapping("/import")
    public RestResult<Integer> importExcel(@RequestParam("file") MultipartFile file) {
        Integer count = collectionRecordService.importExcel(file);
        return RestResult.ok(count);
    }

    @GetMapping("/page")
    public RestResult<IPage<CollectionRecord>> queryRecordPage(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "salesmanName", required = false) String salesmanName,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        CollectionRecordQueryDTO queryDTO = new CollectionRecordQueryDTO();
        queryDTO.setStartDate(startDate);
        queryDTO.setEndDate(endDate);
        queryDTO.setSalesmanName(salesmanName);

        IPage<CollectionRecord> page = collectionRecordService.queryRecordPage(queryDTO, pageNum, pageSize);
        return RestResult.ok(page);
    }

    @GetMapping("/salesman-rank")
    public RestResult<List<SalesmanRankDTO>> getSalesmanRank(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<SalesmanRankDTO> rank = collectionRecordService.getSalesmanRank(startDate, endDate);
        return RestResult.ok(rank);
    }
}