package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.IStringToolService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;


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
    public String executeStringOperation(String input) {
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

}