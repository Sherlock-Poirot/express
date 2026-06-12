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
import com.express.yto.dto.DailyBillExcelDTO;
import com.express.yto.dto.ShopCustomerNameDTO;
import com.express.yto.factory.FileHandlerFactory;
import com.express.yto.model.DailyBill;
import com.express.yto.service.DailyBillService;
import com.express.yto.service.EmployeeService;
import com.express.yto.service.ExcelFileHandler;

import java.util.ArrayList;
import java.util.List;
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
}