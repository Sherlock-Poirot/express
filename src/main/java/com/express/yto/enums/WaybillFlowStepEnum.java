package com.express.yto.enums;

/**
 * 账单工作流步骤枚举
 * 定义每月账单处理流程的固定步骤及顺序，备注字段用于前端展示步骤说明
 * @author Detective
 * @date Created in 2026/9/16
 */
public enum WaybillFlowStepEnum {

    IMPORT("IMPORT", "导入原始账单", 1,
            "导入原始账单Excel文件，支持分多次导入多个文件，全部导入完成后需人工确认方可进入下一步"),
    IMPORT_DIFF("IMPORT_DIFF", "导入重量差异数据", 2,
            "导入重量差异数据Excel文件，可选步骤，无差异数据时可跳过"),
    CLEAN("CLEAN", "清洗运单数据", 3,
            "清洗运单数据，规范字段格式、补全缺失信息，清洗支持重复执行"),
    VALIDATE("VALIDATE", "校验运单数据", 4,
            "校验运单数据完整性与合法性，校验通过后需人工核查确认才能进入计算"),
    CALCULATE("CALCULATE", "计算账单", 5,
            "按价格规则计算每条运单的快递费用，计算完成后需人工核查再执行归档");

    /** 步骤编码 */
    private final String code;
    /** 步骤名称 */
    private final String name;
    /** 步骤顺序 */
    private final int order;
    /** 步骤说明 */
    private final String remark;

    WaybillFlowStepEnum(String code, String name, int order, String remark) {
        this.code = code;
        this.name = name;
        this.order = order;
        this.remark = remark;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public String getRemark() {
        return remark;
    }
}
