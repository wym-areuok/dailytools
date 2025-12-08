package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.QueryInfo;
import com.ruoyi.system.mapper.QueryInfoMapper;
import com.ruoyi.system.service.IQueryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class QueryInfoServiceImpl implements IQueryInfoService {

    @Autowired
    private QueryInfoMapper queryInfoMapper;

    @Override
    public QueryInfo selectQueryInfoByInfoId(Integer infoId) {
        return queryInfoMapper.selectQueryInfoByInfoId(infoId);
    }

    public QueryInfo selectByInfoTitle(String infoTitle) {
        return queryInfoMapper.selectByInfoTitle(infoTitle);
    }

    @Override
    public List<QueryInfo> selectQueryInfoList(QueryInfo queryInfo) {
        return queryInfoMapper.selectQueryInfoList(queryInfo);
    }

    @Override
    @Transactional
    public int insertQueryInfo(QueryInfo queryInfo) {
        validateQueryInfo(queryInfo);
        if (queryInfo.getSearchCount() == null) {
            queryInfo.setSearchCount(0);
        }
        if (StringUtils.isEmpty(queryInfo.getStatus())) {
            queryInfo.setStatus("0");
        }
        queryInfo.setCreateBy(SecurityUtils.getUsername());
        queryInfo.setCreateTime(new Date());
        return queryInfoMapper.insertQueryInfo(queryInfo);
    }

    private void validateQueryInfo(QueryInfo info) {
        if (StringUtils.isEmpty(info.getInfoTitle())) {
            throw new ServiceException("资料标题不能为空");
        }
        if (StringUtils.isEmpty(info.getInfoType())) {
            throw new ServiceException("资料类型不能为空");
        }
    }

    @Override
    @Transactional
    public int updateQueryInfo(QueryInfo queryInfo) {
        validateQueryInfo(queryInfo);
        queryInfo.setUpdateBy(SecurityUtils.getUsername());
        queryInfo.setUpdateTime(new Date());
        return queryInfoMapper.updateQueryInfo(queryInfo);
    }

    @Override
    @Transactional
    public int deleteQueryInfoByInfoId(Integer infoId) {
        return queryInfoMapper.deleteQueryInfoByInfoId(infoId);
    }

    @Override
    @Transactional
    public int deleteQueryInfoByInfoIds(Integer[] infoIds) {
        return queryInfoMapper.deleteQueryInfoByInfoIds(infoIds);
    }

    @Override
    @Transactional
    public String importQueryInfo(List<QueryInfo> infoList, boolean updateSupport, String operName) {
        if (infoList == null || infoList.isEmpty()) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        int duplicateNum = 0;
        int updateNum = 0;
        StringBuilder failureMsg = new StringBuilder();
        for (QueryInfo info : infoList) {
            try {
                validateQueryInfo(info);
                QueryInfo existingInfo = queryInfoMapper.selectByInfoTitle(info.getInfoTitle());
                if (existingInfo == null) {
                    info.setCreateBy(operName);
                    info.setCreateTime(new Date());
                    queryInfoMapper.insertQueryInfo(info);
                    successNum++;
                } else if (updateSupport) {
                    info.setInfoId(existingInfo.getInfoId());
                    info.setUpdateBy(operName);
                    info.setUpdateTime(new Date());
                    queryInfoMapper.updateQueryInfo(info);
                    updateNum++;
                } else {
                    duplicateNum++;
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、资料 [" + info.getInfoTitle() + "] 导入失败：" + e.getMessage();
                failureMsg.append(msg);
            }
        }
        StringBuilder resultMsg = new StringBuilder();
        resultMsg.append("导入结果：成功新增 ")
                .append(successNum).append(" 条，成功更新 ")
                .append(updateNum).append(" 条，跳过重复 ")
                .append(duplicateNum).append(" 条，失败 ")
                .append(failureNum).append(" 条");
        if (failureNum > 0) {
            resultMsg.append("<br>失败明细：").append(failureMsg);
        }
        return resultMsg.toString();
    }
}
