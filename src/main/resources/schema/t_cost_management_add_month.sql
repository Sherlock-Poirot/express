-- 成本管理表添加 month 字段
-- 用于按月统计成本费用

-- 添加 month 字段（字符串类型，格式：yyyy-MM）
ALTER TABLE t_cost_management ADD COLUMN month VARCHAR(7) COMMENT '月份，格式：yyyy-MM';

-- 添加索引提高按月查询性能
CREATE INDEX idx_cost_management_month ON t_cost_management(month);

-- 可选：添加复合索引（按类型和月份查询）
CREATE INDEX idx_cost_management_type_month ON t_cost_management(cost_type, month);

-- 示例数据：更新现有的成本记录，补充 month 字段
-- 假设现有数据是2024年1月的成本
UPDATE t_cost_management SET month = '2024-01' WHERE month IS NULL;

-- 验证表结构
DESC t_cost_management;

-- 查询示例
-- 1. 按月份统计成本
SELECT month, SUM(amount) as total_amount
FROM t_cost_management
GROUP BY month
ORDER BY month DESC;

-- 2. 查询某月的成本
SELECT * FROM t_cost_management WHERE month = '2024-01';

-- 3. 按类型和月份统计
SELECT cost_type, month, SUM(amount) as total_amount
FROM t_cost_management
GROUP BY cost_type, month
ORDER BY month DESC, cost_type;
