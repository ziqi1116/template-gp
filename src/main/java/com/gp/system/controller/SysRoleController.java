package com.gp.system.controller;

import java.util.List;

import com.gp.common.annotation.Log;
import com.gp.common.core.result.Result;
import com.gp.system.domain.SysRole;
import com.gp.system.service.SysRoleService;
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

@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @Operation(summary = "角色列表")
    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return Result.success(roleService.listAll());
    }

    @Operation(summary = "角色详情")
    @GetMapping("/{id}")
    public Result<SysRole> getInfo(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @Operation(summary = "新增角色")
    @Log(title = "角色管理", operType = "1")
    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        if (!roleService.checkRoleKeyUnique(role)) {
            return Result.error("角色Key已存在");
        }
        roleService.save(role);
        return Result.success();
    }

    @Operation(summary = "修改角色")
    @Log(title = "角色管理", operType = "2")
    @PutMapping
    public Result<Void> edit(@RequestBody SysRole role) {
        if (!roleService.checkRoleKeyUnique(role)) {
            return Result.error("角色Key已存在");
        }
        roleService.updateById(role);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @Log(title = "角色管理", operType = "3")
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable List<Long> ids) {
        for (Long id : ids) {
            roleService.checkRoleAllowed(id);
        }
        roleService.removeByIds(ids);
        return Result.success();
    }

}
