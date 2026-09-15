-- ==================== 账单明细导出文件记录表 ====================
-- 异步导出任务的落库记录，前端下载展示页数据源
-- 执行数据库：express_yto

CREATE TABLE IF NOT EXISTS t_export_file (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  task_no     VARCHAR(64)  NOT NULL COMMENT '任务编号（雪花ID）',
  bill_month  VARCHAR(10)  NOT NULL COMMENT '账单月份 yyyy-MM',
  file_name   VARCHAR(255) NOT NULL COMMENT '展示用文件名',
  file_path   VARCHAR(512) NOT NULL COMMENT '服务器磁盘文件完整路径',
  status      VARCHAR(20)  NOT NULL DEFAULT 'RUNNING' COMMENT '状态：RUNNING生成中/SUCCESS成功/FAILED失败',
  error_msg   VARCHAR(1024)         DEFAULT NULL COMMENT '失败原因',
  file_size   BIGINT                 DEFAULT NULL COMMENT '文件大小（字节）',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_task_no (task_no),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单明细导出文件记录';
