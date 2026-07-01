INSERT INTO t_sys_menu (menu_name, path, component, icon, menu_type, parent_id, sort_order, status, permission, create_time)
VALUES ('质控管理', '/quality', '', 'el-icon-s-data', 1, 0, 7, 1, '', NOW());

SET @quality_parent_id = LAST_INSERT_ID();

INSERT INTO t_sys_menu (menu_name, path, component, icon, menu_type, parent_id, sort_order, status, permission, create_time)
VALUES ('虚假签收', '/quality/fake-sign', 'KpiSignRecord', '', 2, @quality_parent_id, 1, 1, '', NOW());

SET @fake_sign_menu_id = LAST_INSERT_ID();

INSERT INTO t_sys_menu (menu_name, path, component, icon, menu_type, parent_id, sort_order, status, permission, create_time)
VALUES ('查询虚假签收记录', '', '', '', 3, @fake_sign_menu_id, 1, 1, 'query', NOW());

INSERT INTO t_sys_menu (menu_name, path, component, icon, menu_type, parent_id, sort_order, status, permission, create_time)
VALUES ('删除虚假签收记录', '', '', '', 3, @fake_sign_menu_id, 2, 1, 'delete', NOW());

INSERT INTO t_sys_menu (menu_name, path, component, icon, menu_type, parent_id, sort_order, status, permission, create_time)
VALUES ('导入虚假签收记录', '', '', '', 3, @fake_sign_menu_id, 3, 1, 'import', NOW());

INSERT INTO t_sys_role_menu (role_id, menu_id) VALUES (1, @quality_parent_id);
INSERT INTO t_sys_role_menu (role_id, menu_id) VALUES (1, @fake_sign_menu_id);

INSERT INTO t_sys_role_menu (role_id, menu_id) SELECT 1, id FROM t_sys_menu WHERE parent_id = @fake_sign_menu_id;