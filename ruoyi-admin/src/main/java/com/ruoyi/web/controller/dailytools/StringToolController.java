package com.ruoyi.web.controller.dailytools;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.IStringToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-11-21
 * @Description: 字符串工具
 */
@RestController
@RequestMapping("/dailytools/stringtool")
public class StringToolController {
    @Autowired
    private IStringToolService stringToolService;

    /**
     * 执行字符串处理操作(中小数量级数据)
     *
     * @author weiyiming
     * @date 2025-11-21
     */
    @PostMapping("/execute")
    public AjaxResult execute(@RequestBody Map<String, Object> data) {
        try {
            String input = (String) data.get("input");
            if (input == null || input.trim().isEmpty()) {
                return AjaxResult.error("输入内容不能为空");
            }
            String result = stringToolService.execute(input.trim());
            return AjaxResult.success("处理成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("处理失败: " + e.getMessage());
        }
    }

    /**
     * 下载模版文件
     *
     * @author weiyiming
     * @date 2025-11-22
     */
    @PostMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        stringToolService.downloadTemplate(response);
    }

    /**
     * 解析Excel写入数据库
     *
     * @author weiyiming
     * @date 2025-11-24
     */
    @PostMapping("/upload")
    @Log(title = "字符串工具处理", businessType = BusinessType.IMPORT)
    public AjaxResult uploadAndProcessExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return AjaxResult.error("上传文件不能为空");
            }
            Long userId;
            try {
                userId = SecurityUtils.getUserId(); // 获取当前用户ID
            } catch (Exception e) {
                return AjaxResult.error("未能获取到当前用户信息，请重新登录后重试");
            }
            String originalFilename = file.getOriginalFilename(); // 保存文件到临时位置
            String extension = originalFilename != null && originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".xlsx";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String fileName = "excel_" + sdf.format(new Date()) + extension;
            String filePath = System.getProperty("java.io.tmpdir") + File.separator + fileName;
            File destFile = new File(filePath);
            file.transferTo(destFile);
            new Thread(() -> { // 异步处理Excel文件
                try {
                    stringToolService.processExcelFile(filePath, userId);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (destFile.exists()) { // 处理完成后删除临时文件
                        destFile.delete();
                    }
                }
            }).start();
            return AjaxResult.success("文件上传成功，正在后台处理，请稍后查看string_tool_temp表的内容");
        } catch (IOException e) {
            return AjaxResult.error("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("处理过程中发生错误：" + e.getMessage());
        }
    }
}