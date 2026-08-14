package com.gp.framework.controller;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gp.common.annotation.Log;
import com.gp.common.core.page.PageQuery;
import com.gp.common.core.page.PageResult;
import com.gp.common.core.result.Result;
import com.gp.common.enums.BusinessTypeEnum;
import com.gp.framework.domain.SysLogininfor;
import com.gp.framework.mapper.SysLogininforMapper;
import com.gp.framework.service.SysLogininforService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "登录日志")
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController {

    @Autowired
    private SysLogininforMapper logininforMapper;

    @Autowired
    private SysLogininforService logininforService;

    @Operation(summary = "登录日志分页查询")
    @GetMapping("/page")
    public Result<PageResult<SysLogininfor>> page(PageQuery pageQuery, SysLogininfor logininfor) {
        Page<SysLogininfor> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        List<SysLogininfor> list = logininforMapper.selectList(null);
        int start = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        int end = Math.min(start + pageQuery.getPageSize(), list.size());
        List<SysLogininfor> pageList = start < list.size() ? list.subList(start, end) : new java.util.ArrayList<>();
        return Result.success(new PageResult<>((long) list.size(), pageList));
    }

    @Operation(summary = "删除登录日志")
    @Log(title = "登录日志", operType = "3")
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable List<Long> ids) {
        logininforMapper.deleteBatchIds(ids);
        return Result.success();
    }

    @Operation(summary = "清空登录日志")
    @Log(title = "登录日志", operType = "6")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        logininforMapper.delete(null);
        return Result.success();
    }

    @Operation(summary = "解锁用户")
    @Log(title = "登录日志", operType = "0")
    @PutMapping("/unlock/{userId}")
    public Result<Void> unlock(@PathVariable Long userId) {
        logininforService.unlockUser(userId);
        return Result.success();
    }

}
