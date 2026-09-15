-- ==================== 新增顶级模块"下载管理"及下载按钮权限 ====================
-- 对应控制器：ExportFileController
--   GET /exportFile/records        -> download:file           （页面菜单：下载列表，展示导出记录）
--   GET /exportFile/download/{id}  -> download:file:download  （按钮权限：列表行内点击下载时校验）
-- 菜单位置：顶级目录"下载管理" > 下载列表（与"结算管理"平级，互不干扰）
-- 注意：menu_type=3 的"下载文件"仅是权限点（权限校验用），前端渲染菜单树时必须过滤，不渲染成可点击菜单项

-- 0. 清理本模块及旧版脚本的遗留数据，保证脚本可重复执行
DELETE rm FROM t_sys_role_menu rm
JOIN t_sys_menu m ON rm.menu_id = m.id
WHERE m.menu_code IN ('settlement:exportFile', 'settlement:exportFile:download',
                      'download', 'download:file', 'download:file:download');
DELETE FROM t_sys_menu WHERE menu_code IN ('settlement:exportFile', 'settlement:exportFile:download',
                                           'download', 'download:file', 'download:file:download');

-- 1. 创建顶级目录"下载管理"（parent_id=0，与结算管理平级）
INSERT INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, path, icon, sort_order, status)
VALUES (0, '下载管理', 'download', 1, '/download', 'el-icon-download', 6, 1);

-- 2. 创建"下载列表"页面菜单（挂在下载管理目录下，进入后展示导出记录列表）
INSERT INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, path, icon, sort_order, status)
VALUES (
    (SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'download' AND menu_type = 1) tmp),
    '下载列表', 'download:file', 2, '/download/list', 'el-icon-document', 1, 1
);

-- 3. 添加"下载文件"按钮权限（挂在下载列表页面下，仅作权限点，不渲染为菜单）
INSERT INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, url, method, sort_order, status)
VALUES (
    (SELECT id FROM (SELECT id FROM t_sys_menu WHERE menu_code = 'download:file') tmp),
    '下载文件', 'download:file:download', 3, '/exportFile/download', 'GET', 1, 1
);

-- 4. 给超级管理员赋权（含顶级目录，否则菜单树不显示）
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM t_sys_role r, t_sys_menu m
WHERE r.role_code = 'ADMIN'
  AND m.menu_code IN ('download', 'download:file', 'download:file:download');

-- 5. 给财务主管赋权
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM t_sys_role r, t_sys_menu m
WHERE r.role_code = 'FINANCE_MANAGER'
  AND m.menu_code IN ('download', 'download:file', 'download:file:download');

-- 查询结果确认
SELECT * FROM t_sys_menu WHERE menu_code IN ('download', 'download:file', 'download:file:download');
