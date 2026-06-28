-- ==================== 添加财务管理和字典管理菜单 ====================

-- 财务管理（父菜单，与系统管理同级）
INSERT IGNORE INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, path, icon, sort_order) VALUES
(0, '财务管理', NULL, 1, '/finance', 'el-icon-wallet', 4),

-- 报销管理（子菜单）
(13, '报销管理', 'finance:expense', 2, '/finance/expense', 'el-icon-receipt', 1),

-- 字典管理（系统管理下的子菜单）
(1, '字典管理', 'sys:dict:manage', 2, '/system/dict', 'el-icon-notebook-2', 4);

-- 财务管理下的按钮权限
INSERT IGNORE INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, sort_order) VALUES
-- 报销管理按钮
(14, '新增报销', 'finance:expense:add', 3, 1),
(14, '编辑报销', 'finance:expense:edit', 3, 2),
(14, '删除报销', 'finance:expense:delete', 3, 3),
(14, '批量删除报销', 'finance:expense:batchDelete', 3, 4),
(14, '审核通过', 'finance:expense:auditPass', 3, 5),
(14, '审核拒绝', 'finance:expense:auditReject', 3, 6),
(14, '查询报销', 'finance:expense:query', 3, 7),
(14, '导出报销', 'finance:expense:export', 3, 8);

-- 字典管理下的按钮权限
INSERT IGNORE INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, sort_order) VALUES
(15, '新增字典', 'sys:dict:add', 3, 1),
(15, '编辑字典', 'sys:dict:edit', 3, 2),
(15, '删除字典', 'sys:dict:delete', 3, 3),
(15, '查询字典', 'sys:dict:query', 3, 4),
(15, '字典项管理', 'sys:dict:item:manage', 3, 5),
(15, '新增字典项', 'sys:dict:item:add', 3, 6),
(15, '编辑字典项', 'sys:dict:item:edit', 3, 7),
(15, '删除字典项', 'sys:dict:item:delete', 3, 8);

-- 给超级管理员赋权（所有菜单）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT 1, id FROM t_sys_menu;

-- 给财务主管赋权（添加财务管理相关菜单）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT 2, id FROM t_sys_menu WHERE id IN (13, 14, 15);

-- 给财务主管赋权（添加财务管理和字典管理的按钮权限）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT 2, id FROM t_sys_menu WHERE menu_code LIKE 'finance:%' OR menu_code LIKE 'sys:dict:%';

-- 给财务文员赋权（基础权限 - 仅查看和新增）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT 3, id FROM t_sys_menu WHERE menu_code IN ('finance:expense:add', 'finance:expense:query', 'sys:dict:query');