package com.express.yto.listener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Detective
 * @date Created in 2026/4/2
 */
public class HeadAliasMapping {

    // 核心映射表：key=Excel别名，value=标准列名
    public static final Map<String, String> ALIAS_MAP = new HashMap<>();

    /**
     *
     * id : 运单号，运单号码
     * scanDate ：扫描时间
     * weight ：重量，计费重量（kg）
     * province ：目的地省，目的地，计费省份
     * destination ：计费目的地名称
     * employeeName ：物料业务员名称
     * kCode ：物料发放客户，发件公司
     * kName ：物料发放客户名称
     * shopId ：物料结算编码
     * shopName ：物料结算名称
     * shopType ：物料类型
     * officeExtra ：特殊派费，加收
     * expense ：总计运费，快递费
     */
    static {
        // 把所有别名都指向标准名称「公司名称」
        ALIAS_MAP.put("运单号", "id");
        ALIAS_MAP.put("运单号码", "id");
        ALIAS_MAP.put("运单编号", "id");

        ALIAS_MAP.put("日期", "scanDate");
        ALIAS_MAP.put("扫描时间", "scanDate");

        ALIAS_MAP.put("重量", "weight");
        ALIAS_MAP.put("计费重量（kg）", "weight");

        ALIAS_MAP.put("目的地省", "province");
        ALIAS_MAP.put("目的地", "province");
        ALIAS_MAP.put("计费省份", "province");

        ALIAS_MAP.put("目的地市", "destination");
        ALIAS_MAP.put("目的城市", "destination");
        ALIAS_MAP.put("计费目的地名称", "destination");

        ALIAS_MAP.put("物料业务员名称", "employeeName");

        ALIAS_MAP.put("发件公司", "kCode");
        ALIAS_MAP.put("物料发放客户", "kCode");

        ALIAS_MAP.put("物料发放客户名称", "kName");

        ALIAS_MAP.put("物料结算编码", "shopId");

        ALIAS_MAP.put("店铺名称", "shopName");
        ALIAS_MAP.put("物料结算名称", "shopName");

        ALIAS_MAP.put("物料类型", "shopType");

        ALIAS_MAP.put("特殊派费", "officeExtra");
        ALIAS_MAP.put("加收", "officeExtra");

//        ALIAS_MAP.put("总计运费", "expense");
//        ALIAS_MAP.put("快递费", "expense");
        // 无限扩展...
    }
}
