package com.express.yto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.express.yto.dao.SysDictItemMapper;
import com.express.yto.model.SysDictItem;
import com.express.yto.service.SysDictItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典项服务实现类
 */
@Slf4j
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements SysDictItemService {

    @Override
    public SysDictItem createDictItem(SysDictItem item) {
        log.info("创建字典项: dictCode={}, dictValue={}", item.getDictCode(), item.getDictValue());
        save(item);
        return item;
    }

    @Override
    public SysDictItem updateDictItem(Long id, SysDictItem item) {
        log.info("更新字典项: id={}", id);
        item.setId(id);
        updateById(item);
        return getById(id);
    }

    @Override
    public boolean deleteDictItem(Long id) {
        log.info("删除字典项: id={}", id);
        return removeById(id);
    }

    @Override
    public boolean deleteDictItemBatch(List<Long> ids) {
        log.info("批量删除字典项: ids={}", ids);
        if (ids == null || ids.isEmpty()) {
            log.warn("批量删除失败：ID列表为空");
            return false;
        }
        return removeByIds(ids);
    }

    @Override
    public List<SysDictItem> getDictItemsByCode(String dictCode) {
        QueryWrapper<SysDictItem> query = new QueryWrapper<>();
        query.eq("dict_code", dictCode);
        query.eq("status", 1);
        query.orderByAsc("sort_order");
        return list(query);
    }
}
