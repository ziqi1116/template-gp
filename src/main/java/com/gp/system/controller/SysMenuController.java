package com.gp.system.controller;

import java.util.List;

import com.gp.common.core.result.Result;
import com.gp.system.domain.SysMenu;
import com.gp.system.domain.RouterVo;
import com.gp.system.service.SysMenuService;
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

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;

    @Operation(summary = "菜单列表")
    @GetMapping("/list")
    public Result<List<SysMenu>> list(SysMenu menu) {
        List<SysMenu> list = menuService.listAll();
        return Result.success(list);
    }

    @Operation(summary = "菜单树")
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        return Result.success(menuService.listMenuTree());
    }

    @Operation(summary = "菜单详情")
    @GetMapping("/{id}")
    public Result<SysMenu> getInfo(@PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    public Result<Void> add(@RequestBody SysMenu menu) {
        menuService.save(menu);
        return Result.success();
    }

    @Operation(summary = "修改菜单")
    @PutMapping
    public Result<Void> edit(@RequestBody SysMenu menu) {
        menuService.updateById(menu);
        return Result.success();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        if (menuService.hasChildByMenuId(id)) {
            return Result.error("存在子菜单,不允许删除");
        }
        menuService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "获取路由")
    @GetMapping("/getRouters")
    public Result<List<RouterVo>> getRouters() {
        Long userId = com.gp.common.utils.SecurityUtils.getUserId();
        boolean isAdmin = com.gp.common.utils.SecurityUtils.isAdmin();
        return Result.success(menuService.getRouters(userId, isAdmin));
    }

    @Operation(summary = "获取菜单权限")
    @GetMapping("/roleMenu/{roleId}")
    public Result<List<Long>> roleMenu(@PathVariable Long roleId) {
        return Result.success(menuService.selectMenuIdsByRoleId(roleId));
    }

    @Operation(summary = "分配菜单权限")
    @PutMapping("/roleMenu")
    public Result<Void> updateRoleMenu(@RequestBody RoleMenuBody body) {
        menuService.deleteRoleMenuByRoleId(body.getRoleId());
        menuService.batchRoleMenu(body.getRoleId(), body.getMenuIds());
        return Result.success();
    }

    public static class RoleMenuBody {

        private Long roleId;
        private List<Long> menuIds;

        public Long getRoleId() {
            return roleId;
        }

        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }

        public List<Long> getMenuIds() {
            return menuIds;
        }

        public void setMenuIds(List<Long> menuIds) {
            this.menuIds = menuIds;
        }

    }

}
