package com.express.yto.enums;

/**
 * @author Detective
 * @date Created in 2026/5/21
 */
public enum PriceModeEnum {

    NORMAL(1, "正常模式"),

    MODE_2(2, "3kg以上面单4元+实重*单价"),

    MODE_3(3, "3kg以上面单4元+实重*单价,四区价格算法 实重*单价+4（面单费），五区算法：实重*单价+4（面单费）"),

    MODE_4(4, "超重算法参考MODE_3，且4区不足5元算5元，5区不足6元算6元");

    private final Integer type;
    private final String remark;

    PriceModeEnum(Integer type, String remark) {
        this.type = type;
        this.remark = remark;
    }

    public Integer getType() {
        return type;
    }

    public String getRemark() {
        return remark;
    }
}
