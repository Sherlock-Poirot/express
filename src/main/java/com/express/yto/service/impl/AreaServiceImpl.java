package com.express.yto.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.AreaMapper;
import com.express.yto.dto.AreaDTO;
import com.express.yto.dto.AreaListDTO;
import com.express.yto.model.Area;
import com.express.yto.service.AreaService;
import com.express.yto.util.AreaUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Detective
 * @date Created in 2026/3/16
 */
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

    @Autowired
    private AreaMapper areaMapper;

    @Override
    public List<AreaListDTO> getList() {
        List<AreaListDTO> list = areaMapper.getList();
        list.forEach(e -> e.setAreaName(AreaUtil.AREA_DICT.get(e.getAreaNum())));
        return list;
    }
}
