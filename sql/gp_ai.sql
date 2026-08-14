-- ========================================
-- GP-Framework AI 智能助手模块（可重复执行）
-- 数据库: gp_framework
-- 内容: AI 会话表 / AI 消息表 + 菜单与角色授权
-- ========================================
USE gp_framework;

-- ========== AI 会话表 ==========
CREATE TABLE IF NOT EXISTS biz_ai_conversation (
  id          BIGINT(20)   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  user_id     BIGINT(20)   NOT NULL                 COMMENT '所属用户ID',
  title       VARCHAR(100) DEFAULT '新对话'          COMMENT '会话标题',
  remark      VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  create_by   VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
  create_time DATETIME     DEFAULT NULL             COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
  update_time DATETIME     DEFAULT NULL             COMMENT '更新时间',
  del_flag    CHAR(1)      DEFAULT '0'              COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (id),
  KEY idx_ai_conversation_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI会话表';

-- ========== AI 消息表 ==========
CREATE TABLE IF NOT EXISTS biz_ai_message (
  id              BIGINT(20)   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  conversation_id BIGINT(20)   NOT NULL                 COMMENT '所属会话ID',
  user_id         BIGINT(20)   NOT NULL                 COMMENT '所属用户ID',
  role            VARCHAR(20)  DEFAULT 'user'           COMMENT '消息角色(user用户 assistant助手)',
  content         MEDIUMTEXT                            COMMENT '消息内容',
  model           VARCHAR(50)  DEFAULT NULL             COMMENT '生成模型(演示模式为mock)',
  remark          VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  create_by       VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
  create_time     DATETIME     DEFAULT NULL             COMMENT '创建时间',
  update_by       VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
  update_time     DATETIME     DEFAULT NULL             COMMENT '更新时间',
  del_flag        CHAR(1)      DEFAULT '0'              COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (id),
  KEY idx_ai_message_conversation (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI消息表';

-- ========================================
-- 菜单数据（沿用扩展脚本的幂等写法，可重复执行）
-- ID 段说明： 1 首页 / 100x 系统管理 / 200x 学生管理 / 2100x 系统监控 / 300x AI 助手
-- ========================================

-- 一级目录: AI 助手
INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT 300, 'AI 助手', 0, 4, 'ai', NULL, 'M', '0', '0', '', 'ChatDotRound', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 300);

  -- AI助手菜单
  INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
  SELECT 301, 'AI助手', 300, 1, 'chat', 'business/ai/chat/index', 'C', '0', '0', 'ai:chat:list', 'ChatDotRound', 'admin', NOW()
  WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 301);

    INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
    SELECT 3011, '对话查询', 301, 1, '', '', 'F', '0', '0', 'ai:chat:query', '#', 'admin', NOW()
    WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 3011);

    INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
    SELECT 3012, '会话新增', 301, 2, '', '', 'F', '0', '0', 'ai:chat:add', '#', 'admin', NOW()
    WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 3012);

    INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
    SELECT 3013, '会话删除', 301, 3, '', '', 'F', '0', '0', 'ai:chat:delete', '#', 'admin', NOW()
    WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE id = 3013);

-- ========================================
-- 角色菜单关联
-- 超级管理员(1)：AI 助手全部权限
-- 普通角色(2)：可使用对话（不含删除）
-- ========================================
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 300 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 300);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 301 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 301);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 3011 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 3011);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 3012 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 3012);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 3013 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 3013);

INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, 300 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 300);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, 301 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 301);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, 3011 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 3011);
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, 3012 WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 3012);
