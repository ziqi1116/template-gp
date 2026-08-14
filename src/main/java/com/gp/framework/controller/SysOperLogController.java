package com.gp.framework.controller;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gp.common.annotation.Log;
import com.gp.common.core.page.PageQuery;
import com.gp.common.core.page.PageResult;
import com.gp.common.core.result.Result;
import com.gp.framework.domain.SysOperLog;
import com.gp.framework.mapper.SysOperLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "操作日志")
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperLogController {

    @Autowired
    private SysOperLogMapper operLogMapper;

    @Operation(summary = "操作日志分页查询")
    @GetMapping("/page")
    public Result<PageResult<SysOperLog>> page(PageQuery pageQuery, SysOperLog operLog) {
        Page<SysOperLog> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        List<SysOperLog> list = operLogMapper.selectList(null);
        int start = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        int end = Math.min(start + pageQuery.getPageSize(), list.size());
        List<SysOperLog> pageList = start < list.size() ? list.subList(start, end) : new java.util.ArrayList<>();
        return Result.success(new PageResult<>((long) list.size(), pageList));
    }

    @Operation(summary = "删除操作日志")
    @Log(title = "操作日志", operType = "3")
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable List<Long> ids) {
        operLogMapper.deleteBatchIds(ids);
        return Result.success();
    }

    @Operation(summary = "清空操作日志")
    @Log(title = "操作日志", operType = "6")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        operLogMapper.delete(null);
        return Result.success();
    }

}
