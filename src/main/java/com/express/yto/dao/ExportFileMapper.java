package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.model.ExportFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单明细导出文件记录 Mapper
 */
@Mapper
public interface ExportFileMapper extends BaseMapper<ExportFile> {
}
