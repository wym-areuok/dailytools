package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-11-24
 * @Description: StringTool的Excel数据处理Mapper接口(使用Map)
 */
public interface StringToolMapper {
    /**
     * 删除指定用户的数据
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 查询指定用户的记录数
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 为表创建索引
     */
    void createIndex();

}