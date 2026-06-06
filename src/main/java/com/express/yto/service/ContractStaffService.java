package com.express.yto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.StaffInput;
import com.express.yto.model.ContractStaff;

/**
 * @author Detective
 * @date Created in 2026/5/9
 */
public interface ContractStaffService extends IService<ContractStaff> {


    IPage<ContractStaff> search(String staffName, String phone, Integer pageNo, Integer pageSize);

    void add(StaffInput input);

    void update(StaffInput input);
}
