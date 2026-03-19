package com.express.yto.enums;

import lombok.Getter;

/**
 * @author Detective
 * @date Created in 2025/9/18
 */
@Getter
public enum FourModelEnum {

    ALL("all", 1, "全量加收"),

    EXCESS("excess", 2, "超出部分");

    private final String modelName;

    private final Integer modeCode;

    private final String remark;

    FourModelEnum(String modelName, Integer modeCode, String remark) {
        this.modelName = modelName;
        this.modeCode = modeCode;
        this.remark = remark;
    }

    public static Integer getModeCodeByName(String modelName) {
        for (FourModelEnum model : values()) {
            if (model.getModelName().equals(modelName)) {
                return model.getModeCode();
            }
        }

        return 0;
    }
}
