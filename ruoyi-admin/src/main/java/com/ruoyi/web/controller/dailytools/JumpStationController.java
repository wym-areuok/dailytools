package com.ruoyi.web.controller.dailytools;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IJumpStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-11-27
 * @Description: 板卡跳站
 */
@RestController
@RequestMapping("/dailytools/jumpStation")
public class JumpStationController {
    @Autowired
    private IJumpStationService jumpStationService;

    /**
     * 执行跳站
     *
     * @author weiyiming
     * @date 2025-11-27
     */
    @PostMapping("/execute")
    public AjaxResult execute(@RequestBody Map<String, Object> data) {
        try {
            String input = (String) data.get("input");
            if (input == null || input.trim().isEmpty()) {
                return AjaxResult.error("输入内容不能为空");
            }
            String result = jumpStationService.execute(input.trim());
            return AjaxResult.success("跳站成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("跳站失败: " + e.getMessage());
        }
    }
}
