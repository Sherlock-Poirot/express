package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.dto.AreaDTO;
import com.express.yto.dto.AreaListDTO;
import com.express.yto.model.Area;
import java.util.List;

/**
 * @author Detective
 * @date Created in 2026/3/16
 */
public interface AreaService extends IService<Area> {


    List<AreaListDTO> getList();
}
