package com.express.yto.service;

import com.express.yto.model.ExtraFee;
import com.baomidou.mybatisplus.extension.service.IService;
    /**
 * @author Detective
 * @date Created in 2025/9/18
 */
public interface ExtraFeeService extends IService<ExtraFee>{


        void importByExcel(String path);
    }
