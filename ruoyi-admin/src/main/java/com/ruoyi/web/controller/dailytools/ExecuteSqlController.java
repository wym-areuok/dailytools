package com.ruoyi.web.controller.dailytools;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.system.domain.SqlResult;
import com.ruoyi.system.service.ISqlExecuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.AbstractMap.SimpleEntry;


/**
 * @Author: weiyiming
 * @CreateTime: 2025-12-10
 * @Description: 执行SQL
 */
@RestController
@RequestMapping("/dailyTools/executeSql")
public class ExecuteSqlController extends BaseController {

    @Autowired
    private ISqlExecuteService sqlExecuteService;

    /**
     * 查询操作
     *
     * @param request
     * @return
     */
    @PreAuthorize("@ss.hasPermi('dailyTools:executeSql:query')")
    @Log(title = "SQL执行工具", businessType = BusinessType.SELECT)
    @PostMapping("/query")
    public AjaxResult executeQuery(@RequestBody SqlResult request) {
        try {
            SimpleEntry<Boolean, String> result = validateSelectSql(request.getSqlContent());
            if (!result.getKey()) {
                return AjaxResult.error("SQL语句验证失败：" + result.getValue());
            }
            // 设置执行超时时间（5秒） 异步执行SQL查询 返回一个Future对象 用于后面获取查询结果
            Future<List<Map<String, Object>>> future = CompletableFuture.supplyAsync(() -> {
                return sqlExecuteService.executeQuery(request.getDbDataSource(), request.getSqlContent());
            });
            try {
                List<Map<String, Object>> queryResult = future.get(5, TimeUnit.SECONDS);
                return AjaxResult.success(queryResult);
            } catch (TimeoutException e) {
                // 取消执行 防止给数据库搞出事
                future.cancel(true);
                return AjaxResult.error("SQL执行超时，请优化SQL语句");
            }
        } catch (Exception e) {
            logger.error("查询SQL执行异常", e);
            return AjaxResult.error("查询执行失败：" + e.getMessage());
        }
    }

    /**
     * 更新操作
     *
     * @param request
     * @return
     */
    @PreAuthorize("@ss.hasPermi('dailyTools:executeSql:update')")
    @Log(title = "SQL执行工具", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public AjaxResult executeUpdate(@RequestBody SqlResult request) {
        try {
            SimpleEntry<Boolean, String> result = validateUpdateSql(request.getSqlContent());
            if (!result.getKey()) {
                return AjaxResult.error("SQL语句验证失败：" + result.getValue());
            }
            // 设置执行超时时间（5秒）
            Future<Integer> future = CompletableFuture.supplyAsync(() -> sqlExecuteService.executeUpdate(request.getDbDataSource(), request.getSqlContent()));
            try {
                int affectedRows = future.get(5, TimeUnit.SECONDS);
                return AjaxResult.success("更新成功", affectedRows);
            } catch (TimeoutException e) {
                future.cancel(true);
                return AjaxResult.error("SQL执行超时，请优化SQL语句");
            }
        } catch (Exception e) {
            logger.error("更新SQL执行异常", e);
            return AjaxResult.error("更新执行失败：" + e.getMessage());
        }
    }

    /**
     * 插入操作
     *
     * @param request
     * @return
     */
    @PreAuthorize("@ss.hasPermi('dailyTools:executeSql:insert')")
    @Log(title = "SQL执行工具", businessType = BusinessType.INSERT)
    @PostMapping("/insert")
    public AjaxResult executeInsert(@RequestBody SqlResult request) {
        try {
            SimpleEntry<Boolean, String> result = validateInsertSql(request.getSqlContent());
            if (!result.getKey()) {
                return AjaxResult.error("SQL语句验证失败：" + result.getValue());
            }
            // 设置执行超时时间（5秒）
            Future<Integer> future = CompletableFuture.supplyAsync(() -> sqlExecuteService.executeInsert(request.getDbDataSource(), request.getSqlContent()));
            try {
                int affectedRows = future.get(5, TimeUnit.SECONDS);
                return AjaxResult.success("插入成功", affectedRows);
            } catch (TimeoutException e) {
                future.cancel(true);
                return AjaxResult.error("SQL执行超时,请优化SQL语句");
            }
        } catch (Exception e) {
            logger.error("插入SQL执行异常", e);
            return AjaxResult.error("插入执行失败：" + e.getMessage());
        }
    }

    /**
     * 删除操作
     *
     * @param request
     * @return
     */
    @PreAuthorize("@ss.hasPermi('dailyTools:executeSql:delete')")
    @Log(title = "SQL执行工具", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult executeDelete(@RequestBody SqlResult request) {
        try {
            SimpleEntry<Boolean, String> result = validateDeleteSql(request.getSqlContent());
            if (!result.getKey()) {
                return AjaxResult.error("SQL语句验证失败：" + result.getValue());
            }
            // 设置执行超时时间（5秒）
            Future<Integer> future = CompletableFuture.supplyAsync(() -> sqlExecuteService.executeDelete(request.getDbDataSource(), request.getSqlContent()));
            try {
                int affectedRows = future.get(5, TimeUnit.SECONDS);
                return AjaxResult.success("删除成功", affectedRows);
            } catch (TimeoutException e) {
                future.cancel(true);
                return AjaxResult.error("SQL执行超时,请优化SQL语句");
            }
        } catch (Exception e) {
            logger.error("删除SQL执行异常", e);
            return AjaxResult.error("删除执行失败：" + e.getMessage());
        }
    }

    /**
     * 验证SELECT语句
     */
    private SimpleEntry<Boolean, String> validateSelectSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return new SimpleEntry<>(false, "SQL语句不能为空");
        }
        String upperSql = sql.trim().toUpperCase();
        if (!upperSql.startsWith("SELECT")) {
            return new SimpleEntry<>(false, "查询语句必须以SELECT开头");
        }
        if (!upperSql.matches(".*\\bTOP\\s+100\\b.*")) {
            return new SimpleEntry<>(false, "查询语句必须包含TOP 100限制");
        }
        if (upperSql.matches(".*\\b(DROP\\s+TABLE|TRUNCATE\\s+TABLE)\\b.*")) {
            return new SimpleEntry<>(false, "查询语句不能包含危险操作");
        }
        if (isBatchOperation(sql)) {
            return new SimpleEntry<>(false, "不允许执行批量SQL操作");
        }
        return new SimpleEntry<>(true, "验证通过");
    }

    /**
     * 验证UPDATE语句
     */
    private SimpleEntry<Boolean, String> validateUpdateSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return new SimpleEntry<>(false, "SQL语句不能为空");
        }
        String upperSql = sql.trim().toUpperCase();
        if (!upperSql.startsWith("UPDATE")) {
            return new SimpleEntry<>(false, "更新语句必须以UPDATE开头");
        }
        if (!upperSql.contains(" WHERE ")) {
            return new SimpleEntry<>(false, "更新语句必须包含WHERE条件");
        }
        String whereClause = extractWhereClause(upperSql);
        if (isAlwaysTrueCondition(whereClause)) {
            return new SimpleEntry<>(false, "WHERE条件不能为恒真条件(如 1=1)");
        }
        if (whereClause.length() < 6) {
            return new SimpleEntry<>(false, "WHERE条件过于简单");
        }
        if (isBatchOperation(sql)) {
            return new SimpleEntry<>(false, "不允许执行批量SQL操作");
        }
        return new SimpleEntry<>(true, "验证通过");
    }

    /**
     * 验证INSERT语句
     */
    private SimpleEntry<Boolean, String> validateInsertSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return new SimpleEntry<>(false, "SQL语句不能为空");
        }
        String upperSql = sql.trim().toUpperCase();
        if (!upperSql.startsWith("INSERT")) {
            return new SimpleEntry<>(false, "插入语句必须以INSERT开头");
        }
        if (isBatchOperation(sql)) {
            return new SimpleEntry<>(false, "不允许执行批量SQL操作");
        }
        return new SimpleEntry<>(true, "验证通过");
    }

    /**
     * 验证DELETE语句
     */
    private SimpleEntry<Boolean, String> validateDeleteSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return new SimpleEntry<>(false, "SQL语句不能为空");
        }
        String upperSql = sql.trim().toUpperCase();
        if (!upperSql.startsWith("DELETE")) {
            return new SimpleEntry<>(false, "删除语句必须以DELETE开头");
        }
        if (!upperSql.contains(" WHERE ")) {
            return new SimpleEntry<>(false, "删除语句必须包含WHERE条件");
        }
        String whereClause = extractWhereClause(upperSql);
        if (isAlwaysTrueCondition(whereClause)) {
            return new SimpleEntry<>(false, "WHERE条件不能为恒真条件(如 1=1)");
        }
        if (whereClause.length() < 6) {
            return new SimpleEntry<>(false, "WHERE条件过于简单");
        }
        if (isBatchOperation(sql)) {
            return new SimpleEntry<>(false, "不允许执行批量SQL操作");
        }
        return new SimpleEntry<>(true, "验证通过");
    }

    /**
     * 提取WHERE子句
     */
    private String extractWhereClause(String sql) {
        if (!sql.contains(" WHERE ")) {
            return "";
        }
        int whereIndex = sql.indexOf(" WHERE ");
        String whereClause = sql.substring(whereIndex + 7).trim();
        // 移除ORDER BY等后续子句
        String[] nextClauses = {" ORDER BY ", " GROUP BY ", " HAVING "};
        for (String clause : nextClauses) {
            int clauseIndex = whereClause.toUpperCase().indexOf(clause);
            if (clauseIndex > 0) {
                whereClause = whereClause.substring(0, clauseIndex).trim();
                break;
            }
        }
        return whereClause;
    }

    /**
     * 判断是否为恒真条件
     */
    private boolean isAlwaysTrueCondition(String whereClause) {
        if (whereClause == null || whereClause.isEmpty()) {
            return false;
        }
        String upperClause = whereClause.toUpperCase();
        // 常见的恒真条件模式
        String[] alwaysTruePatterns = {
                "\\b1\\s*=\\s*1\\b",
                "\\b2\\s*>\\s*1\\b",
                "\\b0\\s*=\\s*0\\b",
                "\\b'[^']*'\\s*=\\s*'[^']*'" // 字符串自相等
        };
        for (String pattern : alwaysTruePatterns) {
            if (upperClause.matches(".*" + pattern + ".*")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否为批处理操作
     */
    private boolean isBatchOperation(String sql) {
        // 检查是否包含多个SQL语句（以分号分隔）
        String[] statements = sql.split(";");
        int nonEmptyStatements = 0;
        for (String statement : statements) {
            if (!statement.trim().isEmpty()) {
                nonEmptyStatements++;
            }
        }
        return nonEmptyStatements > 1;
    }
}