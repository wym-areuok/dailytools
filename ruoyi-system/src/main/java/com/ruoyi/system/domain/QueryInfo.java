package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class QueryInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Integer infoId;

    @Excel(name = "资料标题")
    private String infoTitle;

    @Excel(name = "资料标签")
    private String infoTags;

    @Excel(name = "资料类型", dictType = "info_type")
    private String infoType;

    @Excel(name = "资料内容")
    private String infoContent;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    @Excel(name = "搜索次数")
    private Integer searchCount;

    public void setInfoId(Integer infoId) {
        this.infoId = infoId;
    }

    public Integer getInfoId() {
        return infoId;
    }

    public void setInfoTitle(String infoTitle) {
        this.infoTitle = infoTitle;
    }

    public String getInfoTitle() {
        return infoTitle;
    }

    public void setInfoTags(String infoTags) {
        this.infoTags = infoTags;
    }

    public String getInfoTags() {
        return infoTags;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoContent(String infoContent) {
        this.infoContent = infoContent;
    }

    public String getInfoContent() {
        return infoContent;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setSearchCount(Integer searchCount) {
        this.searchCount = searchCount;
    }

    public Integer getSearchCount() {
        return searchCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("infoId", getInfoId())
                .append("infoTitle", getInfoTitle())
                .append("infoTags", getInfoTags())
                .append("infoType", getInfoType())
                .append("infoContent", getInfoContent())
                .append("status", getStatus())
                .append("searchCount", getSearchCount())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
