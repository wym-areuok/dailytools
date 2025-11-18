IF OBJECT_ID('dbo.QRTZ_FIRED_TRIGGERS', 'U') IS NOT NULL DROP TABLE dbo.QRTZ_FIRED_TRIGGERS;
IF OBJECT_ID('dbo.QRTZ_PAUSED_TRIGGER_GRPS', 'U') IS NOT NULL DROP TABLE dbo.QRTZ_PAUSED_TRIGGER_GRPS;
IF OBJECT_ID('dbo.QRTZ_SCHEDULER_STATE', 'U') IS NOT NULL DROP TABLE dbo.QRTZ_SCHEDULER_STATE;
IF OBJECT_ID('dbo.QRTZ_LOCKS', 'U') IS NOT NULL DROP TABLE dbo.QRTZ_LOCKS;
IF OBJECT_ID('dbo.QRTZ_SIMPLE_TRIGGERS', 'U') IS NOT NULL DROP TABLE dbo.QRTZ_SIMPLE_TRIGGERS;
IF OBJECT_ID('dbo.QRTZ_SIMPROP_TRIGGERS', 'U') IS NOT NULL DROP TABLE dbo.QRTZ_SIMPROP_TRIGGERS;
IF OBJECT_ID('dbo.QRTZ_CRON_TRIGGERS', 'U') IS NOT NULL DROP TABLE dbo.QRTZ_CRON_TRIGGERS;
IF OBJECT_ID('dbo.QRTZ_BLOB_TRIGGERS', 'U') IS NOT NULL DROP TABLE dbo.QRTZ_BLOB_TRIGGERS;
IF OBJECT_ID('dbo.QRTZ_TRIGGERS', 'U') IS NOT NULL DROP TABLE dbo.QRTZ_TRIGGERS;
IF OBJECT_ID('dbo.QRTZ_JOB_DETAILS', 'U') IS NOT NULL DROP TABLE dbo.QRTZ_JOB_DETAILS;
IF OBJECT_ID('dbo.QRTZ_CALENDARS', 'U') IS NOT NULL DROP TABLE dbo.QRTZ_CALENDARS;

-- ----------------------------
-- 1、存储每一个已配置的 jobDetail 的详细信息
-- ----------------------------
CREATE TABLE dbo.QRTZ_JOB_DETAILS (
    sched_name           varchar(120)    NOT NULL,
    job_name             varchar(200)    NOT NULL,
    job_group            varchar(200)    NOT NULL,
    description          nvarchar(250)   NULL,
    job_class_name       varchar(250)    NOT NULL,
    is_durable           varchar(1)      NOT NULL,
    is_nonconcurrent     varchar(1)      NOT NULL,
    is_update_data       varchar(1)      NOT NULL,
    requests_recovery    varchar(1)      NOT NULL,
    job_data             varbinary(max)  NULL,
    PRIMARY KEY (sched_name, job_name, job_group)
);

-- ----------------------------
-- 2、 存储已配置的 Trigger 的信息
-- ----------------------------
CREATE TABLE dbo.QRTZ_TRIGGERS (
    sched_name           varchar(120)    NOT NULL,
    trigger_name         varchar(200)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    job_name             varchar(200)    NOT NULL,
    job_group            varchar(200)    NOT NULL,
    description          nvarchar(250)   NULL,
    next_fire_time       bigint          NULL,
    prev_fire_time       bigint          NULL,
    priority             int             NULL,
    trigger_state        varchar(16)     NOT NULL,
    trigger_type         varchar(8)      NOT NULL,
    start_time           bigint          NOT NULL,
    end_time             bigint          NULL,
    calendar_name        varchar(200)    NULL,
    misfire_instr        smallint        NULL,
    job_data             varbinary(max)  NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, job_name, job_group) REFERENCES QRTZ_JOB_DETAILS(sched_name, job_name, job_group)
);

-- ----------------------------
-- 3、 存储简单的 Trigger，包括重复次数，间隔，以及已触发的次数
-- ----------------------------
CREATE TABLE dbo.QRTZ_SIMPLE_TRIGGERS (
    sched_name           varchar(120)    NOT NULL,
    trigger_name         varchar(200)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    repeat_count         bigint          NOT NULL,
    repeat_interval      bigint          NOT NULL,
    times_triggered      bigint          NOT NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
);

-- ----------------------------
-- 4、 存储 Cron Trigger，包括 Cron 表达式和时区信息
-- ---------------------------- 
CREATE TABLE dbo.QRTZ_CRON_TRIGGERS (
    sched_name           varchar(120)    NOT NULL,
    trigger_name         varchar(200)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    cron_expression      varchar(200)    NOT NULL,
    time_zone_id         varchar(80)     NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
);

-- ----------------------------
-- 5、 Trigger 作为 Blob 类型存储(用于 Quartz 用户用 JDBC 创建他们自己定制的 Trigger 类型，JobStore 并不知道如何存储实例的时候)
-- ---------------------------- 
CREATE TABLE dbo.QRTZ_BLOB_TRIGGERS (
    sched_name           varchar(120)    NOT NULL,
    trigger_name         varchar(200)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    blob_data            varbinary(max)  NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
);

-- ----------------------------
-- 6、 以 Blob 类型存储存放日历信息， quartz可配置一个日历来指定一个时间范围
-- ---------------------------- 
CREATE TABLE dbo.QRTZ_CALENDARS (
    sched_name           varchar(120)    NOT NULL,
    calendar_name        varchar(200)    NOT NULL,
    calendar             varbinary(max)  NOT NULL,
    PRIMARY KEY (sched_name, calendar_name)
);

-- ----------------------------
-- 7、 存储已暂停的 Trigger 组的信息
-- ---------------------------- 
CREATE TABLE dbo.QRTZ_PAUSED_TRIGGER_GRPS (
    sched_name           varchar(120)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    PRIMARY KEY (sched_name, trigger_group)
);

-- ----------------------------
-- 8、 存储与已触发的 Trigger 相关的状态信息，以及相联 Job 的执行信息
-- ---------------------------- 
CREATE TABLE dbo.QRTZ_FIRED_TRIGGERS (
    sched_name           varchar(120)    NOT NULL,
    entry_id             varchar(95)     NOT NULL,
    trigger_name         varchar(200)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    instance_name        varchar(200)    NOT NULL,
    fired_time           bigint          NOT NULL,
    sched_time           bigint          NOT NULL,
    priority             int             NOT NULL,
    state                varchar(16)     NOT NULL,
    job_name             varchar(200)    NULL,
    job_group            varchar(200)    NULL,
    is_nonconcurrent     varchar(1)      NULL,
    requests_recovery    varchar(1)      NULL,
    PRIMARY KEY (sched_name, entry_id)
);

-- ----------------------------
-- 9、 存储少量的有关 Scheduler 的状态信息，假如是用于集群中，可以看到其他的 Scheduler 实例
-- ---------------------------- 
CREATE TABLE dbo.QRTZ_SCHEDULER_STATE (
    sched_name           varchar(120)    NOT NULL,
    instance_name        varchar(200)    NOT NULL,
    last_checkin_time    bigint          NOT NULL,
    checkin_interval     bigint          NOT NULL,
    PRIMARY KEY (sched_name, instance_name)
);

-- ----------------------------
-- 10、 存储程序的悲观锁的信息(假如使用了悲观锁)
-- ---------------------------- 
CREATE TABLE dbo.QRTZ_LOCKS (
    sched_name           varchar(120)    NOT NULL,
    lock_name            varchar(40)     NOT NULL,
    PRIMARY KEY (sched_name, lock_name)
);

-- ----------------------------
-- 11、 Quartz集群实现同步机制的行锁表
-- ---------------------------- 
CREATE TABLE dbo.QRTZ_SIMPROP_TRIGGERS (
    sched_name           varchar(120)    NOT NULL,
    trigger_name         varchar(200)    NOT NULL,
    trigger_group        varchar(200)    NOT NULL,
    str_prop_1           varchar(512)    NULL,
    str_prop_2           varchar(512)    NULL,
    str_prop_3           varchar(512)    NULL,
    int_prop_1           int             NULL,
    int_prop_2           int             NULL,
    long_prop_1          bigint          NULL,
    long_prop_2          bigint          NULL,
    dec_prop_1           numeric(13,4)   NULL,
    dec_prop_2           numeric(13,4)   NULL,
    bool_prop_1          varchar(1)      NULL,
    bool_prop_2          varchar(1)      NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
);