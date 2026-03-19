package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.express.yto.dto.AreaExtraFeeExcelDTO;
import com.express.yto.dto.OverFeeExcelDTO;
import java.util.ArrayList;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.model.ExtraFee;
import com.express.yto.dao.ExtraFeeMapper;
import com.express.yto.service.ExtraFeeService;
/**
 * @author Detective
 * @date Created in 2025/9/18
 */
@Service
public class ExtraFeeServiceImpl extends ServiceImpl<ExtraFeeMapper, ExtraFee> implements ExtraFeeService{

    @Autowired
    private ExtraFeeMapper extraFeeMapper;

    @Override
    public void importByExcel(String path) {
        List<AreaExtraFeeExcelDTO> list = new ArrayList<>();
        EasyExcel.read(path, AreaExtraFeeExcelDTO.class, new ReadListener<AreaExtraFeeExcelDTO>() {
            @Override
            public void invoke(AreaExtraFeeExcelDTO dto, AnalysisContext analysisContext) {
                list.add(dto);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        }).sheet("Sheet1").doRead();

        List<ExtraFee> models = new ArrayList<>();
        for (AreaExtraFeeExcelDTO dto : list) {
            if (null != dto.getBeijing()){
                // 北京
                ExtraFee model = ExtraFee.builder().kCode(dto.getKCode()).areaName("北京").fee(dto.getBeijing()).build();
                models.add(model);
            }
            if (null != dto.getShanghai()){
                // 上海
                ExtraFee model = ExtraFee.builder().kCode(dto.getKCode()).areaName("上海").fee(dto.getShanghai()).build();
                models.add(model);
            }
            if (null != dto.getShenzhen()){
                // 深圳
                ExtraFee model = ExtraFee.builder().kCode(dto.getKCode()).areaName("深圳").fee(dto.getShenzhen()).build();
                models.add(model);
            }
            if (null != dto.getZhoushan()){
                // 深圳
                ExtraFee model = ExtraFee.builder().kCode(dto.getKCode()).areaName("舟山").fee(dto.getShenzhen()).build();
                models.add(model);
            }
        }
        if (CollectionUtils.isNotEmpty(models)){
            extraFeeMapper.insert(models);
        }
    }
}
