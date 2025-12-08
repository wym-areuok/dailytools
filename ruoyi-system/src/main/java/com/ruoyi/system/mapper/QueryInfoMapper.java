package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.QueryInfo;

import java.util.List;

/**
 * 资料库信息Mapper接口
 */
public interface QueryInfoMapper {
    /**
     * 查询资料库信息
     *
     * @param infoId 资料库信息ID
     * @return 资料库信息
     */
    QueryInfo selectQueryInfoByInfoId(Integer infoId);

    /**
     * 通过标题查询资料
     */
    QueryInfo selectByInfoTitle(String infoTitle);

    /**
     * 查询资料库信息列表
     *
     * @param queryInfo 资料库信息
     * @return 资料库信息集合
     */
    List<QueryInfo> selectQueryInfoList(QueryInfo queryInfo);

    /**
     * 新增资料库信息
     *
     * @param queryInfo 资料库信息
     * @return 结果
     */
    int insertQueryInfo(QueryInfo queryInfo);

    /**
     * 修改资料库信息
     *
     * @param queryInfo 资料库信息
     * @return 结果
     */
    int updateQueryInfo(QueryInfo queryInfo);

    /**
     * 删除资料库信息
     *
     * @param infoId 资料库信息ID
     * @return 结果
     */
    int deleteQueryInfoByInfoId(Integer infoId);

    /**
     * 批量删除资料库信息
     *
     * @param infoIds 需要删除的数据ID
     * @return 结果
     */
    int deleteQueryInfoByInfoIds(Integer[] infoIds);
}
