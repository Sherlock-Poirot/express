package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.model.SysDictItem;

import java.util.List;

/**
 * 字典项服务接口
 */
public interface SysDictItemService extends IService<SysDictItem> {

    /**
     * 创建字典项
     * @param item 字典项信息
     * @return 字典项
     */
    SysDictItem createDictItem(SysDictItem item);

    /**
     * 更新字典项
     * @param id 字典项ID
     * @param item 字典项信息
     * @return 更新后的字典项
     */
    SysDictItem updateDictItem(Long id, SysDictItem item);

    /**
     * 删除字典项
     * @param id 字典项ID
     * @return 是否成功
     */
    boolean deleteDictItem(Long id);

    /**
     * 批量删除字典项
     * @param ids 字典项ID列表
     * @return 是否成功
     */
    boolean deleteDictItemBatch(List<Long> ids);

    /**
     * 根据字典编码获取字典项列表
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    List<SysDictItem> getDictItemsByCode(String dictCode);
}
