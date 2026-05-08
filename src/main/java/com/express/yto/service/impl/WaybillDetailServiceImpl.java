package com.express.yto.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.CustomerMapper;
import com.express.yto.dao.ShopEmpMapper;
import com.express.yto.dao.SysTaskMapper;
import com.express.yto.dao.WaybillDetailMapper;
import com.express.yto.dto.CustomerCodeAndNameDTO;
import com.express.yto.dto.EmpBillInfoDTO;
import com.express.yto.dto.ShopCustomerNameDTO;
import com.express.yto.enums.ImportStatus;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.SysTask;
import com.express.yto.model.WaybillDetail;
import com.express.yto.service.WaybillAsyncService;
import com.express.yto.service.WaybillDetailService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        // 4.处理承包区账单 淘宝，特批
        List<EmpBillInfoDTO> empInfo = shopEmpMapper.getEmpInfo();
        for (EmpBillInfoDTO dto : empInfo) {
            waybillDetailMapper.updateEmpInfo(dto.getCustomerName(), dto.getEmpName(), dto.getEmpType());
        }
    }


}
