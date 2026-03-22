package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.DealDataInput;
import com.express.yto.dto.FixedFeeMiniDTO;
import com.express.yto.factory.EmployeeHandlerFactory;
import com.express.yto.service.EmployeeService;
import com.express.yto.util.AreaUtil;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Detective
 * @date Created in 2025/9/25
 */
@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeHandlerFactory employeeFactory;

    @Override
    public void dealEmployeeBill(DealDataInput input) {
        employeeFactory.getHandler(input.getCompanyId()).handle(input);
    }


    private static List<FixedFeeMiniDTO> EMP_FIXED_FEE;

    //    private static BigDecimal PRE_PAYMENT = BigDecimal.valueOf(3);
    private static BigDecimal PRE_PAYMENT = BigDecimal.ZERO;

    private static BigDecimal FOUR_RATE = BigDecimal.valueOf(0.06);

    static {
        EMP_FIXED_FEE = new ArrayList<>(15);
        // 1-3区0.5kg
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(1).fee(BigDecimal.valueOf(2.6).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(0.5)).build());
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(2).fee(BigDecimal.valueOf(2.6).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(0.5)).build());
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(3).fee(BigDecimal.valueOf(2.6).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(0.5)).build());

        // 1-3区1kg
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(1).fee(BigDecimal.valueOf(3.1).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(1)).build());
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(2).fee(BigDecimal.valueOf(3.1).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(1)).build());
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(3).fee(BigDecimal.valueOf(3.1).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(1)).build());

        // 1-3区1.5kg
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(1).fee(BigDecimal.valueOf(3.5).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(1.5)).build());
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(2).fee(BigDecimal.valueOf(3.5).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(1.5)).build());
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(3).fee(BigDecimal.valueOf(3.5).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(1.5)).build());

        // 1-3区2kg
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(1).fee(BigDecimal.valueOf(4).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(2)).build());
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(2).fee(BigDecimal.valueOf(4).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(2)).build());
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(3).fee(BigDecimal.valueOf(4).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(2)).build());

        // 1-3区3kg
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(1).fee(BigDecimal.valueOf(5).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(3)).build());
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(2).fee(BigDecimal.valueOf(5).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(3)).build());
        EMP_FIXED_FEE.add(FixedFeeMiniDTO.builder().area(3).fee(BigDecimal.valueOf(5).subtract(PRE_PAYMENT))
                .weight(BigDecimal.valueOf(3)).build());
    }

    @Override
    public void employeeBill(String readPath, String exportPath) {
        File dir = new File(readPath);
        String[] fileNames = dir.list();
        assert fileNames != null;
        for (String fileName : fileNames) {
            if (!fileName.contains(".xlsx")) {
                continue;
            }
            List<ContractShopExcelDTO> taoBaoList = new ArrayList<>();
            String filePath = readPath + "/" + fileName;
            System.out.println("filePath:" + filePath);
            EasyExcel.read(filePath, ContractShopExcelDTO.class, new ReadListener<ContractShopExcelDTO>() {
                @Override
                public void invoke(ContractShopExcelDTO dto, AnalysisContext analysisContext) {
                    taoBaoList.add(dto);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                }
            }).sheet("淘宝").doRead();
            List<ContractShopExcelDTO> taoBaoExportList = doDealBill(taoBaoList);
            addAmountPrePay(taoBaoExportList);

            List<ContractShopExcelDTO> dispersedList = new ArrayList<>();
            EasyExcel.read(filePath, ContractShopExcelDTO.class, new ReadListener<ContractShopExcelDTO>() {
                @Override
                public void invoke(ContractShopExcelDTO dto, AnalysisContext analysisContext) {
                    dispersedList.add(dto);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                }
            }).sheet("散件").doRead();
            List<ContractShopExcelDTO> dispersedExportList = doDealBill(dispersedList);
            addAmountPrePay(dispersedExportList);

            // TODO 限定还未想好怎么处理
            List<ContractShopExcelDTO> specificList = new ArrayList<>();
            EasyExcel.read(filePath, ContractShopExcelDTO.class, new ReadListener<ContractShopExcelDTO>() {
                @Override
                public void invoke(ContractShopExcelDTO dto, AnalysisContext analysisContext) {
                    specificList.add(dto);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                }
            }).sheet("限定").doRead();

            ExcelWriter writer = EasyExcel.write(exportPath + "/导出" + fileName).build();
            WriteSheet taoBao = EasyExcel.writerSheet(0, "淘宝").head(ContractShopExcelDTO.class).build();
            WriteSheet dispersed = EasyExcel.writerSheet(1, "散件").head(ContractShopExcelDTO.class).build();
            WriteSheet specific = EasyExcel.writerSheet(2, "限定").head(ContractShopExcelDTO.class).build();
            writer.write(taoBaoExportList, taoBao);
            writer.write(dispersedExportList, dispersed);
            writer.write(specificList, specific);
            writer.finish();
            log.info("{}导出完成", fileName);
        }
    }

    private List<ContractShopExcelDTO> doDealBill(List<ContractShopExcelDTO> list) {
        List<ContractShopExcelDTO> data = list;
        int amount = list.size();
        List<ContractShopExcelDTO> export = new ArrayList<>();
        // 处理1-3区固定重量区间的费用
        for (FixedFeeMiniDTO dto : EMP_FIXED_FEE) {
            List<ContractShopExcelDTO> subList = data.stream()
                    .filter(e -> AreaUtil.judgeArea(e.getProvince(), dto.getArea())
                            && e.getWeight().compareTo(dto.getWeight()) <= 0).collect(Collectors.toList());
            subList.forEach(e -> e.setExpense(dto.getFee()));
            data.removeAll(subList);
            export.addAll(subList);
        }
        for (ContractShopExcelDTO dto : data) {
            // 处理1区超重
            if (AreaUtil.getArea(dto.getProvince()) == 1) {
                dto.setExpense(dto.getWeight().multiply(BigDecimal.valueOf(0.7)).add(BigDecimal.valueOf(3.5))
                        .subtract(PRE_PAYMENT).setScale(3, RoundingMode.UP));
            }
            // 处理2区超重
            if (AreaUtil.getArea(dto.getProvince()) == 2) {
                dto.setExpense(dto.getWeight().multiply(BigDecimal.valueOf(2)).add(BigDecimal.valueOf(3.5))
                        .subtract(PRE_PAYMENT).setScale(3, RoundingMode.UP));
            }
            // 处理3区超重
            if (AreaUtil.getArea(dto.getProvince()) == 3) {
                dto.setExpense(dto.getWeight().multiply(BigDecimal.valueOf(3)).add(BigDecimal.valueOf(3.5))
                        .subtract(PRE_PAYMENT).setScale(3, RoundingMode.UP));
            }
            // 处理4区
            if (AreaUtil.getArea(dto.getProvince()) == 4) {
                if (dto.getWeight().compareTo(BigDecimal.valueOf(3)) > 0) {
                    dto.setExpense(dto.getWeight().multiply(BigDecimal.valueOf(4))
                            .add(BigDecimal.valueOf(4)).subtract(PRE_PAYMENT).setScale(3, RoundingMode.UP));
                } else {
                    dto.setExpense(dto.getWeight().setScale(0, RoundingMode.CEILING).subtract(BigDecimal.ONE)
                            .multiply(BigDecimal.valueOf(4)).add(BigDecimal.valueOf(4)).subtract(PRE_PAYMENT));

                }
            }
            // 处理5区
            if (AreaUtil.getArea(dto.getProvince()) == 5) {
                BigDecimal five = dto.getWeight().multiply(BigDecimal.valueOf(15).setScale(3, RoundingMode.UP));
                if (five.compareTo(BigDecimal.valueOf(6)) < 0) {
                    five = BigDecimal.valueOf(6);
                }
                five = five.subtract(PRE_PAYMENT);
                dto.setExpense(five);
            }
        }
        export.addAll(data);

        // 处理加收
        for (ContractShopExcelDTO dto : export) {
            BigDecimal expense = dto.getExpense();
            if (dto.getOfficeExtra() != null) {
                expense = expense.add(dto.getOfficeExtra());
            }
            if (dto.getProvince().contains("北京")) {
                expense = expense.add(BigDecimal.ONE);
            }
            if (dto.getProvince().contains("上海")) {
                expense = expense.add(BigDecimal.ONE);
            }
            dto.setExpense(expense);
        }

        // 处理4区超占比
//        long four = export.stream().filter(e -> AreaUtil.AREA_4.contains(e.getProvince())).count();
//        BigDecimal rate = BigDecimal.valueOf(four).divide(BigDecimal.valueOf(amount), 3, RoundingMode.CEILING);
//        if (rate.compareTo(FOUR_RATE) > 0) {
//            int count = rate.subtract(FOUR_RATE).multiply(BigDecimal.valueOf(amount))
//                    .setScale(0, RoundingMode.CEILING).intValue();
//            for (ContractShopExcelDTO dto : export) {
//                if (AreaUtil.getArea(dto.getProvince()) == 4 && count > 0) {
//                    dto.setExpense(dto.getExpense().add(BigDecimal.ONE));
//                    count--;
//                }
//            }
//        }
        return export;
    }

    private void addAmountPrePay(List<ContractShopExcelDTO> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            BigDecimal pre = BigDecimal.valueOf(list.size() * 3);
            BigDecimal amount = list.stream().map(ContractShopExcelDTO::getExpense)
                    .reduce(BigDecimal::add).get();
            list.add(ContractShopExcelDTO.builder().expense(amount).build());
            list.add(ContractShopExcelDTO.builder().shopType("面单预付").expense(pre).build());
            list.add(ContractShopExcelDTO.builder().shopType("应付合计").expense(amount.subtract(pre)).build());
        }
    }
}
