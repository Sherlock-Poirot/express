package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.dto.AreaListDTO;
import com.express.yto.model.Area;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Detective
 * @date Created in 2026/3/16
 */
@Mapper
public interface AreaMapper extends BaseMapper<Area> {

    List<AreaListDTO> getList();
}
