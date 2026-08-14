package com.gp.business.gen.domain;

import lombok.Data;

/**
 * 代码生成配置
 */
@Data
public class GenConfig {

    /** 数据库表名（从表列表选择） */
    private String tableName;

    /** 模块名（小写，作为包名/路由/接口前缀，如 teacher） */
    private String module;

    /** 实体类名（大驼峰，如 Teacher） */
    private String className;

    /** 功能名（中文，用于标题与菜单，如 教师管理） */
    private String functionName;

    /** 菜单ID起始值（生成 SQL 用，默认 2000 段） */
    private Integer menuId = 2000;

}
