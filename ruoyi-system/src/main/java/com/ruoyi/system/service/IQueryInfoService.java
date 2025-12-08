package com.ruoyi.system.service;


import com.ruoyi.system.domain.QueryInfo;

import java.util.List;

/**
 * 资料库信息Service接口
 */
public interface IQueryInfoService {

    QueryInfo selectQueryInfoByInfoId(Integer infoId);

    List<QueryInfo> selectQueryInfoList(QueryInfo queryInfo);

    int insertQueryInfo(QueryInfo queryInfo);

    int updateQueryInfo(QueryInfo queryInfo);

    int deleteQueryInfoByInfoIds(Integer[] infoIds);

    int deleteQueryInfoByInfoId(Integer infoId);

    String importQueryInfo(List<QueryInfo> queryInfoList, boolean updateSupport, String operName);
}
