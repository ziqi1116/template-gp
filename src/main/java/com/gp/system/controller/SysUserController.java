package com.gp.system.controller;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gp.common.core.page.PageQuery;
import com.gp.common.core.page.PageResult;
import com.gp.common.core.result.Result;
import com.gp.common.utils.SecurityUtils;
import com.gp.system.domain.SysUser;
import com.gp.system.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "用户分页查询")
    @GetMapping("/page")
    public Result<PageResult<SysUser>> page(PageQuery pageQuery, SysUser user) {
        Page<SysUser> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        List<SysUser> list = userMapper.selectUserList(user);
        int start = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        int end = Math.min(start + pageQuery.getPageSize(), list.size());
        List<SysUser> pageList = start < list.size() ? list.subList(start, end) : new java.util.ArrayList<>();
        return Result.success(new PageResult<>((long) list.size(), pageList));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public Result<SysUser> getInfo(@PathVariable Long id) {
        SysUser user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        user.setPassword(passwordEncoder.encode("123456"));
        userMapper.insert(user);
        return Result.success();
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public Result<Void> edit(@RequestBody SysUser user) {
        user.setPassword(null);
        userMapper.updateById(user);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable List<Long> ids) {
        userMapper.deleteBatchIds(ids);
        return Result.success();
    }

    @Operation(summary = "修改状态")
    @PutMapping("/status")
    public Result<Void> changeStatus(@RequestBody SysUser user) {
        SysUser update = new SysUser();
        update.setId(user.getId());
        update.setStatus(user.getStatus());
        userMapper.updateById(update);
        return Result.success();
    }

    @Operation(summary = "当前用户修改密码")
    @PutMapping("/profile/updatePwd")
    public Result<Void> updatePwd(@RequestBody java.util.Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.length() < 6) {
            return Result.error("参数错误，新密码长度至少6位");
        }
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }
        SysUser update = new SysUser();
        update.setId(userId);
        update.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(update);
        return Result.success();
    }

}
