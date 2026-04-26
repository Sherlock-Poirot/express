package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.ShopEmpInput;
import com.express.yto.model.Shop;
import com.express.yto.model.ShopEmp;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
public interface ShopService extends IService<Shop> {

    IPage<ShopEmp> search(String code, String name, String empName, Integer pageNo, Integer pageSize);

    void batchDelete(List<Integer> ids);

    void add(ShopEmpInput input);

    void update(ShopEmpInput input);

    int importShop(MultipartFile file);
}
