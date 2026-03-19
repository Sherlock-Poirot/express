package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.listener.ReadLastRowListener;
import com.express.yto.service.CheckService;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Detective
 * @date Created in 2025/10/11
 */
@Service
@Slf4j
public class CheckServiceImpl implements CheckService {


    @Override
    public RestResult<List<String>> checkAmount(String readPath, String checkPath) {
        Map<String, BigDecimal> dataMap = readExcel(readPath);
        Map<String, BigDecimal> checkMap = readExcel(checkPath);
        List<String> result = new ArrayList<>();
        for (String key : dataMap.keySet()) {
            if (!checkMap.containsKey(key)) {
                continue;
            }
            if (dataMap.get(key).compareTo(checkMap.get(key)) != 0) {
                result.add(key);
            }
            log.info("{},文件检查,原始文件：{}，程序文件：{}", key, dataMap.get(key), checkMap.get(key));
            log.info("-------------------------------------------------");
        }
        return RestResult.ok(result);
    }

    private Map<String, BigDecimal> readExcel(String path) {
        Map<String, BigDecimal> map = new HashMap<>();
        File dir = new File(path);
        String[] fileNames = dir.list();
        assert fileNames != null;
        for (String fileName : fileNames) {
            if (!fileName.contains(".xlsx")) {
                continue;
            }
            String filePath = path + "/" + fileName;
            System.out.println("filePath:" + filePath);
            ReadLastRowListener readLastRowListener = new ReadLastRowListener();
            EasyExcel.read(filePath, ContractShopExcelDTO.class, readLastRowListener).doReadAll();
            BigDecimal amount = readLastRowListener.getLastRow().getExpense().setScale(2, RoundingMode.HALF_UP);
            ;
            fileName = fileName.replaceAll(".xlsx", "");
            fileName = fileName.replaceAll("9月", "");
            map.put(fileName, amount);
        }
        return map;
    }
}
