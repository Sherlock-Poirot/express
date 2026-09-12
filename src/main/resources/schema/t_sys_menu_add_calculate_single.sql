-- ==================== 添加单票费用试算按钮权限 ====================
-- 菜单位置：结算管理 > 账单管理
-- 对应接口：POST /waybill/calculateSingle

-- 1. 添加"单票试算"按钮（挂在账单管理 settlement:bill 下）
INSERT IGNORE INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, url, method, sort_order, status, create_time)
VALUES (
    (SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'settlement:bill') tmp),
    '单票试算', 'settlement:bill:calculateSingle', 3, '/waybill/calculateSingle', 'POST', 5, 1, NOW()
);

-- 2. 给超级管理员赋权
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM t_sys_role r, t_sys_menu m
WHERE r.role_code = 'ADMIN' AND m.menu_code = 'settlement:bill:calculateSingle';

-- 3. 给财务主管赋权
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM t_sys_role r, t_sys_menu m
WHERE r.role_code = 'FINANCE_MANAGER' AND m.menu_code = 'settlement:bill:calculateSingle';

-- 查询结果确认
SELECT * FROM t_sys_menu WHERE menu_code = 'settlement:bill:calculateSingle';
