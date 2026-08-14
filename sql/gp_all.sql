-- ============================================================
-- GP-Framework 完整数据库脚本（一键初始化）
-- 数据库: gp_framework
-- 包含: 系统基础表 + 业务表 + 字典/日志 + AI模块 + 数据大屏 + 代码生成器
-- 使用: mysql -uroot -p < gp_all.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS gp_framework DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE gp_framework;

-- ============================================================
-- 一、系统基础表
-- ============================================================

-- 系统用户表
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

-- 系统角色表
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

-- 系统菜单表
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

-- 系统部门表
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
  id          BIGINT(20)   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  parent_id   BIGINT(20)   DEFAULT 0                COMMENT '父部门ID',
  dept_name   VARCHAR(30)  DEFAULT ''               COMMENT '部门名称',
  order_num   INT(4)       DEFAULT 0                COMMENT '排序',
  leader      VARCHAR(20)  DEFAULT NULL             COMMENT '负责人',
  phone       VARCHAR(20)  DEFAULT ''               COMMENT '联系电话',
  email       VARCHAR(128) DEFAULT ''               COMMENT '邮箱',
  status      CHAR(1)      DEFAULT '0'              COMMENT '状态',
  del_flag    CHAR(1)      DEFAULT '0'              COMMENT '删除标志',
  remark      VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  create_by   VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
  create_time DATETIME     DEFAULT NULL             COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
  update_time DATETIME     DEFAULT NULL             COMMENT '更新时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 用户-角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
  user_id BIGINT(20) NOT NULL COMMENT '用户ID',
  role_id BIGINT(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色-菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
  role_id BIGINT(20) NOT NULL COMMENT '角色ID',
  menu_id BIGINT(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 字典类型表
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
  id          BIGINT(20)   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  dict_name   VARCHAR(100) DEFAULT ''               COMMENT '字典名称',
  dict_type   VARCHAR(100) DEFAULT ''               COMMENT '字典类型',
  status      CHAR(1)      DEFAULT '0'              COMMENT '状态(0启用 1停用)',
  remark      VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  create_by   VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
  create_time DATETIME     DEFAULT NULL             COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
  update_time DATETIME     DEFAULT NULL             COMMENT '更新时间',
  del_flag    CHAR(1)      DEFAULT '0'              COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (id),
  UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 字典数据表
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
  id          BIGINT(20)   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  dict_type   VARCHAR(100) DEFAULT ''               COMMENT '字典类型',
  dict_label  VARCHAR(100) DEFAULT ''               COMMENT '字典标签',
  dict_value  VARCHAR(100) DEFAULT ''               COMMENT '字典键值',
  css_class   VARCHAR(100) DEFAULT NULL             COMMENT '样式属性',
  list_class  VARCHAR(100) DEFAULT NULL             COMMENT '表格样式',
  is_default  CHAR(1)      DEFAULT 'N'              COMMENT '是否默认(Y是 N否)',
  status      CHAR(1)      DEFAULT '0'              COMMENT '状态(0启用 1停用)',
  remark      VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  create_by   VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
  create_time DATETIME     DEFAULT NULL             COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
  update_time DATETIME     DEFAULT NULL             COMMENT '更新时间',
  del_flag    CHAR(1)      DEFAULT '0'              COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- 操作日志表
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
  id          BIGINT(20)   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  title       VARCHAR(50)  DEFAULT ''               COMMENT '模块标题',
  oper_type   VARCHAR(20)  DEFAULT '0'              COMMENT '业务类型(0其他 1新增 2修改 3删除 4导出 5导入 6清空)',
  oper_url    VARCHAR(255) DEFAULT ''               COMMENT '请求URL',
  oper_method VARCHAR(200) DEFAULT ''               COMMENT '请求方法',
  oper_name   VARCHAR(50)  DEFAULT ''               COMMENT '操作人员',
  oper_ip     VARCHAR(128) DEFAULT ''               COMMENT '主机地址',
  oper_param  VARCHAR(2000) DEFAULT ''              COMMENT '请求参数',
  json_result VARCHAR(2000) DEFAULT ''              COMMENT '返回参数',
  status      CHAR(1)      DEFAULT '0'              COMMENT '状态(0正常 1异常)',
  error_msg   VARCHAR(2000) DEFAULT ''              COMMENT '错误消息',
  oper_time   DATETIME     DEFAULT NULL             COMMENT '操作时间',
  cost_time   BIGINT(20)   DEFAULT 0                COMMENT '消耗时间(毫秒)',
  remark      VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  create_by   VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
  create_time DATETIME     DEFAULT NULL             COMMENT '创建时间',
  update_by   VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
  update_time DATETIME     DEFAULT NULL             COMMENT '更新时间',
  del_flag    CHAR(1)      DEFAULT '0'              COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 登录日志表
DROP TABLE IF EXISTS sys_logininfor;
CREATE TABLE sys_logininfor (
  id              BIGINT(20)   NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  user_name       VARCHAR(50)  DEFAULT ''               COMMENT '用户账号',
  ipaddr          VARCHAR(128) DEFAULT ''               COMMENT '登录IP地址',
  login_location  VARCHAR(255) DEFAULT ''               COMMENT '登录地点',
  browser         VARCHAR(50)  DEFAULT ''               COMMENT '浏览器类型',
  os              VARCHAR(50)  DEFAULT ''               COMMENT '操作系统',
  status          CHAR(1)      DEFAULT '0'              COMMENT '状态(0成功 1失败)',
  msg             VARCHAR(255) DEFAULT ''               COMMENT '提示消息',
  login_time      DATETIME     DEFAULT NULL             COMMENT '登录时间',
  remark          VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  create_by       VARCHAR(64)  DEFAULT ''               COMMENT '创建者',
  create_time     DATETIME     DEFAULT NULL             COMMENT '创建时间',
  update_by       VARCHAR(64)  DEFAULT ''               COMMENT '更新者',
  update_time     DATETIME     DEFAULT NULL             COMMENT '更新时间',
  del_flag        CHAR(1)      DEFAULT '0'              COMMENT '删除标志(0存在 2删除)',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ============================================================
-- 二、业务表
-- ============================================================

-- 学生信息表
DROP TABLE IF EXISTS biz_student;
CREATE TABLE biz_student (
  id            BIGINT(20)    NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
  student_no    VARCHAR(32)   NOT NULL                 COMMENT '学号',
  student_name  VARCHAR(64)   NOT NULL                 COMMENT '学生姓名',
  gender        CHAR(1)       DEFAULT '0'              COMMENT '性别(0男 1女 2未知)',
  phone         VARCHAR(20)   DEFAULT NULL             COMMENT '联系电话',
  email         VARCHAR(128)  DEFAULT NULL             COMMENT '邮箱',
  class_name    VARCHAR(64)   DEFAULT NULL             COMMENT '班级名称',
  enroll_date   DATE          DEFAULT NULL             COMMENT '入学日期',
  avatar        VARCHAR(255)  DEFAULT NULL             COMMENT '头像',
  address       VARCHAR(255)  DEFAULT NULL             COMMENT '地址',
  status        CHAR(1)       DEFAULT '0'              COMMENT '状态(0启用 1停用)',
  dept_id       BIGINT(20)    DEFAULT NULL             COMMENT '部门ID',
  remark        VARCHAR(500)  DEFAULT NULL             COMMENT '备注',
  del_flag      CHAR(1)       DEFAULT '0'              COMMENT '删除标志',
  create_by     VARCHAR(64)   DEFAULT ''               COMMENT '创建者',
  create_time   DATETIME      DEFAULT NULL             COMMENT '创建时间',
  update_by     VARCHAR(64)   DEFAULT ''               COMMENT '更新者',
  update_time   DATETIME      DEFAULT NULL             COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_student_no (student_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- AI会话表
DROP TABLE IF EXISTS biz_ai_conversation;
CREATE TABLE biz_ai_conversation (
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

-- AI消息表
DROP TABLE IF EXISTS biz_ai_message;
CREATE TABLE biz_ai_message (
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

-- ============================================================
-- 三、初始数据
-- ============================================================

-- 部门
INSERT INTO sys_dept VALUES (100, 0, '计算机学院', 0, '张院长', '', '', '0', '0', NULL, 'admin', NOW(), '', NULL);

-- 角色
INSERT INTO sys_role VALUES (1, '超级管理员', 'admin', 1, '0', '0', '超级管理员', 'admin', NOW(), '', NULL);
INSERT INTO sys_role VALUES (2, '普通角色', 'common', 2, '0', '0', '普通角色', 'admin', NOW(), '', NULL);

-- 用户 (密码: admin123 -> BCrypt加密)
INSERT INTO sys_user VALUES (1, 100, 'admin', '管理员', 'admin@gp.com', '13888888888', '0', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu.', '0', '/avatars/admin.jpg', '127.0.0.1', NOW(), '0', '管理员', 'admin', NOW(), '', NULL);
INSERT INTO sys_user VALUES (2, 100, 'gp', '测试用户', 'gp@gp.com', '13999999999', '1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu.', '0', '/avatars/gp.jpg', '127.0.0.1', NOW(), '0', '测试用户', 'admin', NOW(), '', NULL);

-- 用户-角色关联
INSERT INTO sys_user_role VALUES (1, 1);
INSERT INTO sys_user_role VALUES (2, 2);

-- 学生示例数据
INSERT INTO biz_student (student_no, student_name, gender, phone, email, class_name, status, remark, create_by, create_time) VALUES
('2024001', '张三', '0', '13800000001', 'zhangsan@gp.com', '计算机2201', '0', '学习委员', 'admin', NOW()),
('2024002', '李四', '1', '13800000002', 'lisi@gp.com', '计算机2201', '0', '班长', 'admin', NOW()),
('2024003', '王五', '0', '13800000003', 'wangwu@gp.com', '计算机2202', '0', NULL, 'admin', NOW()),
('2024004', '赵六', '1', '13800000004', 'zhaoliu@gp.com', '软件2201', '0', '文艺委员', 'admin', NOW()),
('2024005', '孙七', '0', '13800000005', 'sunqi@gp.com', '软件2201', '1', '休学', 'admin', NOW());

-- ============================================================
-- 四、字典数据
-- ============================================================

INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by, create_time) VALUES
(1, '用户性别', 'sys_user_sex', '0', '用户性别列表', 'admin', NOW()),
(2, '菜单状态', 'sys_show_hide', '0', '菜单状态列表', 'admin', NOW()),
(3, '系统开关', 'sys_normal_disable', '0', '系统开关列表', 'admin', NOW()),
(4, '操作类型', 'sys_oper_type', '0', '操作类型列表', 'admin', NOW());

INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, list_class, is_default, status, remark, create_by, create_time) VALUES
(1,  'sys_user_sex',         '男',   '0', 'primary', 'Y', '0', '性别男',   'admin', NOW()),
(2,  'sys_user_sex',         '女',   '1', 'danger',  'N', '0', '性别女',   'admin', NOW()),
(3,  'sys_user_sex',         '未知', '2', 'info',    'N', '0', '性别未知', 'admin', NOW()),
(4,  'sys_show_hide',        '显示', '0', 'primary', 'Y', '0', '显示菜单', 'admin', NOW()),
(5,  'sys_show_hide',        '隐藏', '1', 'danger',  'N', '0', '隐藏菜单', 'admin', NOW()),
(6,  'sys_normal_disable',   '正常', '0', 'success', 'Y', '0', '正常状态', 'admin', NOW()),
(7,  'sys_normal_disable',   '停用', '1', 'danger',  'N', '0', '停用状态', 'admin', NOW()),
(8,  'sys_oper_type',        '新增', '1', 'success', 'N', '0', '新增操作', 'admin', NOW()),
(9,  'sys_oper_type',        '修改', '2', 'warning', 'N', '0', '修改操作', 'admin', NOW()),
(10, 'sys_oper_type',        '删除', '3', 'danger',  'N', '0', '删除操作', 'admin', NOW()),
(11, 'sys_oper_type',        '授权', '4', 'info',    'N', '0', '授权操作', 'admin', NOW()),
(12, 'sys_oper_type',        '导出', '5', 'warning', 'N', '0', '导出操作', 'admin', NOW()),
(13, 'sys_oper_type',        '导入', '6', 'warning', 'N', '0', '导入操作', 'admin', NOW()),
(14, 'sys_oper_type',        '其他', '0', 'info',    'Y', '0', '其他操作', 'admin', NOW());

-- ============================================================
-- 五、菜单数据
-- ============================================================

-- 首页
INSERT INTO sys_menu VALUES (1, '首页', 0, 1, 'dashboard', NULL, NULL, 1, 0, 'C', '0', '0', 'dashboard:view', 'Dashboard', '0', NULL, 'admin', NOW(), '', NULL);

-- 系统管理
INSERT INTO sys_menu VALUES (100, '系统管理', 0, 2, 'system', NULL, NULL, 1, 0, 'M', '0', '0', '', 'Setting', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (101, '用户管理', 100, 1, 'user', 'system/user/index', NULL, 1, 0, 'C', '0', '0', 'system:user:list', 'User', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (1011, '用户查询', 101, 1, '', '', NULL, 1, 0, 'F', '0', '0', 'system:user:query', '#', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (1012, '用户新增', 101, 2, '', '', NULL, 1, 0, 'F', '0', '0', 'system:user:add', '#', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (1013, '用户修改', 101, 3, '', '', NULL, 1, 0, 'F', '0', '0', 'system:user:edit', '#', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (1014, '用户删除', 101, 4, '', '', NULL, 1, 0, 'F', '0', '0', 'system:user:delete', '#', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (102, '角色管理', 100, 2, 'role', 'system/role/index', NULL, 1, 0, 'C', '0', '0', 'system:role:list', 'UserFilled', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (103, '菜单管理', 100, 3, 'menu', 'system/menu/index', NULL, 1, 0, 'C', '0', '0', 'system:menu:list', 'Menu', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (104, '部门管理', 100, 4, 'dept', 'system/dept/index', NULL, 1, 0, 'C', '0', '0', 'system:dept:list', 'OfficeBuilding', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (105, '字典管理', 100, 5, 'dict', '', NULL, 1, 0, 'M', '0', '0', '', 'Collection', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (1051, '字典类型', 105, 1, 'type', 'system/dict/type/index', NULL, 1, 0, 'C', '0', '0', 'system:dict:list', 'list', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (1052, '字典数据', 105, 2, 'data', 'system/dict/data/index', NULL, 1, 0, 'C', '0', '0', 'system:dict:list', 'list', '0', NULL, 'admin', NOW(), '', NULL);

-- 数据大屏
INSERT INTO sys_menu VALUES (310, '数据大屏', 0, 2, 'screen', 'dashboard/screen/index', NULL, 1, 0, 'C', '0', '0', 'dashboard:screen:view', 'DataLine', '0', NULL, 'admin', NOW(), '', NULL);

-- 学生管理
INSERT INTO sys_menu VALUES (200, '学生管理', 0, 3, 'business', NULL, NULL, 1, 0, 'M', '0', '0', '', 'Reading', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (201, '学生列表', 200, 1, 'student', 'business/student/index', NULL, 1, 0, 'C', '0', '0', 'business:student:list', 'User', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (2011, '学生查询', 201, 1, '', '', NULL, 1, 0, 'F', '0', '0', 'business:student:query', '#', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (2012, '学生新增', 201, 2, '', '', NULL, 1, 0, 'F', '0', '0', 'business:student:add', '#', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (2013, '学生修改', 201, 3, '', '', NULL, 1, 0, 'F', '0', '0', 'business:student:edit', '#', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (2014, '学生删除', 201, 4, '', '', NULL, 1, 0, 'F', '0', '0', 'business:student:delete', '#', '0', NULL, 'admin', NOW(), '', NULL);

-- AI 助手
INSERT INTO sys_menu VALUES (300, 'AI 助手', 0, 4, 'ai', NULL, NULL, 1, 0, 'M', '0', '0', '', 'ChatDotRound', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (301, 'AI助手', 300, 1, 'chat', 'business/ai/chat/index', NULL, 1, 0, 'C', '0', '0', 'ai:chat:list', 'ChatDotRound', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (3011, '对话查询', 301, 1, '', '', NULL, 1, 0, 'F', '0', '0', 'ai:chat:query', '#', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (3012, '会话新增', 301, 2, '', '', NULL, 1, 0, 'F', '0', '0', 'ai:chat:add', '#', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (3013, '会话删除', 301, 3, '', '', NULL, 1, 0, 'F', '0', '0', 'ai:chat:delete', '#', '0', NULL, 'admin', NOW(), '', NULL);

-- 系统监控
INSERT INTO sys_menu VALUES (2100, '系统监控', 0, 5, 'monitor', '', NULL, 1, 0, 'M', '0', '0', '', 'Monitor', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (2101, '操作日志', 2100, 1, 'operlog', 'monitor/operlog/index', NULL, 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'Document', '0', NULL, 'admin', NOW(), '', NULL);
INSERT INTO sys_menu VALUES (2102, '登录日志', 2100, 2, 'logininfor', 'monitor/logininfor/index', NULL, 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'Document', '0', NULL, 'admin', NOW(), '', NULL);

-- 代码生成
INSERT INTO sys_menu VALUES (320, '代码生成', 0, 6, 'gen', 'business/gen/index', NULL, 1, 0, 'C', '0', '0', 'gen:tool:view', 'MagicStick', '0', NULL, 'admin', NOW(), '', NULL);

-- ============================================================
-- 六、角色菜单关联
-- ============================================================

-- 超级管理员(1)：拥有全部菜单
INSERT INTO sys_role_menu VALUES (1, 1);
INSERT INTO sys_role_menu VALUES (1, 100);
INSERT INTO sys_role_menu VALUES (1, 101);
INSERT INTO sys_role_menu VALUES (1, 1011);
INSERT INTO sys_role_menu VALUES (1, 1012);
INSERT INTO sys_role_menu VALUES (1, 1013);
INSERT INTO sys_role_menu VALUES (1, 1014);
INSERT INTO sys_role_menu VALUES (1, 102);
INSERT INTO sys_role_menu VALUES (1, 103);
INSERT INTO sys_role_menu VALUES (1, 104);
INSERT INTO sys_role_menu VALUES (1, 105);
INSERT INTO sys_role_menu VALUES (1, 1051);
INSERT INTO sys_role_menu VALUES (1, 1052);
INSERT INTO sys_role_menu VALUES (1, 200);
INSERT INTO sys_role_menu VALUES (1, 201);
INSERT INTO sys_role_menu VALUES (1, 2011);
INSERT INTO sys_role_menu VALUES (1, 2012);
INSERT INTO sys_role_menu VALUES (1, 2013);
INSERT INTO sys_role_menu VALUES (1, 2014);
INSERT INTO sys_role_menu VALUES (1, 300);
INSERT INTO sys_role_menu VALUES (1, 301);
INSERT INTO sys_role_menu VALUES (1, 3011);
INSERT INTO sys_role_menu VALUES (1, 3012);
INSERT INTO sys_role_menu VALUES (1, 3013);
INSERT INTO sys_role_menu VALUES (1, 310);
INSERT INTO sys_role_menu VALUES (1, 320);
INSERT INTO sys_role_menu VALUES (1, 2100);
INSERT INTO sys_role_menu VALUES (1, 2101);
INSERT INTO sys_role_menu VALUES (1, 2102);

-- 普通角色(2)：首页 + 数据大屏 + 学生管理 + 字典 + AI助手(不含删除)
INSERT INTO sys_role_menu VALUES (2, 1);
INSERT INTO sys_role_menu VALUES (2, 310);
INSERT INTO sys_role_menu VALUES (2, 200);
INSERT INTO sys_role_menu VALUES (2, 201);
INSERT INTO sys_role_menu VALUES (2, 2011);
INSERT INTO sys_role_menu VALUES (2, 105);
INSERT INTO sys_role_menu VALUES (2, 1051);
INSERT INTO sys_role_menu VALUES (2, 1052);
INSERT INTO sys_role_menu VALUES (2, 300);
INSERT INTO sys_role_menu VALUES (2, 301);
INSERT INTO sys_role_menu VALUES (2, 3011);
INSERT INTO sys_role_menu VALUES (2, 3012);
