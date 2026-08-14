# GP-Framework 毕业设计开发框架

> Spring Boot 2.7 + Vue3 + Vite + Element Plus + MyBatis Plus + MySQL + Redis + JWT

## 快速启动

### 1. 环境准备

| 工具 | 版本要求 |
|------|----------|
| JDK | 8 或 11 |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| Redis | 任意稳定版 |
| Node.js | 16+ |

### 2. 数据库初始化

```bash
# 登录 MySQL，执行两个 SQL 脚本
mysql -u root -p

# 在 MySQL 中执行：
source gp-admin/sql/gp_framework.sql   # 创建数据库 + 系统表 + 初始数据
source gp-admin/sql/gp_business.sql     # 业务表 + 示例数据
```

或用 Navicat / DataGrip 直接打开 SQL 文件执行。

### 3. 启动 Redis

```bash
redis-server
```

### 4. 修改后端配置

打开 `gp-admin/src/main/resources/application-dev.yml`，修改数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gp_framework?...
    username: root
    password: 你的MySQL密码    # ← 改这里
  redis:
    host: localhost
    port: 6379
```

### 5. 启动后端

用 IDEA 打开 `gp-admin` 目录（或 pom.xml），等待 Maven 下载依赖，然后运行 `GpApplication.java`。

启动成功后访问接口文档：http://localhost:8080/doc.html

### 6. 启动前端

```bash
cd gp-ui
npm install        # 首次需要安装依赖
npm run dev        # 启动开发服务器
```

浏览器访问 http://localhost

### 7. 登录系统

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 超级管理员 |
| gp | admin123 | 普通用户 |

---

## 项目结构

```
template/
├── gp-admin/                    # 后端 (Spring Boot)
│   ├── pom.xml
│   ├── src/main/java/com/gp/
│   │   ├── GpApplication.java        # 启动类
│   │   ├── common/                   # 公共模块
│   │   │   ├── annotation/           #   @Log @Excel
│   │   │   ├── constant/             #   常量
│   │   │   ├── core/                 #   Result/PageQuery/BaseEntity
│   │   │   ├── enums/                #   枚举
│   │   │   ├── exception/            #   业务异常
│   │   │   ├── handler/              #   全局异常+自动填充
│   │   │   └── utils/                #   JWT/Redis/Security工具
│   │   ├── framework/                # 框架配置
│   │   │   ├── config/               #   Security/Redis/MyBatis/CORS
│   │   │   ├── filter/               #   JWT认证过滤器
│   │   │   └── security/             #   LoginUserDetails/认证入口
│   │   ├── system/                   # 系统管理模块
│   │   │   ├── domain/               #   SysUser/SysRole/SysMenu/SysDept
│   │   │   ├── mapper/               #   Mapper接口
│   │   │   ├── service/              #   Login/Token/Menu Service
│   │   │   └── controller/           #   Login/User Controller
│   │   └── business/                 # ★ 业务模块（可插拔）
│   │       └── student/              #   示例：学生管理
│   ├── src/main/resources/
│   │   ├── application.yml           # 主配置
│   │   ├── application-dev.yml       # 开发环境配置
│   │   └── mapper/                   # MyBatis XML
│   └── sql/                          # SQL脚本
│       ├── gp_framework.sql          # 系统表+初始数据
│       └── gp_business.sql           # 业务表+示例数据
│
├── gp-ui/                       # 前端 (Vue3)
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api/                      # API请求
│       ├── views/                    # 页面
│       │   ├── login/                #   登录页
│       │   ├── layout/               #   主布局
│       │   ├── dashboard/            #   首页
│       │   ├── system/user/          #   用户管理
│       │   └── business/student/     #   学生管理
│       ├── store/                    # Pinia状态管理
│       ├── router/                   # 路由+权限守卫
│       ├── utils/                    # Axios封装/Token工具
│       └── directive/                # v-hasPermi权限指令
│
└── 毕业设计开发框架-架构设计文档.md    # 完整架构设计文档
```

---

## 核心功能

| 功能 | 说明 |
|------|------|
| JWT 登录认证 | 账号密码验证 → 生成 JWT → 存入 Redis |
| RBAC 权限 | 用户-角色-菜单三级权限模型 |
| 动态路由 | 前端根据后端菜单 API 自动生成路由 |
| 用户管理 | 完整 CRUD + 状态切换 |
| 学生管理 | 完整 CRUD + 搜索 + 分页（业务模块示例） |
| 统一返回 | 所有接口返回 `Result<T>` |
| 全局异常 | `GlobalExceptionHandler` 统一处理 |
| 逻辑删除 | MyBatis Plus `@TableLogic` |
| 自动填充 | 创建时间/更新时间/创建人/更新人 |
| 接口文档 | Knife4j (Swagger) http://localhost:8080/doc.html |

---

## 新增业务模块

以新增"教师管理"为例：

1. **建表**：参考 `biz_student` 创建 `biz_teacher` 表
2. **后端**：复制 `com.gp.business.student` 包 → 重命名为 `teacher` → 修改字段
3. **前端**：复制 `src/views/business/student/` → 重命名为 `teacher/` → 修改字段
4. **菜单**：在 `sys_menu` 表插入教师管理的菜单和权限记录
5. 完成！无需修改框架代码

---

## 常见问题

**Q: 后端启动报数据库连接失败？**
A: 检查 `application-dev.yml` 中的数据库地址、用户名、密码是否正确。

**Q: 前端 npm install 很慢？**
A: 使用淘宝镜像：`npm install --registry=https://registry.npmmirror.com`

**Q: 登录返回 401？**
A: 检查 Redis 是否已启动，密码是否正确（默认 admin / admin123）。

**Q: 页面空白或路由不显示？**
A: 检查后端 `/getRouters` 接口是否正常返回菜单数据。

**Q: 如何修改 JWT 密钥？**
A: 修改 `application.yml` 中的 `gp.jwt.secret`。
