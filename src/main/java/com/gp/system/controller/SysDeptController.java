package com.gp.system.controller;

import java.util.List;

import com.gp.common.core.result.Result;
import com.gp.system.domain.SysDept;
import com.gp.system.service.SysDeptService;
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

@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    @Autowired
    private SysDeptService deptService;

    @Operation(summary = "部门列表")
    @GetMapping("/list")
    public Result<List<SysDept>> list(SysDept dept) {
        return Result.success(deptService.listAll());
    }

    @Operation(summary = "部门树")
    @GetMapping("/tree")
    public Result<List<SysDept>> tree() {
        return Result.success(deptService.listDeptTree());
    }

    @Operation(summary = "部门详情")
    @GetMapping("/{id}")
    public Result<SysDept> getInfo(@PathVariable Long id) {
        return Result.success(deptService.getById(id));
    }

    @Operation(summary = "新增部门")
    @PostMapping
    public Result<Void> add(@RequestBody SysDept dept) {
        deptService.save(dept);
        return Result.success();
    }

    @Operation(summary = "修改部门")
    @PutMapping
    public Result<Void> edit(@RequestBody SysDept dept) {
        deptService.updateById(dept);
        return Result.success();
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        if (deptService.hasChildByDeptId(id)) {
            return Result.error("存在子部门,不允许删除");
        }
        deptService.removeById(id);
        return Result.success();
    }

}
