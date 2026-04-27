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
import com.express.yto.dao.CustomerMapper;
import com.express.yto.dao.ExtraFeeMapper;
import com.express.yto.dao.FixedFeeMapper;
import com.express.yto.dao.OverFeeMapper;
import com.express.yto.dao.PrepaymentMapper;
import com.express.yto.dao.ShopEmpMapper;
import com.express.yto.dto.CustomerExcelDTO;
import com.express.yto.dto.CustomerInput;
import com.express.yto.dto.CustomerPriceDetailDTO;
import com.express.yto.dto.CustomerPriceInput;
import com.express.yto.dto.CustomerSearchInput;
import com.express.yto.dto.FixedTinyDTO;
import com.express.yto.dto.PriceDeleteInput;
import com.express.yto.dto.PriceDetailDTO;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.Customer;
import com.express.yto.model.ExtraFee;
import com.express.yto.model.FixedFee;
import com.express.yto.model.OverFee;
import com.express.yto.model.Prepayment;
import com.express.yto.model.ShopEmp;
import com.express.yto.service.CustomerService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
            Customer model = Customer.builder().kName(dto.getKName()).kCode(dto.getKCode())
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
    public void add(CustomerInput input) {
        QueryWrapper<Customer> qw = new QueryWrapper<>();
        qw.eq("k_name", input.getKName());
        if (StringUtils.isNotBlank(input.getKCode())) {
            qw.or().eq("k_code", input.getKCode());
        }
        List<Customer> list = customerMapper.selectList(qw);
        if (list.size() > 0) {
            throw new BusinessException("该数据已存在，不可重复添加");
        }
        Customer customer = new Customer();
        BeanUtils.copyProperties(input, customer);
        customerMapper.insert(customer);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        customerMapper.deleteByIds(ids);
        // 获取店铺表，价格表，全都进行删除
        List<Customer> customers = customerMapper.selectBatchIds(ids);
        List<String> codeList = customers.stream().map(Customer::getKCode).collect(Collectors.toList());
        // 删除固定重量价格表
        QueryWrapper<FixedFee> fixedQw = new QueryWrapper<>();
        fixedQw.in("k_code", codeList);
        fixedFeeMapper.delete(fixedQw);
        // 删除续重费用表
        QueryWrapper<OverFee> overQw = new QueryWrapper<>();
        overQw.in("k_code", codeList);
        overFeeMapper.delete(overQw);
        // 删除预付款表
        QueryWrapper<Prepayment> preQw = new QueryWrapper<>();
        preQw.in("k_code", codeList);
        prepaymentMapper.delete(preQw);
        // 删除店铺表
        QueryWrapper<ShopEmp> shopQw = new QueryWrapper<>();
        shopQw.in("k_code", codeList);
        shopEmpMapper.delete(shopQw);
    }

    @Override
    public IPage<Customer> search(CustomerSearchInput input) {
        Page<Customer> page = new Page<>(input.getPageNo(), input.getPageSize());
        QueryWrapper<Customer> qw = new QueryWrapper<>();
        if (StringUtils.isNotBlank(input.getkName())) {
            qw.like("k_name", input.getkName());
        }
        if (StringUtils.isNotBlank(input.getkCode())) {
            qw.eq("k_code", input.getkCode());
        }

        return customerMapper.selectPage(page, qw);
    }

    @Override
    public List<CustomerPriceDetailDTO> getPrice(String kCode) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("k_code", kCode);
        List<FixedFee> fixedList = fixedFeeMapper.selectByMap(map);
        List<OverFee> overList = overFeeMapper.selectByMap(map);
        List<Prepayment> prepaymentList = prepaymentMapper.selectByMap(map);
        List<ExtraFee> extraFeeList = extraFeeMapper.selectByMap(map);
        QueryWrapper<Customer> qw = new QueryWrapper<>();
        qw.eq("k_code", kCode);
        Customer customer = customerMapper.selectOne(qw);

        List<CustomerPriceDetailDTO> result = combine(fixedList, overList, prepaymentList);
        result.sort(Comparator.comparing(CustomerPriceDetailDTO::getEndTime).reversed());
        result.forEach(e -> {
            e.setRemark(buildRemark(customer, extraFeeList));
            e.setName(customer.getKName());
            e.setCode(customer.getKCode());
        });
        return result;
    }

    @Override
    public void deletePrice(PriceDeleteInput input) {
        Map<String, Object> queryMap = new HashMap<>(4);
        queryMap.put("k_code", input.getCode());
        queryMap.put("start_time", input.getStartTime());
        queryMap.put("end_time", input.getEndTime());
        List<FixedFee> fixedList = fixedFeeMapper.selectByMap(queryMap);
        List<OverFee> overList = overFeeMapper.selectByMap(queryMap);
        List<Prepayment> prepaymentList = prepaymentMapper.selectByMap(queryMap);
        queryMap.clear();
        queryMap.put("k_code", input.getCode());
        List<ExtraFee> extraFeeList = extraFeeMapper.selectByMap(queryMap);
        if (deletePriceJudge(fixedList, overList, prepaymentList, extraFeeList)) {
            throw new BusinessException("该时间段没有价格表，请检查时间区间的正确性");
        }
        List<Long> fixedIds = fixedList.stream().map(FixedFee::getId).collect(Collectors.toList());
        List<Long> overIds = overList.stream().map(OverFee::getId).collect(Collectors.toList());
        List<Long> prepaymentIds = prepaymentList.stream().map(Prepayment::getId).collect(Collectors.toList());
        fixedFeeMapper.deleteByIds(fixedIds);
        overFeeMapper.deleteByIds(overIds);
        prepaymentMapper.deleteByIds(prepaymentIds);
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

        // 客户预付款
        Prepayment prepayment = new Prepayment();
        prepayment.setEndTime(END_TIME);
        prepayment.setKCode(code);
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
            overFee.setKCode(code);
            overFee.setArea(dto.getArea());
            overFee.setFee(dto.getOverFee());
            overFee.setFirstFee(dto.getFirstFee());
            overFeeList.add(overFee);
            for (FixedTinyDTO fixed : dto.getFixedList()) {
                FixedFee fixedFee = new FixedFee();
                fixedFee.setEndTime(END_TIME);
                fixedFee.setKCode(code);
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

}
