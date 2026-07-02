INSERT IGNORE INTO t_sys_menu (menu_name, menu_code, menu_type, path, component, icon, url, method, parent_id, sort_order, status)
VALUES ('质控管理', 'quality', 1, '/quality', '', 'el-icon-s-data', '', '', 0, 7, 1);

SET @quality_parent_id = LAST_INSERT_ID();

INSERT IGNORE INTO t_sys_menu (menu_name, menu_code, menu_type, path, component, icon, url, method, parent_id, sort_order, status)
VALUES ('虚假签收', 'quality:fake-sign', 2, '/quality/fake-sign', 'KpiSignRecord', '', '', '', @quality_parent_id, 1, 1);

SET @fake_sign_menu_id = LAST_INSERT_ID();

INSERT IGNORE INTO t_sys_menu (menu_name, menu_code, menu_type, path, component, icon, url, method, parent_id, sort_order, status)
VALUES ('查询虚假签收记录', 'quality:fake-sign:query', 3, '', '', '', '/kpi/sign-record/page', 'GET', @fake_sign_menu_id, 1, 1);

INSERT IGNORE INTO t_sys_menu (menu_name, menu_code, menu_type, path, component, icon, url, method, parent_id, sort_order, status)
VALUES ('删除虚假签收记录', 'quality:fake-sign:delete', 3, '', '', '', '/kpi/sign-record/{id}', 'DELETE', @fake_sign_menu_id, 2, 1);

INSERT IGNORE INTO t_sys_menu (menu_name, menu_code, menu_type, path, component, icon, url, method, parent_id, sort_order, status)
VALUES ('导入虚假签收记录', 'quality:fake-sign:import', 3, '', '', '', '/kpi/sign-record/import', 'POST', @fake_sign_menu_id, 3, 1);

INSERT IGNORE INTO t_sys_menu (menu_name, menu_code, menu_type, path, component, icon, url, method, parent_id, sort_order, status)
VALUES ('月度汇总', 'quality:fake-sign:summary', 3, '', '', '', '/kpi/sign-record/summary/{month}', 'GET', @fake_sign_menu_id, 4, 1);

INSERT IGNORE INTO t_sys_menu (menu_name, menu_code, menu_type, path, component, icon, url, method, parent_id, sort_order, status)
VALUES ('小件员排名', 'quality:fake-sign:courier-rank', 3, '', '', '', '/kpi/sign-record/courier-rank/{month}', 'GET', @fake_sign_menu_id, 5, 1);

INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id) VALUES (1, @quality_parent_id);
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id) VALUES (1, @fake_sign_menu_id);
INSERT IGNORE INTO t_sys_role_menu (role_id, menu_id) SELECT 1, id FROM t_sys_menu WHERE parent_id = @fake_sign_menu_id;