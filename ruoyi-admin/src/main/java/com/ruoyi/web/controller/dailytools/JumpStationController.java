package com.ruoyi.web.controller.dailytools;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.vo.SnInfoVO;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.service.IJumpStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-11-27
 * @Description: 板卡跳站
 */
@RestController
@RequestMapping("/dailytools/jumpStation")
public class JumpStationController extends BaseController {
    @Autowired
    private IJumpStationService jumpStationService;

    /**
     * 获取站点List name-code
     *
     * @author weiyiming
     * @date 2025-12-02
     */
    @GetMapping("/getStationList")
    public AjaxResult getStationList(@RequestParam String jumpType) {
        List<Map<String, Object>> stationList = jumpStationService.getStationList(jumpType);
        return AjaxResult.success(stationList);
    }

    /**
     * 查询SN的信息
     *
     * @author weiyiming
     * @date 2025-12-02
     */
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody Map<String, Object> data) {
        @SuppressWarnings("unchecked")
        List<String> snList = (List<String>) data.get("snList");
        String jumpType = (String) data.get("jumpType");
        startPage();
        List<SnInfoVO> list = jumpStationService.list(snList, jumpType);
        return getDataTable(list);
    }

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
