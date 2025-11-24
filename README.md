## 平台简介

* 前端采用Vue、Element UI。
* 后端采用Spring Boot、Spring Security、Redis & Jwt。
* 权限认证使用Jwt，支持多终端认证系统。
* 支持加载动态权限菜单，多方式轻松权限控制。
* 高效率开发，使用代码生成器可以一键生成前后端代码。

## 内置功能

1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
13. 代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。
14. 系统接口：根据业务代码自动生成相关的api接口文档。
15. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
16. 缓存监控：对系统的缓存信息查询，命令统计等。
17. 在线构建器：拖动表单元素生成相应的HTML代码。
18. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。

## 备注
1. sql脚本在不同服务器的sqlserver导入时要根据sqlserver的安装路径修改脚本中的路径和指定编码(导出sql时如指定则忽略)：
```javascript
FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER02\MSSQL\DATA

COLLATE Chinese_Taiwan_Stroke_BIN

/*示例代码*/
USE [master]
GO
/****** Object:  Database [dailytools]    Script Date: 2025/11/18 23:53:04 ******/
CREATE DATABASE [dailytools]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'dailytools', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER02\MSSQL\DATA\dailytools.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'dailytools_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER02\MSSQL\DATA\dailytools_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 COLLATE Chinese_Taiwan_Stroke_BIN
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
```
2.stringTool模块相关的表string_tool_temp(用于大数量级字符串导入到db存储)
```javascript
-- 如果存在旧表，先删除
DROP TABLE IF EXISTS string_tool_temp;
-- 创建新表 id自增1
CREATE TABLE string_tool_temp (
    id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
    data NVARCHAR(100) NOT NULL,
    user_id INT NOT NULL,
    -- 在创建表的同时创建索引
    INDEX IX_string_tool_temp_user_id NONCLUSTERED (user_id)
);
```