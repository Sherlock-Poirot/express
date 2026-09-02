package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.ValidationResultDTO;
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

    void cleanData(String billMonth);

    void calculateBill(String billMonth);

    String importWaybillDiff(MultipartFile file);

    ValidationResultDTO validateData(String billMonth);

    /**
     * 运单明细归档：将 t_waybill_detail 数据迁移到 t_waybill_detail_copy 后清空原表
     * @param billMonth 账单月份（yyyy-MM），为 null 或空时归档全部
     * @return 归档条数
     */
    int archive(String billMonth);
}
