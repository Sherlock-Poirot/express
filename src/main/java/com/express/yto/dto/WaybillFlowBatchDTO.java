package com.express.yto.dto;

import lombok.Data;

import java.util.List;

/**
 * 账单工作流批次DTO
 * 列表接口返回时不含importFiles，详情接口返回时附带IMPORT步骤的文件清单
 * @author Detective
 * @date Created in 2026/9/16
 */
@Data
public class WaybillFlowBatchDTO {

    /** 账单月份 yyyy-MM */
    private String billMonth;

    /** 当前进行到的步骤编码（第一个未完成的步骤；全部完成时为最后一步） */
    private String currentStepCode;

    /** 步骤列表（按step_order升序） */
    private List<WaybillFlowStepDTO> steps;

    /** IMPORT步骤的导入文件清单（仅详情接口返回） */
    private List<WaybillFlowFileDTO> importFiles;
}
