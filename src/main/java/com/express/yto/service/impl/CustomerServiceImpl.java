package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.CustomerMapper;
import com.express.yto.dto.CustomerExcelDTO;
import com.express.yto.dto.CustomerInput;
import com.express.yto.exception.BusinessException;
import com.express.yto.model.Customer;
import com.express.yto.service.CustomerService;
import java.util.ArrayList;
import java.util.List;
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
}
