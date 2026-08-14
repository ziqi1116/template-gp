package com.gp.system.controller;

import java.util.List;

import com.gp.common.core.result.Result;
import com.gp.system.domain.SysDictType;
import com.gp.system.service.SysDictTypeService;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "字典类型管理")
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController {

    @Autowired
    private SysDictTypeService dictTypeService;

    @Operation(summary = "字典类型列表")
    @GetMapping("/list")
    public Result<List<SysDictType>> list() {
        return Result.success(dictTypeService.listAll());
    }

    @Operation(summary = "字典类型树")
    @GetMapping("/tree")
    public Result<List<SysDictType>> tree() {
        return Result.success(dictTypeService.listAll());
    }

    @Operation(summary = "字典类型详情")
    @GetMapping("/{id}")
    public Result<SysDictType> getInfo(@PathVariable Long id) {
        return Result.success(dictTypeService.getById(id));
    }

    @Operation(summary = "新增字典类型")
    @PostMapping
    public Result<Void> add(@RequestBody SysDictType dictType) {
        if (!dictTypeService.checkDictTypeUnique(dictType)) {
            return Result.error("字典类型已存在");
        }
        dictTypeService.save(dictType);
        return Result.success();
    }

    @Operation(summary = "修改字典类型")
    @PutMapping
    public Result<Void> edit(@RequestBody SysDictType dictType) {
        if (!dictTypeService.checkDictTypeUnique(dictType)) {
            return Result.error("字典类型已存在");
        }
        dictTypeService.updateById(dictType);
        return Result.success();
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable List<Long> ids) {
        dictTypeService.removeByIds(ids);
        return Result.success();
    }

}