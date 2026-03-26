package com.express.yto.util;

import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;

/**
 * @author Detective
 * @date Created in 2026/3/26
 */
public class ExcelUtil {

    public static WriteHandler getBorderStyle() {
        return new HorizontalCellStyleStrategy(
                // 表头样式
                getBorderStyleStyle(),
                // 内容样式
                getBorderStyleStyle()
        );
    }

    private static WriteCellStyle getBorderStyleStyle() {
        WriteCellStyle style = new WriteCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
//        return (cell, head) -> {
//            // 给单元格加 上下左右 全边框
//            cell.setBorderTop(BorderStyle.THIN);      // 上边框
//            cell.setBorderBottom(BorderStyle.THIN);   // 下边框
//            cell.setBorderLeft(BorderStyle.THIN);     // 左边框
//            cell.setBorderRight(BorderStyle.THIN);    // 右边框
//        };
        return style;
    }
}
