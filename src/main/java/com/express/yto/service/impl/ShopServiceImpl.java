package com.express.yto.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.ShopEmpMapper;
import com.express.yto.dao.ShopMapper;
import com.express.yto.dto.ShopEmpExcelDTO;
import com.express.yto.dto.ShopExcelDTO;
import com.express.yto.model.Shop;
import com.express.yto.model.ShopEmp;
import com.express.yto.service.ShopService;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Detective
 * @date Created in 2025/9/13
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ShopEmpMapper shopEmpMapper;

    @Override
    public void batchInsert(String readPath) {
        List<ShopExcelDTO> list = new ArrayList<>();
        EasyExcel.read(readPath, ShopExcelDTO.class, new ReadListener<ShopExcelDTO>() {

            @Override
            public void invoke(ShopExcelDTO dto, AnalysisContext analysisContext) {
                list.add(dto);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        }).sheet("路桥客户9月").doRead();

        List<Shop> shopList = new ArrayList<>();
        for (ShopExcelDTO dto : list) {
            if (StringUtils.isNotBlank(dto.getTaoBao1())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTaoBao1())
                        .platform("菜鸟").build());
            }
            if (StringUtils.isNotBlank(dto.getTaoBao2())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTaoBao2())
                        .platform("菜鸟").build());
            }
            if (StringUtils.isNotBlank(dto.getTaoBao3())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTaoBao3())
                        .platform("菜鸟").build());
            }
            if (StringUtils.isNotBlank(dto.getTaoBao4())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTaoBao4())
                        .platform("菜鸟").build());
            }
            if (StringUtils.isNotBlank(dto.getTaoBao5())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTaoBao5())
                        .platform("菜鸟").build());
            }
            if (StringUtils.isNotBlank(dto.getTaoBao6())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTaoBao6())
                        .platform("菜鸟").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd1())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd1())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd2())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd2())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd3())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd3())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd4())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd4())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd5())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd5())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd6())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd6())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd7())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd7())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd8())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd8())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd9())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd9())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd10())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd10())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd11())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd11())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd12())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd12())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd13())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd13())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd14())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd14())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd15())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd15())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance1())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance1())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance2())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance2())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance3())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance3())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance4())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance4())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance5())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance5())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance6())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance6())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance7())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance7())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance8())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance8())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance9())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance9())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance10())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance10())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance11())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance11())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance12())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance12())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance13())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance13())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance14())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance14())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance15())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance15())
                        .platform("抖音").build());
            }

            if (StringUtils.isNotBlank(dto.getKs1())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getKs1())
                        .platform("快手").build());
            }
            if (StringUtils.isNotBlank(dto.getKs2())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getKs2())
                        .platform("快手").build());
            }
            if (StringUtils.isNotBlank(dto.getKs3())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getKs3())
                        .platform("快手").build());
            }
            if (StringUtils.isNotBlank(dto.getJd1())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getJd1())
                        .platform("京东").build());
            }
            if (StringUtils.isNotBlank(dto.getJd2())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getJd2())
                        .platform("京东").build());
            }
            if (StringUtils.isNotBlank(dto.getXhs1())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getXhs1())
                        .platform("小红书").build());
            }
            if (StringUtils.isNotBlank(dto.getXhs2())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getXhs2())
                        .platform("小红书").build());
            }
            if (StringUtils.isNotBlank(dto.getWph())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getWph())
                        .platform("唯品会").build());
            }
            if (StringUtils.isNotBlank(dto.getYto())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getYto())
                        .platform("圆通").build());
            }
            if (StringUtils.isNotBlank(dto.getTx1())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTx1())
                        .platform("腾讯").build());
            }
            if (StringUtils.isNotBlank(dto.getTx2())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTx2())
                        .platform("腾讯").build());
            }
            if (StringUtils.isNotBlank(dto.getMt())) {
                shopList.add(Shop.builder().kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getMt())
                        .platform("美团").build());
            }
        }
        shopMapper.insert(shopList);

    }

    @Override
    public void export(String fileName) {
        QueryWrapper<Shop> qw = new QueryWrapper<>();
        List<Shop> shopList = shopMapper.selectList(qw);

        QueryWrapper<ShopEmp> qwEmp = new QueryWrapper<>();
        List<ShopEmp> shopEmpList = shopEmpMapper.selectList(qwEmp);

        ExcelWriter excelWriter = EasyExcel.write(fileName).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("直营客户店铺表").head(Shop.class).build();
        WriteSheet writeSheetEmp = EasyExcel.writerSheet("承包区店铺表").head(ShopEmp.class).build();
        excelWriter.write(shopList, writeSheet);
        excelWriter.write(shopEmpList, writeSheetEmp);
        excelWriter.finish();
    }

    @Override
    public void batchInsertEmp(String readPath) {
        List<ShopEmpExcelDTO> list = new ArrayList<>();
        EasyExcel.read(readPath, ShopEmpExcelDTO.class, new ReadListener<ShopEmpExcelDTO>() {

            @Override
            public void invoke(ShopEmpExcelDTO dto, AnalysisContext analysisContext) {
                list.add(dto);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        }).sheet("承包区客户9月").doRead();
        List<ShopEmp> shopList = new ArrayList<>();
        for (ShopEmpExcelDTO dto : list) {
            if (StringUtils.isNotBlank(dto.getTaoBao1())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTaoBao1())
                        .platform("菜鸟").build());
            }
            if (StringUtils.isNotBlank(dto.getTaoBao2())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTaoBao2())
                        .platform("菜鸟").build());
            }
            if (StringUtils.isNotBlank(dto.getTaoBao3())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTaoBao3())
                        .platform("菜鸟").build());
            }
            if (StringUtils.isNotBlank(dto.getTaoBao4())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTaoBao4())
                        .platform("菜鸟").build());
            }
            if (StringUtils.isNotBlank(dto.getTaoBao5())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTaoBao5())
                        .platform("菜鸟").build());
            }
            if (StringUtils.isNotBlank(dto.getTaoBao6())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTaoBao6())
                        .platform("菜鸟").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd1())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd1())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd2())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd2())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd3())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd3())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd4())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd4())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd5())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd5())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd6())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd6())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd7())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd7())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd8())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd8())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd9())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd9())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd10())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd10())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd11())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd11())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd12())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd12())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd13())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd13())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd14())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd14())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getPdd15())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getPdd15())
                        .platform("拼多多").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance1())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance1())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance2())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance2())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance3())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance3())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance4())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance4())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance5())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance5())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance6())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance6())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance7())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance7())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance8())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance8())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance9())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance9())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance10())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance10())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance11())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance11())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance12())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance12())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance13())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance13())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance14())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance14())
                        .platform("抖音").build());
            }
            if (StringUtils.isNotBlank(dto.getByteDance15())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getByteDance15())
                        .platform("抖音").build());
            }

            if (StringUtils.isNotBlank(dto.getKs1())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getKs1())
                        .platform("快手").build());
            }
            if (StringUtils.isNotBlank(dto.getKs2())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getKs2())
                        .platform("快手").build());
            }
            if (StringUtils.isNotBlank(dto.getKs3())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getKs3())
                        .platform("快手").build());
            }
            if (StringUtils.isNotBlank(dto.getJd1())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getJd1())
                        .platform("京东").build());
            }
            if (StringUtils.isNotBlank(dto.getJd2())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getJd2())
                        .platform("京东").build());
            }
            if (StringUtils.isNotBlank(dto.getXhs1())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getXhs1())
                        .platform("小红书").build());
            }
            if (StringUtils.isNotBlank(dto.getXhs2())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getXhs2())
                        .platform("小红书").build());
            }
            if (StringUtils.isNotBlank(dto.getWph())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getWph())
                        .platform("唯品会").build());
            }
            if (StringUtils.isNotBlank(dto.getYto())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getYto())
                        .platform("圆通").build());
            }
            if (StringUtils.isNotBlank(dto.getTx1())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTx1())
                        .platform("腾讯").build());
            }
            if (StringUtils.isNotBlank(dto.getTx2())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getTx2())
                        .platform("腾讯").build());
            }
            if (StringUtils.isNotBlank(dto.getMt())) {
                shopList.add(ShopEmp.builder().empName(dto.getEmpName()).empType(dto.getEmpType()).kCode(dto.getKCode()).kName(dto.getKName()).shopName(dto.getMt())
                        .platform("美团").build());
            }
        }
        shopEmpMapper.insert(shopList);
    }

}
