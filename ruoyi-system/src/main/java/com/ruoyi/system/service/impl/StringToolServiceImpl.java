package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.UtilException;
import com.ruoyi.system.service.IStringToolService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;


/**
 * @Author: weiyiming
 * @CreateTime: 2025-11-21
 * @Description: 字符串工具
 */
@Service
public class StringToolServiceImpl implements IStringToolService {
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
            Sheet sheet = workbook.createSheet("字符串处理模板");
            // 创建样式 - 居中对齐且为文本格式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
            DataFormat dataFormat = workbook.createDataFormat();
            headerStyle.setDataFormat(dataFormat.getFormat("@")); // 文本格式
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("待处理字符串");
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

}