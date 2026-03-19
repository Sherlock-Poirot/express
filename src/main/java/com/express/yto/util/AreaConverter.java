package com.express.yto.util;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * @author Detective
 * @date Created in 2025/10/28
 */
public class AreaConverter implements Converter<Integer> {

    // 支持的Java数据类型（实体类中字段的类型）
    @Override
    public Class<Integer> supportJavaTypeKey() {
        return Integer.class;
    }

    // Excel中显示的数据类型（这里为字符串）
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    // 核心转换逻辑：编码→文本
    @Override
    public WriteCellData<String> convertToExcelData(
            Integer code,  // 实体类中的编码值（如1）
            ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {

        // 编码与文本的映射关系（可从数据库/字典表加载）
        switch (code) {
            case 1:
                return new WriteCellData<>("一区");
            case 2:
                return new WriteCellData<>("二区");
            case 3:
                return new WriteCellData<>("三区");
            case 4:
                return new WriteCellData<>("四区");
            case 5:
                return new WriteCellData<>("五区");
            default:
                return new WriteCellData<>("未知区域"); // 默认值
        }
    }
}
