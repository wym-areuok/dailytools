package com.ruoyi.common.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-12-05
 * @Description: 跳站的LOG
 */
@Data
public class ChangeWcLogDTO {
    /*Id - 主键*/
    private Integer id;
    /*SN表的主键ID*/
    private Integer snoId;
    /*SN序列号*/
    private String mcbSno;
    /*原始站点*/
    private String originalWc;
    /*目标站点*/
    private String destWc;
    /*跳站原因*/
    private String reason;
    /*创建人*/
    private String creator;
    /*创建时间*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cdt;
}
