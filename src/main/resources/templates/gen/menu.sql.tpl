-- ========================================
-- ${functionName} 菜单与权限（可重复执行）
-- 生成于 ${date}，menuId 起始值 ${menuId}，如冲突请整体调整
-- ========================================
USE gp_framework;

-- 一级菜单: ${functionName}
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT ${menuId}, '${functionName}', 0, 3, '${module}', 'business/${module}/index', 'C', '0', '0', '${module}:${varName}:list', 'Document', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = ${menuId});

  -- 按钮权限
  INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
  SELECT ${menuId}1, '${functionName}查询', ${menuId}, 1, '', '', 'F', '0', '0', '${module}:${varName}:query', '#', 'admin', NOW()
  WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = ${menuId}1);

  INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
  SELECT ${menuId}2, '${functionName}新增', ${menuId}, 2, '', '', 'F', '0', '0', '${module}:${varName}:add', '#', 'admin', NOW()
  WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = ${menuId}2);

  INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
  SELECT ${menuId}3, '${functionName}修改', ${menuId}, 3, '', '', 'F', '0', '0', '${module}:${varName}:edit', '#', 'admin', NOW()
  WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = ${menuId}3);

  INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
  SELECT ${menuId}4, '${functionName}删除', ${menuId}, 4, '', '', 'F', '0', '0', '${module}:${varName}:delete', '#', 'admin', NOW()
  WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = ${menuId}4);

-- 角色授权：超级管理员(1) 全部；普通角色(2) 查询与新增
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, ${menuId} WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = ${menuId});
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, ${menuId}1 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = ${menuId}1);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, ${menuId}2 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = ${menuId}2);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, ${menuId}3 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = ${menuId}3);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, ${menuId}4 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = ${menuId}4);

INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, ${menuId} WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = ${menuId});
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, ${menuId}1 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = ${menuId}1);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, ${menuId}2 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = ${menuId}2);
