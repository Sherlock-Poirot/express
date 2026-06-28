-- 报销管理表
CREATE TABLE IF NOT EXISTS `t_expense` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `expense_type` INT NOT NULL COMMENT '报销类型（关联字典expense_type）',
  `expense_name` VARCHAR(200) NOT NULL COMMENT '报销名称/描述',
  `amount` DECIMAL(12,2) NOT NULL COMMENT '报销金额',
  `applicant` VARCHAR(50) COMMENT '申请人',
  `expense_date` DATE COMMENT '报销日期（格式：yyyy-MM-dd）',
  `month` VARCHAR(20) COMMENT '所属月份（格式：yyyy-MM）',
  `status` TINYINT DEFAULT 0 COMMENT '审核状态（关联字典audit_status）',
  `transfer_time` DATE COMMENT '转账时间',
  `transfer_type` INT COMMENT '转账方式（关联字典transfer_type）',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_expense_type` (`expense_type`),
  KEY `idx_month` (`month`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报销管理表';
