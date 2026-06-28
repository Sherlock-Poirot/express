package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.model.SysDictItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {

    List<SysDictItem> selectByDictCode(@Param("dictCode") String dictCode);

    SysDictItem selectByDictCodeAndValue(@Param("dictCode") String dictCode, @Param("dictValue") String dictValue);
}
