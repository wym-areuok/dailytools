package com.ruoyi.web.controller.dailytools;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.dto.ChangePwdDTO;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.IChangePwdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-12-07
 * @Description: 修改FIS账号密码
 */
@RestController
@RequestMapping("/dailytools/changePwd")
public class ChangePwdController extends BaseController {
    @Autowired
    private IChangePwdService changePwdService;

    /**
     * 修改FIS用户密码
     *
     * @author weiyiming
     * @date 2025-12-07
     */
    @Log(title = "FisWeb改密", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult changePwd(@Valid @RequestBody ChangePwdDTO pwdDTO) {
        try {
            boolean result = changePwdService.changePwd(
                    pwdDTO.getFisNumber(),
                    pwdDTO.getFactory(),
                    pwdDTO.getPassword()
            );
            if (result) {
                return AjaxResult.success("密码修改成功");
            } else {
                return AjaxResult.error("密码修改失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("密码修改异常：" + e.getMessage());
        }
    }
}
