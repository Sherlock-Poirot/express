package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.model.WaybillDetailOriginal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WaybillDetailOriginalMapper extends BaseMapper<WaybillDetailOriginal> {

    void insertBatch(@Param("list") List<WaybillDetailOriginal> list);
}