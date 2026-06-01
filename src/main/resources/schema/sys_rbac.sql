-- ==================== RBAC权限系统表结构 ====================

-- 1. 用户表
CREATE TABLE IF NOT EXISTS t_sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用，1启用',
    contract_id BIGINT COMMENT '预留：承包区ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_contract_id (contract_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. 角色表
CREATE TABLE IF NOT EXISTS t_sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用，1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3. 菜单/权限表
CREATE TABLE IF NOT EXISTS t_sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID（0为顶级）',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_code VARCHAR(100) UNIQUE COMMENT '权限编码（如：bill:list）',
    menu_type TINYINT NOT NULL COMMENT '类型：1目录，2菜单，3按钮',
    path VARCHAR(200) COMMENT '前端路由路径',
    component VARCHAR(200) COMMENT '前端组件路径',
    icon VARCHAR(50) COMMENT '图标',
    url VARCHAR(200) COMMENT '后端接口URL',
    method VARCHAR(10) COMMENT 'HTTP方法（GET/POST/PUT/DELETE）',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用，1启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_menu_code (menu_code),
    INDEX idx_url (url)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 4. 用户-角色关联表
CREATE TABLE IF NOT EXISTS t_sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 5. 角色-菜单关联表
CREATE TABLE IF NOT EXISTS t_sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    INDEX idx_role_id (role_id),
    INDEX idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ==================== 初始化数据 ====================

-- 初始化角色
INSERT IGNORE INTO t_sys_role (role_name, role_code, description) VALUES
('超级管理员', 'ADMIN', '拥有系统所有权限'),
('财务主管', 'FINANCE_MANAGER', '账单审核、费用管理，查看完整数据'),
('财务文员', 'FINANCE_STAFF', '账单录入、查看基础数据（数据脱敏）');

-- 初始化菜单（系统管理）
INSERT IGNORE INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, path, icon, sort_order) VALUES
(0, '系统管理', NULL, 1, '/system', 'el-icon-setting', 1),
(1, '用户管理', 'sys:user:manage', 2, '/system/user', 'el-icon-user', 1),
(1, '角色管理', 'sys:role:manage', 2, '/system/role', 'el-icon-s-custom', 2),
(1, '菜单管理', 'sys:menu:manage', 2, '/system/menu', 'el-icon-menu', 3),

-- 账单管理
(0, '账单管理', NULL, 1, '/bill', 'el-icon-document', 2),
(5, '账单列表', 'bill:list', 2, '/bill/list', 'el-icon-document-copy', 1),
(5, '账单录入', 'bill:add', 2, '/bill/add', 'el-icon-plus', 2),
(5, '账单审核', 'bill:audit', 2, '/bill/audit', 'el-icon-check', 3),
(5, '账单导出', 'bill:export', 2, '/bill/export', 'el-icon-download', 4),

-- 报表中心
(0, '报表中心', NULL, 1, '/report', 'el-icon-s-data', 3),
(10, '量本利报表', 'report:profit', 2, '/report/profit', 'el-icon-trending-up', 1);

-- 给超级管理员赋权（所有菜单）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT 1, id FROM t_sys_menu;

-- 创建默认超级管理员账号（密码：admin123）
-- 使用标准的 BCrypt 加密值
INSERT IGNORE INTO t_sys_user (username, password, real_name, status) VALUES
('admin', '$2a$10$dXJ3SW6G7P50lGmMkkm5lOZ7r5j5k4j3l2k1j0h9g8f7e6d5c4b3a2', '超级管理员', 1);

-- 给财务主管赋权（除了系统管理相关）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT 2, id FROM t_sys_menu WHERE id NOT IN (1, 2, 3, 4);

-- 给财务文员赋权（基础权限）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT 3, id FROM t_sys_menu WHERE id IN (5, 6, 10);