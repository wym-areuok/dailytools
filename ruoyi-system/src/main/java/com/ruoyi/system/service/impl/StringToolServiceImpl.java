package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.UtilException;
import com.ruoyi.system.mapper.StringToolMapper;
import com.ruoyi.system.service.IStringToolService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-11-21
 * @Description: 字符串工具
 */
@Service
public class StringToolServiceImpl implements IStringToolService {
    private static final Logger log = LoggerFactory.getLogger(StringToolServiceImpl.class);

    @Autowired
    private StringToolMapper stringToolMapper;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    // 根据系统内存动态计算批处理大小
    private static final int BATCH_SIZE = calculateBatchSize();

    // 定义资源限制参数
    private static final long MAX_PROCESSING_TIME = 10 * 60 * 1000; // 最大处理时间10分钟
    private static final int MAX_ROWS = 1000000; // 最大处理行数100万行

    /**
     * 执行字符串处理操作(中小数量级数据)
     *
     * @author weiyiming
     * @date 2025-11-21
     */
    public String execute(String input) {
        // 空值检查
        if (input == null) {
            return "()";
        }
        /*估算StringBuilder预分配空间 StringBuilder默认初始容量通常是16 SN是10位 custSN23位
        如果 input.length() * 1.2 计算结果大于 32，则使用计算结果作为容量
        如果 input.length() * 1.2 计算结果小于 32，则使用 32 作为容量*/
        int allocateSpaceNum = Math.max(32, (int) (input.length() * 1.2));
        StringBuilder result = new StringBuilder(allocateSpaceNum);
        result.append('(');
        boolean first = true;
        try (BufferedReader reader = new BufferedReader(new StringReader(input))) {
            String line;
            // 逐行读取处理
            while ((line = reader.readLine()) != null) {
                // 过滤空行（trim后为空的行）
                if (!line.trim().isEmpty()) {
                    // 去除行中的空格
                    String cleanedLine = line.replaceAll("\\s+", "");
                    if (!cleanedLine.isEmpty()) {
                        if (!first) {
                            result.append(',');
                        }
                        result.append('\'').append(cleanedLine).append('\'');
                        first = false;
                    }
                }
            }
            result.append(')');
            return result.toString();
        } catch (IOException e) {
            // 正常情况下不会发生IOException，因为是StringReader
            return "(executeStringOperation方法执行错误)";
        }
    }

    /**
     * 下载模版文件
     *
     * @author weiyiming
     * @date 2025-11-22
     */
    public void downloadTemplate(HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=stringToolTemplate.xlsx");
            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("StringProcessTemplate");
            // 创建样式 - 居中对齐且为文本格式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
            DataFormat dataFormat = workbook.createDataFormat();
            headerStyle.setDataFormat(dataFormat.getFormat("@")); // 文本格式
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("StringToBeProcessed");
            headerCell.setCellStyle(headerStyle); // 应用样式
            // 创建示例行
            Row exampleRow = sheet.createRow(1);
            Cell exampleCell = exampleRow.createCell(0);
            exampleCell.setCellValue("SNQWERTYUI");
            exampleCell.setCellStyle(headerStyle); // 应用相同样式
            // 设置列宽
            sheet.setColumnWidth(0, 30 * 256);
            // 写入响应流
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new UtilException("生成模板文件失败");
        }
    }

    /**
     * 根据可用内存动态计算批处理大小
     *
     * @author weiyiming
     * @date 2025-11-24
     */
    private static int calculateBatchSize() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        if (maxMemory > 2L * 1024 * 1024 * 1024) { // > 2GB
            return 2000;
        } else if (maxMemory > 1L * 1024 * 1024 * 1024) { // > 1GB
            return 1000;
        } else {
            return 500;
        }
    }

    /**
     * 解析Excel写入数据库
     *
     * @author weiyiming
     * @date 2025-11-24
     */
    public void processExcelFile(String filePath, Long userId) {
        long startTime = System.currentTimeMillis();
        log.info("开始处理Excel文件: {}, 用户ID: {}", filePath, userId);
        // 添加资源保护机制
        final long deadline = startTime + MAX_PROCESSING_TIME;
        // 检查当前用户是否有数据
        int recordCount = stringToolMapper.countByUserId(userId);
        log.info("用户 {} 当前有 {} 条记录", userId, recordCount);
        // 如果用户已有数据，则先删除再插入
        if (recordCount > 0) {
            stringToolMapper.deleteByUserId(userId);
            log.info("已删除用户 {} 的原有数据，共 {} 条记录", userId, recordCount);
        }
        // 创建/检查索引
        stringToolMapper.createIndex();
        log.info("索引创建/检查完成");
        // 解析Excel文件并分批插入数据
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            int totalRows = sheet.getLastRowNum() + 1;
            // 添加资源保护：检查行数是否超过限制
            if (totalRows > MAX_ROWS) {
                throw new RuntimeException("Excel行数超过最大限制: " + MAX_ROWS);
            }
            log.info("Excel总行数: {}", totalRows);
            // 使用JDBC直接连接进行高性能批量插入
            batchInsertWithStreaming(sheet, userId, deadline);
            log.info("Excel数据处理完成，共处理 {} 行有效数据", totalRows - 1);
        } catch (Exception e) {
            log.error("处理Excel文件时发生错误: ", e);
            throw new RuntimeException("处理Excel文件失败", e);
        }
        long endTime = System.currentTimeMillis();
        log.info("整个处理过程耗时: {} ms", (endTime - startTime));
    }

    /**
     * 流式处理Excel数据并批量插入数据库，针对大数据量优化
     *
     * @author weiyiming
     * @date 2025-11-24
     */
    private void batchInsertWithStreaming(Sheet sheet, Long userId, long deadline) throws SQLException {
        String sql = "INSERT INTO string_tool_temp(data, user_id) VALUES (?, ?)";
        try (Connection connection = sqlSessionFactory.getConfiguration()
                .getEnvironment().getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            int count = 0;
            int totalProcessed = 0;
            // 跳过标题行，从第二行开始读取数据
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                // 添加资源保护：检查是否超时
                if (System.currentTimeMillis() > deadline) {
                    throw new RuntimeException("处理时间超过最大限制: " + (MAX_PROCESSING_TIME / 1000 / 60) + " 分钟");
                }
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue; // 跳过空行
                }
                Cell cell = row.getCell(0); // 获取第一列数据
                if (cell == null) {
                    continue; // 跳过空单元格
                }
                String cellValue = getCellValueAsString(cell);
                if (cellValue == null || cellValue.trim().isEmpty()) {
                    continue; // 跳过空值
                }
                ps.setString(1, cellValue.trim());
                ps.setLong(2, userId);
                ps.addBatch();
                count++;
                totalProcessed++;
                // 达到批次大小时执行批量插入
                if (count >= BATCH_SIZE) {
                    ps.executeBatch();
                    connection.commit();
                    ps.clearBatch();
                    count = 0;
                    log.info("已插入 {} 条记录", totalProcessed);
                }
            }
            // 处理剩余不足一批次的数据
            if (count > 0) {
                ps.executeBatch();
                connection.commit();
                log.info("最后插入 {} 条记录", count);
            }
        } catch (SQLException e) {
            log.error("批量插入数据时发生错误: ", e);
            throw e;
        }
    }

    /**
     * 获取excel每行内容
     *
     * @author weiyiming
     * @date 2025-11-24
     */
    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 防止科学计数法
                    DataFormatter formatter = new DataFormatter();
                    return formatter.formatCellValue(cell);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

}