package com.ruoyi.common.core.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-12-07
 * @Description: 修改FIS账号密码DTO
 */
@Data
public class ChangePwdDTO {
    /*FIS账号*/
    @NotBlank(message = "FIS账号不能为空")
    private String fisNumber;
    /*厂别*/
    @NotBlank(message = "厂别不能为空")
    private String factory;
    /*密码*/
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "密码只能由字母和数字组成")
    private String password;
}
