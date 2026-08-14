-- ========================================
-- GP-Framework 数据大屏模块（可重复执行）
-- 数据库: gp_framework
-- 内容: 数据大屏菜单与角色授权（页面数据由 /dashboard/screen 接口实时统计，无需建表）
-- ========================================
USE gp_framework;

-- 一级菜单: 数据大屏（根级菜单，component 指向大屏页面，登录即可见）
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT 310, '数据大屏', 0, 2, 'screen', 'dashboard/screen/index', 'C', '0', '0', 'dashboard:screen:view', 'DataLine', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 310);

-- 角色授权：超级管理员(1) 与 普通角色(2) 均可见大屏（展示型页面）
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 310 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 310);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, 310 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 310);
