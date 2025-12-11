package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-12-10
 * @Description: Sql执行返回结果的实体类
 */
public class SqlResult extends BaseEntity {
    /**
     * 数据源
     */
    private String dbDataSource;

    /**
     * SQL内容
     */
    private String sqlContent;

    public String getDbDataSource() {
        return dbDataSource;
    }

    public void setDbDataSource(String dbDataSource) {
        this.dbDataSource = dbDataSource;
    }

    public String getSqlContent() {
        return sqlContent;
    }

    public void setSqlContent(String sqlContent) {
        this.sqlContent = sqlContent;
    }
}