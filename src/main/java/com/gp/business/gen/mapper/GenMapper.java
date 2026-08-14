package com.gp.business.gen.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 代码生成器：读取 MySQL 元数据（information_schema）
 */
@Mapper
public interface GenMapper {

    /** 当前库中所有业务表（排除 sys_ 前缀系统表） */
    @Select("SELECT TABLE_NAME AS name, IFNULL(TABLE_COMMENT, '') AS comment " +
            "FROM information_schema.TABLES " +
            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_TYPE = 'BASE TABLE' " +
            "AND TABLE_NAME NOT LIKE 'sys\\\\_%' " +
            "ORDER BY TABLE_NAME")
    List<Map<String, Object>> selectTables();

    /** 某张表的字段元数据（名称 / 类型 / 注释） */
    @Select("SELECT COLUMN_NAME AS name, DATA_TYPE AS dataType, IFNULL(COLUMN_COMMENT, '') AS comment " +
            "FROM information_schema.COLUMNS " +
            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = #{table} " +
            "ORDER BY ORDINAL_POSITION")
    List<Map<String, Object>> selectColumns(@Param("table") String table);

}
