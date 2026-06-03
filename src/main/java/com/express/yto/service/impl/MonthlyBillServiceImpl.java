package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.ContractStaffMapper;
import com.express.yto.dao.MonthlyBillMapper;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.MonthlyBillExportDTO;
import com.express.yto.dto.MonthlyBillSearchInput;
import com.express.yto.dto.MonthlyBillSummaryDTO;
import com.express.yto.model.ContractStaff;
import com.express.yto.model.MonthlyBill;
import com.express.yto.model.WaybillDetail;
import com.express.yto.service.MonthlyBillService;
import com.express.yto.util.ExcelUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MonthlyBillServiceImpl extends ServiceImpl<MonthlyBillMapper, MonthlyBill> implements MonthlyBillService {

    @Autowired
    private MonthlyBillMapper monthlyBillMapper;

    @Autowired
    private ContractStaffMapper contractStaffMapper;

    @Override
    public IPage<MonthlyBill> search(MonthlyBillSearchInput input) {
        Page<MonthlyBill> page = new Page<>(input.getPageNo(), input.getPageSize());
        QueryWrapper<MonthlyBill> qw = new QueryWrapper<>();
        qw.eq("type", input.getType());

        if ("time".equals(input.getDimensionType())) {
            if (StringUtils.isNotBlank(input.getBillMonth())) {
                qw.eq("bill_month", input.getBillMonth());
            }
        } else if ("customer".equals(input.getDimensionType())) {
            if (StringUtils.isNotBlank(input.getCustomerName())) {
                qw.like("cust_name", input.getCustomerName());
            }
        }

        if (StringUtils.isNotBlank(input.getSortField())) {
            String sortField = input.getSortField();
            if ("custName".equals(sortField)) {
                sortField = "cust_name";
            } else if ("receiveCount".equals(sortField)) {
                sortField = "receive_count";
            } else if ("receivableAmount".equals(sortField)) {
                sortField = "receivable_amount";
            }

            if ("asc".equalsIgnoreCase(input.getSortOrder())) {
                qw.orderByAsc(sortField);
            } else {
                qw.orderByDesc(sortField);
            }
        } else {
            qw.orderByDesc("create_time");
        }

        return monthlyBillMapper.selectPage(page, qw);
    }

    @Override
    public void updateBill(MonthlyBill input) {
        input.setUpdateTime(LocalDateTime.now());
        monthlyBillMapper.updateById(input);
    }

    @Override
    @Transactional
    public void generateSummaryBill(String billMonth) {
        monthlyBillMapper.deleteByBillMonth(billMonth);

        List<MonthlyBill> billList = new ArrayList<>();

        List<MonthlyBillSummaryDTO> directCustomerList = monthlyBillMapper.getDirectCustomerData();
        for (MonthlyBillSummaryDTO dto : directCustomerList) {
            billList.add(MonthlyBill.builder()
                    .billMonth(billMonth)
                    .custName(dto.getCustName())
                    .code(dto.getCode())
                    .receiveCount(dto.getReceiveCount())
                    .avgWeight(dto.getAvgWeight())
                    .receivableAmount(dto.getReceivableAmount())
                    .type(0)
                    .build());
        }

        List<MonthlyBillSummaryDTO> employeeList = monthlyBillMapper.getEmployeeData();
        for (MonthlyBillSummaryDTO dto : employeeList) {
            billList.add(MonthlyBill.builder()
                    .billMonth(billMonth)
                    .custName(dto.getCustName())
                    .code(dto.getCode())
                    .receiveCount(dto.getReceiveCount())
                    .avgWeight(dto.getAvgWeight())
                    .receivableAmount(dto.getReceivableAmount())
                    .type(1)
                    .build());
        }

        List<MonthlyBillSummaryDTO> contractLooseList = monthlyBillMapper.getContractLooseData();
        for (MonthlyBillSummaryDTO dto : contractLooseList) {
            billList.add(MonthlyBill.builder()
                    .billMonth(billMonth)
                    .custName(dto.getCustName() + dto.getCustType())
                    .code(dto.getCode())
                    .receiveCount(dto.getReceiveCount())
                    .avgWeight(dto.getAvgWeight())
                    .receivableAmount(dto.getReceivableAmount())
                    .type(2)
                    .build());
        }

        List<MonthlyBillSummaryDTO> contractTaobaoLimitedList = monthlyBillMapper.getContractTaobaoLimitedData();
        for (MonthlyBillSummaryDTO dto : contractTaobaoLimitedList) {
            billList.add(MonthlyBill.builder()
                    .billMonth(billMonth)
                    .custName(dto.getCustName() + dto.getCustType())
                    .code(dto.getCode())
                    .receiveCount(dto.getReceiveCount())
                    .avgWeight(dto.getAvgWeight())
                    .receivableAmount(dto.getReceivableAmount())
                    .type(2)
                    .build());
        }

        List<MonthlyBillSummaryDTO> contractSpecialList = monthlyBillMapper.getContractSpecialData();
        for (MonthlyBillSummaryDTO dto : contractSpecialList) {
            billList.add(MonthlyBill.builder()
                    .billMonth(billMonth)
                    .custName(dto.getEmpName() + dto.getCustType() + "-" + dto.getCustName())
                    .code(dto.getCode())
                    .receiveCount(dto.getReceiveCount())
                    .avgWeight(dto.getAvgWeight())
                    .receivableAmount(dto.getReceivableAmount())
                    .type(2)
                    .build());
        }

        if (!billList.isEmpty()) {
            monthlyBillMapper.insert(billList);
        }
    }

    @Override
    public void exportSummary(MonthlyBillSearchInput input, OutputStream outputStream) {
        QueryWrapper<MonthlyBill> qw = new QueryWrapper<>();
        
        if ("time".equals(input.getDimensionType())) {
            if (StringUtils.isNotBlank(input.getBillMonth())) {
                qw.eq("bill_month", input.getBillMonth());
            }
        } else if ("customer".equals(input.getDimensionType())) {
            if (StringUtils.isNotBlank(input.getCustomerName())) {
                qw.like("cust_name", input.getCustomerName());
            }
        }

        if (StringUtils.isNotBlank(input.getSortField())) {
            String sortField = input.getSortField();
            if ("custName".equals(sortField)) {
                sortField = "cust_name";
            } else if ("receiveCount".equals(sortField)) {
                sortField = "receive_count";
            } else if ("receivableAmount".equals(sortField)) {
                sortField = "receivable_amount";
            }

            if ("asc".equalsIgnoreCase(input.getSortOrder())) {
                qw.orderByAsc(sortField);
            } else {
                qw.orderByDesc(sortField);
            }
        } else {
            qw.orderByDesc("create_time");
        }

        qw.eq("type", input.getType());
        List<MonthlyBill> allList = monthlyBillMapper.selectList(qw);
        
        List<MonthlyBillExportDTO> directCustomerList = new ArrayList<>();
        List<MonthlyBillExportDTO> contractAreaList = new ArrayList<>();
        List<MonthlyBillExportDTO> employeeLooseList = new ArrayList<>();
        
        for (MonthlyBill bill : allList) {
            MonthlyBillExportDTO dto = convertToDTO(bill);
            if (bill.getType() == 0) {
                directCustomerList.add(dto);
            } else if (bill.getType() == 1) {
                contractAreaList.add(dto);
            } else if (bill.getType() == 2) {
                employeeLooseList.add(dto);
            }
        }

        ExcelWriter excelWriter = EasyExcel.write(outputStream)
                .registerWriteHandler(ExcelUtil.getBillStyle())
                .registerWriteHandler(ExcelUtil.getColumnWidthStyle())
                .build();
        
        WriteSheet sheet1 = EasyExcel.writerSheet(0, "直营客户")
                .head(MonthlyBillExportDTO.class)
                .build();
        excelWriter.write(directCustomerList, sheet1);
        
        WriteSheet sheet2 = EasyExcel.writerSheet(1, "承包区")
                .head(MonthlyBillExportDTO.class)
                .build();
        excelWriter.write(contractAreaList, sheet2);
        
        WriteSheet sheet3 = EasyExcel.writerSheet(2, "业务员散件")
                .head(MonthlyBillExportDTO.class)
                .build();
        excelWriter.write(employeeLooseList, sheet3);
        
        excelWriter.finish();
    }

    @Override
    public void exportSummaryByBillMonth(String billMonth, OutputStream outputStream) {
        List<MonthlyBill> directCustomerList = monthlyBillMapper.selectList(
                new QueryWrapper<MonthlyBill>()
                        .eq("bill_month", billMonth)
                        .eq("type", 0)
                        .orderByDesc("create_time")
        );
        
        List<MonthlyBill> contractAreaList = monthlyBillMapper.selectList(
                new QueryWrapper<MonthlyBill>()
                        .eq("bill_month", billMonth)
                        .eq("type", 1)
                        .orderByDesc("create_time")
        );
        
        List<MonthlyBill> employeeLooseList = monthlyBillMapper.selectList(
                new QueryWrapper<MonthlyBill>()
                        .eq("bill_month", billMonth)
                        .eq("type", 2)
                        .orderByDesc("create_time")
        );

        List<MonthlyBillExportDTO> directCustomerDTOList = directCustomerList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        List<MonthlyBillExportDTO> contractAreaDTOList = contractAreaList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        List<MonthlyBillExportDTO> employeeLooseDTOList = employeeLooseList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        ExcelWriter excelWriter = EasyExcel.write(outputStream)
                .registerWriteHandler(ExcelUtil.getBillStyle())
                .build();
        
        WriteSheet sheet1 = EasyExcel.writerSheet(0, "直营客户")
                .head(MonthlyBillExportDTO.class)
                .build();
        excelWriter.write(directCustomerDTOList, sheet1);
        setColumnWidths(excelWriter, 0);
        
        WriteSheet sheet2 = EasyExcel.writerSheet(1, "业务员散件")
                .head(MonthlyBillExportDTO.class)
                .build();
        excelWriter.write(contractAreaDTOList, sheet2);
        setColumnWidths(excelWriter, 1);
        
        WriteSheet sheet3 = EasyExcel.writerSheet(2, "承包区")
                .head(MonthlyBillExportDTO.class)
                .build();
        excelWriter.write(employeeLooseDTOList, sheet3);
        setColumnWidths(excelWriter, 2);
        
        excelWriter.finish();
    }
    
    private void setColumnWidths(ExcelWriter excelWriter, int sheetIndex) {
        Sheet sheet = excelWriter.writeContext().writeWorkbookHolder().getWorkbook().getSheetAt(sheetIndex);
        sheet.setColumnWidth(0, 12 * 256);
        sheet.setColumnWidth(1, 10 * 256);
        sheet.setColumnWidth(2, 40 * 256);
        for (int i = 3; i < 12; i++) {
            sheet.setColumnWidth(i, 15 * 256);
        }
    }

    private MonthlyBillExportDTO convertToDTO(MonthlyBill bill) {
        return MonthlyBillExportDTO.builder()
                .verifySign(bill.getVerifySign())
                .billMonth(bill.getBillMonth() != null ? bill.getBillMonth().substring(5) + "月" : "")
                .custName(bill.getCustName())
                .receiveCount(bill.getReceiveCount())
                .divideLine("")
                .receivableAmount(bill.getReceivableAmount())
                .specialRemark(bill.getSpecialRemark())
                .transferType(bill.getTransferType())
                .actualAmount(bill.getActualAmount())
                .transferDate(bill.getTransferDate() != null ? bill.getTransferDate().toString() : "")
                .remark(bill.getRemark())
                .build();
    }

    @Override
    public void exportDetail(String billMonth, String customerName, Integer type, OutputStream outputStream) {
        List<WaybillDetail> waybillDetailList;
        if (type == 0) {
            waybillDetailList = monthlyBillMapper.getDirectCustomerDetailList(billMonth, customerName);
        } else if (type == 1) {
            waybillDetailList = monthlyBillMapper.getEmployeeLooseDetailList(billMonth, customerName);
        } else {
            waybillDetailList = new ArrayList<>();
        }

        List<ContractShopExcelDTO> excelDTOList = waybillDetailList.stream()
                .map(this::convertToContractShopExcelDTO)
                .collect(Collectors.toList());

        ExcelWriter excelWriter = EasyExcel.write(outputStream, ContractShopExcelDTO.class)
                .registerWriteHandler(ExcelUtil.getBillStyle())
                .build();
        WriteSheet writeSheet = EasyExcel.writerSheet("明细").build();
        excelWriter.write(excelDTOList, writeSheet);
        excelWriter.finish();
    }

    private ContractShopExcelDTO convertToContractShopExcelDTO(WaybillDetail detail) {
        return ContractShopExcelDTO.builder()
                .id(detail.getWaybillNo())
                .scanDate(detail.getScanTime())
                .weight(detail.getWeight())
                .province(detail.getProvince())
                .destination(detail.getDestination())
                .employeeName(detail.getSalesmanName())
                .code(detail.getSendCustomer())
                .name(detail.getSendCustomerName())
                .shopId(detail.getSettleCode())
                .shopName(detail.getSettleName())
                .shopType(detail.getMaterialType())
                .officeExtra(detail.getExtraFee())
                .expense(detail.getExpressFee())
                .build();
    }

    @Override
    public void exportAllDetail(String billMonth, OutputStream outputStream) {
        String tempDir = System.getProperty("user.dir") + File.separator + "temp_export_" + System.currentTimeMillis();
        
        try {
            Path basePath = Paths.get(tempDir);
            Files.createDirectories(basePath);
            
            String directCustomerDir = tempDir + File.separator + "直营客户";
            String employeeDir = tempDir + File.separator + "业务员";
            String contractAreaDir = tempDir + File.separator + "承包区";
            
            Files.createDirectories(Paths.get(directCustomerDir));
            Files.createDirectories(Paths.get(employeeDir));
            Files.createDirectories(Paths.get(contractAreaDir));
            
            List<MonthlyBill> billList = monthlyBillMapper.selectList(
                    new QueryWrapper<MonthlyBill>().eq("bill_month", billMonth)
            );
            
            for (MonthlyBill bill : billList) {
                String customerName = bill.getCustName();
                if (StringUtils.isBlank(customerName)) {
                    continue;
                }
                
                List<WaybillDetail> detailList;
                String targetDir;
                String fileName;
                
                if (bill.getType() == 0) {
                    detailList = monthlyBillMapper.getDirectCustomerDetailList(billMonth, customerName);
                    targetDir = directCustomerDir;
                    fileName = customerName + ".xlsx";
                } else if (bill.getType() == 1) {
                    detailList = monthlyBillMapper.getEmployeeLooseDetailList(billMonth, customerName);
                    targetDir = employeeDir;
                    fileName = customerName + ".xlsx";
                } else {
                    continue;
                }
                
                if (detailList == null || detailList.isEmpty()) {
                    continue;
                }
                
                List<ContractShopExcelDTO> excelDTOList = detailList.stream()
                        .map(this::convertToContractShopExcelDTO)
                        .collect(Collectors.toList());
                
                String filePath = targetDir + File.separator + fileName;
                EasyExcel.write(filePath, ContractShopExcelDTO.class)
                        .registerWriteHandler(ExcelUtil.getBillStyle())
                        .sheet("明细")
                        .doWrite(excelDTOList);
            }

            List<ContractStaff> staffList = contractStaffMapper.selectList(new QueryWrapper<ContractStaff>()
                    .eq("staff_type",0).groupBy("contract_name"));
            for (ContractStaff staff : staffList){
                // 散件

                // 淘宝

                // 限定

                // 特批
            }
            
            zipDirectory(tempDir, outputStream);
            
        } catch (Exception e) {
            throw new RuntimeException("导出明细失败", e);
        } finally {
            deleteDirectory(new File(tempDir));
        }
    }

    private void zipDirectory(String sourceDir, OutputStream outputStream) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            File sourceFile = new File(sourceDir);
            addFileToZip(sourceFile, sourceFile.getName(), zos);
        }
    }

    private void addFileToZip(File file, String entryName, ZipOutputStream zos) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File childFile : files) {
                    addFileToZip(childFile, entryName + "/" + childFile.getName(), zos);
                }
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file)) {
                ZipEntry zipEntry = new ZipEntry(entryName);
                zos.putNextEntry(zipEntry);
                
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                
                zos.closeEntry();
            }
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }
}