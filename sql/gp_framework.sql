-- ========================================
-- GP-Framework 基础表结构与初始数据
-- 数据库: gp_framework
-- ========================================

CREATE DATABASE IF NOT EXISTS gp_framework DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE gp_framework;

-- ========== 系统用户表 ==========
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id          BIGINT(20)   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  dept_id     BIGINT(20)   DEFAULT NULL             COMMENT '部门ID',
  user_name   VARCHAR(30)  NOT NULL                 COMMENT '用户名',
  nick_name   VARCHAR(30)  NOT NULL                 COMMENT '昵称',
  email       VARCHAR(50)  DEFAULT ''               COMMENT '邮箱',
  phone       VARCHAR(20)  DEFAULT ''               COMMENT '手机号',
  sex         CHAR(1)      DEFAULT '0'              COMMENT '性别(0男 1女)',
  password    VARCHAR(100) DEFAULT ''               COMMENT '密码',
  status      CHAR(1)      DEFAULT '0'              COMMENT '状态(0启用 1停用)',
  avatar      VARCHAR(256) DEFAULT ''               COMMENT '头像',
  login_ip    VARCHAR(128) DEFAULT ''               COMMENT '最后登录IP',
  login_date  DATETIME     DEFAULT NULL             COMMENT '最后登录时间',
  del_flag    CHAR(1)      DEFAULT '0'              COMMENT '删除标志(0存在 2删除)',
  remark      VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  create_by   VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
  create_time DATETIME     DEFAULT NULL             COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
  update_time DATETIME     DEFAULT NULL             COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_name (user_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ========== 系统角色表 ==========
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id          BIGINT(20)   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  role_name   VARCHAR(30)  NOT NULL                 COMMENT '角色名称',
  role_key    VARCHAR(100) NOT NULL                 COMMENT '角色标识',
  role_sort   INT(4)       NOT NULL                 COMMENT '排序',
  status      CHAR(1)      DEFAULT '0'              COMMENT '状态(0启用 1停用)',
  del_flag    CHAR(1)      DEFAULT '0'              COMMENT '删除标志',
  remark      VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  create_by   VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
  create_time DATETIME     DEFAULT NULL             COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
  update_time DATETIME     DEFAULT NULL             COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ========== 系统菜单表 ==========
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
  id          BIGINT(20)   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  menu_name   VARCHAR(50)  NOT NULL                 COMMENT '菜单名称',
  parent_id   BIGINT(20)   DEFAULT 0                COMMENT '父菜单ID',
  order_num   INT(4)       DEFAULT 0                COMMENT '排序',
  path        VARCHAR(200) DEFAULT ''               COMMENT '路由地址',
  component   VARCHAR(255) DEFAULT NULL             COMMENT '组件路径',
  query_param VARCHAR(255) DEFAULT NULL             COMMENT '路由参数',
  is_frame    INT(1)       DEFAULT 1                COMMENT '是否外链(0是 1否)',
  is_cache    INT(1)       DEFAULT 0                COMMENT '是否缓存(0缓存 1不缓存)',
  menu_type   CHAR(1)      DEFAULT ''               COMMENT '类型(M目录 C菜单 F按钮)',
  visible     CHAR(1)      DEFAULT '0'              COMMENT '显示(0显示 1隐藏)',
  status      CHAR(1)      DEFAULT '0'              COMMENT '状态(0启用 1停用)',
  perms       VARCHAR(100) DEFAULT NULL             COMMENT '权限标识',
  icon        VARCHAR(100) DEFAULT '#'              COMMENT '图标',
  del_flag    CHAR(1)      DEFAULT '0'              COMMENT '删除标志',
  remark      VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  create_by   VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
  create_time DATETIME     DEFAULT NULL             COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
  update_time DATETIME     DEFAULT NULL             COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ========== 系统部门表 ==========
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
  id          BIGINT(20)   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  parent_id   BIGINT(20)   DEFAULT 0                COMMENT '父部门ID',
  dept_name   VARCHAR(30)  DEFAULT ''               COMMENT '部门名称',
  order_num   INT(4)       DEFAULT 0                COMMENT '排序',
  leader      VARCHAR(20)  DEFAULT NULL             COMMENT '负责人',
  status      CHAR(1)      DEFAULT '0'              COMMENT '状态',
  del_flag    CHAR(1)      DEFAULT '0'              COMMENT '删除标志',
  create_by   VARCHAR(64)  DEFAULT '',
  create_time DATETIME     DEFAULT NULL,
  update_by   VARCHAR(64)  DEFAULT '',
  update_time DATETIME     DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ========== 用户-角色关联表 ==========
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
  user_id BIGINT(20) NOT NULL COMMENT '用户ID',
  role_id BIGINT(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ========== 角色-菜单关联表 ==========
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
  role_id BIGINT(20) NOT NULL COMMENT '角色ID',
  menu_id BIGINT(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ========================================
-- 初始数据
-- ========================================

-- 部门
INSERT INTO sys_dept VALUES (100, 0, '计算机学院', 0, '张院长', '0', '0', 'admin', NOW(), '', NULL);

-- 角色
INSERT INTO sys_role VALUES (1, '超级管理员', 'admin', 1, '0', '0', '超级管理员', 'admin', NOW(), '', NULL);
INSERT INTO sys_role VALUES (2, '普通角色', 'common', 2, '0', '0', '普通角色', 'admin', NOW(), '', NULL);

-- 用户 (密码: admin123 -> BCrypt加密)
INSERT INTO sys_user VALUES (1, 100, 'admin', '管理员', 'admin@gp.com', '13888888888', '0', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu.', '0', '/avatars/admin.jpg', '127.0.0.1', NOW(), '0', '管理员', 'admin', NOW(), '', NULL);
INSERT INTO sys_user VALUES (2, 100, 'gp', '测试用户', 'gp@gp.com', '13999999999', '1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu.', '0', '/avatars/gp.jpg', '127.0.0.1', NOW(), '0', '测试用户', 'admin', NOW(), '', NULL);

-- 用户-角色关联
INSERT INTO sys_user_role VALUES (1, 1);
INSERT INTO sys_user_role VALUES (2, 2);

-- ========================================
-- 菜单数据
-- ========================================

-- 一级目录: 首页
INSERT INTO sys_menu VALUES (1, '首页', 0, 1, 'dashboard', NULL, NULL, 1, 0, 'C', '0', '0', 'dashboard:view', 'Dashboard', '0', NULL, 'admin', NOW(), '', NULL);

-- 一级目录: 系统管理
INSERT INTO sys_menu VALUES (100, '系统管理', 0, 2, 'system', NULL, NULL, 1, 0, 'M', '0', '0', '', 'Setting', '0', NULL, 'admin', NOW(), '', NULL);
  -- 用户管理菜单
  INSERT INTO sys_menu VALUES (101, '用户管理', 100, 1, 'user', 'system/user/index', NULL, 1, 0, 'C', '0', '0', 'system:user:list', 'User', '0', NULL, 'admin', NOW(), '', NULL);
    INSERT INTO sys_menu VALUES (1011, '用户查询', 101, 1, '', '', NULL, 1, 0, 'F', '0', '0', 'system:user:query', '#', '0', NULL, 'admin', NOW(), '', NULL);
    INSERT INTO sys_menu VALUES (1012, '用户新增', 101, 2, '', '', NULL, 1, 0, 'F', '0', '0', 'system:user:add', '#', '0', NULL, 'admin', NOW(), '', NULL);
    INSERT INTO sys_menu VALUES (1013, '用户修改', 101, 3, '', '', NULL, 1, 0, 'F', '0', '0', 'system:user:edit', '#', '0', NULL, 'admin', NOW(), '', NULL);
    INSERT INTO sys_menu VALUES (1014, '用户删除', 101, 4, '', '', NULL, 1, 0, 'F', '0', '0', 'system:user:delete', '#', '0', NULL, 'admin', NOW(), '', NULL);

-- 一级目录: 学生管理
INSERT INTO sys_menu VALUES (200, '学生管理', 0, 3, 'business', NULL, NULL, 1, 0, 'M', '0', '0', '', 'Reading', '0', NULL, 'admin', NOW(), '', NULL);
  -- 学生列表菜单
  INSERT INTO sys_menu VALUES (201, '学生列表', 200, 1, 'student', 'business/student/index', NULL, 1, 0, 'C', '0', '0', 'business:student:list', 'User', '0', NULL, 'admin', NOW(), '', NULL);
    INSERT INTO sys_menu VALUES (2011, '学生查询', 201, 1, '', '', NULL, 1, 0, 'F', '0', '0', 'business:student:query', '#', '0', NULL, 'admin', NOW(), '', NULL);
    INSERT INTO sys_menu VALUES (2012, '学生新增', 201, 2, '', '', NULL, 1, 0, 'F', '0', '0', 'business:student:add', '#', '0', NULL, 'admin', NOW(), '', NULL);
    INSERT INTO sys_menu VALUES (2013, '学生修改', 201, 3, '', '', NULL, 1, 0, 'F', '0', '0', 'business:student:edit', '#', '0', NULL, 'admin', NOW(), '', NULL);
    INSERT INTO sys_menu VALUES (2014, '学生删除', 201, 4, '', '', NULL, 1, 0, 'F', '0', '0', 'business:student:delete', '#', '0', NULL, 'admin', NOW(), '', NULL);

-- 角色-菜单关联 (admin 角色拥有所有菜单)
INSERT INTO sys_role_menu VALUES (1, 1);
INSERT INTO sys_role_menu VALUES (1, 100);
INSERT INTO sys_role_menu VALUES (1, 101);
INSERT INTO sys_role_menu VALUES (1, 1011);
INSERT INTO sys_role_menu VALUES (1, 1012);
INSERT INTO sys_role_menu VALUES (1, 1013);
INSERT INTO sys_role_menu VALUES (1, 1014);
INSERT INTO sys_role_menu VALUES (1, 200);
INSERT INTO sys_role_menu VALUES (1, 201);
INSERT INTO sys_role_menu VALUES (1, 2011);
INSERT INTO sys_role_menu VALUES (1, 2012);
INSERT INTO sys_role_menu VALUES (1, 2013);
INSERT INTO sys_role_menu VALUES (1, 2014);

-- common 角色拥有首页和学生管理
INSERT INTO sys_role_menu VALUES (2, 1);
INSERT INTO sys_role_menu VALUES (2, 200);
INSERT INTO sys_role_menu VALUES (2, 201);
INSERT INTO sys_role_menu VALUES (2, 2011);
