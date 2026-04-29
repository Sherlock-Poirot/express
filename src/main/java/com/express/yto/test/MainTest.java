package com.express.yto.test;

import static com.express.yto.listener.HeadAliasMapping.ALIAS_MAP;
import static com.express.yto.util.ExcelUtil.getBorderStyle;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.express.yto.dto.ContractShopExcelDTO;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Detective
 * @date Created in 2026/4/2
 */
public class MainTest {

    public static void main(String[] args) {
        String filepath = "/Users/detective/Desktop/zto/";
        File dir = new File(filepath);
        File[] files = dir.listFiles();
        assert files != null;
        for (File file : files) {
            if (!file.getName().contains(".xlsx")) {
                continue;
            }
            List<ContractShopExcelDTO> list = new ArrayList<>();
            EasyExcel.read(file, new AnalysisEventListener<Map<Integer, String>>() {
                Map<Integer, String> headMapping = new HashMap<>();

                @Override
                public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                    // 遍历表头
                    for (Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                        Integer index = entry.getKey();
                        String cellValue = entry.getValue().getStringValue(); // 取Excel表头名字

                        // 别名匹配
                        String fieldName = ALIAS_MAP.getOrDefault(cellValue, cellValue);
                        headMapping.put(index, fieldName);
                    }
                }

                // 读取每一行
                @Override
                public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
                    ContractShopExcelDTO dto = new ContractShopExcelDTO();

                    // 自动赋值
                    for (Entry<Integer, String> entry : headMapping.entrySet()) {
                        Integer index = entry.getKey();
                        String field = entry.getValue();
                        String value = rowData.get(index);

                        switch (field) {
                            case "id":
                                dto.setId(value);
                                break;
                            case "scanDate":
                                dto.setScanDate(parseAuto(value));
                                break;
                            case "weight":
                                dto.setWeight(new BigDecimal(value));
                                break;
                            case "province":
                                dto.setProvince(value);
                                break;
                            case "destination":
                                dto.setDestination(value);
                                break;
                            case "employeeName":
                                dto.setEmployeeName(value);
                                break;
                            case "kCode":
                                dto.setCode(value);
                                break;
                            case "kName":
                                dto.setName(value);
                                break;
                            case "shopId":
                                dto.setShopId(value);
                                break;
                            case "shopName":
                                dto.setShopName(value);
                                break;
                            case "shopType":
                                dto.setShopType(value);
                                break;
                            case "officeExtra":
                                if (StringUtils.isBlank(value)){
                                    dto.setOfficeExtra(BigDecimal.ZERO);
                                }else {
                                    dto.setOfficeExtra(new BigDecimal(value));
                                }
                                break;
                            default:
                        }
                    }
                    list.add(dto);
                }


                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {

                }
            }).doReadAll();
            System.out.println(1);
            EasyExcel.write(filepath + "/测试" + file.getName(), ContractShopExcelDTO.class).sheet()
                    .registerWriteHandler(getBorderStyle()).doWrite(list);
        }
    }

    // 支持的所有格式，你可以随便加
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "yyyyMMdd",
            "yyyy年MM月dd日",
            "yyyy年M月d日"
    );

    public static LocalDate parseAuto(String dateStr) {
        if (dateStr == null || StringUtils.isBlank(dateStr)) {
            return null;
        }

        for (String format : SUPPORTED_FORMATS) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignore) {
                // 格式不匹配，继续试下一个
            }
        }

        // 所有格式都不匹配返回 null
        return null;
    }
}
