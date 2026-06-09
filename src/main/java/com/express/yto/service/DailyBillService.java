package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.model.DailyBill;
import org.springframework.web.multipart.MultipartFile;

public interface DailyBillService extends IService<DailyBill> {
    String uploadBill(MultipartFile file);
}