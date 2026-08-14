-- ========================================
-- 业务表: 学生信息表 + 示例数据
-- ========================================
USE gp_framework;

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

-- 示例数据
INSERT INTO biz_student (student_no, student_name, gender, phone, email, class_name, status, remark, create_by, create_time) VALUES
('2024001', '张三', '0', '13800000001', 'zhangsan@gp.com', '计算机2201', '0', '学习委员', 'admin', NOW()),
('2024002', '李四', '1', '13800000002', 'lisi@gp.com', '计算机2201', '0', '班长', 'admin', NOW()),
('2024003', '王五', '0', '13800000003', 'wangwu@gp.com', '计算机2202', '0', NULL, 'admin', NOW()),
('2024004', '赵六', '1', '13800000004', 'zhaoliu@gp.com', '软件2201', '0', '文艺委员', 'admin', NOW()),
('2024005', '孙七', '0', '13800000005', 'sunqi@gp.com', '软件2201', '1', '休学', 'admin', NOW());
