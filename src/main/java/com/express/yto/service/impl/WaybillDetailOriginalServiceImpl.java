package com.express.yto.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.WaybillDetailOriginalMapper;
import com.express.yto.model.WaybillDetailOriginal;
import com.express.yto.service.WaybillDetailOriginalService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WaybillDetailOriginalServiceImpl extends ServiceImpl<WaybillDetailOriginalMapper, WaybillDetailOriginal> 
        implements WaybillDetailOriginalService {

    @Override
    public void insertBatch(List<WaybillDetailOriginal> list) {
        baseMapper.insertBatch(list);
    }
}