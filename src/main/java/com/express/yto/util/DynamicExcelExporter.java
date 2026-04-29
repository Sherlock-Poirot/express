package com.express.yto.util;

import com.alibaba.excel.EasyExcel;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Detective
 * @date Created in 2025/9/24
 */
public class DynamicExcelExporter {

    /**
     * 动态导出Excel
     *
     * @param filePath 导出文件路径
     * @param headers  动态表头（key:字段名, value:表头显示名称）
     * @param dataList 导出数据（Map的key对应headers的key，value为字段值）
     */
    public static void export(String filePath, Map<String, String> headers, List<Map<String, Object>> dataList) {
        try (OutputStream out = new FileOutputStream(filePath)) {
            // 1. 构建表头信息
            List<List<String>> head = new ArrayList<>();
            for (String headerName : headers.values()) {
                List<String> column = new ArrayList<>();
                column.add(headerName); // 表头单元格内容
                head.add(column);
            }

            // 2. 构建数据列表（按表头顺序排列）
            List<List<Object>> exportData = new ArrayList<>();
            for (Map<String, Object> data : dataList) {
                List<Object> row = new ArrayList<>();
                // 按表头字段顺序添加数据
                for (String fieldKey : headers.keySet()) {
                    row.add(data.getOrDefault(fieldKey, ""));
                }
                exportData.add(row);
            }

            // 3. 动态写入Excel
            EasyExcel.write(out)
                    .head(head) // 设置动态表头
                    .sheet("动态数据") // 工作表名称
                    .doWrite(exportData); // 写入动态数据

            System.out.println("Excel导出成功：" + filePath);
        } catch (Exception e) {
            System.err.println("Excel导出失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

}
