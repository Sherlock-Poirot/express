-- ==================== 账单工作流状态重置脚本 ====================
-- 用途：手动将指定月份（或全部月份）的工作流步骤状态复位到初始 WAITING 态
-- 场景：服务重启导致步骤卡在 RUNNING、测试数据清理、整批作废重来
-- 注意：本脚本只复位流程状态和导入文件记录，不动 t_waybill_detail 运单数据；
--      如需连运单数据一起清掉（整批重来），请同时执行文末"三"的语句
-- 执行数据库：express_yto

-- ============ 一、指定月份重置（把 2026-09 改成目标月份） ============

-- 1. 删除该月的导入文件记录（IMPORT步骤的文件明细账一并清空）
DELETE FROM t_waybill_flow_file WHERE bill_month = '2026-09';

-- 2. 该月步骤全部复位到 WAITING，清空执行痕迹（任务号/错误/操作人/起止时间）
UPDATE t_waybill_flow_step
SET status     = 'WAITING',
    task_no    = NULL,
    error_msg  = NULL,
    operator   = NULL,
    start_time = NULL,
    end_time   = NULL
WHERE bill_month = '2026-09';

-- 3. 校验（执行后应全部为 WAITING 且无文件记录）
SELECT * FROM t_waybill_flow_step WHERE bill_month = '2026-09' ORDER BY step_order;
SELECT * FROM t_waybill_flow_file WHERE bill_month = '2026-09';

-- ============ 二、全部月份重置（慎用，确认后再放开注释执行） ============
-- DELETE FROM t_waybill_flow_file;
-- UPDATE t_waybill_flow_step
-- SET status = 'WAITING', task_no = NULL, error_msg = NULL,
--     operator = NULL, start_time = NULL, end_time = NULL;

-- ============ 三、（可选）整批重来：连运单数据一起清，回到导入前 ============
-- 统一按 scan_time 月份范围删除（左闭右开）：bill_month 字段清洗后才写入，导入态数据以 scan_time 为准；
-- 已归档数据存于 t_waybill_detail_copy，不在本表，不受影响
-- DELETE FROM t_waybill_detail
-- WHERE scan_time >= '2026-09-01' AND scan_time < '2026-10-01';
-- DELETE FROM t_waybill_detail_original
-- WHERE scan_time >= '2026-09-01' AND scan_time < '2026-10-01';
