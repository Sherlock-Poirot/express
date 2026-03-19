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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Detective
 * @date Created in 2026/3/13
 */
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
}
