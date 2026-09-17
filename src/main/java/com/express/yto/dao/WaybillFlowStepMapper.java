package com.express.yto.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.express.yto.model.WaybillFlowStep;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 账单工作流步骤 Mapper
 * @author Detective
 * @date Created in 2026/9/16
 */
@Mapper
public interface WaybillFlowStepMapper extends BaseMapper<WaybillFlowStep> {

    /**
     * 批量初始化月度步骤（INSERT IGNORE）
     * 依赖 uk_bill_month_step 唯一键防重：定时任务重复执行或多实例并发执行时自动忽略已存在记录
     * @param steps 步骤记录列表
     * @return 实际插入条数
     */
    @Insert("<script>" +
            "INSERT IGNORE INTO t_waybill_flow_step (bill_month, step_code, step_name, step_order, status, remark) VALUES " +
            "<foreach collection='list' item='s' separator=','>" +
            "(#{s.billMonth}, #{s.stepCode}, #{s.stepName}, #{s.stepOrder}, #{s.status}, #{s.remark})" +
            "</foreach>" +
            "</script>")
    int insertIgnoreBatch(@Param("list") List<WaybillFlowStep> steps);
}
