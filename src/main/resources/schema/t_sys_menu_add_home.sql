-- 添加首页菜单（顶级菜单）
INSERT INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, path, component, icon, url, method, sort_order, status, create_time)
VALUES (0, '首页', 'home', 1, '/home', 'Home/index', 'home', '/home', 'GET', 1, 1, NOW());

-- 添加首页查看按钮权限
INSERT INTO t_sys_menu (parent_id, menu_name, menu_code, menu_type, path, component, icon, url, method, sort_order, status, create_time)
VALUES (
    (SELECT id FROM t_sys_menu WHERE menu_code = 'home'), 
    '首页查看', 'home:view', 3, '/home/region-distribution', '', '', '/home/region-distribution', 'GET', 1, 1, NOW()
);

-- 查询新添加的菜单
SELECT * FROM t_sys_menu WHERE menu_code LIKE 'home%';
