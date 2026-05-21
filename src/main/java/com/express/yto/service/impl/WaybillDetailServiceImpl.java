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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
    public void cleanData(LocalDate date) {
        // 1.更新扶持派费，上海，昆山市，太仓市 extra_fee 更新为0.1
        // TODO 扶持派费会有变动这里写死是因为暂时只为圆通写的到时候要做成可配置的并且有效时间也要加上
        waybillDetailMapper.updateExtraFee(date);
        // 2.混绑店铺需要更新 物料发放客户名称（send_customer_name）和客户编码（send_customer）
        // 2.1 查询账单表的 店铺-客户 数据
        List<ShopCustomerNameDTO> billShopCustomerList = waybillDetailMapper.getShopCustomer();
        Map<String, String> billShopCustomerMap = billShopCustomerList.stream()
                .collect(Collectors.toMap(ShopCustomerNameDTO::getShopName,    // key：店铺名
                        ShopCustomerNameDTO::getCustomerName,// value：客户名
                        (oldValue, newValue) -> oldValue     //  key 重复时，保留旧值（防止报错）
                ));
        // 2.2 拿店铺名称去店铺表查询客户数据，如果不一致就标记然后更新
        List<ShopCustomerNameDTO> shopList = shopEmpMapper.getShopCustomer();
        Map<String, String> shopCustomerMap = shopList.stream()
                .collect(Collectors.toMap(ShopCustomerNameDTO::getShopName,    // key：店铺名
                        ShopCustomerNameDTO::getCustomerName,// value：客户名
                        (oldValue, newValue) -> oldValue     //  key 重复时，保留旧值（防止报错）
                ));
        // 存放【需要更新】的店铺 + 正确客户
        List<ShopCustomerNameDTO> needUpdateList = new ArrayList<>();

        // 遍历账单里的店铺关系
        for (Map.Entry<String, String> entry : billShopCustomerMap.entrySet()) {
            String shopName = entry.getKey();
            String billCustomer = entry.getValue();

            // 拿到正确的客户名
            String realCustomer = shopCustomerMap.get(shopName);

            // 1. 店铺表中不存在 → 跳过
            if (realCustomer == null) {
                continue;
            }

            // 2. 客户名不一致 → 需要更新
            if (!Objects.equals(billCustomer, realCustomer)) {
                ShopCustomerNameDTO dto = new ShopCustomerNameDTO();
                dto.setShopName(shopName);
                dto.setCustomerName(realCustomer); // 放【正确客户】

                needUpdateList.add(dto);
            }
        }
        // 最终 needUpdateList 就是你要批量更新的数据
        for (ShopCustomerNameDTO dto : needUpdateList) {
            waybillDetailMapper.updateCustomerByShopName(dto.getShopName(), dto.getCustomerName());
        }
        // 3.清洗客户编码send_customer
        List<CustomerCodeAndNameDTO> billCodeNameList = waybillDetailMapper.getCustomerNameAndCode();
        Map<String, String> billCustomerMap = billCodeNameList.stream()
                .collect(Collectors.toMap(CustomerCodeAndNameDTO::getCustomerName,    // key：店铺名
                        CustomerCodeAndNameDTO::getCustomerCode,// value：客户名
                        (oldValue, newValue) -> oldValue     //  key 重复时，保留旧值（防止报错）
                ));
        // 获取最新的账单的客户名称和客户编码
        List<CustomerCodeAndNameDTO> customerCodeName = customerMapper.getCustomerNameAndCode();
        Map<String, String> customerMap = customerCodeName.stream()
                .collect(Collectors.toMap(CustomerCodeAndNameDTO::getCustomerName,    // key：店铺名
                        CustomerCodeAndNameDTO::getCustomerCode,// value：客户名
                        (oldValue, newValue) -> oldValue     //  key 重复时，保留旧值（防止报错）
                ));

        List<CustomerCodeAndNameDTO> needUpdateCodeList = new ArrayList<>();
        for (Map.Entry<String, String> entry : billCustomerMap.entrySet()) {
            String customerName = entry.getKey();
            String billCustomerCode = entry.getValue();

            String realCode = customerMap.get(customerName);

            if (realCode == null) {
                continue;
            }

            // 2. 客户编码不一致 → 需要更新
            if (!Objects.equals(billCustomerCode, realCode)) {
                CustomerCodeAndNameDTO dto = new CustomerCodeAndNameDTO();
                dto.setCustomerName(customerName);
                dto.setCustomerCode(realCode); // 放【正确客户编码】

                needUpdateCodeList.add(dto);
            }
        }
        for (CustomerCodeAndNameDTO dto : needUpdateCodeList) {
            waybillDetailMapper.updateCode(dto.getCustomerName(), dto.getCustomerCode());
        }
        // 4.处理承包区账单 淘宝，特批，散件
        List<EmpBillInfoDTO> empInfo = shopEmpMapper.getEmpInfo();
        for (EmpBillInfoDTO dto : empInfo) {
            waybillDetailMapper.updateEmpInfo(dto.getCustomerName(), dto.getEmpName(), dto.getEmpType());
        }
        // 5.清洗业务员和承包区的散件数据
        waybillDetailMapper.updateDiscrete();
    }

    @Override
    public void calculateBill(LocalDate date) {
        // TODO 特殊算法，梁瑞阳，陈丽芝需要特别对待
        // 查询所有客户分类，计算时分3块 1.直营客户，2.承包区，3.业务员
        // 获取直营客户的代码和名称
        List<CustomerCodeAndNameDTO> codeAndName = waybillDetailMapper.getDirectCustomer();
        // 1.直营客户计算账单
        executeDirectCustomerTask(codeAndName);

        // 2.承包区计算账单
        List<ContractShopExcelDTO> updateList = new ArrayList<>();
        // TODO 承包区分为4块 散单，淘宝，限定，特批
        List<ContractShopExcelDTO> aliLoose = waybillDetailMapper.getEmpAliLoose();
        List<ContractShopExcelDTO> afterAliLoose = employeeService.aliAndLoose(aliLoose,"yto_576017", false);
        List<ContractShopExcelDTO> limit = waybillDetailMapper.getEmpLimit();
        List<ContractShopExcelDTO> afterLimit = employeeService.aliAndLoose(limit,"yto_576017", true);
        updateList.addAll(afterAliLoose);
        updateList.addAll(afterLimit);
        // TODO 特批
        List<ContractShopExcelDTO> special = waybillDetailMapper.getSpecialEmpBill();
        List<ContractShopExcelDTO> afterSpecial = employeeService.dealSpecial(special);
        // TODO 3.业务员账单计算

    }

    private void executeDirectCustomerTask(List<CustomerCodeAndNameDTO> codeAndName) {
        if (codeAndName == null || codeAndName.isEmpty()) {
            log.info("【直营客户账单】暂无数据需要处理");
            return;
        }

        // 切 15 份
        List<List<CustomerCodeAndNameDTO>> splitList = ListUtil.split(codeAndName, 15);

        //  独立线程池（用完即销）
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(15);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("direct-bill-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        log.info("【直营客户账单】开始异步处理，总数据量：{}，分成15个线程执行", codeAndName.size());

        // 提交任务
        for (List<CustomerCodeAndNameDTO> subList : splitList) {
            executor.execute(() -> {
                for (CustomerCodeAndNameDTO dto : subList) {
                    try {
                        calculateDirectBill(dto);
                    } catch (Exception e) {
                        //  优雅日志：记录哪个客户失败 + 异常信息
                        log.error("【直营客户账单】处理失败，客户编码：{}，客户名称：{}",
                                dto.getCustomerCode(), dto.getCustomerName(), e);
                    }
                }
            });
        }

        // 关闭线程池（不阻塞，不等待）
        executor.shutdown();
        log.info("【直营客户账单】所有任务已提交至线程池");
    }

    // 计算账单
    private void calculateDirectBill(CustomerCodeAndNameDTO dto) {
        // TODO 如果是陈丽芝，梁瑞阳，ceo南山趣多多，周清成，赵洋洋维护客户 特别处理
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
        List<BillIdAndFeeDTO> idAndFeeList = dealList.stream().map(e -> {
            BillIdAndFeeDTO idAndFee = new BillIdAndFeeDTO();
            idAndFee.setBillId(e.getId());
            idAndFee.setFee(e.getExpense());
            return idAndFee;
        }).collect(Collectors.toList());

        waybillDetailMapper.updateFeeBatch(idAndFeeList);
    }

}
