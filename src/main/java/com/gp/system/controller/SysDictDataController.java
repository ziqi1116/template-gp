package com.gp.system.controller;

import java.util.List;

import com.gp.common.core.result.Result;
import com.gp.system.domain.SysDictData;
import com.gp.system.service.SysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "字典数据管理")
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {

    @Autowired
    private SysDictDataService dictDataService;

    @Operation(summary = "字典数据列表")
    @GetMapping("/list")
    public Result<List<SysDictData>> list(@RequestParam(required = false) String dictType) {
        if (dictType != null && !dictType.isEmpty()) {
            return Result.success(dictDataService.listByDictType(dictType));
        }
        return Result.success(dictDataService.list());
    }

    @Operation(summary = "根据字典类型查询")
    @GetMapping("/type/{dictType}")
    public Result<List<SysDictData>> listByDictType(@PathVariable String dictType) {
        return Result.success(dictDataService.listByDictType(dictType));
    }

    @Operation(summary = "字典数据详情")
    @GetMapping("/{id}")
    public Result<SysDictData> getInfo(@PathVariable Long id) {
        return Result.success(dictDataService.getById(id));
    }

    @Operation(summary = "新增字典数据")
    @PostMapping
    public Result<Void> add(@RequestBody SysDictData dictData) {
        dictDataService.save(dictData);
        return Result.success();
    }

    @Operation(summary = "修改字典数据")
    @PutMapping
    public Result<Void> edit(@RequestBody SysDictData dictData) {
        dictDataService.updateById(dictData);
        return Result.success();
    }

    @Operation(summary = "删除字典数据")
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable List<Long> ids) {
        dictDataService.removeByIds(ids);
        return Result.success();
    }

}