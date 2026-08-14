-- ========================================
-- GP-Framework 代码生成器菜单（可重复执行）
-- 数据库: gp_framework
-- 说明: 开发工具，仅超级管理员可见
-- ========================================
USE gp_framework;

INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT 320, '代码生成', 0, 6, 'gen', 'business/gen/index', 'C', '0', '0', 'gen:tool:view', 'MagicStick', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 320);

-- 仅超级管理员(1)授权
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 320 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 320);
