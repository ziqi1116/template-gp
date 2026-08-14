package com.gp.business.${module}.controller;

import java.util.List;

import com.gp.business.${module}.domain.${className};
import com.gp.business.${module}.service.${className}Service;
import com.gp.common.annotation.Log;
import com.gp.common.core.page.PageQuery;
import com.gp.common.core.page.PageResult;
import com.gp.common.core.result.Result;
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

/**
 * ${functionName} Controller
 *
 * 生成于 ${date}
 */
@Tag(name = "${functionName}")
@RestController
@RequestMapping("/${module}")
public class ${className}Controller {

    @Autowired
    private ${className}Service ${varName}Service;

    @Operation(summary = "${functionName}分页查询")
    @GetMapping("/page")
    public Result<PageResult<${className}>> page(PageQuery pageQuery, ${className} query) {
        return Result.success(${varName}Service.pageList(pageQuery, query));
    }

    @Operation(summary = "${functionName}详情")
    @GetMapping("/{id}")
    public Result<${className}> getInfo(@PathVariable Long id) {
        return Result.success(${varName}Service.getById(id));
    }

    @Operation(summary = "新增${functionName}")
    @Log(title = "${functionName}", operType = "1")
    @PostMapping
    public Result<Void> add(@RequestBody ${className} ${varName}) {
        ${varName}Service.save(${varName});
        return Result.success();
    }

    @Operation(summary = "修改${functionName}")
    @Log(title = "${functionName}", operType = "2")
    @PutMapping
    public Result<Void> edit(@RequestBody ${className} ${varName}) {
        ${varName}Service.updateById(${varName});
        return Result.success();
    }

    @Operation(summary = "删除${functionName}")
    @Log(title = "${functionName}", operType = "3")
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable List<Long> ids) {
        ${varName}Service.removeByIds(ids);
        return Result.success();
    }

}
