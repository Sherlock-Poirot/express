package com.express.yto.listener.cashflow;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
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
 * 支付宝资金流水导入监听器
 * 支付宝账单文件前若干行为说明内容，后跟标题行、数据行；
 * 说明行数量可能变化，且csv存在空行时EasyExcel会压缩逻辑行号，因此不依赖固定标题行数，
 * 而是动态识别标题行（出现"交易时间"列的行），之前的行全部视为说明行跳过。
 * 交易状态不是"交易成功"的行（退款、关闭、等待付款等）不导入。
 * 列映射：交易时间→operateTime，交易对方→custName，商品说明→remark，
 * 收/支→actualType（只有"收入"为收入，其余为支出），金额→amount（收入为正、其余为负）
 * @author Detective
 * @date Created in 2026/10/20
 */
@Slf4j
public class AlipayCashFlowListener extends AnalysisEventListener<Map<Integer, String>> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 交易状态只有该值才导入 */
    private static final String SUCCESS_STATUS = "交易成功";

    /** 表头名 -> 列索引 */
    private final Map<String, Integer> headColumnMap = new HashMap<>();

    /** 标题行是否已识别 */
    private boolean headParsed = false;

    /** 解析结果 */
    private final List<CashFlowDetail> detailList = new ArrayList<>();

    @Override
    public void invoke(Map<Integer, String> row, AnalysisContext context) {
        // 标题行识别前都是说明行，逐行尝试识别标题行
        if (!headParsed) {
            parseHeadRow(row);
            return;
        }

        Integer rowIndex = context.readRowHolder().getRowIndex();
        String tradeTime = getCell(row, headColumnMap.get("交易时间"));
        // 交易时间为空的行（文件尾部汇总行、空行）直接跳过
        if (tradeTime.isEmpty()) {
            return;
        }
        // 交易状态不是"交易成功"的不导入
        if (!SUCCESS_STATUS.equals(getCell(row, headColumnMap.get("交易状态")))) {
            return;
        }

        Integer partyCol = headColumnMap.get("交易对方");
        Integer remarkCol = headColumnMap.get("商品说明");

        CashFlowDetail detail = new CashFlowDetail();
        detail.setOperateTime(parseTradeTime(tradeTime, rowIndex));
        if (partyCol != null) {
            detail.setCustName(getCell(row, partyCol));
        }
        // 收/支：只有"收入"记为收入(1)且金额为正，其余一律支出(-1)且金额为负
        boolean income = "收入".equals(getCell(row, headColumnMap.get("收/支")));
        detail.setActualType(income ? 1 : -1);
        BigDecimal amount = parseAmount(getCell(row, headColumnMap.get("金额")), rowIndex).abs();
        detail.setAmount(income ? amount : amount.negate());
        if (remarkCol != null) {
            detail.setRemark(getCell(row, remarkCol));
        }
        detail.setMatchStatus(0);
        detailList.add(detail);
    }

    /**
     * 尝试将当前行识别为标题行：出现"交易时间"列才认定，避免说明行误判；
     * 识别成功后校验关键列并记录列映射，识别不到静默跳过（说明行）
     */
    private void parseHeadRow(Map<Integer, String> row) {
        if (findCellIndex(row, "交易时间") == null) {
            return;
        }
        headColumnMap.clear();
        for (Map.Entry<Integer, String> entry : row.entrySet()) {
            String headName = entry.getValue() == null ? "" : entry.getValue().replace("\uFEFF", "").trim();
            if (!headName.isEmpty()) {
                headColumnMap.put(headName, entry.getKey());
            }
        }
        // 金额/收支/交易状态是必须的列，缺失说明不是标准支付宝交易明细文件
        Integer amountCol = findColumn("金额", "金额");
        if (amountCol == null || headColumnMap.get("收/支") == null || headColumnMap.get("交易状态") == null) {
            throw new BusinessException("支付宝流水文件表头不匹配，请确认上传的是支付宝交易明细导出文件");
        }
        // 金额列实际列名可能是"金额（元）"等变体，按实际识别结果回写，供数据行取列使用
        headColumnMap.put("金额", amountCol);
        headParsed = true;
        log.info("支付宝流水标题行识别成功，列映射：{}", headColumnMap);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("支付宝流水Sheet读取完成，共{}条数据", detailList.size());
    }

    public List<CashFlowDetail> getDetailList() {
        return detailList;
    }

    /**
     * 在单行中查找单元格文本精确等于指定值的列索引（识别标题行用）
     */
    private Integer findCellIndex(Map<Integer, String> row, String exactText) {
        for (Map.Entry<Integer, String> entry : row.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue().replace("\uFEFF", "").trim();
            if (exactText.equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 先按表头精确名匹配，匹配不到再用关键字模糊匹配（兼容"金额（元）"/"金额(元)"等差异）
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
            throw new BusinessException("支付宝流水第" + (rowIndex == null ? "?" : rowIndex + 1)
                    + "行交易时间格式无法识别：" + value);
        }
    }

    /**
     * 解析金额：去掉¥、￥、逗号、空格等非数字字符
     */
    private BigDecimal parseAmount(String value, Integer rowIndex) {
        if (value.isEmpty()) {
            throw new BusinessException("支付宝流水第" + (rowIndex == null ? "?" : rowIndex + 1) + "行金额为空");
        }
        String cleaned = value.replaceAll("[^0-9.\\-]", "");
        if (cleaned.isEmpty() || "-".equals(cleaned) || ".".equals(cleaned)) {
            throw new BusinessException("支付宝流水第" + (rowIndex == null ? "?" : rowIndex + 1)
                    + "行金额格式无法识别：" + value);
        }
        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            throw new BusinessException("支付宝流水第" + (rowIndex == null ? "?" : rowIndex + 1)
                    + "行金额格式无法识别：" + value);
        }
    }
}
