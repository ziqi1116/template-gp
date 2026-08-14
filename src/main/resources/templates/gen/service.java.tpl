package com.gp.business.${module}.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.business.${module}.domain.${className};
import com.gp.business.${module}.mapper.${className}Mapper;
import com.gp.common.core.page.PageQuery;
import com.gp.common.core.page.PageResult;
import org.springframework.stereotype.Service;

/**
 * ${functionName} Service
 *
 * 生成于 ${date}
 */
@Service
public class ${className}Service extends ServiceImpl<${className}Mapper, ${className}> {

    /** 分页查询（支持${searchNote}模糊搜索） */
    public PageResult<${className}> pageList(PageQuery pageQuery, ${className} query) {
        LambdaQueryWrapper<${className}> wrapper = new LambdaQueryWrapper<>();
${queryConditions}
        wrapper.orderByDesc(${className}::getId);

        Page<${className}> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<${className}> result = this.page(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

}
