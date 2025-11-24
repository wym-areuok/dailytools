package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-11-24
 * @Description: StringTool的Excel数据处理Mapper接口(使用Map)
 */
@Mapper
public interface StringToolMapper {
    /**
     * 清空表数据
     */
    void truncateTable();

    /**
     * 为表创建索引
     */
    void createIndex();
}
