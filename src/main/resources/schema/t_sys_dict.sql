-- ==================== 数据字典表 ====================

-- 1. 字典主表（字典类型）
CREATE TABLE IF NOT EXISTS `t_sys_dict` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '字典编码（唯一标识，如：expense_type）',
  `dict_name` VARCHAR(100) NOT NULL COMMENT '字典名称（如：报销类型）',
  `description` VARCHAR(500) COMMENT '字典描述',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用，1启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_code` (`dict_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典主表';

-- 2. 字典项表（字典值）
CREATE TABLE IF NOT EXISTS `t_sys_dict_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_code` VARCHAR(50) NOT NULL COMMENT '字典编码（关联t_sys_dict.dict_code）',
  `dict_value` VARCHAR(100) NOT NULL COMMENT '字典值（存储到业务表中的值）',
  `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签（显示给用户的名称）',
  `description` VARCHAR(500) COMMENT '字典项描述',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用，1启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_code_value` (`dict_code`, `dict_value`),
  KEY `idx_dict_code` (`dict_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典项表';

-- ==================== 初始化字典数据 ====================

-- 初始化报销类型字典
INSERT IGNORE INTO t_sys_dict (dict_code, dict_name, description) VALUES
('expense_type', '报销类型', '报销管理中使用的报销类型分类');

-- 初始化报销类型字典项
INSERT IGNORE INTO t_sys_dict_item (dict_code, dict_value, dict_label, description, sort_order) VALUES
('expense_type', '1', '交通费', '交通出行相关费用', 1),
('expense_type', '2', '餐饮费', '餐饮用餐相关费用', 2),
('expense_type', '3', '住宿费', '住宿相关费用', 3),
('expense_type', '4', '办公费', '办公用品等相关费用', 4),
('expense_type', '5', '其他', '其他类型费用', 5);

-- 初始化成本类型字典（用于成本管理模块）
INSERT IGNORE INTO t_sys_dict (dict_code, dict_name, description) VALUES
('cost_type', '成本类型', '成本管理中使用的成本类型分类');

INSERT IGNORE INTO t_sys_dict_item (dict_code, dict_value, dict_label, description, sort_order) VALUES
('cost_type', '1', '场地成本', '场地租赁等费用', 1),
('cost_type', '2', '人工成本', '人员薪资等费用', 2),
('cost_type', '3', '操作成本', '操作相关费用', 3),
('cost_type', '4', '运能成本', '运输能力相关费用', 4),
('cost_type', '5', '折旧成本', '设备折旧等费用', 5),
('cost_type', '6', '其他', '其他类型成本', 6);

-- 初始化审核状态字典
INSERT IGNORE INTO t_sys_dict (dict_code, dict_name, description) VALUES
('audit_status', '审核状态', '报销、账单等审核状态');

INSERT IGNORE INTO t_sys_dict_item (dict_code, dict_value, dict_label, description, sort_order) VALUES
('audit_status', '0', '待审核', '等待审核处理', 1),
('audit_status', '1', '已通过', '审核通过', 2),
('audit_status', '2', '已拒绝', '审核拒绝', 3);

-- 初始化承包区客户类型字典
INSERT IGNORE INTO t_sys_dict (dict_code, dict_name, description) VALUES
('emp_type', '承包区客户类型', '运单管理中承包区客户类型');

INSERT IGNORE INTO t_sys_dict_item (dict_code, dict_value, dict_label, description, sort_order) VALUES
('emp_type', '散件', '散件客户', '散件类型客户', 1),
('emp_type', '淘宝', '淘宝客户', '淘宝类型客户', 2),
('emp_type', '限定', '限定客户', '限定类型客户', 3),
('emp_type', '特批', '特批客户', '特批类型客户', 4);

-- 初始化转账方式字典
INSERT IGNORE INTO t_sys_dict (dict_code, dict_name, description) VALUES
('transfer_type', '转账方式', '报销管理中使用的转账方式');

INSERT IGNORE INTO t_sys_dict_item (dict_code, dict_value, dict_label, description, sort_order) VALUES
('transfer_type', '1', '银行转账', '通过银行进行转账', 1),
('transfer_type', '2', '支付宝', '通过支付宝进行转账', 2),
('transfer_type', '3', '微信', '通过微信进行转账', 3),
('transfer_type', '4', '现金', '现金支付', 4),
('transfer_type', '5', '其他', '其他转账方式', 5);
