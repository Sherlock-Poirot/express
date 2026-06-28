package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.SysDictItemMapper;
import com.express.yto.dao.SysDictMapper;
import com.express.yto.model.SysDict;
import com.express.yto.model.SysDictItem;
import com.express.yto.service.SysDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典服务实现类
 */
@Slf4j
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    @Autowired
    private SysDictItemMapper dictItemMapper;

    @Override
    public List<SysDictItem> getDictItems(String dictCode) {
        return dictItemMapper.selectByDictCode(dictCode);
    }

    @Override
    public Map<String, String> getDictMap(String dictCode) {
        List<SysDictItem> items = getDictItems(dictCode);
        Map<String, String> map = new HashMap<>();
        for (SysDictItem item : items) {
            map.put(item.getDictValue(), item.getDictLabel());
        }
        return map;
    }

    @Override
    public String getDictLabel(String dictCode, String dictValue) {
        if (dictValue == null || dictValue.trim().isEmpty()) {
            return dictValue;
        }
        SysDictItem item = dictItemMapper.selectByDictCodeAndValue(dictCode, dictValue);
        return item != null ? item.getDictLabel() : dictValue;
    }

    @Override
    public SysDict createDict(SysDict dict) {
        log.info("创建字典: {}", dict.getDictCode());
        save(dict);
        return dict;
    }

    @Override
    public SysDict updateDict(Long id, SysDict dict) {
        log.info("更新字典: id={}", id);
        dict.setId(id);
        updateById(dict);
        return getById(id);
    }

    @Override
    @Transactional
    public boolean deleteDict(Long id) {
        log.info("删除字典: id={}", id);
        SysDict dict = getById(id);
        if (dict == null) {
            log.warn("删除字典失败：字典不存在，id={}", id);
            return false;
        }
        // 删除字典及其所有字典项
        QueryWrapper<SysDictItem> itemQuery = new QueryWrapper<>();
        itemQuery.eq("dict_code", dict.getDictCode());
        dictItemMapper.delete(itemQuery);
        return removeById(id);
    }
}
