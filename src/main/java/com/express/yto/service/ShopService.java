package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.model.Shop;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
public interface ShopService extends IService<Shop> {

    void batchInsert(String readPath);

    void export(String fileName);

    void batchInsertEmp(String readPath);
}
