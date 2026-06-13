package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.core.collection.ListUtil;
import com.express.yto.dao.DailyBillMapper;
import com.express.yto.dao.ShopEmpMapper;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.CustomerCodeAndNameDTO;
import com.express.yto.dto.CustomerStatisticsDTO;
import com.express.yto.dto.CustomerStatisticsSummaryDTO;
import com.express.yto.dto.DailyBillExcelDTO;
import com.express.yto.dto.ShopCustomerNameDTO;
import com.express.yto.factory.FileHandlerFactory;
import com.express.yto.model.DailyBill;
import com.express.yto.model.Prepayment;
import com.express.yto.model.Area;
import com.express.yto.service.AreaService;
import com.express.yto.service.DailyBillService;
import com.express.yto.service.EmployeeService;
import com.express.yto.service.ExcelFileHandler;
import com.express.yto.service.PolicyService;
import com.express.yto.service.PrepaymentService;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DailyBillServiceImpl extends ServiceImpl<DailyBillMapper, DailyBill> implements DailyBillService {

    @Autowired
    private ShopEmpMapper shopEmpMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FileHandlerFactory factory;

    @Autowired
    private PrepaymentService prepaymentService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private PolicyService policyService;

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/M/d H:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/M/d")
    };
    private static final int BATCH_SIZE = 1000;

    @Override
    @Transactional
    public String uploadBill(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        log.info("开始上传每日账单: {}", fileName);

        List<DailyBill> dataList = new ArrayList<>(BATCH_SIZE);
        int[] counts = {0, 0}; // successCount, failCount

        try (InputStream is = file.getInputStream()) {
            ExcelReaderBuilder readerBuilder = EasyExcel.read(is, DailyBillExcelDTO.class, 
                new AnalysisEventListener<DailyBillExcelDTO>() {
                    @Override
                    public void invoke(DailyBillExcelDTO dto, AnalysisContext context) {
                        try {
                            DailyBill bill = convertToEntity(dto);
                            if (bill.getWaybillNo() != null && !bill.getWaybillNo().isEmpty() 
                                    && bill.getCustomerCode() != null && !bill.getCustomerCode().isEmpty()) {
                                dataList.add(bill);
                                counts[0]++;
                                if (dataList.size() >= BATCH_SIZE) {
                                    baseMapper.insertBatch(dataList);
                                    dataList.clear();
                                }
                            }
                        } catch (Exception e) {
                            counts[1]++;
                            log.warn("第{}行数据解析失败: {}", context.readRowHolder().getRowIndex() + 1, e.getMessage());
                        }
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        if (!dataList.isEmpty()) {
                            baseMapper.insertBatch(dataList);
                            dataList.clear();
                        }
                    }
                });

            if (fileName != null && fileName.toLowerCase().endsWith(".csv")) {
                readerBuilder.excelType(ExcelTypeEnum.CSV).charset(StandardCharsets.UTF_8);
            }

            readerBuilder.sheet().doRead();

        } catch (Exception e) {
            log.error("上传每日账单失败", e);
            return "上传失败: " + e.getMessage();
        }

        log.info("每日账单上传完成，成功{}条，失败{}条", counts[0], counts[1]);
        return String.format("每日账单上传完成，成功%d条，失败%d条", counts[0], counts[1]);
    }

    private DailyBill convertToEntity(DailyBillExcelDTO dto) {
        DailyBill bill = new DailyBill();
        bill.setWaybillNo(dto.getWaybillNo());
        bill.setCustomerCode(dto.getCustomerCode());
        bill.setCustomerName(dto.getCustomerName());
        bill.setChargeWeight(dto.getChargeWeight());
        bill.setTotalAmount(dto.getTotalAmount());
        
        String merchantCode = dto.getMerchantCode();
        String merchantName = dto.getMerchantName();
        if (merchantCode == null || merchantCode.isEmpty()) {
            merchantCode = dto.getCustomerCode();
        }
        if (merchantName == null || merchantName.isEmpty()) {
            merchantName = dto.getCustomerName();
        }
        bill.setMerchantCode(merchantCode);
        bill.setMerchantName(merchantName);
        
        bill.setSettleProvince(dto.getSettleProvince());
        bill.setChargeDate(parseDate(dto.getChargeDate()));
        bill.setRebateAmount(null);
        bill.setCustomerFee(null);
        bill.setImportTime(LocalDateTime.now());
        return bill;
    }

    @Override
    @Transactional
    public String syncCustomerInfoFromShopEmp(LocalDate date) {
        log.info("开始同步 t_daily_bill 表客户信息, 日期筛选: {}", date);
        
        List<DailyBill> distinctList;
        if (date != null) {
            distinctList = baseMapper.selectDistinctMerchantCustomerByDate(date);
        } else {
            distinctList = baseMapper.selectDistinctMerchantCustomer();
        }
        
        List<ShopCustomerNameDTO> shopEmpList = shopEmpMapper.getShopCustomer();
        
        List<DailyBill> updateList = new ArrayList<>();
        int skipCount = 0;
        int notFoundCount = 0;
        
        for (DailyBill bill : distinctList) {
            String merchantCode = bill.getMerchantCode();
            String merchantName = bill.getMerchantName();
            String currentCustomerCode = bill.getCustomerCode();
            String currentCustomerName = bill.getCustomerName();
            
            ShopCustomerNameDTO shopEmp = shopEmpList.stream()
                    .filter(e -> merchantCode.equals(e.getShopId()))
                    .findFirst()
                    .orElse(null);
            
            if (shopEmp == null) {
                notFoundCount++;
                continue;
            }
            
            String shopCustomerCode = shopEmp.getCustomerCode();
            String shopCustomerName = shopEmp.getCustomerName();
            String shopEmpName = shopEmp.getEmpName();
            String shopEmpType = shopEmp.getEmpType();
            
            boolean needUpdate = !shopCustomerCode.equals(currentCustomerCode) || 
                                !shopCustomerName.equals(currentCustomerName) ||
                                (shopEmpName != null && !shopEmpName.isEmpty()) ||
                                (shopEmpType != null && !shopEmpType.isEmpty());
            
            if (needUpdate) {
                DailyBill updateBill = new DailyBill();
                updateBill.setMerchantCode(merchantCode);
                updateBill.setMerchantName(merchantName);
                updateBill.setCustomerCode(shopCustomerCode);
                updateBill.setCustomerName(shopCustomerName);
                if (shopEmpName != null && !shopEmpName.isEmpty()) {
                    updateBill.setEmpName(shopEmpName);
                }
                if (shopEmpType != null && !shopEmpType.isEmpty()) {
                    updateBill.setEmpType(shopEmpType);
                }
                updateList.add(updateBill);
                log.info("待更新客户信息: merchantCode={}, merchantName={}, oldCustomerCode={}, newCustomerCode={}, empName={}, empType={}", 
                    merchantCode, merchantName, currentCustomerCode, shopCustomerCode, shopEmpName, shopEmpType);
            } else {
                skipCount++;
            }
        }
        
        int updateCount = 0;
        if (!updateList.isEmpty()) {
            baseMapper.batchUpdateCustomerInfo(updateList);
            updateCount = updateList.size();
            log.info("批量更新完成，共更新{}条记录", updateCount);
        }
        
        log.info("客户信息同步完成，更新{}条，跳过{}条，未找到对应商家{}条", updateCount, skipCount, notFoundCount);
        return String.format("客户信息同步完成，更新%d条，跳过%d条，未找到对应商家%d条", updateCount, skipCount, notFoundCount);
    }

   

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr.trim(), formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    @Override
    @Transactional
    public String calculateBill(LocalDate date) {
        log.info("开始计算 t_daily_bill 表费用, 日期筛选: {}", date);

        QueryWrapper<DailyBill> queryWrapper = new QueryWrapper<>();
        if (date != null) {
            queryWrapper.eq("charge_date", date);
        }
        queryWrapper.isNull("customer_fee");

        List<DailyBill> billList = list(queryWrapper);
        if (billList.isEmpty()) {
            log.info("未找到需要计算的账单数据");
            return "未找到需要计算的账单数据";
        }

        log.info("找到{}条需要计算的账单", billList.size());

        List<DailyBill> directBillList = billList.stream()
                .filter(bill -> bill.getEmpType() == null || bill.getEmpType().isEmpty())
                .collect(Collectors.toList());

        List<DailyBill> contractBillList = billList.stream()
                .filter(bill -> bill.getEmpType() != null && !bill.getEmpType().isEmpty())
                .collect(Collectors.toList());

        List<CustomerCodeAndNameDTO> directCustomerList = directBillList.stream()
                .filter(bill -> bill.getCustomerCode() != null && !bill.getCustomerCode().isEmpty())
                .map(bill -> {
                    CustomerCodeAndNameDTO dto = new CustomerCodeAndNameDTO();
                    dto.setCustomerCode(bill.getCustomerCode());
                    dto.setCustomerName(bill.getCustomerName());
                    return dto;
                })
                .distinct()
                .collect(Collectors.toList());

        executeDirectCustomerTask(directCustomerList, directBillList);

        List<ContractShopExcelDTO> updateList = new ArrayList<>();

        if (!contractBillList.isEmpty()) {
            List<DailyBill> aliLooseBillList = contractBillList.stream()
                    .filter(bill -> "散件".equals(bill.getEmpType()) || "淘宝".equals(bill.getEmpType()))
                    .collect(Collectors.toList());
            if (!aliLooseBillList.isEmpty()) {
                List<ContractShopExcelDTO> aliLoose = convertToContractShopExcelDTO(aliLooseBillList);
                List<ContractShopExcelDTO> afterAliLoose = employeeService.aliAndLoose(aliLoose, "yto_576017", false);
                updateList.addAll(afterAliLoose);
            }

            List<DailyBill> limitBillList = contractBillList.stream()
                    .filter(bill -> "限定".equals(bill.getEmpType()))
                    .collect(Collectors.toList());
            if (!limitBillList.isEmpty()) {
                List<ContractShopExcelDTO> limitList = convertToContractShopExcelDTO(limitBillList);
                List<ContractShopExcelDTO> afterLimit = employeeService.aliAndLoose(limitList, "yto_576017_limit", true);
                updateList.addAll(afterLimit);
            }

            List<DailyBill> specialBillList = contractBillList.stream()
                    .filter(bill -> "特批".equals(bill.getEmpType()))
                    .collect(Collectors.toList());
            if (!specialBillList.isEmpty()) {
                List<ContractShopExcelDTO> specialList = convertToContractShopExcelDTO(specialBillList);
                List<ContractShopExcelDTO> afterSpecial = employeeService.dealSpecial(specialList);
                updateList.addAll(afterSpecial);
            }

            Map<String, ContractShopExcelDTO> dtoMap = updateList.stream()
                    .collect(Collectors.toMap(ContractShopExcelDTO::getId, dto -> dto));

            for (DailyBill bill : contractBillList) {
                ContractShopExcelDTO dto = dtoMap.get(bill.getWaybillNo());
                if (dto != null && dto.getExpense() != null) {
                    bill.setCustomerFee(dto.getExpense());
                }
            }
        }

        if (!billList.isEmpty()) {
            addPrepaymentBack(billList);

            int batchSize = 20000;
            List<List<DailyBill>> partitionList = ListUtil.split(billList, batchSize);

            log.info("批量更新运单费用，总条数：{}，分批数：{}", billList.size(), partitionList.size());

            for (List<DailyBill> batchList : partitionList) {
                updateBatchById(batchList);
            }

            log.info("批量更新运单费用全部完成");
        }

        log.info("费用计算完成，处理{}条", updateList.size());
        return String.format("费用计算完成，处理%d条", updateList.size());
    }

    private void addPrepaymentBack(List<DailyBill> billList) {
        Map<String, List<DailyBill>> billByQueryCodeMap = new HashMap<>();

        for (DailyBill bill : billList) {
            if (bill.getCustomerFee() == null) {
                continue;
            }

            String queryCode = getPrepaymentQueryCode(bill);
            if (queryCode == null || queryCode.isEmpty()) {
                continue;
            }

            billByQueryCodeMap.computeIfAbsent(queryCode, k -> new ArrayList<>()).add(bill);
        }

        if (billByQueryCodeMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, List<DailyBill>> entry : billByQueryCodeMap.entrySet()) {
            String queryCode = entry.getKey();
            List<DailyBill> customerBills = entry.getValue();

            List<Prepayment> prepaymentList = prepaymentService.list(new QueryWrapper<Prepayment>().eq("code", queryCode));
            if (prepaymentList.isEmpty()) {
                continue;
            }

            for (DailyBill bill : customerBills) {
                LocalDate chargeDate = bill.getChargeDate();
                BigDecimal prepayment = prepaymentList.stream()
                        .filter(p -> chargeDate != null 
                                && !chargeDate.isBefore(p.getStartTime()) 
                                && !chargeDate.isAfter(p.getEndTime()))
                        .map(Prepayment::getPreFee)
                        .findFirst()
                        .orElse(null);

                if (prepayment != null && prepayment.compareTo(BigDecimal.ZERO) > 0) {
                bill.setCustomerFee(bill.getCustomerFee().add(prepayment));
            }
            }
        }
    }

    private String getPrepaymentQueryCode(DailyBill bill) {
        String empType = bill.getEmpType();
        
        if (empType == null || empType.isEmpty()) {
            return bill.getCustomerCode();
        }
        
        if ("散件".equals(empType) || "淘宝".equals(empType)) {
            return "yto_576017";
        }
        
        if ("限定".equals(empType)) {
            return "yto_576017_limit";
        }
        
        return bill.getCustomerCode();
    }

    private void executeDirectCustomerTask(List<CustomerCodeAndNameDTO> codeAndName, List<DailyBill> billList) {
        if (codeAndName == null || codeAndName.isEmpty()) {
            log.info("【直营客户账单】暂无数据需要处理");
            return;
        }

        log.info("【直营客户账单】开始处理，总客户数：{}", codeAndName.size());

        for (CustomerCodeAndNameDTO dto : codeAndName) {
            calculateDirectBill(dto, billList);
        }

        log.info("【直营客户账单】所有任务已完成");
    }

    private void calculateDirectBill(CustomerCodeAndNameDTO dto, List<DailyBill> billList) {
        log.info("开始处理{}数据", dto.getCustomerName());
        
        try {
            ExcelFileHandler handler = factory.getCustomerHandler(dto.getCustomerName());
            
            List<DailyBill> customerBillList = billList.stream()
                    .filter(bill -> dto.getCustomerCode().equals(bill.getCustomerCode()))
                    .collect(Collectors.toList());

            if (customerBillList.isEmpty()) {
                log.info("客户{}没有需要处理的账单", dto.getCustomerName());
                return;
            }

            List<ContractShopExcelDTO> contractList = customerBillList.stream()
                    .map(bill -> {
                        ContractShopExcelDTO contractDto = new ContractShopExcelDTO();
                        contractDto.setId(bill.getWaybillNo());
                        contractDto.setScanDate(bill.getChargeDate());
                        contractDto.setWeight(bill.getChargeWeight());
                        contractDto.setProvince(bill.getSettleProvince());
                        contractDto.setDestination("");
                        contractDto.setCode(bill.getCustomerCode());
                        contractDto.setName(bill.getCustomerName());
                        contractDto.setShopId(bill.getMerchantCode());
                        contractDto.setShopName(bill.getMerchantName());
                        return contractDto;
                    })
                    .collect(Collectors.toList());

            String companyId = "yto_576017";
            if ("陈丽芝".equals(dto.getCustomerName())) {
                companyId = dto.getCustomerCode();
            }

            List<ContractShopExcelDTO> resultList = handler.handle(contractList, companyId);

            Map<String, ContractShopExcelDTO> resultMap = resultList.stream()
                    .collect(Collectors.toMap(ContractShopExcelDTO::getId, item -> item));

            for (DailyBill bill : customerBillList) {
                ContractShopExcelDTO resultDto = resultMap.get(bill.getWaybillNo());
                if (resultDto != null && resultDto.getExpense() != null) {
                    bill.setCustomerFee(resultDto.getExpense());
                }
            }

            log.info("客户{}处理完成，处理{}条", dto.getCustomerName(), customerBillList.size());
        } catch (Exception e) {
            log.error("处理客户{}数据失败: {}", dto.getCustomerName(), e.getMessage());
        }
    }

    private List<ContractShopExcelDTO> convertToContractShopExcelDTO(List<DailyBill> billList) {
        return billList.stream()
                .map(bill -> {
                    ContractShopExcelDTO dto = new ContractShopExcelDTO();
                    dto.setId(bill.getWaybillNo());
                    dto.setScanDate(bill.getChargeDate());
                    dto.setWeight(bill.getChargeWeight());
                    dto.setProvince(bill.getSettleProvince());
                    dto.setDestination("");
                    dto.setCode(bill.getCustomerCode());
                    dto.setName(bill.getCustomerName());
                    dto.setShopId(bill.getMerchantCode());
                    dto.setShopName(bill.getMerchantName());
                    dto.setShopType(bill.getEmpType());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerStatisticsDTO> getCustomerStatistics(LocalDate date) {
        log.info("开始查询客户统计数据, 日期: {}", date);

        QueryWrapper<DailyBill> queryWrapper = new QueryWrapper<>();
        if (date != null) {
            queryWrapper.eq("charge_date", date);
        }

        List<DailyBill> billList = list(queryWrapper);
        if (billList.isEmpty()) {
            log.info("未找到账单数据");
            return new ArrayList<>();
        }

        List<Area> areaList = areaService.list(new QueryWrapper<Area>().eq("company_id", "yto_576017"));

        Map<String, List<DailyBill>> billByCustomerMap = billList.stream()
                .filter(bill -> bill.getCustomerCode() != null && !bill.getCustomerCode().isEmpty())
                .collect(Collectors.groupingBy(bill -> bill.getCustomerCode() + "_" + bill.getCustomerName()));

        int totalCount = billList.size();

        List<CustomerStatisticsDTO> resultList = new ArrayList<>();

        for (Map.Entry<String, List<DailyBill>> entry : billByCustomerMap.entrySet()) {
            String key = entry.getKey();
            String customerCode = key.split("_")[0];
            String customerName = key.substring(key.indexOf("_") + 1);
            List<DailyBill> customerBills = entry.getValue();

            CustomerStatisticsDTO dto = new CustomerStatisticsDTO();
            dto.setCustomerCode(customerCode);
            dto.setCustomerName(customerName);

            int customerCount = customerBills.size();
            dto.setTotalCount(customerCount);

            BigDecimal totalWeight = customerBills.stream()
                    .map(bill -> bill.getChargeWeight() != null ? bill.getChargeWeight() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avgWeight = customerCount > 0 ? totalWeight.divide(BigDecimal.valueOf(customerCount), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
            dto.setAvgWeight(avgWeight);

            BigDecimal proportion = totalCount > 0 ? BigDecimal.valueOf(customerCount).divide(BigDecimal.valueOf(totalCount), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
            dto.setProportion(proportion);

            int weight05Count = 0, weight10Count = 0, weight15Count = 0, weight20Count = 0, weight30Count = 0, weightOver30Count = 0;
            for (DailyBill bill : customerBills) {
                BigDecimal weight = bill.getChargeWeight();
                if (weight == null) {
                    continue;
                }
                int compare05 = weight.compareTo(BigDecimal.valueOf(0.5));
                int compare10 = weight.compareTo(BigDecimal.valueOf(1.0));
                int compare15 = weight.compareTo(BigDecimal.valueOf(1.5));
                int compare20 = weight.compareTo(BigDecimal.valueOf(2.0));
                int compare30 = weight.compareTo(BigDecimal.valueOf(3.0));

                if (compare05 <= 0) {
                    weight05Count++;
                } else if (compare10 <= 0) {
                    weight10Count++;
                } else if (compare15 <= 0) {
                    weight15Count++;
                } else if (compare20 <= 0) {
                    weight20Count++;
                } else if (compare30 <= 0) {
                    weight30Count++;
                } else {
                    weightOver30Count++;
                }
            }

            dto.setWeight05Percent(customerCount > 0 ? BigDecimal.valueOf(weight05Count).divide(BigDecimal.valueOf(customerCount), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            dto.setWeight10Percent(customerCount > 0 ? BigDecimal.valueOf(weight10Count).divide(BigDecimal.valueOf(customerCount), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            dto.setWeight15Percent(customerCount > 0 ? BigDecimal.valueOf(weight15Count).divide(BigDecimal.valueOf(customerCount), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            dto.setWeight20Percent(customerCount > 0 ? BigDecimal.valueOf(weight20Count).divide(BigDecimal.valueOf(customerCount), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            dto.setWeight30Percent(customerCount > 0 ? BigDecimal.valueOf(weight30Count).divide(BigDecimal.valueOf(customerCount), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            dto.setWeightOver30Percent(customerCount > 0 ? BigDecimal.valueOf(weightOver30Count).divide(BigDecimal.valueOf(customerCount), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);

            int area1Count = 0, area2Count = 0, area3Count = 0, area4Count = 0, area5Count = 0;
            for (DailyBill bill : customerBills) {
                String destProvince = bill.getSettleProvince();
                if (destProvince == null || destProvince.isEmpty()) {
                    continue;
                }

                Area matchedArea = null;
                for (Area area : areaList) {
                    String areaCity = area.getAreaCity();
                    if (areaCity != null && areaCity.contains(destProvince)) {
                        matchedArea = area;
                        break;
                    }
                }

                if (matchedArea != null) {
                    Integer areaNum = matchedArea.getAreaNum();
                    if (areaNum != null) {
                        switch (areaNum) {
                            case 1: area1Count++; break;
                            case 2: area2Count++; break;
                            case 3: area3Count++; break;
                            case 4: area4Count++; break;
                            case 5: area5Count++; break;
                        }
                    }
                }
            }

            dto.setArea1Percent(customerCount > 0 ? BigDecimal.valueOf(area1Count).divide(BigDecimal.valueOf(customerCount), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            dto.setArea2Percent(customerCount > 0 ? BigDecimal.valueOf(area2Count).divide(BigDecimal.valueOf(customerCount), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            dto.setArea3Percent(customerCount > 0 ? BigDecimal.valueOf(area3Count).divide(BigDecimal.valueOf(customerCount), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            dto.setArea4Percent(customerCount > 0 ? BigDecimal.valueOf(area4Count).divide(BigDecimal.valueOf(customerCount), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            dto.setArea5Percent(customerCount > 0 ? BigDecimal.valueOf(area5Count).divide(BigDecimal.valueOf(customerCount), 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);

            BigDecimal totalAmount = customerBills.stream()
                    .map(bill -> bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setTotalAmount(totalAmount);

            BigDecimal customerFee = customerBills.stream()
                    .map(bill -> bill.getCustomerFee() != null ? bill.getCustomerFee() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setCustomerFee(customerFee);

            BigDecimal rebateAmount = BigDecimal.ZERO;
            dto.setRebateAmount(rebateAmount);

            BigDecimal profit = customerFee.add(rebateAmount).subtract(totalAmount);
            dto.setProfit(profit);

            if (date != null) {
                LocalDate yesterday = date.minusDays(1);
                QueryWrapper<DailyBill> yesterdayQuery = new QueryWrapper<>();
                yesterdayQuery.eq("charge_date", yesterday);
                yesterdayQuery.eq("customer_code", customerCode);
                int yesterdayCount = (int) count(yesterdayQuery);

                BigDecimal dayOnDayRatio = null;
                if (yesterdayCount > 0 && customerCount > 0) {
                    dayOnDayRatio = BigDecimal.valueOf(customerCount).divide(BigDecimal.valueOf(yesterdayCount), 4, BigDecimal.ROUND_HALF_UP)
                            .subtract(BigDecimal.ONE);
                }
                dto.setDayOnDayRatio(dayOnDayRatio);
            }

            resultList.add(dto);
        }

        resultList.sort((a, b) -> b.getTotalCount() - a.getTotalCount());

        log.info("客户统计查询完成，共{}个客户", resultList.size());
        return resultList;
    }

    @Override
    public CustomerStatisticsSummaryDTO getCustomerStatisticsSummary(LocalDate date) {
        log.info("开始查询客户统计汇总数据, 日期: {}", date);

        QueryWrapper<DailyBill> queryWrapper = new QueryWrapper<>();
        if (date != null) {
            queryWrapper.eq("charge_date", date);
        }

        List<DailyBill> billList = list(queryWrapper);
        if (billList.isEmpty()) {
            log.info("未找到账单数据");
            CustomerStatisticsSummaryDTO emptySummary = new CustomerStatisticsSummaryDTO();
            emptySummary.setCustomerCount(0);
            emptySummary.setTotalCount(0);
            emptySummary.setAvgWeight(BigDecimal.ZERO);
            emptySummary.setAvgDayOnDayRatio(null);
            emptySummary.setTotalAmount(BigDecimal.ZERO);
            emptySummary.setTotalCustomerFee(BigDecimal.ZERO);
            emptySummary.setTotalRebateAmount(BigDecimal.ZERO);
            emptySummary.setTotalProfit(BigDecimal.ZERO);
            emptySummary.setFixedPolicyFee(BigDecimal.ZERO);
            return emptySummary;
        }

        CustomerStatisticsSummaryDTO summary = new CustomerStatisticsSummaryDTO();

        int totalCount = billList.size();
        summary.setTotalCount(totalCount);

        Map<String, List<DailyBill>> billByCustomerMap = billList.stream()
                .filter(bill -> bill.getCustomerCode() != null && !bill.getCustomerCode().isEmpty())
                .collect(Collectors.groupingBy(bill -> bill.getCustomerCode() + "_" + bill.getCustomerName()));
        summary.setCustomerCount(billByCustomerMap.size());

        BigDecimal totalWeight = billList.stream()
                .map(bill -> bill.getChargeWeight() != null ? bill.getChargeWeight() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgWeight = totalCount > 0 ? totalWeight.divide(BigDecimal.valueOf(totalCount), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
        summary.setAvgWeight(avgWeight);

        BigDecimal totalAmount = billList.stream()
                .map(bill -> bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalAmount(totalAmount);

        BigDecimal totalCustomerFee = billList.stream()
                .map(bill -> bill.getCustomerFee() != null ? bill.getCustomerFee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalCustomerFee(totalCustomerFee);

        BigDecimal totalRebateAmount = BigDecimal.ZERO;
        summary.setTotalRebateAmount(totalRebateAmount);

        // 查询固定政策收费（政策类型为2-固定收费的金额总和）
        BigDecimal fixedPolicyFee = policyService.getFixedPolicyTotalAmount();
        summary.setFixedPolicyFee(fixedPolicyFee != null ? fixedPolicyFee : BigDecimal.ZERO);

        // 总盈利 = 客户中转费 + 返利 - 成本 + 固定政策收费
        BigDecimal totalProfit = totalCustomerFee.add(totalRebateAmount).subtract(totalAmount).add(fixedPolicyFee != null ? fixedPolicyFee : BigDecimal.ZERO);
        summary.setTotalProfit(totalProfit);

        if (date != null) {
            LocalDate yesterday = date.minusDays(1);

            QueryWrapper<DailyBill> yesterdayQuery = new QueryWrapper<>();
            yesterdayQuery.eq("charge_date", yesterday);
            int yesterdayTotalCount = (int) count(yesterdayQuery);

            BigDecimal avgDayOnDayRatio = null;
            if (yesterdayTotalCount > 0) {
                avgDayOnDayRatio = BigDecimal.valueOf(totalCount)
                        .divide(BigDecimal.valueOf(yesterdayTotalCount), 4, BigDecimal.ROUND_HALF_UP)
                        .subtract(BigDecimal.ONE);
            }
            summary.setAvgDayOnDayRatio(avgDayOnDayRatio);
        } else {
            summary.setAvgDayOnDayRatio(null);
        }

        log.info("客户统计汇总查询完成");
        return summary;
    }
}