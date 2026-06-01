package com.express.yto.util;

import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * @author Detective
 * @date Created in 2026/3/26
 */
public class ExcelUtil {

    public static WriteHandler getBillStyle() {
        return new HorizontalCellStyleStrategy(
                getHeaderStyle(),
                getContentStyle()
        );
    }

    public static WriteHandler getColumnWidthStyle() {
        return new LongestMatchColumnWidthStyleStrategy();
    }

    public static WriteHandler getBorderStyle() {
        return new HorizontalCellStyleStrategy(
                getBorderStyleStyle(),
                getBorderStyleStyle()
        );
    }

    private static WriteCellStyle getHeaderStyle() {
        WriteCellStyle style = new WriteCellStyle();
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        style.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        style.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        
        style.setHorizontalAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        WriteFont font = new WriteFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setFontName("微软雅黑");
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setWriteFont(font);
        
        return style;
    }

    private static WriteCellStyle getContentStyle() {
        WriteCellStyle style = new WriteCellStyle();
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        style.setHorizontalAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        WriteFont font = new WriteFont();
        font.setFontHeightInPoints((short) 11);
        font.setFontName("微软雅黑");
        style.setWriteFont(font);
        
        return style;
    }

    private static WriteCellStyle getBorderStyleStyle() {
        WriteCellStyle style = new WriteCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}