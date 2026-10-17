package com.express.yto.listener.cashflow;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.CashFlowDetail;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信资金流水导入监听器
 * 微信账单文件前17行为无关说明内容，第18行为标题行，数据从第19行开始；
 * 文件末尾可能存在汇总行（交易时间为空），直接跳过。
 * 列映射：交易时间→operateTime，交易对方→custName，收/支→actualType，
 * 金额(元)→amount（收入为正、其余为负），备注→remark
 * @author Detective
 * @date Created in 2026/10/17
 */
@Slf4j
public class WeChatCashFlowListener extends AnalysisEventListener<Map<Integer, String>> {

    /** 表头占用行数（前17行说明 + 第18行标题），供EasyExcel.read().headRowNumber()使用 */
    public static final int HEAD_ROW_NUMBER = 18;

    /** 表头所在行（0-based）：第18行 */
    private static final int HEAD_ROW_INDEX = HEAD_ROW_NUMBER - 1;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 表头名 -> 列索引 */
    private final Map<String, Integer> headColumnMap = new HashMap<>();

    /** 解析结果 */
    private final List<CashFlowDetail> detailList = new ArrayList<>();

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        Integer rowIndex = context.readRowHolder().getRowIndex();
        if (rowIndex == null || rowIndex != HEAD_ROW_INDEX) {
            return;
        }
        headColumnMap.clear();
        for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
            ReadCellData<?> cell = entry.getValue();
            if (cell == null || cell.getStringValue() == null) {
                continue;
            }
            // 去掉BOM头和首尾空格
            String headName = cell.getStringValue().replace("\uFEFF", "").trim();
            if (!headName.isEmpty()) {
                headColumnMap.put(headName, entry.getKey());
            }
        }
    }

    @Override
    public void invoke(Map<Integer, String> row, AnalysisContext context) {
        Integer rowIndex = context.readRowHolder().getRowIndex();
        Integer timeCol = findColumn("交易时间", null);
        Integer amountCol = findColumn("金额(元)", "金额");
        // 表头不匹配直接阻断导入，避免静默导入脏数据
        if (timeCol == null || amountCol == null) {
            throw new BusinessException("微信流水文件表头不匹配，请确认第18行是否为标题行");
        }

        String tradeTime = getCell(row, timeCol);
        // 交易时间为空的行（文件尾部汇总行、空行）直接跳过
        if (tradeTime.isEmpty()) {
            return;
        }

        Integer partyCol = findColumn("交易对方", null);
        Integer typeCol = findColumn("收/支", null);
        Integer remarkCol = findColumn("备注", null);

        CashFlowDetail detail = new CashFlowDetail();
        detail.setOperateTime(parseTradeTime(tradeTime, rowIndex));
        if (partyCol != null) {
            detail.setCustName(getCell(row, partyCol));
        }
        // 收/支：只有"收入"记为收入(1)且金额为正，其余一律支出(-1)且金额为负
        String inOutType = typeCol == null ? "" : getCell(row, typeCol);
        boolean income = "收入".equals(inOutType);
        detail.setActualType(income ? 1 : -1);
        BigDecimal amount = parseAmount(getCell(row, amountCol), rowIndex).abs();
        detail.setAmount(income ? amount : amount.negate());
        if (remarkCol != null) {
            detail.setRemark(getCell(row, remarkCol));
        }
        detail.setMatchStatus(0);
        detailList.add(detail);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("微信流水Sheet读取完成，共{}条数据", detailList.size());
    }

    public List<CashFlowDetail> getDetailList() {
        return detailList;
    }

    /**
     * 先按表头精确名匹配，匹配不到再用关键字模糊匹配（兼容全角/半角括号等差异）
     */
    private Integer findColumn(String exactName, String fuzzyKey) {
        Integer col = headColumnMap.get(exactName);
        if (col != null) {
            return col;
        }
        if (fuzzyKey == null) {
            return null;
        }
        for (Map.Entry<String, Integer> entry : headColumnMap.entrySet()) {
            if (entry.getKey().contains(fuzzyKey)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 取单元格字符串并去BOM、首尾空格
     */
    private String getCell(Map<Integer, String> row, Integer colIndex) {
        String value = row.get(colIndex);
        return value == null ? "" : value.replace("\uFEFF", "").trim();
    }

    /**
     * 解析交易时间，兼容 yyyy-MM-dd HH:mm:ss 和 yyyy/MM/dd HH:mm:ss
     */
    private LocalDateTime parseTradeTime(String value, Integer rowIndex) {
        String normalized = value.replace("/", "-").trim();
        try {
            return LocalDateTime.parse(normalized, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessException("微信流水第" + (rowIndex == null ? "?" : rowIndex + 1)
                    + "行交易时间格式无法识别：" + value);
        }
    }

    /**
     * 解析金额：去掉¥、￥、逗号、空格等非数字字符
     */
    private BigDecimal parseAmount(String value, Integer rowIndex) {
        if (value.isEmpty()) {
            throw new BusinessException("微信流水第" + (rowIndex == null ? "?" : rowIndex + 1) + "行金额为空");
        }
        String cleaned = value.replaceAll("[^0-9.\\-]", "");
        if (cleaned.isEmpty() || "-".equals(cleaned) || ".".equals(cleaned)) {
            throw new BusinessException("微信流水第" + (rowIndex == null ? "?" : rowIndex + 1)
                    + "行金额格式无法识别：" + value);
        }
        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            throw new BusinessException("微信流水第" + (rowIndex == null ? "?" : rowIndex + 1)
                    + "行金额格式无法识别：" + value);
        }
    }
}
