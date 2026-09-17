-- ==================== 账单工作流步骤表 ====================
-- 每月账单处理流程（导入原始账单→导入重量差异→清洗→校验→计算）的状态跟踪
-- 每月1号由定时任务初始化当月5个步骤记录（WAITING态），uk唯一键保证重复执行/多实例并发不会重复初始化
-- 执行数据库：express_yto

CREATE TABLE IF NOT EXISTS t_waybill_flow_step (
  id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  bill_month  VARCHAR(10)   NOT NULL COMMENT '账单月份 yyyy-MM',
  step_code   VARCHAR(32)   NOT NULL COMMENT '步骤编码：IMPORT/IMPORT_DIFF/CLEAN/VALIDATE/CALCULATE',
  step_name   VARCHAR(64)   NOT NULL COMMENT '步骤名称',
  step_order  INT           NOT NULL COMMENT '步骤顺序：1-5',
  status      VARCHAR(20)   NOT NULL DEFAULT 'WAITING' COMMENT '状态：WAITING待执行/RUNNING执行中/SUCCESS成功/FAILED失败/PASSED人工确认通过/SKIPPED已跳过',
  remark      VARCHAR(512)           DEFAULT NULL COMMENT '步骤说明（该步骤做什么及人工注意事项）',
  task_no     VARCHAR(64)            DEFAULT NULL COMMENT '关联sys_task.task_no（导入步骤进度查询复用）',
  error_msg   VARCHAR(1024)          DEFAULT NULL COMMENT '失败原因',
  operator    VARCHAR(64)            DEFAULT NULL COMMENT '操作人',
  start_time  DATETIME               DEFAULT NULL COMMENT '开始时间',
  end_time    DATETIME               DEFAULT NULL COMMENT '结束时间',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_bill_month_step (bill_month, step_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单工作流步骤表';

-- ==================== 账单工作流导入文件记录表 ====================
-- 记录每个账单月份导入的Excel文件（文件数量不固定），作为导入步骤人工确认"已导齐"的依据

CREATE TABLE IF NOT EXISTS t_waybill_flow_file (
  id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  bill_month  VARCHAR(10)   NOT NULL COMMENT '账单月份 yyyy-MM',
  file_name   VARCHAR(255)  NOT NULL COMMENT '原始文件名',
  file_md5    VARCHAR(64)            DEFAULT NULL COMMENT '文件MD5（防重复导入）',
  task_no     VARCHAR(64)            DEFAULT NULL COMMENT '关联sys_task.task_no',
  status      VARCHAR(20)   NOT NULL DEFAULT 'RUNNING' COMMENT '状态：RUNNING导入中/SUCCESS成功/FAILED失败',
  row_count   INT           NOT NULL DEFAULT 0 COMMENT '成功导入条数',
  error_msg   VARCHAR(1024)          DEFAULT NULL COMMENT '失败原因',
  operator    VARCHAR(64)            DEFAULT NULL COMMENT '上传人',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_bill_month (bill_month),
  KEY idx_task_no (task_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单工作流导入文件记录表';

-- ==================== 首次上线手动初始化当前月，之后由定时任务每月1号自动创建 ====================
INSERT IGNORE INTO t_waybill_flow_step (bill_month, step_code, step_name, step_order, status, remark) VALUES
('2026-09', 'IMPORT',      '导入原始账单',     1, 'WAITING', '导入原始账单Excel文件，支持分多次导入多个文件，全部导入完成后需人工确认方可进入下一步'),
('2026-09', 'IMPORT_DIFF', '导入重量差异数据', 2, 'WAITING', '导入重量差异数据Excel文件，可选步骤，无差异数据时可跳过'),
('2026-09', 'CLEAN',       '清洗运单数据',     3, 'WAITING', '清洗运单数据，规范字段格式、补全缺失信息，清洗支持重复执行'),
('2026-09', 'VALIDATE',    '校验运单数据',     4, 'WAITING', '校验运单数据完整性与合法性，校验通过后需人工核查确认才能进入计算'),
('2026-09', 'CALCULATE',   '计算账单',         5, 'WAITING', '按价格规则计算每条运单的快递费用，计算完成后需人工核查再执行归档');
