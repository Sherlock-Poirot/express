package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.express.yto.dao.CustomerMapper;
import com.express.yto.dao.ExtraFeeMapper;
import com.express.yto.dao.FixedFeeMapper;
import com.express.yto.dao.OverFeeMapper;
import com.express.yto.dao.PrepaymentMapper;
import com.express.yto.dto.BillCompileDTO;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.DealDataInput;
import com.express.yto.dto.FourRateExcelDTO;
import com.express.yto.dto.PreDealDTO;
import com.express.yto.dto.RestResult;
import com.express.yto.factory.FileHandlerFactory;
import com.express.yto.model.Customer;
import com.express.yto.model.ExtraFee;
import com.express.yto.model.FixedFee;
import com.express.yto.model.OverFee;
import com.express.yto.model.Prepayment;
import com.express.yto.service.BackUpService;
import com.express.yto.service.DealDataService;
import com.express.yto.service.EmployeeService;
import com.express.yto.util.AreaUtil;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
@Service
@Slf4j
public class DealDataServiceImpl implements DealDataService {

    @Autowired
    private FileHandlerFactory factory;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private BackUpService backUpService;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ExtraFeeMapper extraFeeMapper;

    @Autowired
    private FixedFeeMapper fixedFeeMapper;

    @Autowired
    private OverFeeMapper overFeeMapper;

    @Autowired
    private PrepaymentMapper prepaymentMapper;

    private static final String FOLD_BILL_CUSTOMER = "余训正,许晓亭,蔡心怡清清美家居旗舰店坝头,陈宇健,通泰及宇航物流叶总鞋子,邵东亮,尾舍,萱宝嗳妈咪黄力军,张卫丽,现望使桐屿富通家园,ceo南山及趣多多,王晨,郑永华,圣强,浙江耀都科技有限公司,王欣怡,郑远,陈舒婷";

    @Override
    public void doDeal(String readPath, String exportPath, Boolean springFestival, String companyId) {
        File dir = new File(readPath);
        String[] fileNames = dir.list();
        assert fileNames != null;
        for (String fileName : fileNames) {
            if (!fileName.contains(".xlsx")) {
                continue;
            }
            List<ContractShopExcelDTO> list = new ArrayList<>();
            String filePath = readPath + "/" + fileName;
            System.out.println("filePath:" + filePath);
            EasyExcel.read(filePath, ContractShopExcelDTO.class, new ReadListener<ContractShopExcelDTO>() {
                @Override
                public void invoke(ContractShopExcelDTO dto, AnalysisContext analysisContext) {
                    list.add(dto);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    // 移除最后一条数据（集合非空时）
//                    if (!list.isEmpty()) {
//                        list.remove(list.size() - 1);
//                    }
                }
            }).doReadAll();
            List<ContractShopExcelDTO> exportList = factory.getHandler(fileName).handle(list, companyId);
            if (springFestival) {
                // 春节加收
                for (ContractShopExcelDTO dto : exportList) {
                    if (StringUtils.isBlank(dto.getKName())) {
                        continue;
                    }
                    BigDecimal expense = dto.getExpense();
                    // 郑红志，张宁 2.10-2.22涨价3元续重涨价1元
                    LocalDate s1 = LocalDate.of(2026, 2, 9);
                    LocalDate e1 = LocalDate.of(2026, 2, 23);

                    if ("郑红志，张宁".contains(dto.getKName())) {
                        if (dto.getScanDate().isAfter(s1) && dto.getScanDate().isBefore(e1)) {
                            expense = expense.add(BigDecimal.valueOf(3));
                            if (dto.getOverFlag()) {
                                expense = expense.add(dto.getWeight().setScale(0, RoundingMode.CEILING)
                                        .subtract(BigDecimal.ONE));
                            }
                        }
                        dto.setExpense(expense);
                        continue;
                    }
                    LocalDate s2 = LocalDate.of(2026, 2, 10);
                    LocalDate e2 = LocalDate.of(2026, 2, 22);
                    // ceo南山及趣多多 2.11-2.21涨价1元续重涨价1元
                    // 余训正 2.11-2.21涨价1.5元续重涨价1元，折单客户只考虑续重就行
                    if (dto.getScanDate().isAfter(s2) && dto.getScanDate().isBefore(e2)) {
                        if (dto.getOverFlag()) {
                            expense = expense.add(dto.getWeight().setScale(0, RoundingMode.CEILING)
                                    .subtract(BigDecimal.ONE));
                        }
                        if (!FOLD_BILL_CUSTOMER.contains(dto.getKName())) {
                            // 非折单客户加2元
                            expense = expense.add(BigDecimal.valueOf(2));
                        }
                        dto.setExpense(expense);
                    }
                }
            }
            // 增加一行总计
            BigDecimal amount = exportList.stream().map(ContractShopExcelDTO::getExpense)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            exportList.add(ContractShopExcelDTO.builder().expense(amount).build());
            EasyExcel.write(exportPath + "/2月" + fileName, ContractShopExcelDTO.class).sheet().doWrite(exportList);
            log.info("写入完成:{}", fileName);
        }
    }

    @Override
    public void employeeBill(String readPath, String exportPath) {
        employeeService.employeeBill(readPath, exportPath);
    }

    @Override
    public RestResult<PreDealDTO> preDeal(String path) {
        File dir = new File(path);
        String[] files = dir.list();
        assert files != null;
        for (String fileName : files) {
            if (!fileName.contains(".xlsx")) {
                continue;
            }
            List<ContractShopExcelDTO> list = new ArrayList<>();
//            log.info("当前读取的文件名称：{}",fileName);
            EasyExcel.read(path + "/" + fileName, ContractShopExcelDTO.class, new ReadListener<ContractShopExcelDTO>() {
                @Override
                public void invoke(ContractShopExcelDTO dto, AnalysisContext analysisContext) {
                    list.add(dto);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                }
            }).doReadAll();
            List<String> codes = list.stream().map(ContractShopExcelDTO::getKCode).distinct()
                    .collect(Collectors.toList());
            fileName = fileName.replaceAll("1月", "");
            fileName = fileName.replaceAll("2月", "");
            fileName = fileName.replaceAll("3月", "");
            fileName = fileName.replaceAll("4月", "");
            fileName = fileName.replaceAll("5月", "");
            fileName = fileName.replaceAll("6月", "");
            fileName = fileName.replaceAll("7月", "");
            fileName = fileName.replaceAll("8月", "");
            fileName = fileName.replaceAll("9月", "");
            fileName = fileName.replaceAll("10月", "");
            fileName = fileName.replaceAll("11月", "");
            fileName = fileName.replaceAll("12月", "");
            fileName = fileName.replaceAll(".xlsx", "");
            QueryWrapper<Customer> cqw = new QueryWrapper<>();
            cqw.eq("k_name", fileName);
            Customer customer = customerMapper.selectOne(cqw);
            if (customer != null && StringUtils.isNotBlank(customer.getKCode())) {
                String kCode = customer.getKCode();
                // 预付款
                QueryWrapper<Prepayment> pqWrapper = new QueryWrapper<>();
                pqWrapper.eq("k_code", kCode);
                List<Prepayment> prepaymentList = prepaymentMapper.selectList(pqWrapper);

                if (CollectionUtils.isEmpty(prepaymentList)) {
                    log.info("{},缺少预付金额请检查", fileName);
                }

                // 固定重量区间价格
                QueryWrapper<FixedFee> fixWrapper = new QueryWrapper<>();
                fixWrapper.eq("k_code", kCode);
                List<FixedFee> fixedFeeList = fixedFeeMapper.selectList(fixWrapper);
                if (CollectionUtils.isEmpty(fixedFeeList)) {
                    log.info("{},缺少固定重量区间价格请检查", fileName);
                }

                // 续重费用
                QueryWrapper<OverFee> overWrapper = new QueryWrapper<>();
                overWrapper.eq("k_code", kCode);
                List<OverFee> overFeeList = overFeeMapper.selectList(overWrapper);
                if (CollectionUtils.isEmpty(overFeeList)) {
                    log.info("{},缺少续重价格请检查", fileName);
                }

                // 地区加收
                QueryWrapper<ExtraFee> eqWrapper = new QueryWrapper<>();
                eqWrapper.eq("k_code", kCode);
                List<ExtraFee> extraFeeList = extraFeeMapper.selectList(eqWrapper);
                if (CollectionUtils.isEmpty(extraFeeList)) {
                    log.info("{},缺少地区加收请检查", fileName);
                }

                if (codes.size() > 1) {
                    log.info("{},存在两个以上K码,数据库K码为：{}", fileName, customer.getKCode());
                }
            } else {
                log.info("{},不存在客户表", fileName);
            }
        }

        return null;
    }

    @Override
    public void count(String path) {
        int count = 0;
        BigDecimal result = BigDecimal.ZERO;
        File dir = new File(path);
        String[] fileNames = dir.list();
        assert fileNames != null;
        for (String fileName : fileNames) {
            BigDecimal weight = BigDecimal.ZERO;
            if (!fileName.contains(".xlsx")) {
                continue;
            }
            List<ContractShopExcelDTO> list = new ArrayList<>();
            String filePath = path + "/" + fileName;
            EasyExcel.read(filePath, ContractShopExcelDTO.class, new ReadListener<ContractShopExcelDTO>() {
                @Override
                public void invoke(ContractShopExcelDTO dto, AnalysisContext analysisContext) {
                    list.add(dto);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                }
            }).doReadAll();
            log.info("{},数量:{}", filePath, list.size());
            count = count + list.size();
            BigDecimal sum = list.stream().map(ContractShopExcelDTO::getWeight).reduce(weight, BigDecimal::add);
            result = result.add(sum);
        }
        log.info("总数量，{}", count);
        log.info("总重量，{}", result);
    }

    @Override
    public void fourRate(DealDataInput input) {
        String readPath = input.getReadPath();
        String exportPath = input.getExportPath();
        File dir = new File(readPath);
        String[] fileNames = dir.list();
        assert fileNames != null;
        List<FourRateExcelDTO> export = new ArrayList<>(fileNames.length);
        for (String fileName : fileNames) {
            if (!fileName.contains(".xlsx")) {
                continue;
            }
            List<ContractShopExcelDTO> list = new ArrayList<>();
            String filePath = readPath + "/" + fileName;
            System.out.println("filePath:" + filePath);
            EasyExcel.read(filePath, ContractShopExcelDTO.class, new ReadListener<ContractShopExcelDTO>() {
                @Override
                public void invoke(ContractShopExcelDTO dto, AnalysisContext analysisContext) {
                    list.add(dto);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    // 移除最后一条数据（集合非空时）
//                    if (!list.isEmpty()) {
//                        list.remove(list.size() - 1);
//                        System.out.println("已跳过最后一条数据");
//                    }
                }
            }).sheet().doRead();
            QueryWrapper<Customer> wrapper = new QueryWrapper<>();
            wrapper.eq("k_name", fileName.replaceAll(".xlsx", ""));
            boolean haiNan = customerMapper.selectOne(wrapper).getThreeFlag();
            int count;
            if (haiNan) {
                count = (int) list.stream().filter(e -> AreaUtil.AREA_4_2.contains(e.getProvince())).count();
            } else {
                count = (int) list.stream().filter(e -> AreaUtil.AREA_4.contains(e.getProvince())).count();
            }
            String rate = BigDecimal.valueOf(count).divide(BigDecimal.valueOf(list.size()), 5, RoundingMode.CEILING)
                    .multiply(BigDecimal.valueOf(100)).toString();
            rate = rate.substring(0, rate.length() - 2) + "%";
            FourRateExcelDTO dto = FourRateExcelDTO.builder().kName(fileName.replaceAll(".xlsx", ""))
                    .amount(BigDecimal.valueOf(list.size()))
                    .fourCount(BigDecimal.valueOf(count)).fourRate(rate).build();
            export.add(dto);
        }
        String month = LocalDate.now().getDayOfMonth() + "月";
        String exportName = "公司" + month + "四区占比.xlsx";
        EasyExcel.write(exportPath + "/" + exportName, FourRateExcelDTO.class).sheet().doWrite(export);
    }

    @Override
    public void compile(DealDataInput input) {
        String readPath = input.getReadPath();
        String exportPath = input.getExportPath();
        File dir = new File(readPath);
        String[] fileNames = dir.list();
        assert fileNames != null;
        List<BillCompileDTO> result = new ArrayList<>(fileNames.length);
        for (String fileName : fileNames) {
            if (!fileName.contains(".xlsx")) {
                continue;
            }
            ContractShopExcelDTO data = ContractShopExcelDTO.builder()
                    .kName(fileName.replaceAll("[\\d月]", "").replaceAll(".xlsx", "")).build();
            String filePath = readPath + "/" + fileName;
            System.out.println("filePath:" + filePath);
            EasyExcel.read(filePath, ContractShopExcelDTO.class, new ReadListener<ContractShopExcelDTO>() {
                @Override
                public void invoke(ContractShopExcelDTO dto, AnalysisContext analysisContext) {
                    data.setExpense(dto.getExpense());
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                }
            }).sheet().doRead();
            BillCompileDTO dto = new BillCompileDTO();
            dto.setName(data.getKName());
            dto.setCount(data.getExpense());
            result.add(dto);
        }
        EasyExcel.write(exportPath + "/汇总.xlsx", BillCompileDTO.class).sheet().doWrite(result);
    }

    @Override
    public void backUp() {
        backUpService.backUp();
    }
}
