-- ========================================
-- GP-Framework 扩展脚本（字典 / 日志 / 菜单扩展）
-- 数据库: gp_framework
-- 说明: 可重复执行，新增字段和菜单数据
-- ========================================
USE gp_framework;

-- ========================================
-- 1) 为继承 BaseEntity 的表补齐 del_flag 字段
-- ========================================

DROP PROCEDURE IF EXISTS _add_column_if_missing;
DELIMITER //
CREATE PROCEDURE _add_column_if_missing(
    IN p_table VARCHAR(64),
    IN p_column VARCHAR(64),
    IN p_definition TEXT
)
BEGIN
    DECLARE cnt INT;
    SELECT COUNT(*) INTO cnt
      FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = p_table
       AND COLUMN_NAME = p_column;
    IF cnt = 0 THEN
        SET @ddl_sql = CONCAT('ALTER TABLE `', p_table, '` ADD COLUMN ', p_definition);
        PREPARE stmt FROM @ddl_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //
DELIMITER ;

CALL _add_column_if_missing('sys_dict_type', 'del_flag',
    'del_flag CHAR(1) DEFAULT ''0'' COMMENT ''删除标志(0存在 2删除)'' AFTER update_time');
CALL _add_column_if_missing('sys_dict_data', 'del_flag',
    'del_flag CHAR(1) DEFAULT ''0'' COMMENT ''删除标志(0存在 2删除)'' AFTER update_time');
CALL _add_column_if_missing('sys_oper_log', 'del_flag',
    'del_flag CHAR(1) DEFAULT ''0'' COMMENT ''删除标志(0存在 2删除)'' AFTER update_time');
CALL _add_column_if_missing('sys_logininfor', 'del_flag',
    'del_flag CHAR(1) DEFAULT ''0'' COMMENT ''删除标志(0存在 2删除)'' AFTER update_time');

-- ========================================
-- 2) 为 sys_dept 补齐前端页面所需字段
-- ========================================

CALL _add_column_if_missing('sys_dept', 'phone',
    'phone VARCHAR(20) DEFAULT '''' COMMENT ''联系电话'' AFTER leader');
CALL _add_column_if_missing('sys_dept', 'email',
    'email VARCHAR(128) DEFAULT '''' COMMENT ''邮箱'' AFTER phone');
CALL _add_column_if_missing('sys_dept', 'remark',
    'remark VARCHAR(500) DEFAULT NULL COMMENT ''备注'' AFTER update_time');

DROP PROCEDURE IF EXISTS _add_column_if_missing;

-- ========================================
-- 3) 初始化字典数据（若尚未存在）
-- ========================================

INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by, create_time)
SELECT 1, '用户性别', 'sys_user_sex', '0', '用户性别列表', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE id = 1);
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by, create_time)
SELECT 2, '菜单状态', 'sys_show_hide', '0', '菜单状态列表', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE id = 2);
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by, create_time)
SELECT 3, '系统开关', 'sys_normal_disable', '0', '系统开关列表', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE id = 3);
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by, create_time)
SELECT 4, '操作类型', 'sys_oper_type', '0', '操作类型列表', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE id = 4);

INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 1, 'sys_user_sex', '男', '0', 'primary', 'Y', '0', '性别男', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 2, 'sys_user_sex', '女', '1', 'danger', 'N', '0', '性别女', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 2);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 3, 'sys_user_sex', '未知', '2', 'info', 'N', '0', '性别未知', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 3);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 4, 'sys_show_hide', '显示', '0', 'primary', 'Y', '0', '显示菜单', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 4);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 5, 'sys_show_hide', '隐藏', '1', 'danger', 'N', '0', '隐藏菜单', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 5);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 6, 'sys_normal_disable', '正常', '0', 'success', 'Y', '0', '正常状态', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 6);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 7, 'sys_normal_disable', '停用', '1', 'danger', 'N', '0', '停用状态', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 7);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 8, 'sys_oper_type', '新增', '1', 'success', 'N', '0', '新增操作', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 8);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 9, 'sys_oper_type', '修改', '2', 'warning', 'N', '0', '修改操作', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 9);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 10, 'sys_oper_type', '删除', '3', 'danger', 'N', '0', '删除操作', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 10);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 11, 'sys_oper_type', '授权', '4', 'info', 'N', '0', '授权操作', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 11);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 12, 'sys_oper_type', '导出', '5', 'warning', 'N', '0', '导出操作', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 12);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 13, 'sys_oper_type', '导入', '6', 'warning', 'N', '0', '导入操作', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 13);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time)
SELECT 14, 'sys_oper_type', '其他', '0', 'info', 'Y', '0', '其他操作', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE id = 14);

-- ========================================
-- 4) 菜单扩展：角色/菜单/部门/字典 + 系统监控
--    父级 ID 说明：
--      100 系统管理  101 用户管理
--      200 业务管理(学生)
-- ========================================

-- 系统管理 > 角色管理
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT 102, '角色管理', 100, 2, 'role', 'system/role/index', 'C', '0', '0', 'system:role:list', 'UserFilled', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 102);

-- 系统管理 > 菜单管理
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT 103, '菜单管理', 100, 3, 'menu', 'system/menu/index', 'C', '0', '0', 'system:menu:list', 'Menu', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 103);

-- 系统管理 > 部门管理
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT 104, '部门管理', 100, 4, 'dept', 'system/dept/index', 'C', '0', '0', 'system:dept:list', 'OfficeBuilding', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 104);

-- 系统管理 > 字典管理
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT 105, '字典管理', 100, 5, 'dict', '', 'M', '0', '0', '', 'Collection', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 105);

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT 1051, '字典类型', 105, 1, 'type', 'system/dict/type/index', 'C', '0', '0', 'system:dict:list', 'list', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 1051);

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT 1052, '字典数据', 105, 2, 'data', 'system/dict/data/index', 'C', '0', '0', 'system:dict:list', 'list', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 1052);

-- 系统监控（一级目录）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT 2100, '系统监控', 0, 5, 'monitor', '', 'M', '0', '0', '', 'Monitor', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 2100);

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT 2101, '操作日志', 2100, 1, 'operlog', 'monitor/operlog/index', 'C', '0', '0', 'monitor:operlog:list', 'Document', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 2101);

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT 2102, '登录日志', 2100, 2, 'logininfor', 'monitor/logininfor/index', 'C', '0', '0', 'monitor:logininfor:list', 'Document', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 2102);

-- ========================================
-- 5) 角色菜单关联（超级管理员拥有全部新增菜单）
-- ========================================

INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 102 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 102);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 103 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 103);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 104 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 104);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 105 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 105);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 1051 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 1051);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 1052 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 1052);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2100 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2100);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2101 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2101);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2102 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2102);

-- 普通角色仅开放字典和业务相关菜单
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, 105 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 105);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, 1051 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 1051);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, 1052 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 1052);
