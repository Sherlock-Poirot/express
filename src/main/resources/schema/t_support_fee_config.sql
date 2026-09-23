-- ==================== 扶持派费配置表 ====================
-- 用途：替代原 updateExtraFee 中写死的扶持派费规则（原规则：province='上海' 或 destination 含太仓/昆山，固定加收0.1元）
-- 匹配逻辑（清洗时执行）：
--   t_waybill_detail.province = province（精确匹配）
--   city 为空：该省全部运单加收（只按省）
--   city 非空：AND destination LIKE '%city%'（模糊匹配，city 填市名关键字如：太仓）
--   AND scan_time >= start_time AND scan_time < end_time（左闭右开，按运单日期区间生效）
-- 命中的运单 extra_fee = extra_fee

CREATE TABLE IF NOT EXISTS t_support_fee_config (
  id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  province    VARCHAR(32)   NOT NULL COMMENT '省份（精确匹配运单 province 字段）',
  city        VARCHAR(64)   DEFAULT NULL COMMENT '目的地市（模糊匹配运单 destination 字段；为空表示该省全部加收）',
  extra_fee   DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '加收金额（元/单）',
  start_time  DATE          NOT NULL COMMENT '生效开始日期（含当天）',
  end_time    DATE          NOT NULL COMMENT '生效结束日期（不含当天）',
  remark      VARCHAR(255)  DEFAULT NULL COMMENT '备注',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_province (province),
  KEY idx_time_range (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='扶持派费配置表';

-- 初始数据：历史写死规则迁移（长期生效区间；原规则"上海"只按省不限制市，故 city 留空）
INSERT INTO t_support_fee_config (province, city, extra_fee, start_time, end_time, remark) VALUES
('上海', NULL, 0.1, '2000-01-01', '2099-12-31', '历史规则迁移：原 province=''上海''全省加收，0.1元/单'),
('江苏', '太仓', 0.1, '2000-01-01', '2099-12-31', '历史规则迁移：原 destination 含太仓，0.1元/单'),
('江苏', '昆山', 0.1, '2000-01-01', '2099-12-31', '历史规则迁移：原 destination 含昆山，0.1元/单');
