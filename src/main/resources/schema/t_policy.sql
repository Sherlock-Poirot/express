CREATE TABLE t_policy (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    policy_type TINYINT NOT NULL COMMENT '政策类型：1-基数返利，2-固定收费，3-动态返利',
    weight_left DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '重量左区间（kg）',
    weight_right DECIMAL(10,2) NOT NULL DEFAULT 99999 COMMENT '重量右区间（kg）',
    base_amount DECIMAL(10,2) COMMENT '基数（元）',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额（元）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_policy_type (policy_type),
    INDEX idx_weight_range (weight_left, weight_right)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='政策管理表';
