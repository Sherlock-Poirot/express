package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.model.DailyBill;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface DailyBillService extends IService<DailyBill> {
    String uploadBill(MultipartFile file);
    
    String syncCustomerInfoFromShopEmp(LocalDate date);
    
    String calculateBill(LocalDate date);
}