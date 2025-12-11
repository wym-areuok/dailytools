package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.ISqlExecuteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-12-10
 * @Description: 执行SQL
 */
@Service
public class SqlExecuteServiceImpl implements ISqlExecuteService {
    private static final Logger logger = LoggerFactory.getLogger(SqlExecuteServiceImpl.class);

    //TODO 为了测试暂时将本地作为主数据源
    // 注入主数据源 (LOCALHOST)
    @Autowired
    @Qualifier("dataSource")
    private DataSource localhostDataSource;

    // 注入其他数据源（使用required = false 即使不存在也不会报错）
    @Autowired(required = false)
    @Qualifier("iptfisDb71DataSource")
    private DataSource iptfisDb71DataSource;

    @Autowired(required = false)
    @Qualifier("iptfisDb70DataSource")
    private DataSource iptfisDb70DataSource;

    @Autowired(required = false)
    @Qualifier("itefisDbOnlineDataSource")
    private DataSource itefisDbOnlineDataSource;

    // 读取数据源开关配置
    @Value("${spring.datasource.druid.extra.iptfis_db_71.enabled}")
    private boolean iptfisDb71Enabled;

    @Value("${spring.datasource.druid.extra.iptfis_db_70.enabled}")
    private boolean iptfisDb70Enabled;

    @Value("${spring.datasource.druid.extra.itefis_db_online.enabled}")
    private boolean itefisDbOnlineEnabled;

    /**
     * 执行查询
     *
     * @param dbName
     * @param sql
     * @return
     */
    @Override
    public List<Map<String, Object>> executeQuery(String dbName, String sql) {
        long startTime = System.currentTimeMillis();
        logger.info("开始执行查询SQL: {}, 数据库: {}", sql, dbName);
        List<Map<String, Object>> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // 根据dbName获取对应的数据源
            DataSource dataSource = getDataSourceByDbName(dbName);
            // 获取数据库连接
            connection = dataSource.getConnection();
            // 使用PreparedStatement防止SQL注入
            statement = connection.prepareStatement(sql);
            // 设置查询超时时间（秒）
            statement.setQueryTimeout(5);
            resultSet = statement.executeQuery();
            // 获取结果集元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 处理结果集
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i); // 使用getColumnLabel支持别名
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                result.add(row);
            }
            long endTime = System.currentTimeMillis();
            logger.info("查询执行完成,耗时: {} ms,返回 {} 行数据", (endTime - startTime), result.size());
            return result;
        } catch (SQLException e) {
            logger.error("执行查询SQL出错: {}", sql, e);
            throw new RuntimeException("执行查询SQL出错: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    /**
     * 执行更新
     *
     * @param dbName
     * @param sql
     * @return
     */
    @Override
    public int executeUpdate(String dbName, String sql) {
        long startTime = System.currentTimeMillis();
        logger.info("开始执行更新SQL: {}, 数据库: {}", sql, dbName);
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            DataSource dataSource = getDataSourceByDbName(dbName);
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(5);
            int rowsAffected = statement.executeUpdate();
            long endTime = System.currentTimeMillis();
            logger.info("更新执行完成,耗时: {} ms,影响 {} 行数据", (endTime - startTime), rowsAffected);
            return rowsAffected;
        } catch (SQLException e) {
            logger.error("执行更新SQL出错: {}", sql, e);
            throw new RuntimeException("执行更新SQL出错: " + e.getMessage(), e);
        } finally {
            closeResources(null, statement, connection);
        }
    }

    /**
     * 执行插入
     *
     * @param dbName
     * @param sql
     * @return
     */
    @Override
    public int executeInsert(String dbName, String sql) {
        long startTime = System.currentTimeMillis();
        logger.info("开始执行插入SQL: {}, 数据库: {}", sql, dbName);
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            DataSource dataSource = getDataSourceByDbName(dbName);
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(5);
            int rowsAffected = statement.executeUpdate();
            long endTime = System.currentTimeMillis();
            logger.info("插入执行完成,耗时: {} ms,影响 {} 行数据", (endTime - startTime), rowsAffected);
            return rowsAffected;
        } catch (SQLException e) {
            logger.error("执行插入SQL出错: {}", sql, e);
            throw new RuntimeException("执行插入SQL出错: " + e.getMessage(), e);
        } finally {
            closeResources(null, statement, connection);
        }
    }

    /**
     * 执行删除
     *
     * @param dbName
     * @param sql
     * @return
     */
    @Override
    public int executeDelete(String dbName, String sql) {
        long startTime = System.currentTimeMillis();
        logger.info("开始执行删除SQL: {}, 数据库: {}", sql, dbName);
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            DataSource dataSource = getDataSourceByDbName(dbName);
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(5);
            int rowsAffected = statement.executeUpdate();
            long endTime = System.currentTimeMillis();
            logger.info("删除执行完成,耗时: {} ms,影响 {} 行数据", (endTime - startTime), rowsAffected);
            return rowsAffected;
        } catch (SQLException e) {
            logger.error("执行删除SQL出错: {}", sql, e);
            throw new RuntimeException("执行删除SQL出错: " + e.getMessage(), e);
        } finally {
            closeResources(null, statement, connection);
        }
    }

    /**
     * 根据数据库名称获取对应的数据源
     *
     * @param dbName
     * @return
     */
    private DataSource getDataSourceByDbName(String dbName) {
        switch (dbName) {
            case "LOCALHOST":
                return localhostDataSource;
            case "IPTFIS-DB-71":
                if (iptfisDb71DataSource != null && iptfisDb71Enabled) {
                    return iptfisDb71DataSource;
                }
                if (!iptfisDb71Enabled) {
                    throw new RuntimeException("数据源 IPTFIS-DB-71 未启用");
                }
                throw new RuntimeException("数据源 IPTFIS-DB-71 未配置");
            case "IPTFIS-DB-70":
                if (iptfisDb70DataSource != null && iptfisDb70Enabled) {
                    return iptfisDb70DataSource;
                }
                if (!iptfisDb70Enabled) {
                    throw new RuntimeException("数据源 IPTFIS-DB-70 未启用");
                }
                throw new RuntimeException("数据源 IPTFIS-DB-70 未配置");
            case "ITEFIS-DB-ONLINE":
                if (itefisDbOnlineDataSource != null && itefisDbOnlineEnabled) {
                    return itefisDbOnlineDataSource;
                }
                if (!itefisDbOnlineEnabled) {
                    throw new RuntimeException("数据源 ITEFIS-DB-ONLINE 未启用");
                }
                throw new RuntimeException("数据源 ITEFIS-DB-ONLINE 未配置");
            default:
                throw new RuntimeException("未知数据库名称: " + dbName + "，请检查是否维护");
        }
    }

    /**
     * 关闭数据库资源
     *
     * @param resultSet
     * @param statement
     * @param connection
     */
    private void closeResources(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.warn("关闭ResultSet时出错", e);
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.warn("关闭Statement时出错", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.warn("关闭Connection时出错", e);
            }
        }
    }
}