package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.model.SysTask;
import com.express.yto.model.WaybillDetail;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Detective
 * @date Created in 2026/5/6
 */
public interface WaybillDetailService extends IService<WaybillDetail> {


    String importWaybill(MultipartFile file);

    SysTask getImportTask(String taskNo);

    void cleanData(LocalDate date);

    void calculateBill(LocalDate date);
}
