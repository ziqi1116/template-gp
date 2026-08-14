package com.gp.business.dashboard.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 数据大屏统计查询（聚合 SQL 直接写在注解中，均为只读查询）
 */
@Mapper
public interface DashboardMapper {

    /** 近 N 天每日新增用户数 */
    @Select("SELECT DATE_FORMAT(create_time, '%m-%d') AS date, COUNT(*) AS value " +
            "FROM sys_user WHERE del_flag = '0' AND create_time >= #{start} " +
            "GROUP BY DATE_FORMAT(create_time, '%m-%d')")
    List<Map<String, Object>> userTrend(@Param("start") String start);

    /** 近 N 天每日登录次数 */
    @Select("SELECT DATE_FORMAT(login_time, '%m-%d') AS date, COUNT(*) AS value " +
            "FROM sys_logininfor WHERE del_flag = '0' AND login_time >= #{start} " +
            "GROUP BY DATE_FORMAT(login_time, '%m-%d')")
    List<Map<String, Object>> loginTrend(@Param("start") String start);

    /** 各角色的用户数量分布（关联表无逻辑删除字段） */
    @Select("SELECT r.role_name AS name, COUNT(ur.user_id) AS value " +
            "FROM sys_role r " +
            "LEFT JOIN sys_user_role ur ON ur.role_id = r.id " +
            "WHERE r.del_flag = '0' GROUP BY r.id, r.role_name")
    List<Map<String, Object>> roleDistribution();

    /** 各班级学生数量 */
    @Select("SELECT IFNULL(NULLIF(class_name, ''), '未填写') AS name, COUNT(*) AS value " +
            "FROM biz_student WHERE del_flag = '0' GROUP BY name ORDER BY value DESC")
    List<Map<String, Object>> classDistribution();

    /** 学生性别分布 */
    @Select("SELECT IFNULL(gender, '2') AS name, COUNT(*) AS value " +
            "FROM biz_student WHERE del_flag = '0' GROUP BY name")
    List<Map<String, Object>> genderDistribution();

    /** 操作日志按业务类型统计 */
    @Select("SELECT IFNULL(NULLIF(oper_type, ''), '0') AS name, COUNT(*) AS value " +
            "FROM sys_oper_log WHERE del_flag = '0' GROUP BY name")
    List<Map<String, Object>> operTypeDistribution();

}
