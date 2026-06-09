package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.model.DailyBill;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DailyBillMapper extends BaseMapper<DailyBill> {
    void insertBatch(@Param("list") List<DailyBill> list);
}