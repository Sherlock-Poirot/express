package com.express.yto.service.impl;

import static com.express.yto.util.AreaUtil.AREA_DICT;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.CustomerMapper;
import com.express.yto.dao.FixedFeeMapper;
import com.express.yto.dao.OverFeeMapper;
import com.express.yto.dao.PrepaymentMapper;
import com.express.yto.dto.CustomerExcelDTO;
import com.express.yto.dto.CustomerInput;
import com.express.yto.dto.CustomerPriceDetailDTO;
import com.express.yto.dto.CustomerSearchInput;
import com.express.yto.dto.FixedTinyDTO;
import com.express.yto.dto.PriceDetailDTO;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.Customer;
import com.express.yto.model.FixedFee;
import com.express.yto.model.OverFee;
import com.express.yto.model.Prepayment;
import com.express.yto.service.CustomerService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void delete(List<Integer> ids) {
        customerMapper.deleteByIds(ids);
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
        return combine(fixedList, overList, prepaymentList);
    }

    private List<CustomerPriceDetailDTO> combine(List<FixedFee> fixedList, List<OverFee> overList, List<Prepayment> prepaymentList) {

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
