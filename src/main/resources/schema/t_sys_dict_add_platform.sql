-- ==================== 资金流水平台字典 ====================
-- 对应后端枚举 CashFlowPlatformEnum：平台类型决定资金流水导入时的解析处理器
-- 注意：新增平台除在此加字典项外，还需后端新增对应的 Listener/Handler 并在枚举中登记，三者值必须一致

-- 初始化资金流水平台字典
INSERT IGNORE INTO t_sys_dict (dict_code, dict_name, description, sort_order) VALUES
('platform', '资金流水平台', '实收资金流水导入/查询使用的平台类型，对应后端解析处理器', 0);

-- 初始化资金流水平台字典项（dict_value 与 CashFlowPlatformEnum.type 一一对应）
INSERT IGNORE INTO t_sys_dict_item (dict_code, dict_value, dict_label, description, sort_order) VALUES
('platform', '1', '微信',     '微信账单，对应微信流水解析处理器', 1),
('platform', '2', '支付宝',   '支付宝账单，对应支付宝流水解析处理器', 2),
('platform', '3', '台州银行', '台州银行流水，对应台州银行解析处理器', 3),
('platform', '4', '工商银行', '工商银行流水，对应工商银行解析处理器', 4),
('platform', '5', '农商银行', '农商银行流水，对应农商银行解析处理器', 5);
