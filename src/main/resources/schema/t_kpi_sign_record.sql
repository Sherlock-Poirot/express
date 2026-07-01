CREATE TABLE IF NOT EXISTS `t_kpi_sign_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `data_date` DATE NOT NULL COMMENT '数据日期',
  `waybill_no` VARCHAR(100) NOT NULL COMMENT '运单号',
  `report_time` DATETIME COMMENT '催查/上报/投诉时间',
  `fake_sign_type` VARCHAR(50) COMMENT '虚假签收大类',
  `sign_time` DATETIME COMMENT '签收时间',
  `precision_delivery` TINYINT(1) DEFAULT 0 COMMENT '精准派：0-否，1-是',
  `pre_sign_call` TINYINT(1) DEFAULT 0 COMMENT '签前电联：0-否，1-是',
  `courier_code` VARCHAR(50) COMMENT '小件员编码',
  `courier_name` VARCHAR(50) COMMENT '小件员名称',
  `branch_name` VARCHAR(100) COMMENT '分部名称',
  `is_qualified` TINYINT(1) DEFAULT 0 COMMENT '达标：0-否，1-是',
  `efficiency_hours` DECIMAL(10,2) COMMENT '时效(h)',
  `month` VARCHAR(20) COMMENT '所属月份（yyyy-MM）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='KPI签收记录表';