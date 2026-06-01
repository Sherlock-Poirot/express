package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.model.WaybillDetailOriginal;
import java.util.List;

public interface WaybillDetailOriginalService extends IService<WaybillDetailOriginal> {

    void insertBatch(List<WaybillDetailOriginal> list);
}