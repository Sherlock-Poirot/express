package com.express.yto.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.express.yto.model.SysDict;
import com.express.yto.model.SysDictItem;

import java.util.List;
import java.util.Map;

/**
 * 字典服务接口
 */
public interface SysDictService extends IService<SysDict> {

    /**
     * 获取指定字典编码的所有字典项
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    List<SysDictItem> getDictItems(String dictCode);

    /**
     * 获取指定字典编码的字典项（Map格式，key为dictValue，value为dictLabel）
     * @param dictCode 字典编码
     * @return Map格式的字典项
     */
    Map<String, String> getDictMap(String dictCode);

    /**
     * 根据字典编码和字典值获取字典标签
     * @param dictCode 字典编码
     * @param dictValue 字典值
     * @return 字典标签，如果不存在返回原值
     */
    String getDictLabel(String dictCode, String dictValue);

    /**
     * 创建字典
     * @param dict 字典信息
     * @return 字典
     */
    SysDict createDict(SysDict dict);

    /**
     * 更新字典
     * @param id 字典ID
     * @param dict 字典信息
     * @return 更新后的字典
     */
    SysDict updateDict(Long id, SysDict dict);

    /**
     * 删除字典（级联删除字典项）
     * @param id 字典ID
     * @return 是否成功
     */
    boolean deleteDict(Long id);
}
