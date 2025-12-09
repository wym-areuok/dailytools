package com.ruoyi.system.service;

import com.ruoyi.system.domain.QueryInfo;

import java.util.List;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-12-10
 * @Description: 资料查询
 */
public interface IQueryInfoService {

    List<QueryInfo> selectQueryInfoList(QueryInfo queryInfo);

    QueryInfo selectQueryInfoByInfoId(Integer infoId);

    int insertQueryInfo(QueryInfo queryInfo);

    int updateQueryInfo(QueryInfo queryInfo);

    int deleteQueryInfoByInfoIds(Integer[] infoIds);

    int deleteQueryInfoByInfoId(Integer infoId);

    String importQueryInfo(List<QueryInfo> queryInfoList, boolean updateSupport, String operName);
}
