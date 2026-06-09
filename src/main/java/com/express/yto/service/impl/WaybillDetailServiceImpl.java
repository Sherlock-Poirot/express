package com.express.yto.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.CustomerMapper;
import com.express.yto.dao.ShopEmpMapper;
import com.express.yto.dao.SysTaskMapper;
import com.express.yto.dao.WaybillDetailMapper;
import com.express.yto.dto.BillIdAndFeeDTO;
import com.express.yto.dto.ContractShopExcelDTO;
import com.express.yto.dto.CustomerCodeAndNameDTO;
import com.express.yto.dto.EmpBillInfoDTO;
import com.express.yto.dto.ShopCustomerNameDTO;
import com.express.yto.enums.ImportStatus;
import com.express.yto.exception.BusinessException;
import com.express.yto.factory.FileHandlerFactory;
import com.express.yto.model.SysTask;
import com.express.yto.model.WaybillDetail;
import com.express.yto.service.EmployeeService;
import com.express.yto.service.ExcelFileHandler;
import com.express.yto.service.WaybillAsyncService;
import com.express.yto.service.WaybillDetailService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Detective
 * @date Created in 2026/5/6
 */
@Service
@Slf4j
public class WaybillDetailServiceImpl extends ServiceImpl<WaybillDetailMapper, WaybillDetail> implements
        WaybillDetailService {


    @Autowired
    private WaybillDetailMapper waybillDetailMapper;

    @Autowired
    private SysTaskMapper sysTaskMapper;

    @Autowired
    private ShopEmpMapper shopEmpMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private WaybillAsyncService waybillAsyncService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FileHandlerFactory factory;

    @Autowired
    private ThreadPoolTaskExecutor asyncExecutor;

    @Override
    public String importWaybill(MultipartFile file) {
        String taskNo = IdUtil.getSnowflakeNextIdStr();
        SysTask task = new SysTask();
        task.setTaskNo(taskNo);
        task.setTaskType(Thread.currentThread().getStackTrace()[1].getMethodName());
        task.setMessage("导入原始账单");
        task.setStatus(ImportStatus.RUNNING.getCode());
        sysTaskMapper.insert(task);

        try {
            // 🔥 关键：主线程先把文件读成字节数组（文件不会被删）
            byte[] fileBytes = file.getBytes();
            // 传 byte[]，不再传 MultipartFile
            waybillAsyncService.doImportAsync(fileBytes, taskNo);
        } catch (IOException e) {
            throw new BusinessException("读取文件失败");
        }

        return taskNo;
    }

    @Override
    public SysTask getImportTask(String taskNo) {
        QueryWrapper<SysTask> qw = new QueryWrapper<>();
        qw.eq("task_no", taskNo);
        return sysTaskMapper.selectOne(qw);
    }

    @Override
    @Transactional
    public void cleanData(String billMonth) {
        int count = waybillDetailMapper.countByBillMonth(billMonth);
        if (count == 0) {
            log.info("{} 月没有数据，无需清洗", billMonth);
            return;
        }
        // 1.更新扶持派费，上海，昆山市，太仓市 extra_fee 更新为0.1
        // TODO 扶持派费会有变动这里写死是因为暂时只为圆通写的到时候要做成可配置的并且有效时间也要加上
        waybillDetailMapper.updateExtraFee(billMonth);
        waybillDetailMapper.updateEmpType(billMonth);
//        waybillDetailMapper.updateExpressFee(date);
        // 2.混绑店铺需要更新 物料发放客户名称（send_customer_name）和客户编码（send_customer）
        // 2.1 查询账单表的 店铺-客户 数据
        // TODO 改一下逻辑，先获取店铺名称和平台名称，再获取店铺表里的相应数据，然后更新客户名称和客户编码
        List<ShopCustomerNameDTO> billShopCustomerList = waybillDetailMapper.getShopCustomer(billMonth);

        List<ShopCustomerNameDTO> shopList = shopEmpMapper.getShopCustomer();

        for (ShopCustomerNameDTO dto : billShopCustomerList) {
            ShopCustomerNameDTO shop = shopList.stream()
                    .filter(s -> s.getShopName().equals(dto.getShopName()) 
                            && s.getMaterialType().equals(dto.getMaterialType()))
                    .findFirst()
                    .orElse(null);
            if (shop != null) {
                String materialTypeWithSuffix = dto.getMaterialType();
                waybillDetailMapper.updateCustomerByShopName(dto.getShopName(), materialTypeWithSuffix, shop.getCustomerName(), shop.getCustomerCode(), billMonth);
            }
        }
        // 4.处理承包区账单 淘宝，特批，散件
        List<EmpBillInfoDTO> empInfo = shopEmpMapper.getEmpInfo();
        for (EmpBillInfoDTO dto : empInfo) {
            waybillDetailMapper.updateEmpInfo(dto.getCustomerName(), dto.getEmpName(), dto.getEmpType(), billMonth);
        }
        // 5.清洗业务员和承包区的散件数据
        waybillDetailMapper.updateDiscrete(billMonth);
    }

    @Override
    public void calculateBill(String billMonth) {
        int count = waybillDetailMapper.countByBillMonth(billMonth);
        if (count == 0) {
            log.info("{} 月没有数据，无需计算", billMonth);
            return;
        }
        // 特殊算法，梁瑞阳，陈丽芝，周清成，ceo茹彬彬，赵洋洋维护客户需要特别对待
        // 查询所有客户分类，计算时分3块 1.直营客户，2.承包区，3.业务员
        // 获取直营客户的代码和名称
        List<CustomerCodeAndNameDTO> codeAndName = waybillDetailMapper.getDirectCustomer(billMonth);
        // 1.直营客户计算账单
        executeDirectCustomerTask(codeAndName);

        // 2.承包区计算账单
        List<ContractShopExcelDTO> updateList = new ArrayList<>();
        // 承包区分为4块 散单，淘宝，限定，特批
        List<ContractShopExcelDTO> aliLoose = waybillDetailMapper.getEmpAliLoose(billMonth);
        List<ContractShopExcelDTO> afterAliLoose = employeeService.aliAndLoose(aliLoose, "yto_576017", false);
        List<ContractShopExcelDTO> limit = waybillDetailMapper.getEmpLimit(billMonth);
        List<ContractShopExcelDTO> afterLimit = employeeService.aliAndLoose(limit, "yto_576017_limit", true);
        updateList.addAll(afterAliLoose);
        updateList.addAll(afterLimit);
        // 特批
        List<ContractShopExcelDTO> special = waybillDetailMapper.getSpecialEmpBill(billMonth);
        List<ContractShopExcelDTO> afterSpecial = employeeService.dealSpecial(special);
        updateList.addAll(afterSpecial);
        // 3.业务员账单计算
        List<ContractShopExcelDTO> companyEmpList = waybillDetailMapper.getCompanyLoose(billMonth);
        List<ContractShopExcelDTO> dealList = factory.getCustomerHandler("业务员").handle(companyEmpList, "yto_576017");
        updateList.addAll(dealList);
        if (CollectionUtils.isNotEmpty(updateList)) {
            // 每 2 万条分一片（HuTool 分片）
            int batchSize = 20000;
            List<List<ContractShopExcelDTO>> partitionList = ListUtil.split(updateList, batchSize);

            log.info("批量更新运单费用，总条数：{}，分批数：{}", updateList.size(), partitionList.size());

            // 循环每一批执行更新
            for (List<ContractShopExcelDTO> batchList : partitionList) {
                updateWayBillIdAndFee(batchList);
            }

            log.info("批量更新运单费用全部完成");
        }
    }

    @Override
    public String importWaybillDiff(MultipartFile file) {
        String taskNo = IdUtil.getSnowflakeNextIdStr();
        SysTask task = new SysTask();
        task.setTaskNo(taskNo);
        task.setTaskType(Thread.currentThread().getStackTrace()[1].getMethodName());
        task.setMessage("导入差异重量");
        task.setStatus(ImportStatus.RUNNING.getCode());
        sysTaskMapper.insert(task);

        try {
            // 🔥 关键：主线程先把文件读成字节数组（文件不会被删）
            byte[] fileBytes = file.getBytes();
            // 传 byte[]，不再传 MultipartFile
            waybillAsyncService.doImportDiffAsync(fileBytes, taskNo);
        } catch (IOException e) {
            throw new BusinessException("读取文件失败");
        }

        return taskNo;
    }

    private void updateWayBillIdAndFee(List<ContractShopExcelDTO> dealList) {
        if (CollectionUtils.isEmpty(dealList)) {
            log.info("更新运单费用列表为空，跳过");
            return;
        }

        int batchSize = 100;
        List<List<ContractShopExcelDTO>> partitions = ListUtil.split(dealList, batchSize);

        log.info("开始更新运单费用，总条数：{}，分 {} 批执行（每批 {} 条）",
                dealList.size(), partitions.size(), batchSize);

        for (int i = 0; i < partitions.size(); i++) {
            List<ContractShopExcelDTO> batch = partitions.get(i);

            List<BillIdAndFeeDTO> idAndFeeList = batch.stream().map(e -> {
                BillIdAndFeeDTO idAndFee = new BillIdAndFeeDTO();
                idAndFee.setBillId(e.getId());
                idAndFee.setFee(e.getExpense());
                return idAndFee;
            }).collect(Collectors.toList());

            waybillDetailMapper.updateFeeBatch(idAndFeeList);
            log.info("第 {}/{} 批更新完成（{} 条）", i + 1, partitions.size(), batch.size());
        }

        log.info("更新运单费用完成，总计更新 {} 条", dealList.size());
    }

    private void executeDirectCustomerTask(List<CustomerCodeAndNameDTO> codeAndName) {
        if (codeAndName == null || codeAndName.isEmpty()) {
            log.info("【直营客户账单】暂无数据需要处理");
            return;
        }

        int batchSize = 5;
        List<List<CustomerCodeAndNameDTO>> splitList = ListUtil.split(codeAndName, batchSize);

        CountDownLatch latch = new CountDownLatch(splitList.size());

        log.info("【直营客户账单】开始异步处理，总数据量：{}，分成 {} 个批次执行（每批 {} 个客户）",
                codeAndName.size(), splitList.size(), batchSize);

        for (List<CustomerCodeAndNameDTO> subList : splitList) {
            asyncExecutor.execute(() -> {
                try {
                    for (CustomerCodeAndNameDTO dto : subList) {
                        calculateDirectBill(dto);
                    }
                } catch (Exception e) {
                    log.error("【直营客户账单】处理批次数据失败", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            log.info("【直营客户账单】正在等待所有任务完成...");
            latch.await();
            log.info("【直营客户账单】所有任务已完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("【直营客户账单】等待任务完成时被中断", e);
        }
    }

    // 计算账单
    private void calculateDirectBill(CustomerCodeAndNameDTO dto) {
        // 如果是陈丽芝，梁瑞阳，ceo南山趣多多，周清成，赵洋洋维护客户 特别处理
        log.info("开始处理{}数据", dto.getCustomerName());
        ExcelFileHandler handler = factory.getCustomerHandler(dto.getCustomerName());
        QueryWrapper<WaybillDetail> qw = new QueryWrapper<>();
        qw.eq("send_customer", dto.getCustomerCode());
        List<WaybillDetail> waybillDetailList = waybillDetailMapper.selectList(qw);
        List<ContractShopExcelDTO> list = waybillDetailList.stream().map(detail -> {
            ContractShopExcelDTO bill = new ContractShopExcelDTO();

            // 1. 相同名字段自动拷贝（weight, province, destination 等）
            BeanUtils.copyProperties(detail, bill);

            // 2. 字段名不一样 → 精准手动映射
            bill.setId(detail.getWaybillNo());                  // 运单编号
            bill.setScanDate(detail.getScanTime());             // 扫描时间（直接LocalDate赋值）
            bill.setEmployeeName(detail.getSalesmanName());     // 物料业务员
            bill.setCode(detail.getSendCustomer());             // 物料发放客户
            bill.setName(detail.getSendCustomerName());         // 物料发放客户名称
            bill.setShopId(detail.getSettleCode());             // 物料结算编码
            bill.setShopName(detail.getSettleName());           // 物料结算名称
            bill.setShopType(detail.getMaterialType());         // 物料类型
            bill.setOfficeExtra(detail.getExtraFee());          // 加收
            bill.setExpense(detail.getExpressFee());            // 快递费

            // 3. 默认值
            bill.setOverFlag(false);
            bill.setProcessed(false);

            return bill;
        }).collect(Collectors.toList());
        List<ContractShopExcelDTO> dealList = handler.handle(list, "yto_576017");
        updateWayBillIdAndFee(dealList);
    }

}
