package com.ruoyi.common.core.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-12-05
 * @Description: 跳站业务相关DTO
 */
@Data
public class JumpStationDTO {
    private List<String> snList;
    private String station;
    private String jumpType;
    private String remark;
}
