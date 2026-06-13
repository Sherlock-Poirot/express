CREATE TABLE `t_cost_management` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `cost_type` INT NOT NULL COMMENT '成本类型：1-场地成本，2-人工成本，3-操作成本，4-运能成本，5-折旧成本',
  `cost_name` VARCHAR(100) NOT NULL COMMENT '成本名称/描述',
  `amount` DECIMAL(12,2) NOT NULL COMMENT '金额',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_cost_type` (`cost_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成本管理表';
