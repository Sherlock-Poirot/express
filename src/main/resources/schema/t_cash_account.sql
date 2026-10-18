-- ==================== 资金账户表 ====================
-- 用途：维护收款账户信息，同一平台（银行/微信/支付宝）可有多个账户（如私户、公户）
-- 关联：
--   platform 关联字典（1微信，2支付宝，3台州银行，4工商银行，5农商银行），见 CashFlowPlatformEnum
--   t_cash_flow_detail.account_id 关联本表 id，流水按账户维度分类

CREATE TABLE IF NOT EXISTS `t_cash_account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_name` VARCHAR(200) NOT NULL COMMENT '账户名称',
  `account_no` VARCHAR(100) DEFAULT NULL COMMENT '账号',
  `platform` INT NOT NULL COMMENT '平台类型（1微信，2支付宝，3台州银行，4工商银行，5农商银行）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_platform` (`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资金账户表';

-- 实收流水表增加账户关联字段（流水按账户分类）
ALTER TABLE t_cash_flow_detail
  ADD COLUMN account_id BIGINT DEFAULT NULL COMMENT '账户id（关联t_cash_account.id）' AFTER platform,
  ADD KEY idx_account_id (account_id);
