-- 添加运单数据校验按钮权限
-- 先查询运单管理菜单的ID，作为父菜单
-- 如果运单管理菜单不存在，先创建运单管理菜单

-- 1. 创建运单管理菜单（如果不存在）
INSERT IGNORE INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, path, icon, sort_order)
VALUES (5, '运单管理', 'settlement:waybill', 2, '/settlement/waybill', 'el-icon-document', 5);

-- 2. 添加运单数据校验按钮权限
INSERT IGNORE INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, url, method, sort_order, status)
VALUES (
    (SELECT id FROM t_sys_menu WHERE menu_code = 'settlement:waybill'),
    '数据校验', 'settlement:waybill:validate', 3, '/waybill/validate', 'POST', 4, 1
);

-- 3. 给超级管理员赋权
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT r.id, m.id 
FROM t_sys_role r, t_sys_menu m 
WHERE r.role_code = 'ADMIN' AND m.menu_code = 'settlement:waybill:validate';

-- 4. 给财务主管赋权
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT r.id, m.id 
FROM t_sys_role r, t_sys_menu m 
WHERE r.role_code = 'FINANCE_MANAGER' AND m.menu_code = 'settlement:waybill:validate';

-- 查询结果
SELECT * FROM t_sys_menu WHERE menu_code LIKE 'settlement:waybill%';
