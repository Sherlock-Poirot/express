package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.express.yto.dao.FixedFeeMapper;
import com.express.yto.dao.OverFeeMapper;
import com.express.yto.dao.PrepaymentMapper;
import com.express.yto.model.FixedFee;
import com.express.yto.model.OverFee;
import com.express.yto.model.Prepayment;
import com.express.yto.service.BackUpService;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Detective
 * @date Created in 2026/3/13
 */
@Slf4j
@Service
public class BackUpServiceImpl implements BackUpService {

    @Autowired
    private FixedFeeMapper fixedFeeMapper;

    @Autowired
    private OverFeeMapper overFeeMapper;

    @Autowired
    private PrepaymentMapper prepaymentMapper;


    @Override
    public void backUp() {
        // 删除bak表的数据
        fixedFeeMapper.deleteBak();
        overFeeMapper.deleteBak();
        prepaymentMapper.deleteBak();
        // 备份数据
        fixedFeeMapper.insertBak();
        overFeeMapper.insertBak();
        prepaymentMapper.insertBak();
    }

    @Override
    public void updateInFirstDay() {
        // 删除结束时间不是最后一天的数据
        QueryWrapper<FixedFee> fixedWrapper = new QueryWrapper<>();
        fixedWrapper.apply("DAYOFMONTH(end_time) != 1");
        QueryWrapper<OverFee> overWrapper = new QueryWrapper<>();
        overWrapper.apply("DAYOFMONTH(end_time) != 1");
        QueryWrapper<Prepayment> preWrapper = new QueryWrapper<>();
        preWrapper.apply("DAYOFMONTH(end_time) != 1");
        fixedFeeMapper.delete(fixedWrapper);
        overFeeMapper.delete(overWrapper);
        prepaymentMapper.delete(preWrapper);

        // 更新开始时间
        // 2. 设置 start_time = 上个月第一天（核心：用数据库函数计算）
        // 3. 设置 end_time = 本月第一天
        String startWord = "start_time";
        String endWord = "end_time";
        String startSentence = "DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 MONTH), '%Y-%m-01')";
        String endSentence = "DATE_FORMAT(NOW(), '%Y-%m-01')";
        UpdateWrapper<FixedFee> fixedUpdateWp = new UpdateWrapper<>();
        fixedUpdateWp.set(startWord, startSentence);
        fixedUpdateWp.set(endWord, endSentence);
        UpdateWrapper<OverFee> overUpdaterWp = new UpdateWrapper<>();
        overUpdaterWp.set(startWord, startSentence);
        overUpdaterWp.set(endWord, endSentence);
        UpdateWrapper<Prepayment> preUpdateWp = new UpdateWrapper<>();
        preUpdateWp.set(startWord, startSentence);
        preUpdateWp.set(endWord, endSentence);
        fixedFeeMapper.update(null, fixedUpdateWp);
        overFeeMapper.update(null, overUpdaterWp);
        prepaymentMapper.update(null, preUpdateWp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void monthlyBackupAndClean() {
        // 第一步：先清空三张_bak表，再把当前全量数据备份进去（复用现有备份SQL）
        log.info("月度任务开始：备份价格表到_bak表");
        backUp();
        log.info("月度任务：价格表备份完成");

        // 第二步：删除原表中 end_time 早于半年前的历史数据
        // lt 条件天然不会命中 end_time 为 NULL 的长期有效规则（NULL比较结果为unknown）
        LocalDate halfYearAgo = LocalDate.now().minusMonths(6);
        log.info("月度任务：开始清理end_time早于{}的数据", halfYearAgo);

        int fixedDeleted = fixedFeeMapper.delete(
                new QueryWrapper<FixedFee>().lt("end_time", halfYearAgo));
        int overDeleted = overFeeMapper.delete(
                new QueryWrapper<OverFee>().lt("end_time", halfYearAgo));
        int prepaymentDeleted = prepaymentMapper.delete(
                new QueryWrapper<Prepayment>().lt("end_time", halfYearAgo));

        log.info("月度任务完成，清理历史数据：t_fixed_fee {}条，t_over_fee {}条，t_prepayment {}条",
                fixedDeleted, overDeleted, prepaymentDeleted);
    }
}
