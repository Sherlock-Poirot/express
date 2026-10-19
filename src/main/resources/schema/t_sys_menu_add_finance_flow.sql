-- ==================== 财务管理 > 资金账户/应收流水/实收流水 菜单权限脚本 ====================
-- 菜单位置：财务管理 > 资金账户管理 / 应收流水管理 / 实收流水管理
-- 对应控制器：
--   CashAccountController (/cashAccount/*)
--   FlowDetailController   (/flowDetail/recharge/*、/flowDetail/cash/*、/flowDetail/match、/flowDetail/unmatch)
-- 前置依赖：t_sys_menu_add_finance_dict.sql（已创建"财务管理"顶级目录）
-- 注意：财务管理顶级目录 menu_code 为 NULL，子菜单 parent_id 通过 menu_name 定位

-- 0. 清理本模块旧数据，保证脚本可重复执行
DELETE rm FROM t_sys_role_menu rm
JOIN t_sys_menu m ON rm.menu_id = m.id
WHERE m.menu_code LIKE 'finance:cashAccount%'
   OR m.menu_code LIKE 'finance:rechargeFlow%'
   OR m.menu_code LIKE 'finance:cashFlow%';

DELETE FROM t_sys_menu
WHERE menu_code LIKE 'finance:cashAccount%'
   OR menu_code LIKE 'finance:rechargeFlow%'
   OR menu_code LIKE 'finance:cashFlow%';

-- 1. 三个二级菜单（挂在"财务管理"目录下）
INSERT INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, path, icon, sort_order) VALUES
(
    (SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_name = '财务管理' AND menu_type = 1 AND parent_id = 0 LIMIT 1) tmp),
    '资金账户管理', 'finance:cashAccount', 2, '/finance/cashAccount', 'el-icon-bank-card', 2
),
(
    (SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_name = '财务管理' AND menu_type = 1 AND parent_id = 0 LIMIT 1) tmp),
    '应收流水管理', 'finance:rechargeFlow', 2, '/finance/rechargeFlow', 'el-icon-tickets', 3
),
(
    (SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_name = '财务管理' AND menu_type = 1 AND parent_id = 0 LIMIT 1) tmp),
    '实收流水管理', 'finance:cashFlow', 2, '/finance/cashFlow', 'el-icon-money', 4
);

-- 2. 资金账户管理按钮权限
INSERT INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, url, method, sort_order) VALUES
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:cashAccount') tmp), '查询',     'finance:cashAccount:query',       3, '/cashAccount/search', 'GET',    1),
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:cashAccount') tmp), '新增',     'finance:cashAccount:add',         3, '/cashAccount/add',    'POST',   2),
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:cashAccount') tmp), '编辑',     'finance:cashAccount:edit',        3, '/cashAccount/update', 'POST',   3),
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:cashAccount') tmp), '批量删除', 'finance:cashAccount:batchDelete', 3, '/cashAccount/batch',  'DELETE', 4);

-- 3. 应收流水管理按钮权限（含匹配/取消匹配）
INSERT INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, url, method, sort_order) VALUES
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:rechargeFlow') tmp), '查询',     'finance:rechargeFlow:query',       3, '/flowDetail/recharge/search',    'GET',    1),
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:rechargeFlow') tmp), '导入',     'finance:rechargeFlow:import',      3, '/flowDetail/recharge/import',    'POST',   2),
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:rechargeFlow') tmp), '编辑',     'finance:rechargeFlow:edit',        3, '/flowDetail/recharge/update',    'POST',   3),
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:rechargeFlow') tmp), '批量删除', 'finance:rechargeFlow:batchDelete', 3, '/flowDetail/recharge/batch',     'DELETE', 4),
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:rechargeFlow') tmp), '自动匹配', 'finance:rechargeFlow:autoMatch',   3, '/flowDetail/recharge/autoMatch', 'POST',   5),
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:rechargeFlow') tmp), '匹配',     'finance:rechargeFlow:match',       3, '/flowDetail/match',              'POST',   6),
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:rechargeFlow') tmp), '取消匹配', 'finance:rechargeFlow:unmatch',     3, '/flowDetail/unmatch',            'POST',   7);

-- 4. 实收流水管理按钮权限
INSERT INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, url, method, sort_order) VALUES
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:cashFlow') tmp), '查询',     'finance:cashFlow:query',       3, '/flowDetail/cash/search', 'GET',    1),
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:cashFlow') tmp), '导入',     'finance:cashFlow:import',      3, '/flowDetail/cash/import', 'POST',   2),
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:cashFlow') tmp), '编辑',     'finance:cashFlow:edit',        3, '/flowDetail/cash/update', 'POST',   3),
((SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'finance:cashFlow') tmp), '批量删除', 'finance:cashFlow:batchDelete', 3, '/flowDetail/cash/batch',  'DELETE', 4);

-- 5. 给超级管理员角色赋权（admin 用户已通过 t_sys_user_role 关联 ADMIN 角色，自动获得以下权限）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM t_sys_role r JOIN t_sys_menu m
WHERE r.role_code = 'ADMIN'
  AND (m.menu_code LIKE 'finance:cashAccount%'
       OR m.menu_code LIKE 'finance:rechargeFlow%'
       OR m.menu_code LIKE 'finance:cashFlow%');

-- 6. 给财务主管角色赋权（全部按钮）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM t_sys_role r JOIN t_sys_menu m
WHERE r.role_code = 'FINANCE_MANAGER'
  AND (m.menu_code LIKE 'finance:cashAccount%'
       OR m.menu_code LIKE 'finance:rechargeFlow%'
       OR m.menu_code LIKE 'finance:cashFlow%');

-- 7. 给财务文员角色赋权（仅菜单 + 查询按钮，不允许增删改/导入/匹配）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM t_sys_role r JOIN t_sys_menu m
WHERE r.role_code = 'FINANCE_STAFF'
  AND m.menu_code IN (
      'finance:cashAccount',         'finance:cashAccount:query',
      'finance:rechargeFlow',        'finance:rechargeFlow:query',
      'finance:cashFlow',            'finance:cashFlow:query'
  );

-- 8. 校验
SELECT id, parent_id, menu_name, menu_code, menu_type, sort_order
FROM t_sys_menu
WHERE menu_code LIKE 'finance:cashAccount%'
   OR menu_code LIKE 'finance:rechargeFlow%'
   OR menu_code LIKE 'finance:cashFlow%'
ORDER BY parent_id, menu_type, sort_order;


-- ==================== 资金流水模块建表脚本 ====================
-- 包含表：t_cash_account / t_recharge_flow_detail / t_cash_flow_detail / t_recharge_cash_relation
-- 结构导出自开发库 express_yto，用于新环境初始化；已按依赖顺序排列（账户表在前）
-- 使用 IF NOT EXISTS，已有表时跳过，不影响存量数据

-- 1. 资金账户表
CREATE TABLE IF NOT EXISTS `t_cash_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户名称',
  `account_no` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号',
  `platform` int NOT NULL COMMENT '平台类型（1微信，2支付宝，3台州银行，4工商银行，5农商银行）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_platform` (`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资金账户表';

-- 2. 应收明细-面单流水明细表
CREATE TABLE IF NOT EXISTS `t_recharge_flow_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `from_shop_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源店铺id',
  `from_shop_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源店铺名称',
  `from_cust_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源客户编码',
  `from_cust_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源客户名称',
  `to_shop_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目的店铺id',
  `to_shop_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目的店铺名称',
  `to_cust_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目的客户编码',
  `to_cust_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目的客户名称',
  `operate_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作类型',
  `recharge_platform` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '充值平台',
  `stock` int DEFAULT NULL COMMENT '库存',
  `operate_qty` int DEFAULT NULL COMMENT '操作数量',
  `unit_price` decimal(12,4) DEFAULT NULL COMMENT '单价',
  `amount` decimal(12,2) DEFAULT NULL COMMENT '金额',
  `pay_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付方式',
  `special_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '特殊备注',
  `after_stock` int DEFAULT NULL COMMENT '操作后库存',
  `operate_time` datetime DEFAULT NULL COMMENT '操作时间',
  `operator` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `match_status` int DEFAULT '0' COMMENT '匹配状态（0未匹配，1已匹配）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_to_cust_code` (`to_cust_code`),
  KEY `idx_operate_type` (`operate_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应收明细-面单流水明细表';

-- 3. 实收流水表
CREATE TABLE IF NOT EXISTS `t_cash_flow_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `operate_time` datetime DEFAULT NULL COMMENT '操作时间',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类目',
  `cust_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '客户名称',
  `amount` decimal(12,2) DEFAULT NULL COMMENT '金额',
  `actual_type` int DEFAULT NULL COMMENT '实际收支类型（-1支出，1收入）',
  `mark_type` int DEFAULT NULL COMMENT '标记收支类型（-1支出，0不计收支，1收入）',
  `platform` int DEFAULT NULL COMMENT '平台（关联字典platform）',
  `account_id` bigint DEFAULT NULL COMMENT '账户id（关联t_cash_account.id）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `match_status` int DEFAULT '0' COMMENT '匹配状态（0未匹配，1已匹配）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实收流水表';

-- 4. 应收-实收关联关系表
CREATE TABLE IF NOT EXISTS `t_recharge_cash_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `recharge_id` bigint NOT NULL COMMENT '应收流水id（关联t_recharge_flow_detail.id）',
  `cash_id` bigint NOT NULL COMMENT '实收流水id（关联t_cash_flow_detail.id）',
  `relate_amount` decimal(12,2) DEFAULT NULL COMMENT '本次关联金额（一笔实收可能拆分关联多笔应收）',
  `relate_time` datetime DEFAULT NULL COMMENT '关联时间',
  `operator` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_recharge_id` (`recharge_id`),
  KEY `idx_cash_id` (`cash_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应收-实收关联关系表';
