-- ==================== 扶持派费配置菜单权限脚本 ====================
-- 菜单位置：结算管理 > 扶持派费配置
-- 权限码：settlement:supportFeeConfig 及子按钮
-- 执行前确保已执行 t_support_fee_config.sql 建表脚本

-- 1. 添加"扶持派费配置"菜单（挂在结算管理顶级目录下，menu_type=2 页面）
INSERT IGNORE INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, path, icon, sort_order) VALUES
(
    (SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'settlement') tmp),
    '扶持派费配置', 'settlement:supportFeeConfig', 2, '/settlement/supportFeeConfig', 'el-icon-money', 6
);

-- 2. 添加按钮权限（menu_type=3）
INSERT IGNORE INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, sort_order) VALUES
(
    (SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'settlement:supportFeeConfig') tmp),
    '查询', 'settlement:supportFeeConfig:query', 3, 1
),
(
    (SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'settlement:supportFeeConfig') tmp),
    '新增', 'settlement:supportFeeConfig:add', 3, 2
),
(
    (SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'settlement:supportFeeConfig') tmp),
    '编辑', 'settlement:supportFeeConfig:edit', 3, 3
),
(
    (SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'settlement:supportFeeConfig') tmp),
    '批量删除', 'settlement:supportFeeConfig:batchDelete', 3, 4
);

-- 3. 给超级管理员赋权（所有菜单）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM t_sys_role r JOIN t_sys_menu m
WHERE r.role_code = 'ADMIN' AND m.menu_code LIKE 'settlement:supportFeeConfig%';

-- 4. 给财务主管赋权（结算管理相关）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM t_sys_role r JOIN t_sys_menu m
WHERE r.role_code = 'FINANCE_MANAGER' AND m.menu_code LIKE 'settlement:supportFeeConfig%';

-- 5. 校验（执行后确认菜单行和按钮行已插入）
SELECT id, parent_id, menu_name, menu_code, menu_type FROM t_sys_menu
WHERE menu_code LIKE 'settlement:supportFeeConfig%' ORDER BY menu_type, sort_order;
