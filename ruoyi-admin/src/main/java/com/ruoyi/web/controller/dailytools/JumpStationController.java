package com.ruoyi.web.controller.dailytools;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.dto.JumpStationDTO;
import com.ruoyi.common.core.domain.vo.SnInfoVO;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
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
    public TableDataInfo list(@RequestBody JumpStationDTO queryDTO) {
        startPage();
        List<SnInfoVO> list = jumpStationService.list(queryDTO.getSnList(), queryDTO.getJumpType());
        return getDataTable(list);
    }

    /**
     * 执行跳站
     *
     * @author weiyiming
     * @date 2025-11-27
     */
    @PostMapping("/execute")
    public AjaxResult execute(@RequestBody JumpStationDTO jsDTO) {
        try {
            if (jsDTO.getSnList() == null || jsDTO.getSnList().isEmpty()) {
                return AjaxResult.error("SN列表不能为空");
            }
            if (jsDTO.getStation() == null || jsDTO.getStation().isEmpty()) {
                return AjaxResult.error("目标站点不能为空");
            }
            if (jsDTO.getJumpType() == null || jsDTO.getJumpType().isEmpty()) {
                return AjaxResult.error("跳站类型不能为空");
            }
            if (jsDTO.getRemark() == null || jsDTO.getRemark().isEmpty()) {
                return AjaxResult.error("备注不能为空");
            }
            String result = jumpStationService.execute(
                    jsDTO.getSnList(),
                    jsDTO.getStation(),
                    jsDTO.getJumpType(),
                    jsDTO.getRemark()
            );
            return AjaxResult.success("跳站成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("跳站失败: " + e.getMessage());
        }
    }

    /**
     * 执行跳站撤回
     *
     * @author weiyiming
     * @date 2025-11-27
     */
    @PostMapping("/undoExecute")
    public AjaxResult undoExecute(@RequestBody JumpStationDTO jsDTO) {
        return AjaxResult.success("执行跳站撤回chengg");
    }
}