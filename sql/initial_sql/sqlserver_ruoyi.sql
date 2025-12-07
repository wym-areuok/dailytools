/* 1、部门表 */
IF OBJECT_ID('dbo.sys_dept', 'U') IS NOT NULL DROP TABLE dbo.sys_dept;
CREATE TABLE dbo.sys_dept (
  dept_id           bigint          IDENTITY(200,1) NOT NULL,
  parent_id         bigint          DEFAULT 0,
  ancestors         varchar(50)     DEFAULT '',
  dept_name         nvarchar(30)    DEFAULT '',
  order_num         int             DEFAULT 0,
  leader            nvarchar(20)    DEFAULT NULL,
  phone             varchar(11)     DEFAULT NULL,
  email             varchar(50)     DEFAULT NULL,
  status            char(1)         DEFAULT '0',
  del_flag          char(1)         DEFAULT '0',
  create_by         nvarchar(64)    DEFAULT '',
  create_time       datetime        DEFAULT GETDATE(),
  update_by         nvarchar(64)    DEFAULT '',
  update_time       datetime        NULL,
  PRIMARY KEY (dept_id)
);

SET IDENTITY_INSERT dbo.sys_dept ON;
INSERT INTO dbo.sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time) 
VALUES
(100, 0, '0', N'若依科技', 0, N'若依', '15888888888', 'ry@qq.com', '0', '0', N'admin', GETDATE()),
(101, 100, '0,100', N'深圳总公司', 1, N'若依', '15888888888', 'ry@qq.com', '0', '0', N'admin', GETDATE()),
(102, 100, '0,100', N'长沙分公司', 2, N'若依', '15888888888', 'ry@qq.com', '0', '0', N'admin', GETDATE()),
(103, 101, '0,100,101', N'研发部门', 1, N'若依', '15888888888', 'ry@qq.com', '0', '0', N'admin', GETDATE()),
(104, 101, '0,100,101', N'市场部门', 2, N'若依', '15888888888', 'ry@qq.com', '0', '0', N'admin', GETDATE()),
(105, 101, '0,100,101', N'测试部门', 3, N'若依', '15888888888', 'ry@qq.com', '0', '0', N'admin', GETDATE()),
(106, 101, '0,100,101', N'财务部门', 4, N'若依', '15888888888', 'ry@qq.com', '0', '0', N'admin', GETDATE()),
(107, 101, '0,100,101', N'运维部门', 5, N'若依', '15888888888', 'ry@qq.com', '0', '0', N'admin', GETDATE()),
(108, 102, '0,100,102', N'市场部门', 1, N'若依', '15888888888', 'ry@qq.com', '0', '0', N'admin', GETDATE()),
(109, 102, '0,100,102', N'财务部门', 2, N'若依', '15888888888', 'ry@qq.com', '0', '0', N'admin', GETDATE());
SET IDENTITY_INSERT dbo.sys_dept OFF;

/* 2、用户信息表 */
IF OBJECT_ID('dbo.sys_user', 'U') IS NOT NULL DROP TABLE dbo.sys_user;
CREATE TABLE dbo.sys_user (
  user_id           bigint          IDENTITY(100,1) NOT NULL,
  dept_id           bigint          DEFAULT NULL,
  user_name         nvarchar(30)    NOT NULL,
  nick_name         nvarchar(30)    NOT NULL,
  user_type         varchar(2)      DEFAULT '00',
  email             varchar(50)     DEFAULT '',
  phonenumber       varchar(11)     DEFAULT '',
  sex               char(1)         DEFAULT '0',
  avatar            varchar(100)    DEFAULT '',
  password          varchar(100)    DEFAULT '',
  status            char(1)         DEFAULT '0',
  del_flag          char(1)         DEFAULT '0',
  login_ip          varchar(128)    DEFAULT '',
  login_date        datetime        DEFAULT GETDATE(),
  pwd_update_date   datetime        DEFAULT GETDATE(),
  create_by         nvarchar(64)    DEFAULT '',
  create_time       datetime        DEFAULT GETDATE(),
  update_by         nvarchar(64)    DEFAULT '',
  update_time       datetime        NULL,
  remark            nvarchar(500)   DEFAULT NULL,
  PRIMARY KEY (user_id)
);

SET IDENTITY_INSERT dbo.sys_user ON;
INSERT INTO dbo.sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, update_by, update_time, remark)
VALUES
(1, 103, N'admin', N'若依', '00', 'ry@163.com', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', GETDATE(), GETDATE(), N'admin', GETDATE(), '', NULL, N'管理员'),
(2, 105, N'ry', N'若依', '00', 'ry@qq.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', GETDATE(), GETDATE(), N'admin', GETDATE(), '', NULL, N'测试员');
SET IDENTITY_INSERT dbo.sys_user OFF;

/* 3、岗位信息表 */
IF OBJECT_ID('dbo.sys_post', 'U') IS NOT NULL DROP TABLE dbo.sys_post;
CREATE TABLE dbo.sys_post (
  post_id       bigint          IDENTITY(1,1) NOT NULL,
  post_code     varchar(64)     NOT NULL,
  post_name     nvarchar(50)    NOT NULL,
  post_sort     int             NOT NULL,
  status        char(1)         NOT NULL,
  create_by     nvarchar(64)    DEFAULT '',
  create_time   datetime        DEFAULT GETDATE(),
  update_by     nvarchar(64)    DEFAULT '',
  update_time   datetime        NULL,
  remark        nvarchar(500)   DEFAULT NULL,
  PRIMARY KEY (post_id)
);

SET IDENTITY_INSERT dbo.sys_post ON;
INSERT INTO dbo.sys_post (post_id, post_code, post_name, post_sort, status, create_by, create_time, update_by, update_time, remark)
VALUES
(1, 'ceo', N'董事长', 1, '0', N'admin', GETDATE(), '', NULL, ''),
(2, 'se', N'项目经理', 2, '0', N'admin', GETDATE(), '', NULL, ''),
(3, 'hr', N'人力资源', 3, '0', N'admin', GETDATE(), '', NULL, ''),
(4, 'user', N'普通员工', 4, '0', N'admin', GETDATE(), '', NULL, '');
SET IDENTITY_INSERT dbo.sys_post OFF;

/* 4、角色信息表 */
IF OBJECT_ID('dbo.sys_role', 'U') IS NOT NULL DROP TABLE dbo.sys_role;
CREATE TABLE dbo.sys_role (
  role_id              bigint          IDENTITY(100,1) NOT NULL,
  role_name            nvarchar(30)    NOT NULL,
  role_key             varchar(100)    NOT NULL,
  role_sort            int             NOT NULL,
  data_scope           char(1)         DEFAULT '1',
  menu_check_strictly  tinyint         DEFAULT 1,
  dept_check_strictly  tinyint         DEFAULT 1,
  status               char(1)         NOT NULL,
  del_flag             char(1)         DEFAULT '0',
  create_by            nvarchar(64)    DEFAULT '',
  create_time          datetime        DEFAULT GETDATE(),
  update_by            nvarchar(64)    DEFAULT '',
  update_time          datetime        NULL,
  remark               nvarchar(500)   DEFAULT NULL,
  PRIMARY KEY (role_id)
);

SET IDENTITY_INSERT dbo.sys_role ON;
INSERT INTO dbo.sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
VALUES
(1, N'超级管理员', 'admin', 1, '1', 1, 1, '0', '0', N'admin', GETDATE(), '', NULL, N'超级管理员'),
(2, N'普通角色', 'common', 2, '2', 1, 1, '0', '0', N'admin', GETDATE(), '', NULL, N'普通角色');
SET IDENTITY_INSERT dbo.sys_role OFF;

/* 5、菜单权限表 */
IF OBJECT_ID('dbo.sys_menu', 'U') IS NOT NULL DROP TABLE dbo.sys_menu;
CREATE TABLE dbo.sys_menu (
  menu_id           bigint          IDENTITY(2000,1) NOT NULL,
  menu_name         nvarchar(50)    NOT NULL,
  parent_id         bigint          DEFAULT 0,
  order_num         int             DEFAULT 0,
  path              varchar(200)    DEFAULT '',
  component         varchar(255)    DEFAULT NULL,
  [query]           varchar(255)    DEFAULT NULL,
  route_name        varchar(50)     DEFAULT '',
  is_frame          int             DEFAULT 1,
  is_cache          int             DEFAULT 0,
  menu_type         char(1)         DEFAULT '',
  visible           char(1)         DEFAULT '0',
  status            char(1)         DEFAULT '0',
  perms             varchar(100)    DEFAULT NULL,
  icon              varchar(100)    DEFAULT '#',
  create_by         nvarchar(64)    DEFAULT '',
  create_time       datetime        DEFAULT GETDATE(),
  update_by         nvarchar(64)    DEFAULT '',
  update_time       datetime        NULL,
  remark            nvarchar(500)   DEFAULT '',
  PRIMARY KEY (menu_id)
);

SET IDENTITY_INSERT dbo.sys_menu ON;
INSERT INTO dbo.sys_menu (menu_id, menu_name, parent_id, order_num, path, component, [query], route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) 
VALUES
(1, N'系统管理', 0, 1, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'system', N'admin', GETDATE()),
(2, N'系统监控', 0, 2, 'monitor', NULL, '', '', 1, 0, 'M', '0', '0', '', 'monitor', N'admin', GETDATE()),
(3, N'系统工具', 0, 3, 'tool', NULL, '', '', 1, 0, 'M', '0', '0', '', 'tool', N'admin', GETDATE()),
(4, N'若依官网', 0, 4, 'http://ruoyi.vip', NULL, '', '', 0, 0, 'M', '0', '0', '', 'guide', N'admin', GETDATE()),
(100, N'用户管理', 1, 1, 'user', 'system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', N'admin', GETDATE()),
(101, N'角色管理', 1, 2, 'role', 'system/role/index', '', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', N'admin', GETDATE()),
(102, N'菜单管理', 1, 3, 'menu', 'system/menu/index', '', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', N'admin', GETDATE()),
(103, N'部门管理', 1, 4, 'dept', 'system/dept/index', '', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', N'admin', GETDATE()),
(104, N'岗位管理', 1, 5, 'post', 'system/post/index', '', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', N'admin', GETDATE()),
(105, N'字典管理', 1, 6, 'dict', 'system/dict/index', '', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', N'admin', GETDATE()),
(106, N'参数设置', 1, 7, 'config', 'system/config/index', '', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', N'admin', GETDATE()),
(107, N'通知公告', 1, 8, 'notice', 'system/notice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', N'admin', GETDATE()),
(108, N'日志管理', 1, 9, 'log', '', '', '', 1, 0, 'M', '0', '0', '', 'log', N'admin', GETDATE()),
(109, N'在线用户', 2, 1, 'online', 'monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', N'admin', GETDATE()),
(110, N'定时任务', 2, 2, 'job', 'monitor/job/index', '', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', N'admin', GETDATE()),
(111, N'数据监控', 2, 3, 'druid', 'monitor/druid/index', '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', N'admin', GETDATE()),
(112, N'服务监控', 2, 4, 'server', 'monitor/server/index', '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', N'admin', GETDATE()),
(113, N'缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', N'admin', GETDATE()),
(114, N'缓存列表', 2, 6, 'cacheList', 'monitor/cache/list', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis-list', N'admin', GETDATE()),
(115, N'表单构建', 3, 1, 'build', 'tool/build/index', '', '', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', N'admin', GETDATE()),
(116, N'代码生成', 3, 2, 'gen', 'tool/gen/index', '', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', N'admin', GETDATE()),
(117, N'系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', N'admin', GETDATE()),
(500, N'操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', N'admin', GETDATE()),
(501, N'登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', N'admin', GETDATE()),
(1000, N'用户查询', 100, 1, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', N'admin', GETDATE()),
(1001, N'用户新增', 100, 2, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', N'admin', GETDATE()),
(1002, N'用户修改', 100, 3, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', N'admin', GETDATE()),
(1003, N'用户删除', 100, 4, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', N'admin', GETDATE()),
(1004, N'用户导出', 100, 5, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', N'admin', GETDATE()),
(1005, N'用户导入', 100, 6, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', N'admin', GETDATE()),
(1006, N'重置密码', 100, 7, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', N'admin', GETDATE()),
(1007, N'角色查询', 101, 1, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', N'admin', GETDATE()),
(1008, N'角色新增', 101, 2, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', N'admin', GETDATE()),
(1009, N'角色修改', 101, 3, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', N'admin', GETDATE()),
(1010, N'角色删除', 101, 4, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', N'admin', GETDATE()),
(1011, N'角色导出', 101, 5, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', N'admin', GETDATE()),
(1012, N'菜单查询', 102, 1, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', N'admin', GETDATE()),
(1013, N'菜单新增', 102, 2, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', N'admin', GETDATE()),
(1014, N'菜单修改', 102, 3, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', N'admin', GETDATE()),
(1015, N'菜单删除', 102, 4, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', N'admin', GETDATE()),
(1016, N'部门查询', 103, 1, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', N'admin', GETDATE()),
(1017, N'部门新增', 103, 2, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', N'admin', GETDATE()),
(1018, N'部门修改', 103, 3, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', N'admin', GETDATE()),
(1019, N'部门删除', 103, 4, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', N'admin', GETDATE()),
(1020, N'岗位查询', 104, 1, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', N'admin', GETDATE()),
(1021, N'岗位新增', 104, 2, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', N'admin', GETDATE()),
(1022, N'岗位修改', 104, 3, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', N'admin', GETDATE()),
(1023, N'岗位删除', 104, 4, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', N'admin', GETDATE()),
(1024, N'岗位导出', 104, 5, '', NULL, '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', N'admin', GETDATE()),
(1025, N'字典查询', 105, 1, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', N'admin', GETDATE()),
(1026, N'字典新增', 105, 2, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', N'admin', GETDATE()),
(1027, N'字典修改', 105, 3, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', N'admin', GETDATE()),
(1028, N'字典删除', 105, 4, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', N'admin', GETDATE()),
(1029, N'字典导出', 105, 5, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', N'admin', GETDATE()),
(1030, N'参数查询', 106, 1, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', N'admin', GETDATE()),
(1031, N'参数新增', 106, 2, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', N'admin', GETDATE()),
(1032, N'参数修改', 106, 3, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', N'admin', GETDATE()),
(1033, N'参数删除', 106, 4, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', N'admin', GETDATE()),
(1034, N'参数导出', 106, 5, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', N'admin', GETDATE()),
(1035, N'公告查询', 107, 1, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', N'admin', GETDATE()),
(1036, N'公告新增', 107, 2, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', N'admin', GETDATE()),
(1037, N'公告修改', 107, 3, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', N'admin', GETDATE()),
(1038, N'公告删除', 107, 4, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', N'admin', GETDATE()),
(1039, N'操作查询', 500, 1, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', N'admin', GETDATE()),
(1040, N'操作删除', 500, 2, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', N'admin', GETDATE()),
(1041, N'日志导出', 500, 3, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', N'admin', GETDATE()),
(1042, N'登录查询', 501, 1, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', N'admin', GETDATE()),
(1043, N'登录删除', 501, 2, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', N'admin', GETDATE()),
(1044, N'日志导出', 501, 3, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', N'admin', GETDATE()),
(1045, N'账户解锁', 501, 4, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', N'admin', GETDATE()),
(1046, N'在线查询', 109, 1, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', N'admin', GETDATE()),
(1047, N'批量强退', 109, 2, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', N'admin', GETDATE()),
(1048, N'单条强退', 109, 3, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', N'admin', GETDATE()),
(1049, N'任务查询', 110, 1, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', N'admin', GETDATE()),
(1050, N'任务新增', 110, 2, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', N'admin', GETDATE()),
(1051, N'任务修改', 110, 3, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', N'admin', GETDATE()),
(1052, N'任务删除', 110, 4, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', N'admin', GETDATE()),
(1053, N'状态修改', 110, 5, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', N'admin', GETDATE()),
(1054, N'任务导出', 110, 6, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', N'admin', GETDATE()),
(1055, N'生成查询', 116, 1, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', N'admin', GETDATE()),
(1056, N'生成修改', 116, 2, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', N'admin', GETDATE()),
(1057, N'生成删除', 116, 3, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', N'admin', GETDATE()),
(1058, N'导入代码', 116, 4, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', N'admin', GETDATE()),
(1059, N'预览代码', 116, 5, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', N'admin', GETDATE()),
(1060, N'生成代码', 116, 6, '#', NULL, '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', N'admin', GETDATE());
SET IDENTITY_INSERT dbo.sys_menu OFF;

/* 6、用户和角色关联表 */
IF OBJECT_ID('dbo.sys_user_role', 'U') IS NOT NULL DROP TABLE dbo.sys_user_role;
CREATE TABLE dbo.sys_user_role (
    user_id   bigint NOT NULL,
    role_id   bigint NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

INSERT INTO dbo.sys_user_role (user_id, role_id) 
VALUES 
(1, 1),
(2, 2);

/* 7、角色和菜单关联表 */
IF OBJECT_ID('dbo.sys_role_menu', 'U') IS NOT NULL DROP TABLE dbo.sys_role_menu;
CREATE TABLE dbo.sys_role_menu (
    role_id   bigint NOT NULL,
    menu_id   bigint NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

INSERT INTO dbo.sys_role_menu (role_id, menu_id) 
VALUES 
(2, 1),
(2, 2),
(2, 3),
(2, 4),
(2, 100),
(2, 101),
(2, 102),
(2, 103),
(2, 104),
(2, 105),
(2, 106),
(2, 107),
(2, 108),
(2, 109),
(2, 110),
(2, 111),
(2, 112),
(2, 113),
(2, 114),
(2, 115),
(2, 116),
(2, 117),
(2, 500),
(2, 501),
(2, 1000),
(2, 1001),
(2, 1002),
(2, 1003),
(2, 1004),
(2, 1005),
(2, 1006),
(2, 1007),
(2, 1008),
(2, 1009),
(2, 1010),
(2, 1011),
(2, 1012),
(2, 1013),
(2, 1014),
(2, 1015),
(2, 1016),
(2, 1017),
(2, 1018),
(2, 1019),
(2, 1020),
(2, 1021),
(2, 1022),
(2, 1023),
(2, 1024),
(2, 1025),
(2, 1026),
(2, 1027),
(2, 1028),
(2, 1029),
(2, 1030),
(2, 1031),
(2, 1032),
(2, 1033),
(2, 1034),
(2, 1035),
(2, 1036),
(2, 1037),
(2, 1038),
(2, 1039),
(2, 1040),
(2, 1041),
(2, 1042),
(2, 1043),
(2, 1044),
(2, 1045),
(2, 1046),
(2, 1047),
(2, 1048),
(2, 1049),
(2, 1050),
(2, 1051),
(2, 1052),
(2, 1053),
(2, 1054),
(2, 1055),
(2, 1056),
(2, 1057),
(2, 1058),
(2, 1059),
(2, 1060);

/* 8、角色和部门关联表 */
IF OBJECT_ID('dbo.sys_role_dept', 'U') IS NOT NULL DROP TABLE dbo.sys_role_dept;
CREATE TABLE dbo.sys_role_dept (
    role_id   bigint NOT NULL,
    dept_id   bigint NOT NULL,
    PRIMARY KEY (role_id, dept_id)
);

INSERT INTO dbo.sys_role_dept (role_id, dept_id) 
VALUES 
(2, 100),
(2, 101),
(2, 105);

/* 9、用户与岗位关联表 */
IF OBJECT_ID('dbo.sys_user_post', 'U') IS NOT NULL DROP TABLE dbo.sys_user_post;
CREATE TABLE dbo.sys_user_post (
    user_id   bigint NOT NULL,
    post_id   bigint NOT NULL,
    PRIMARY KEY (user_id, post_id)
);

INSERT INTO dbo.sys_user_post (user_id, post_id) 
VALUES 
(1, 1),
(2, 2);

/* 10、操作日志记录 */
IF OBJECT_ID('dbo.sys_oper_log', 'U') IS NOT NULL DROP TABLE dbo.sys_oper_log;
CREATE TABLE dbo.sys_oper_log (
    oper_id           bigint          IDENTITY(100,1) NOT NULL,
    title             nvarchar(50)    DEFAULT '',
    business_type     int             DEFAULT 0,
    method            varchar(200)    DEFAULT '',
    request_method    varchar(10)     DEFAULT '',
    operator_type     int             DEFAULT 0,
    oper_name         nvarchar(50)    DEFAULT '',
    dept_name         nvarchar(50)    DEFAULT '',
    oper_url          varchar(255)    DEFAULT '',
    oper_ip           varchar(128)    DEFAULT '',
    oper_location     nvarchar(255)   DEFAULT '',
    oper_param        varchar(2000)   DEFAULT '',
    json_result       varchar(2000)   DEFAULT '',
    status            int             DEFAULT 0,
    error_msg         nvarchar(2000)  DEFAULT '',
    oper_time         datetime        DEFAULT GETDATE(),
    cost_time         bigint          DEFAULT 0,
    PRIMARY KEY (oper_id)
);

CREATE INDEX idx_sys_oper_log_bt ON dbo.sys_oper_log(business_type);
CREATE INDEX idx_sys_oper_log_s  ON dbo.sys_oper_log(status);
CREATE INDEX idx_sys_oper_log_ot ON dbo.sys_oper_log(oper_time);

/* 11、字典类型表 */
IF OBJECT_ID('dbo.sys_dict_type', 'U') IS NOT NULL DROP TABLE dbo.sys_dict_type;
CREATE TABLE dbo.sys_dict_type (
    dict_id          bigint          IDENTITY(100,1) NOT NULL,
    dict_name        nvarchar(100)   DEFAULT '',
    dict_type        varchar(100)    DEFAULT '',
    status           char(1)         DEFAULT '0',
    create_by        nvarchar(64)    DEFAULT '',
    create_time      datetime        DEFAULT GETDATE(),
    update_by        nvarchar(64)    DEFAULT '',
    update_time      datetime        NULL,
    remark           nvarchar(500)   DEFAULT NULL,
    PRIMARY KEY (dict_id),
    CONSTRAINT UQ_dict_type UNIQUE (dict_type)
);

SET IDENTITY_INSERT dbo.sys_dict_type ON;
INSERT INTO dbo.sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) 
VALUES 
(1, N'用户性别', 'sys_user_sex', '0', N'admin', GETDATE(), '', NULL, N'用户性别列表'),
(2, N'菜单状态', 'sys_show_hide', '0', N'admin', GETDATE(), '', NULL, N'菜单状态列表'),
(3, N'系统开关', 'sys_normal_disable', '0', N'admin', GETDATE(), '', NULL, N'系统开关列表'),
(4, N'任务状态', 'sys_job_status', '0', N'admin', GETDATE(), '', NULL, N'任务状态列表'),
(5, N'任务分组', 'sys_job_group', '0', N'admin', GETDATE(), '', NULL, N'任务分组列表'),
(6, N'系统是否', 'sys_yes_no', '0', N'admin', GETDATE(), '', NULL, N'系统是否列表'),
(7, N'通知类型', 'sys_notice_type', '0', N'admin', GETDATE(), '', NULL, N'通知类型列表'),
(8, N'通知状态', 'sys_notice_status', '0', N'admin', GETDATE(), '', NULL, N'通知状态列表'),
(9, N'操作类型', 'sys_oper_type', '0', N'admin', GETDATE(), '', NULL, N'操作类型列表'),
(10, N'系统状态', 'sys_common_status', '0', N'admin', GETDATE(), '', NULL, N'登录状态列表');
SET IDENTITY_INSERT dbo.sys_dict_type OFF;

/* 12、字典数据表 */
IF OBJECT_ID('dbo.sys_dict_data', 'U') IS NOT NULL DROP TABLE dbo.sys_dict_data;
CREATE TABLE dbo.sys_dict_data (
    dict_code        bigint          IDENTITY(100,1) NOT NULL,
    dict_sort        int             DEFAULT 0,
    dict_label       nvarchar(100)   DEFAULT '',
    dict_value       varchar(100)    DEFAULT '',
    dict_type        varchar(100)    DEFAULT '',
    css_class        varchar(100)    DEFAULT NULL,
    list_class       varchar(100)    DEFAULT NULL,
    is_default       char(1)         DEFAULT 'N',
    status           char(1)         DEFAULT '0',
    create_by        nvarchar(64)    DEFAULT '',
    create_time      datetime        DEFAULT GETDATE(),
    update_by        nvarchar(64)    DEFAULT '',
    update_time      datetime        NULL,
    remark           nvarchar(500)   DEFAULT NULL,
    PRIMARY KEY (dict_code)
);

SET IDENTITY_INSERT dbo.sys_dict_data ON;
INSERT INTO dbo.sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time) 
VALUES 
(1,  1, N'男', '0', 'sys_user_sex', '', '', 'Y', '0', N'admin', GETDATE()),
(2,  2, N'女', '1', 'sys_user_sex', '', '', 'N', '0', N'admin', GETDATE()),
(3,  3, N'未知', '2', 'sys_user_sex', '', '', 'N', '0', N'admin', GETDATE()),
(4,  1, N'显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', N'admin', GETDATE()),
(5,  2, N'隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', N'admin', GETDATE()),
(6,  1, N'正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', N'admin', GETDATE()),
(7,  2, N'停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', N'admin', GETDATE()),
(8,  1, N'正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', N'admin', GETDATE()),
(9,  2, N'暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', N'admin', GETDATE()),
(10, 1, N'默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', N'admin', GETDATE()),
(11, 2, N'系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', N'admin', GETDATE()),
(12, 1, N'是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', N'admin', GETDATE()),
(13, 2, N'否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', N'admin', GETDATE()),
(14, 1, N'通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', N'admin', GETDATE()),
(15, 2, N'公告', '2', 'sys_notice_type', '', 'success', 'N', '0', N'admin', GETDATE()),
(16, 1, N'正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', N'admin', GETDATE()),
(17, 2, N'关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', N'admin', GETDATE()),
(18, 99, N'其他', '0', 'sys_oper_type', '', 'info', 'N', '0', N'admin', GETDATE()),
(19, 1, N'新增', '1', 'sys_oper_type', '', 'info', 'N', '0', N'admin', GETDATE()),
(20, 2, N'修改', '2', 'sys_oper_type', '', 'info', 'N', '0', N'admin', GETDATE()),
(21, 3, N'删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', N'admin', GETDATE()),
(22, 4, N'授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', N'admin', GETDATE()),
(23, 5, N'导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', N'admin', GETDATE()),
(24, 6, N'导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', N'admin', GETDATE()),
(25, 7, N'强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', N'admin', GETDATE()),
(26, 8, N'生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', N'admin', GETDATE()),
(27, 9, N'清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', N'admin', GETDATE()),
(28, 1, N'成功', '0', 'sys_common_status', '', 'primary', 'N', '0', N'admin', GETDATE()),
(29, 2, N'失败', '1', 'sys_common_status', '', 'danger', 'N', '0', N'admin', GETDATE());
SET IDENTITY_INSERT dbo.sys_dict_data OFF;

/* 13、参数配置表 */
IF OBJECT_ID('dbo.sys_config', 'U') IS NOT NULL DROP TABLE dbo.sys_config;
CREATE TABLE dbo.sys_config (
    config_id         int             IDENTITY(100,1) NOT NULL,
    config_name       nvarchar(100)   DEFAULT '',
    config_key        varchar(100)    DEFAULT '',
    config_value      nvarchar(500)   DEFAULT '',
    config_type       char(1)         DEFAULT 'N',
    create_by         nvarchar(64)    DEFAULT '',
    create_time       datetime        DEFAULT GETDATE(),
    update_by         nvarchar(64)    DEFAULT '',
    update_time       datetime        NULL,
    remark            nvarchar(500)   DEFAULT NULL,
    PRIMARY KEY (config_id)
);

SET IDENTITY_INSERT dbo.sys_config ON;
INSERT INTO dbo.sys_config (config_id, config_name, config_key, config_value, config_type, create_by, create_time) 
VALUES 
(1, N'主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', N'admin', GETDATE()),
(2, N'用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', N'admin', GETDATE()),
(3, N'主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', N'admin', GETDATE()),
(4, N'账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', N'admin', GETDATE()),
(5, N'账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', N'admin', GETDATE()),
(6, N'用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', N'admin', GETDATE()),
(7, N'用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 'Y', N'admin', GETDATE()),
(8, N'用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 'Y', N'admin', GETDATE());
SET IDENTITY_INSERT dbo.sys_config OFF;

/* 14、系统访问记录 */
IF OBJECT_ID('dbo.sys_logininfor', 'U') IS NOT NULL DROP TABLE dbo.sys_logininfor;
CREATE TABLE dbo.sys_logininfor (
    info_id        bigint          IDENTITY(100,1) NOT NULL,
    user_name      nvarchar(50)    DEFAULT '',
    ipaddr         varchar(128)    DEFAULT '',
    login_location nvarchar(255)   DEFAULT '',
    browser        varchar(50)     DEFAULT '',
    os             varchar(50)     DEFAULT '',
    status         char(1)         DEFAULT '0',
    msg            nvarchar(255)   DEFAULT '',
    login_time     datetime        DEFAULT GETDATE(),
    PRIMARY KEY (info_id)
);

CREATE INDEX idx_sys_logininfor_s  ON dbo.sys_logininfor(status);
CREATE INDEX idx_sys_logininfor_lt ON dbo.sys_logininfor(login_time);

/* 15、定时任务调度表 */
IF OBJECT_ID('dbo.sys_job', 'U') IS NOT NULL DROP TABLE dbo.sys_job;
CREATE TABLE dbo.sys_job (
    job_id              bigint          IDENTITY(100,1) NOT NULL,
    job_name            nvarchar(64)    DEFAULT '',
    job_group           varchar(64)     DEFAULT 'DEFAULT',
    invoke_target       varchar(500)    NOT NULL,
    cron_expression     varchar(255)    DEFAULT '',
    misfire_policy      varchar(20)     DEFAULT '3',
    concurrent          char(1)         DEFAULT '1',
    status              char(1)         DEFAULT '0',
    create_by           nvarchar(64)    DEFAULT '',
    create_time         datetime        DEFAULT GETDATE(),
    update_by           nvarchar(64)    DEFAULT '',
    update_time         datetime        NULL,
    remark              nvarchar(500)   DEFAULT '',
    PRIMARY KEY (job_id)
);

SET IDENTITY_INSERT dbo.sys_job ON;
INSERT INTO dbo.sys_job (job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time) 
VALUES 
(1, N'系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', N'admin', GETDATE()),
(2, N'系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(''ry'')', '0/15 * * * * ?', '3', '1', '1', N'admin', GETDATE()),
(3, N'系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(''ry'', true, 2000, 316.50, 100)', '0/20 * * * * ?', '3', '1', '1', N'admin', GETDATE());
SET IDENTITY_INSERT dbo.sys_job OFF;

/* 16、定时任务调度日志表 */
IF OBJECT_ID('dbo.sys_job_log', 'U') IS NOT NULL DROP TABLE dbo.sys_job_log;
CREATE TABLE dbo.sys_job_log (
    job_log_id          bigint          IDENTITY(100,1) NOT NULL,
    job_name            nvarchar(64)    NOT NULL,
    job_group           varchar(64)     NOT NULL,
    invoke_target       varchar(500)    NOT NULL,
    job_message         nvarchar(500)   DEFAULT NULL,
    status              char(1)         DEFAULT '0',
    exception_info      nvarchar(2000)  DEFAULT '',
    create_time         datetime        DEFAULT GETDATE(),
    PRIMARY KEY (job_log_id)
);

/* 17、通知公告表 */
IF OBJECT_ID('dbo.sys_notice', 'U') IS NOT NULL DROP TABLE dbo.sys_notice;
CREATE TABLE dbo.sys_notice (
    notice_id         int             IDENTITY(10,1) NOT NULL,
    notice_title      nvarchar(50)    NOT NULL,
    notice_type       char(1)         NOT NULL,
    notice_content    nvarchar(MAX)   DEFAULT NULL,
    status            char(1)         DEFAULT '0',
    create_by         nvarchar(64)    DEFAULT '',
    create_time       datetime        DEFAULT GETDATE(),
    update_by         nvarchar(64)    DEFAULT '',
    update_time       datetime        NULL,
    remark            nvarchar(255)   DEFAULT NULL,
    PRIMARY KEY (notice_id)
);

SET IDENTITY_INSERT dbo.sys_notice ON;
INSERT INTO dbo.sys_notice (notice_id, notice_title, notice_type, notice_content, status, create_by, create_time) 
VALUES 
(1, N'温馨提醒：2018-07-01 若依新版本发布啦', '2', N'新版本内容', '0', N'admin', GETDATE()),
(2, N'维护通知：2018-07-01 若依系统凌晨维护', '1', N'维护内容', '0', N'admin', GETDATE());
SET IDENTITY_INSERT dbo.sys_notice OFF;

/* 18、代码生成业务表 */
IF OBJECT_ID('dbo.gen_table', 'U') IS NOT NULL DROP TABLE dbo.gen_table;
CREATE TABLE dbo.gen_table (
    table_id          bigint          IDENTITY(1,1) NOT NULL,
    table_name        varchar(200)    DEFAULT '',
    table_comment     nvarchar(500)   DEFAULT '',
    sub_table_name    varchar(64)     DEFAULT NULL,
    sub_table_fk_name varchar(64)     DEFAULT NULL,
    class_name        varchar(100)    DEFAULT '',
    tpl_category      varchar(200)    DEFAULT 'crud',
    tpl_web_type      varchar(30)     DEFAULT '',
    package_name      varchar(100)    DEFAULT '',
    module_name       varchar(30)     DEFAULT '',
    business_name     varchar(30)     DEFAULT '',
    function_name     nvarchar(50)    DEFAULT '',
    function_author   nvarchar(50)    DEFAULT '',
    gen_type          char(1)         DEFAULT '0',
    gen_path          varchar(200)    DEFAULT '/',
    options           varchar(1000)   DEFAULT NULL,
    create_by         nvarchar(64)    DEFAULT '',
    create_time       datetime        DEFAULT GETDATE(),
    update_by         nvarchar(64)    DEFAULT '',
    update_time       datetime        NULL,
    remark            nvarchar(500)   DEFAULT NULL,
    PRIMARY KEY (table_id)
);

/* 19、代码生成业务表字段 */
IF OBJECT_ID('dbo.gen_table_column', 'U') IS NOT NULL DROP TABLE dbo.gen_table_column;
CREATE TABLE dbo.gen_table_column (
    column_id         bigint          IDENTITY(1,1) NOT NULL,
    table_id          bigint          DEFAULT NULL,
    column_name       varchar(200)    DEFAULT NULL,
    column_comment    nvarchar(500)   DEFAULT NULL,
    column_type       varchar(100)    DEFAULT NULL,
    java_type         varchar(500)    DEFAULT NULL,
    java_field        varchar(200)    DEFAULT NULL,
    is_pk             char(1)         DEFAULT NULL,
    is_increment      char(1)         DEFAULT NULL,
    is_required       char(1)         DEFAULT NULL,
    is_insert         char(1)         DEFAULT NULL,
    is_edit           char(1)         DEFAULT NULL,
    is_list           char(1)         DEFAULT NULL,
    is_query          char(1)         DEFAULT NULL,
    query_type        varchar(200)    DEFAULT 'EQ',
    html_type         varchar(200)    DEFAULT NULL,
    dict_type         varchar(200)    DEFAULT '',
    sort              int             DEFAULT NULL,
    create_by         nvarchar(64)    DEFAULT '',
    create_time       datetime        DEFAULT GETDATE(),
    update_by         nvarchar(64)    DEFAULT '',
    update_time       datetime        NULL,
    PRIMARY KEY (column_id)
);