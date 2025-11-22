package com.ruoyi.web.controller.dailytools;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.IStringToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

}
