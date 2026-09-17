package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.model.WaybillFlowFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单工作流导入文件记录 Mapper
 * @author Detective
 * @date Created in 2026/9/16
 */
@Mapper
public interface WaybillFlowFileMapper extends BaseMapper<WaybillFlowFile> {
}
