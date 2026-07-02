CREATE TABLE IF NOT EXISTS `t_collection_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `waybill_no` VARCHAR(100) DEFAULT NULL COMMENT '运单号',
  `expected_collection_time` DATETIME DEFAULT NULL COMMENT '应揽收时间',
  `actual_collection_time` DATETIME DEFAULT NULL COMMENT '实际揽收时间',
  `order_no` VARCHAR(100) DEFAULT NULL COMMENT '订单号',
  `salesman_name` VARCHAR(50) DEFAULT NULL COMMENT '业务员姓名',
  `collection_delay_minutes` INT DEFAULT NULL COMMENT '揽收延迟分钟数（自动计算）',
  `is_delay` TINYINT(1) DEFAULT NULL COMMENT '是否延迟：0-否，1-是（自动计算）',
  `month` VARCHAR(20) DEFAULT NULL COMMENT '所属月份（yyyy-MM）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_waybill_no` (`waybill_no`) USING BTREE,
  KEY `idx_salesman_name` (`salesman_name`) USING BTREE,
  KEY `idx_month` (`month`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='揽收记录表';