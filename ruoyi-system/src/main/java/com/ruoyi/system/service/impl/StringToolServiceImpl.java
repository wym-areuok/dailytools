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
        // 使用BufferedReader按行读取，避免一次性加载所有内容到内存
        try (BufferedReader reader = new BufferedReader(new StringReader(input))) {
            StringBuilder result = new StringBuilder(input.length() + 100);
            result.append('(');
            boolean first = true;
            String line;
            // 逐行读取处理
            while ((line = reader.readLine()) != null) {
                // 过滤空行
                if (line != null && !line.trim().isEmpty()) {
                    if (!first) {
                        result.append(',');
                    }
                    result.append('\'').append(line).append('\'');
                    first = false;
                }
            }
            result.append(')');
            return result.toString();
        } catch (IOException e) {
            // 正常情况下不会发生IOException，因为是StringReader
            return "executeStringOperation方法执行错误";
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