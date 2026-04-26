package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.ShopEmpMapper;
import com.express.yto.dao.ShopMapper;
import com.express.yto.dto.ShopEmpInput;
import com.express.yto.model.Shop;
import com.express.yto.model.ShopEmp;
import com.express.yto.service.ShopService;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ShopEmpMapper shopEmpMapper;

    @Override
    public IPage<ShopEmp> search(String code, String name, String empName, Integer pageNo, Integer pageSize) {
        Page<ShopEmp> page = new Page<>(pageNo, pageSize);
        QueryWrapper<ShopEmp> qw = new QueryWrapper<>();
        if (StringUtils.isNotBlank(code)) {
            qw.eq("k_code", code);
        }
        if (StringUtils.isNotBlank(name)) {
            qw.like("k_name", name);
        }
        if (StringUtils.isNotBlank(empName)) {
            qw.like("emp_name", empName);
        }
        qw.orderByDesc("id");
        return shopEmpMapper.selectPage(page, qw);
    }

    @Override
    public void batchDelete(List<Integer> ids) {
        shopEmpMapper.deleteByIds(ids);
    }

    @Override
    public void add(ShopEmpInput input) {
        ShopEmp shopEmp = new ShopEmp();
        BeanUtils.copyProperties(input, shopEmp);
        shopEmpMapper.insert(shopEmp);
    }

    @Override
    public void update(ShopEmpInput input) {
        ShopEmp shopEmp = new ShopEmp();
        BeanUtils.copyProperties(input, shopEmp);
        shopEmpMapper.updateById(shopEmp);
    }

    @Override
    public int importShop(MultipartFile file) {
        try {
            // 1. 一次性读取所有Excel数据到 list
            List<ShopEmp> dataList = EasyExcel.read(file.getInputStream())
                    .head(ShopEmp.class)
                    .sheet()
                    .doReadSync();

            if (CollectionUtils.isEmpty(dataList)) {
                return 0;
            }

            // 2. 一次性批量插入（1次IO！）
            shopEmpMapper.insert(dataList);

            return dataList.size();
        } catch (Exception e) {
            throw new RuntimeException("导入失败：" + e.getMessage());
        }
    }

}
