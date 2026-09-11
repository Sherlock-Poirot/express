package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.ContractShopExcelDTO;
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

    /**
     * 单条运单费用试算：按客户名称路由到对应的 ExcelFileHandler 计算费用
     * @param dto 运单信息（需包含客户名称、重量、省份、扫描时间等）
     * @return 计算后的运单信息（expense 字段已填充费用）
     */
    ContractShopExcelDTO calculateSingleBill(com.express.yto.dto.ContractShopExcelDTO dto);
}
