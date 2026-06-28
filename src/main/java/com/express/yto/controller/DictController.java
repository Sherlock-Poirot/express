package com.express.yto.controller;

import com.express.yto.dto.RestResult;
import com.express.yto.model.SysDict;
import com.express.yto.model.SysDictItem;
import com.express.yto.service.SysDictItemService;
import com.express.yto.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 字典管理控制器
 * 提供字典和字典项的CRUD接口
 */
@RestController
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private SysDictService dictService;

    @Autowired
    private SysDictItemService dictItemService;

    // ==================== 字典管理 ====================

    /**
     * 创建字典
     * @param dict 字典信息
     * @return 字典
     */
    @PostMapping("/create")
    public RestResult<SysDict> createDict(@RequestBody SysDict dict) {
        SysDict result = dictService.createDict(dict);
        return RestResult.ok(result);
    }

    /**
     * 查询字典列表
     * @return 字典列表
     */
    @GetMapping("/list")
    public RestResult<List<SysDict>> listDicts() {
        List<SysDict> result = dictService.list();
        return RestResult.ok(result);
    }

    /**
     * 更新字典
     * @param id 字典ID
     * @param dict 字典信息
     * @return 更新后的字典
     */
    @PutMapping("/{id}")
    public RestResult<SysDict> updateDict(@PathVariable Long id, @RequestBody SysDict dict) {
        SysDict result = dictService.updateDict(id, dict);
        return RestResult.ok(result);
    }

    /**
     * 删除字典（级联删除字典项）
     * @param id 字典ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public RestResult<Boolean> deleteDict(@PathVariable Long id) {
        boolean result = dictService.deleteDict(id);
        return RestResult.ok(result);
    }

    // ==================== 字典项管理 ====================

    /**
     * 创建字典项
     * @param item 字典项信息
     * @return 字典项
     */
    @PostMapping("/item/create")
    public RestResult<SysDictItem> createDictItem(@RequestBody SysDictItem item) {
        SysDictItem result = dictItemService.createDictItem(item);
        return RestResult.ok(result);
    }

    /**
     * 更新字典项
     * @param id 字典项ID
     * @param item 字典项信息
     * @return 更新后的字典项
     */
    @PutMapping("/item/{id}")
    public RestResult<SysDictItem> updateDictItem(@PathVariable Long id, @RequestBody SysDictItem item) {
        SysDictItem result = dictItemService.updateDictItem(id, item);
        return RestResult.ok(result);
    }

    /**
     * 删除字典项
     * @param id 字典项ID
     * @return 是否成功
     */
    @DeleteMapping("/item/{id}")
    public RestResult<Boolean> deleteDictItem(@PathVariable Long id) {
        boolean result = dictItemService.deleteDictItem(id);
        return RestResult.ok(result);
    }

    /**
     * 批量删除字典项
     * @param ids 字典项ID列表
     * @return 是否成功
     */
    @DeleteMapping("/item/batch/delete")
    public RestResult<Boolean> deleteDictItemBatch(@RequestBody List<Long> ids) {
        boolean result = dictItemService.deleteDictItemBatch(ids);
        return RestResult.ok(result);
    }

    // ==================== 查询接口 ====================

    /**
     * 根据字典编码获取字典项列表
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    @GetMapping("/items/{dictCode}")
    public RestResult<List<SysDictItem>> getDictItems(@PathVariable String dictCode) {
        List<SysDictItem> result = dictService.getDictItems(dictCode);
        return RestResult.ok(result);
    }

    /**
     * 根据字典编码获取字典项（Map格式）
     * @param dictCode 字典编码
     * @return Map格式的字典项
     */
    @GetMapping("/map/{dictCode}")
    public RestResult<Map<String, String>> getDictMap(@PathVariable String dictCode) {
        Map<String, String> result = dictService.getDictMap(dictCode);
        return RestResult.ok(result);
    }

    /**
     * 根据字典编码和字典值获取字典标签
     * @param dictCode 字典编码
     * @param dictValue 字典值
     * @return 字典标签
     */
    @GetMapping("/label")
    public RestResult<String> getDictLabel(
            @RequestParam String dictCode,
            @RequestParam String dictValue) {
        String result = dictService.getDictLabel(dictCode, dictValue);
        return RestResult.ok(result);
    }
}
