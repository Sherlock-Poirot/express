package com.express.yto.service.impl;

import static com.express.yto.util.AreaUtil.getAreaName;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.CustomerMapper;
import com.express.yto.dao.FixedFeeMapper;
import com.express.yto.dao.OverFeeMapper;
import com.express.yto.dao.PrepaymentMapper;
import com.express.yto.dto.FixedTinyDTO;
import com.express.yto.dto.OverFeeExcelDTO;
import com.express.yto.dto.OverFeeExcelMergeDTO;
import com.express.yto.dto.PriceExcelInput;
import com.express.yto.model.FixedFee;
import com.express.yto.model.OverFee;
import com.express.yto.model.Prepayment;
import com.express.yto.service.OverFeeService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
@Service
public class OverFeeServiceImpl extends ServiceImpl<OverFeeMapper, OverFee> implements OverFeeService {

    @Autowired
    private OverFeeMapper overFeeMapper;

    @Autowired
    private PrepaymentMapper prepaymentMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private FixedFeeMapper fixedFeeMapper;

    @Override
    @Transactional
    public void importByExcel(String path) {
        List<OverFeeExcelDTO> list = new ArrayList<>();
        EasyExcel.read(path, OverFeeExcelDTO.class, new ReadListener<OverFeeExcelDTO>() {
            @Override
            public void invoke(OverFeeExcelDTO dto, AnalysisContext analysisContext) {
                list.add(dto);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        }).sheet("Sheet1").doRead();
        List<OverFee> models = new ArrayList<>(list.size());
        List<Prepayment> prepaymentList = new ArrayList<>(list.size());
        for (OverFeeExcelDTO dto : list) {
            OverFee over_1 = OverFee.builder().code(dto.getCode()).area(1).fee(dto.getAreaOneExtraFee())
                    .startTime(dto.getStartTime()).endTime(dto.getEndTime()).firstWeight(
                            BigDecimal.ONE).firstFee(dto.getAreaOneFee()).build();
            OverFee over_2 = OverFee.builder().code(dto.getCode()).area(2).fee(dto.getAreaTwoExtraFee())
                    .startTime(dto.getStartTime()).endTime(dto.getEndTime()).firstWeight(
                            BigDecimal.ONE).firstFee(dto.getAreaTwoFee()).build();
            OverFee over_3 = OverFee.builder().code(dto.getCode()).area(3).fee(dto.getAreaThreeExtraFee())
                    .startTime(dto.getStartTime()).endTime(dto.getEndTime()).firstWeight(
                            BigDecimal.ONE).firstFee(dto.getAreaThreeFee()).build();
            OverFee over_4 = OverFee.builder().code(dto.getCode()).area(4).fee(dto.getAreaFourExtraFee())
                    .startTime(dto.getStartTime()).endTime(dto.getEndTime()).firstWeight(
                            BigDecimal.ONE).firstFee(dto.getAreaFourFee()).build();
            OverFee over_5 = OverFee.builder().code(dto.getCode()).area(5).fee(dto.getAreaFiveExtraFee())
                    .startTime(dto.getStartTime()).endTime(dto.getEndTime()).firstWeight(
                            BigDecimal.ONE).firstFee(dto.getAreaFiveFee()).build();
            models.add(over_1);
            models.add(over_2);
            models.add(over_3);
            models.add(over_4);
            models.add(over_5);
            Prepayment prepayment = Prepayment.builder().code(dto.getCode()).preFee(dto.getPrePayment())
                    .startTime(dto.getStartTime()).endTime(dto.getEndTime()).build();
            prepaymentList.add(prepayment);
        }
        overFeeMapper.insert(models);
        prepaymentMapper.insert(prepaymentList);
    }

    @Override
    public void export(PriceExcelInput input) {
        // TODO 先查询K码对应的客户名称进行分组
        // TODO 查询对应名称的重量段和相应的费用
        // TODO 组建对应的表头
        QueryWrapper<FixedFee> qw = new QueryWrapper<>();
        List<FixedFee> fixedFeeList = fixedFeeMapper.selectList(qw);
        Map<String, List<FixedFee>> fixedMap = fixedFeeList.stream().collect(Collectors.groupingBy(FixedFee::getCode));
//        List<OverFeeExcelMergeDTO> exportList = overFeeMapper.getExcelByCode(input.getCodeList());
        List<OverFeeExcelMergeDTO> exportList = new ArrayList<>();
        Map<String, List<OverFeeExcelMergeDTO>> map = exportList.stream()
                .collect(Collectors.groupingBy(OverFeeExcelMergeDTO::getCode));
        Map<String, String> nameMap = exportList.stream()
                .collect(Collectors.toMap(
                        OverFeeExcelMergeDTO::getCode,
                        OverFeeExcelMergeDTO::getName,
                        (existingValue, newValue) -> existingValue  // 重复key时保留第一个值
                        // (existingValue, newValue) -> newValue  // 重复key时保留最后一个值
                ));
        try (ExcelWriter excelWriter = EasyExcel.write(input.getExportPath(), OverFeeExcelMergeDTO.class).build()) {
            for (String code : map.keySet()) {
                String name = nameMap.get(code);
                // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样
                WriteSheet writeSheet = EasyExcel.writerSheet(name).build();
                WriteTable writeTable1 = EasyExcel.writerTable(0).needHead(Boolean.FALSE).build();
                excelWriter.write(map.get(code), writeSheet, writeTable1);
                // TODO 构建标题头信息
                List<FixedFee> fixedList = fixedMap.get(code);
                if (CollectionUtils.isNotEmpty(fixedList)) {
                    WriteTable writeTable2 = EasyExcel.writerTable(1).head(head(fixedList)).needHead(Boolean.TRUE)
                            .build();
                    excelWriter.write(data(fixedList), writeSheet, writeTable2);
                }
            }
        }
    }

    private List<List<String>> data(List<FixedFee> fixedList) {
        // TODO 暂时不考虑在当月会有修改重量段价格的情况（eg：不考虑本来有3个重量段，然后变成4个的情况）
        int count = (int) fixedList.stream().map(FixedFee::getWeight).distinct().count();
        List<FixedFee> dataList = fixedList.stream().sorted(Comparator.comparing(FixedFee::getStartTime)
                .thenComparing(FixedFee::getArea).thenComparing(FixedFee::getWeight)).collect(Collectors.toList());
        List<List<String>> result = new ArrayList<>();
        Map<String, List<FixedTinyDTO>> map = new LinkedHashMap<>();
        for (FixedFee fixed : dataList) {
            String key = fixed.getArea() + "&" + fixed.getStartTime() + "&" + fixed.getEndTime();
            if (map.containsKey(key)) {
                map.get(key).add(FixedTinyDTO.builder().weight(fixed.getWeight()).fee(fixed.getFee()).build());
            } else {
                List<FixedTinyDTO> value = new ArrayList<>();
                value.add(FixedTinyDTO.builder().weight(fixed.getWeight()).fee(fixed.getFee()).build());
                map.put(key, value);
            }
        }
        for (String key : map.keySet()) {
            List<String> data = new ArrayList<>();
            String[] keys = key.split("&");
            List<FixedTinyDTO> value = map.get(key).stream().sorted(Comparator.comparing(FixedTinyDTO::getWeight))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(value)) {
                continue;
            }
            data.add(getAreaName(keys[0]));
            int i = 0;
            for (FixedTinyDTO dto : value) {
                data.add(dto.getFee().toString());
                i++;
            }
            if (i < count) {
                for (int j = 0; j < (count - i); j++) {
                    data.add("首重+续重");
                }
            }
            data.add(keys[1]);
            data.add(keys[2]);
            result.add(data);
        }
        return result;
    }

    private List<List<String>> head(List<FixedFee> fixedList) {
        List<List<String>> head = new ArrayList<>();
        // 先处理数据
        List<BigDecimal> weight = fixedList.stream().map(FixedFee::getWeight).distinct().collect(Collectors.toList());
        head.add(Collections.singletonList("区域"));

        for (BigDecimal e : weight) {
            head.add(Collections.singletonList(e + "kg"));
        }
        head.add(Collections.singletonList("开始时间"));
        head.add(Collections.singletonList("结束时间"));
        return head;
    }
}
