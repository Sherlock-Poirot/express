package com.express.yto.service.impl;

import static com.express.yto.util.AreaUtil.AREA_DICT;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.AreaMapper;
import com.express.yto.dao.CustomerMapper;
import com.express.yto.dao.ExtraFeeMapper;
import com.express.yto.dao.FixedFeeMapper;
import com.express.yto.dao.OverFeeMapper;
import com.express.yto.dao.PrepaymentMapper;
import com.express.yto.dao.ShopEmpMapper;
import com.express.yto.dto.CustomerCodeAndNameDTO;
import com.express.yto.dto.CustomerDetailDTO;
import com.express.yto.dto.CustomerExcelDTO;
import com.express.yto.dto.CustomerPriceDetailDTO;
import com.express.yto.dto.CustomerPriceInput;
import com.express.yto.dto.CustomerSearchInput;
import com.express.yto.dto.ExtraFeeDTO;
import com.express.yto.dto.FixedTinyDTO;
import com.express.yto.dto.PriceDeleteInput;
import com.express.yto.dto.PriceDetailDTO;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.Area;
import com.express.yto.model.Customer;
import com.express.yto.model.ExtraFee;
import com.express.yto.model.FixedFee;
import com.express.yto.model.OverFee;
import com.express.yto.model.Prepayment;
import com.express.yto.model.ShopEmp;
import com.express.yto.service.CustomerService;
import java.math.BigDecimal;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Detective
 * @date Created in 2025/9/10
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private FixedFeeMapper fixedFeeMapper;

    @Autowired
    private OverFeeMapper overFeeMapper;

    @Autowired
    private PrepaymentMapper prepaymentMapper;

    @Autowired
    private ExtraFeeMapper extraFeeMapper;

    @Autowired
    private ShopEmpMapper shopEmpMapper;

    @Autowired
    private AreaMapper areaMapper;

    private static final LocalDate END_TIME = LocalDate.of(2999, 12, 31);

    @Override
    public void importByExcel(String filePath) {
        List<CustomerExcelDTO> list = new ArrayList<>();
        EasyExcel.read(filePath, CustomerExcelDTO.class, new ReadListener<CustomerExcelDTO>() {
            @Override
            public void invoke(CustomerExcelDTO dto, AnalysisContext analysisContext) {
                list.add(dto);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        }).doReadAll();
        List<Customer> customers = new ArrayList<>();
        for (CustomerExcelDTO dto : list) {
            Customer model = Customer.builder().custName(dto.getName()).code(dto.getCode())
                    .threeFlag(dto.getFlagHaiNan()).fourRate(dto.getFourRate()).fourModel(dto.getFourModel())
                    .fourFee(dto.getFourFee()).build();
            customers.add(model);
        }
        if (CollectionUtils.isNotEmpty(customers)) {
            customerMapper.insert(customers);
        }
        // TODO 预付 和 加收
    }

    @Override
    public void add(CustomerDetailDTO input) {
        QueryWrapper<Customer> qw = new QueryWrapper<>();
        qw.eq("cust_name", input.getCustName());
        if (StringUtils.isNotBlank(input.getCode())) {
            qw.or().eq("code", input.getCode());
        } else {
            throw new BusinessException("客户编码不能为空");
        }
        List<Customer> list = customerMapper.selectList(qw);
        if (list.size() > 0) {
            throw new BusinessException("该数据已存在，不可重复添加");
        }
        Customer customer = new Customer();
        BeanUtils.copyProperties(input, customer);
        customerMapper.insert(customer);
        List<ExtraFee> extraFees = new ArrayList<>();
        for (ExtraFeeDTO extra : input.getExtra()) {
            extraFees.add(ExtraFee.builder().code(input.getCode()).areaName(extra.getAreaName()).fee(extra.getFee())
                    .build());
        }
        extraFeeMapper.insert(extraFees);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        // 获取店铺表，价格表，全都进行删除
        List<Customer> customers = customerMapper.selectBatchIds(ids);
        List<String> codeList = customers.stream().map(Customer::getCode).collect(Collectors.toList());
        // 删除固定重量价格表
        QueryWrapper<FixedFee> fixedQw = new QueryWrapper<>();
        fixedQw.in("code", codeList);
        fixedFeeMapper.delete(fixedQw);
        // 删除续重费用表
        QueryWrapper<OverFee> overQw = new QueryWrapper<>();
        overQw.in("code", codeList);
        overFeeMapper.delete(overQw);
        // 删除预付款表
        QueryWrapper<Prepayment> preQw = new QueryWrapper<>();
        preQw.in("code", codeList);
        prepaymentMapper.delete(preQw);
        // 删除店铺表
        QueryWrapper<ShopEmp> shopQw = new QueryWrapper<>();
        shopQw.in("code", codeList);
        shopEmpMapper.delete(shopQw);
        customerMapper.deleteByIds(ids);
    }

    @Override
    public IPage<Customer> search(CustomerSearchInput input) {
        Page<Customer> page = new Page<>(input.getPageNo(), input.getPageSize());
        QueryWrapper<Customer> qw = new QueryWrapper<>();
        if (StringUtils.isNotBlank(input.getName())) {
            qw.like("cust_name", input.getName());
        }
        if (StringUtils.isNotBlank(input.getCode())) {
            qw.eq("code", input.getCode());
        }
        // 排序规则：1. start_time 非NULL排前面(IS NULL=0在前,=1在后) 2. start_time倒序(近的在前) 3. id倒序兜底
        qw.last("ORDER BY start_time IS NULL, start_time DESC, id DESC");

        return customerMapper.selectPage(page, qw);
    }

    @Override
    public List<CustomerPriceDetailDTO> getPrice(String kCode) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("code", kCode);
        List<FixedFee> fixedList = fixedFeeMapper.selectByMap(map);
        List<OverFee> overList = overFeeMapper.selectByMap(map);
        List<Prepayment> prepaymentList = prepaymentMapper.selectByMap(map);
        List<ExtraFee> extraFeeList = extraFeeMapper.selectByMap(map);
        QueryWrapper<Customer> qw = new QueryWrapper<>();
        qw.eq("code", kCode);
        Customer customer = customerMapper.selectOne(qw);

        List<CustomerPriceDetailDTO> result = combine(fixedList, overList, prepaymentList);
        result.sort(Comparator.comparing(CustomerPriceDetailDTO::getEndTime).reversed());
        result.forEach(e -> {
            e.setRemark(buildRemark(customer, extraFeeList));
            e.setName(customer.getCustName());
            e.setCode(customer.getCode());
        });
        QueryWrapper<Area> areaQw = new QueryWrapper<>();
        areaQw.eq("company_id", "yto_576017");
        List<Area> areaList = areaMapper.selectList(areaQw);
        for (Area area : areaList) {
            if (customer.getThreeFlag()) {
                if (area.getAreaNum().equals(3)) {
                    area.setAreaCity(area.getAreaCity() + "、海南省");
                }
                if (area.getAreaNum().equals(4)) {
                    area.setAreaCity(area.getAreaCity().replaceAll("、海南省", ""));
                }
            }
        }
        result.forEach(e -> e.setAreas(areaList));
        return result;
    }

    @Override
    public void deletePrice(PriceDeleteInput input) {
        Map<String, Object> queryMap = new HashMap<>(4);
        queryMap.put("code", input.getCode());
        queryMap.put("start_time", input.getStartTime());
        queryMap.put("end_time", input.getEndTime());
        List<FixedFee> fixedList = fixedFeeMapper.selectByMap(queryMap);
        List<OverFee> overList = overFeeMapper.selectByMap(queryMap);
        List<Prepayment> prepaymentList = prepaymentMapper.selectByMap(queryMap);
        queryMap.clear();
        queryMap.put("code", input.getCode());
        List<ExtraFee> extraFeeList = extraFeeMapper.selectByMap(queryMap);
        if (deletePriceJudge(fixedList, overList, prepaymentList, extraFeeList)) {
            throw new BusinessException("该时间段没有价格表，请检查时间区间的正确性");
        }
        // 只有列表非空时才执行删除，避免生成 IN () 非法SQL
        if (CollUtil.isNotEmpty(fixedList)) {
            List<Long> fixedIds = fixedList.stream().map(FixedFee::getId).collect(Collectors.toList());
            fixedFeeMapper.deleteByIds(fixedIds);
        }
        if (CollUtil.isNotEmpty(overList)) {
            List<Long> overIds = overList.stream().map(OverFee::getId).collect(Collectors.toList());
            overFeeMapper.deleteByIds(overIds);
        }
        if (CollUtil.isNotEmpty(prepaymentList)) {
            List<Long> prepaymentIds = prepaymentList.stream().map(Prepayment::getId).collect(Collectors.toList());
            prepaymentMapper.deleteByIds(prepaymentIds);
        }
    }

    @Transactional
    @Override
    public void addPrice(List<CustomerPriceInput> input) {
        // 先把所有的历史的价格表的结束时间改成参数的开始时间
        String code = input.get(0).getCode();
        LocalDate endTime = input.get(0).getStartTime();
        LocalDate startTime = input.get(0).getStartTime();
        prepaymentMapper.updateEndTime(code, endTime);
        fixedFeeMapper.updateEndTime(code, endTime);
        overFeeMapper.updateEndTime(code, endTime);

        // 更新客户表最近价格开始时间（冗余字段，用于列表展示）
        QueryWrapper<Customer> custQw = new QueryWrapper<>();
        custQw.eq("code", code);
        customerMapper.update(Customer.builder().startTime(startTime).build(), custQw);

        // 客户预付款
        Prepayment prepayment = new Prepayment();
        prepayment.setEndTime(END_TIME);
        prepayment.setCode(code);
        prepayment.setStartTime(startTime);
        prepayment.setPreFee(input.get(0).getPrepayment());
        prepaymentMapper.insert(prepayment);

        List<OverFee> overFeeList = new ArrayList<>(5);
        List<FixedFee> fixedFeeList = new ArrayList<>();
        for (CustomerPriceInput dto : input) {
            OverFee overFee = new OverFee();
            overFee.setEndTime(END_TIME);
            overFee.setFirstWeight(BigDecimal.ONE);
            overFee.setStartTime(startTime);
            overFee.setCode(code);
            overFee.setArea(dto.getArea());
            overFee.setFee(dto.getOverFee());
            overFee.setFirstFee(dto.getFirstFee());
            overFeeList.add(overFee);
            for (FixedTinyDTO fixed : dto.getFixedList()) {
                FixedFee fixedFee = new FixedFee();
                fixedFee.setEndTime(END_TIME);
                fixedFee.setCode(code);
                fixedFee.setArea(dto.getArea());
                fixedFee.setStartTime(startTime);
                fixedFee.setWeight(fixed.getWeight());
                fixedFee.setFee(fixed.getFee());
                fixedFeeList.add(fixedFee);
            }
        }
        if (CollectionUtils.isNotEmpty(overFeeList)) {
            overFeeMapper.insert(overFeeList);
        }
        if (CollectionUtils.isNotEmpty(fixedFeeList)) {
            fixedFeeMapper.insert(fixedFeeList);
        }
    }

    @Override
    public CustomerDetailDTO getDetail(String code) {
        QueryWrapper<Customer> qw = new QueryWrapper<>();
        qw.eq("code", code);
        Customer customer = customerMapper.selectOne(qw);
        QueryWrapper<ExtraFee> extraQw = new QueryWrapper<>();
        extraQw.eq("code", code);
        List<ExtraFee> list = extraFeeMapper.selectList(extraQw);
        CustomerDetailDTO result = new CustomerDetailDTO();
        BeanUtils.copyProperties(customer, result);
        List<ExtraFeeDTO> extraFeeList = new ArrayList<>(list.size());
        for (ExtraFee extraFee : list) {
            extraFeeList.add(ExtraFeeDTO.builder().areaName(extraFee.getAreaName()).fee(extraFee.getFee()).build());
        }
        result.setExtra(extraFeeList);
        return result;
    }

    @Transactional
    @Override
    public void updateCustomer(CustomerDetailDTO input) {
        Customer oldCustomer = customerMapper.selectById(input.getId());
        String oldCode = oldCustomer != null ? oldCustomer.getCode() : null;
        String newCode = input.getCode();

        Customer customer = new Customer();
        BeanUtils.copyProperties(input, customer);
        customerMapper.updateById(customer);

        if (StringUtils.isNotBlank(oldCode) && !oldCode.equals(newCode)) {
            QueryWrapper<FixedFee> fixedQw = new QueryWrapper<>();
            fixedQw.eq("code", oldCode);
            fixedFeeMapper.update(FixedFee.builder().code(newCode).build(), fixedQw);

            QueryWrapper<OverFee> overQw = new QueryWrapper<>();
            overQw.eq("code", oldCode);
            overFeeMapper.update(OverFee.builder().code(newCode).build(), overQw);

            QueryWrapper<Prepayment> prepaymentQw = new QueryWrapper<>();
            prepaymentQw.eq("code", oldCode);
            prepaymentMapper.update(Prepayment.builder().code(newCode).build(), prepaymentQw);

            QueryWrapper<ExtraFee> extraQw = new QueryWrapper<>();
            extraQw.eq("code", oldCode);
            extraFeeMapper.update(ExtraFee.builder().code(newCode).build(), extraQw);
        }

        QueryWrapper<ExtraFee> qw = new QueryWrapper<>();
        qw.eq("code", newCode);
        extraFeeMapper.delete(qw);
        List<ExtraFee> list = new ArrayList<>();
        for (ExtraFeeDTO extra : input.getExtra()) {
            list.add(ExtraFee.builder().code(newCode).areaName(extra.getAreaName()).fee(extra.getFee()).build());
        }
        extraFeeMapper.insert(list);
    }

    @Override
    public List<CustomerCodeAndNameDTO> fuzzyMatch(String code, String name) {
        QueryWrapper<Customer> qw = new QueryWrapper<>();
        if (StringUtils.isNotBlank(code)) {
            qw.like("code", code);
        }
        if (StringUtils.isNotBlank(name)) {
            qw.like("cust_name", name);
        }
        List<Customer> list = customerMapper.selectList(qw);
        return list.stream().map(e -> CustomerCodeAndNameDTO.builder().customerCode(e.getCode())
                .customerName(e.getCustName()).build()).collect(Collectors.toList());
    }

    private Boolean deletePriceJudge(List<FixedFee> fixedList, List<OverFee> overList, List<Prepayment> prepaymentList,
            List<ExtraFee> extraFeeList) {
        return CollectionUtils.isEmpty(fixedList) && CollectionUtils.isEmpty(overList) && CollectionUtils
                .isEmpty(prepaymentList) && CollectionUtils.isEmpty(extraFeeList);
    }

    /**
     * 构建价格备注信息
     */
    private String buildRemark(Customer customer, List<ExtraFee> extraFeeList) {
        // 空客户直接返回默认备注
        if (customer == null) {
            return "其余加收按总部通知为准";
        }

        StringBuilder sb = new StringBuilder();

        // 1. 四区发货规则
        sb.append("四区发货占比").append(customer.getFourRate()).append("%以上,");
        sb.append("excess".equals(customer.getFourModel()) ? "超出部分" : "全部");
        sb.append("四区加收").append(customer.getFourFee()).append(" 元/票;\n");

        // 2. 附加费
        if (CollUtil.isNotEmpty(extraFeeList)) {
            extraFeeList.forEach(fee -> {
                sb.append(fee.getAreaName())
                        .append("地区加收 ")
                        .append(fee.getFee())
                        .append(" 元/票\n");
            });
        }

        // 3. 尾部固定备注
        sb.append("其余加收按总部通知为准");

        return sb.toString();
    }

    private List<CustomerPriceDetailDTO> combine(List<FixedFee> fixedList, List<OverFee> overList,
            List<Prepayment> prepaymentList) {

        // ====================== 1. 构建 fixedMap（优化版） ======================
        Map<String, List<FixedTinyDTO>> fixedMap = new HashMap<>();
        for (FixedFee fee : fixedList) {
            String key = StringUtils.joinWith("&", fee.getStartTime(), fee.getEndTime(), fee.getArea());
            FixedTinyDTO tinyDTO = FixedTinyDTO.builder()
                    .weight(fee.getWeight())
                    .fee(fee.getFee())
                    .build();

            // 一行代替 if/else + put，这才是 Map 正确用法！
            fixedMap.computeIfAbsent(key, k -> new ArrayList<>()).add(tinyDTO);
        }

        // ====================== 2. 构建 detailMap（优化版） ======================
        Map<String, List<PriceDetailDTO>> detailMap = new HashMap<>();
        for (OverFee overFee : overList) {
            String timeKey = StringUtils.joinWith("&", overFee.getStartTime(), overFee.getEndTime());
            String fixKey = StringUtils.joinWith("&", overFee.getStartTime(), overFee.getEndTime(), overFee.getArea());

            PriceDetailDTO priceDetail = PriceDetailDTO.builder()
                    .startTime(overFee.getStartTime())
                    .endTime(overFee.getEndTime())
                    .area(AREA_DICT.get(overFee.getArea()))
                    .firstFee(overFee.getFirstFee())
                    .overFee(overFee.getFee())
                    .fixedFee(fixedMap.get(fixKey))
                    .build();

            // 同样一行搞定
            detailMap.computeIfAbsent(timeKey, k -> new ArrayList<>()).add(priceDetail);
        }

        // ====================== 3. 组装最终结果 ======================
        List<CustomerPriceDetailDTO> result = new ArrayList<>();
        for (Prepayment prepayment : prepaymentList) {
            String key = StringUtils.joinWith("&", prepayment.getStartTime(), prepayment.getEndTime());

            CustomerPriceDetailDTO dto = new CustomerPriceDetailDTO();
            dto.setStartTime(prepayment.getStartTime().toString());
            dto.setEndTime(prepayment.getEndTime().toString());
            dto.setPrepayment(prepayment.getPreFee());
            dto.setDetail(detailMap.get(key));

            result.add(dto);
        }

        return result;
    }

    /**
     * 导出所有客户价格表：每客户一个sheet（名称=客户名称），详情为空的客户跳过
     * 表结构对齐前端Price.vue详情弹窗：开始/结束日期、预付款、区域、各固定重量档、首重、续重 + 备注/区域说明
     */
    @Override
    public void exportAllPrice(HttpServletResponse response) {
        QueryWrapper<Customer> qw = new QueryWrapper<>();
        qw.orderByAsc("code");
        List<Customer> customers = customerMapper.selectList(qw);
        if (CollectionUtils.isEmpty(customers)) {
            throw new BusinessException("暂无客户数据，无法导出");
        }

        int exportedCount = 0;
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = buildHeaderStyle(workbook);
            CellStyle mergedStyle = buildMergedStyle(workbook);
            Set<String> usedSheetNames = new HashSet<>();

            for (Customer customer : customers) {
                // 复用详情接口逻辑，保证导出内容与页面展示一致
                List<CustomerPriceDetailDTO> priceList = getPrice(customer.getCode());
                if (CollectionUtils.isEmpty(priceList)) {
                    continue;
                }
                // 过滤明细行为空的时段，全部为空则该客户不导出
                List<CustomerPriceDetailDTO> periods = priceList.stream()
                        .filter(p -> CollectionUtils.isNotEmpty(p.getDetail()))
                        .collect(Collectors.toList());
                if (periods.isEmpty()) {
                    continue;
                }
                Sheet sheet = workbook.createSheet(buildUniqueSheetName(customer.getCustName(), usedSheetNames));
                writePriceSheet(sheet, periods, headerStyle, mergedStyle);
                exportedCount++;
            }

            if (exportedCount == 0) {
                throw new BusinessException("所有客户均无价格明细，未生成导出文件");
            }

            String fileName = "客户价格表_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + ".xlsx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName
                    + "\"; filename*=UTF-8''" + encodedFileName);
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new BusinessException("导出价格表失败：" + e.getMessage());
        }
    }

    /**
     * 写入单个客户的价格sheet：表头 + 各时段数据行 + 备注行（合并） + 区域说明行（合并）
     */
    private void writePriceSheet(Sheet sheet, List<CustomerPriceDetailDTO> periods,
            CellStyle headerStyle, CellStyle mergedStyle) {
        // 1. 汇总该客户所有时段的固定重量档位（数值升序去重），作为动态表头
        TreeSet<BigDecimal> weights = new TreeSet<>();
        periods.forEach(p -> p.getDetail().forEach(d -> {
            if (CollectionUtils.isNotEmpty(d.getFixedFee())) {
                d.getFixedFee().forEach(f -> {
                    if (f.getWeight() != null) {
                        weights.add(f.getWeight());
                    }
                });
            }
        }));
        int totalCols = 6 + weights.size();

        // 2. 表头
        Row header = sheet.createRow(0);
        int col = 0;
        header.createCell(col++).setCellValue("开始日期");
        header.createCell(col++).setCellValue("结束日期");
        header.createCell(col++).setCellValue("预付款");
        header.createCell(col++).setCellValue("区域");
        for (BigDecimal w : weights) {
            header.createCell(col++).setCellValue(w.stripTrailingZeros().toPlainString() + "kg");
        }
        header.createCell(col++).setCellValue("首重价格");
        header.createCell(col).setCellValue("续重价格");
        for (int i = 0; i < totalCols; i++) {
            header.getCell(i).setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 14 * 256);
        }

        // 3. 数据行（每时段一组，行结构与前端详情弹窗一致）
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int rowIdx = 1;
        String areaNote = null;
        for (CustomerPriceDetailDTO period : periods) {
            if (areaNote == null && CollectionUtils.isNotEmpty(period.getAreas())) {
                areaNote = buildAreaNote(period.getAreas());
            }
            for (PriceDetailDTO d : period.getDetail()) {
                Map<BigDecimal, BigDecimal> feeMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(d.getFixedFee())) {
                    d.getFixedFee().forEach(f -> feeMap.put(f.getWeight(), f.getFee()));
                }
                Row row = sheet.createRow(rowIdx++);
                col = 0;
                row.createCell(col++).setCellValue(d.getStartTime() == null ? "" : d.getStartTime().format(fmt));
                row.createCell(col++).setCellValue(d.getEndTime() == null ? "" : d.getEndTime().format(fmt));
                writeNumberCell(row, col++, period.getPrepayment());
                row.createCell(col++).setCellValue(d.getArea() == null ? "" : d.getArea());
                for (BigDecimal w : weights) {
                    BigDecimal fee = feeMap.get(w);
                    row.createCell(col++).setCellValue(fee == null ? "-" : fee.stripTrailingZeros().toPlainString());
                }
                writeNumberCell(row, col++, d.getFirstFee());
                writeNumberCell(row, col, d.getOverFee());
            }
            // 备注行（合并整行）
            if (StringUtils.isNotBlank(period.getRemark())) {
                rowIdx = writeMergedRow(sheet, rowIdx, totalCols, "备注：" + period.getRemark(), mergedStyle);
            }
        }

        // 4. 区域说明行（合并整行，取自第一时段的区域列表）
        if (areaNote != null) {
            writeMergedRow(sheet, rowIdx, totalCols, "区域说明：" + areaNote, mergedStyle);
        }
        sheet.createFreezePane(0, 1);
    }

    /**
     * 写数值单元格（空值写"-"）
     */
    private void writeNumberCell(Row row, int col, BigDecimal value) {
        Cell cell = row.createCell(col);
        if (value == null) {
            cell.setCellValue("-");
        } else {
            cell.setCellValue(value.doubleValue());
        }
    }

    /**
     * 写整行合并的说明行（备注/区域说明），返回下一行行号
     */
    private int writeMergedRow(Sheet sheet, int rowIdx, int totalCols, String text, CellStyle style) {
        Row row = sheet.createRow(rowIdx);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, totalCols - 1));
        Cell cell = row.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(style);
        return rowIdx + 1;
    }

    /**
     * 拼接区域说明：一区：xx、xx；二区：xx ...
     */
    private String buildAreaNote(List<Area> areas) {
        return areas.stream()
                .sorted(Comparator.comparing(Area::getAreaNum))
                .map(a -> areaLabel(a.getAreaNum()) + "：" + a.getAreaCity())
                .collect(Collectors.joining("；"));
    }

    private String areaLabel(Integer areaNum) {
        switch (areaNum == null ? 0 : areaNum) {
            case 1: return "一区";
            case 2: return "二区";
            case 3: return "三区";
            case 4: return "四区";
            case 5: return "五区";
            default: return "其他";
        }
    }

    /**
     * 生成合法且唯一的sheet名：去除Excel非法字符、限长31、重名自动加后缀
     */
    private String buildUniqueSheetName(String rawName, Set<String> used) {
        String base = StringUtils.isBlank(rawName) ? "未命名" : rawName.trim().replaceAll("[\\\\/:*?\\[\\]]", "");
        if (base.length() > 28) {
            base = base.substring(0, 28);
        }
        String name = base;
        int i = 2;
        while (used.contains(name)) {
            name = base + "(" + i++ + ")";
        }
        used.add(name);
        return name;
    }

    private CellStyle buildHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle buildMergedStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setWrapText(true);
        return style;
    }

}
