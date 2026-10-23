-- ============================================================
-- 客户-付款方映射表
-- 用途：自动匹配时通过资金流水的付款方名称（cust_name）锁定系统客户
-- 数据来源：手动匹配成功时自动沉淀（归一化后upsert）+ 手动维护
-- 归一化规则：剥离微信备注预付款前缀（如 S2.45，会随预付款变化非稳定标识），
--            见 PayerNameUtil.normalizePayerName，沉淀与匹配必须共用同一规则
-- ============================================================
CREATE TABLE IF NOT EXISTS `t_cust_payer_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `cust_code` varchar(50) NOT NULL COMMENT '客户编码（面单明细to_cust_code）',
  `payer_name` varchar(200) NOT NULL COMMENT '付款方名称（归一化后的稳定标识，前缀已剥离；剥离失败存原文）',
  `account_id` bigint DEFAULT NULL COMMENT '常用收款账户id（可选，付款方名称歧义时辅助收敛）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cust_payer` (`cust_code`, `payer_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户-付款方映射表（手动匹配沉淀+手动维护）';
