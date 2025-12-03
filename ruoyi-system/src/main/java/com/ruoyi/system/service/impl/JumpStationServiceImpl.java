package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.vo.SnInfoVO;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.service.IJumpStationService;
import com.ruoyi.system.service.ISysDictDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-11-27
 * @Description: 板卡跳站
 */
@Service
public class JumpStationServiceImpl implements IJumpStationService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ISysDictDataService dictDataService;

    private static final Logger logger = LoggerFactory.getLogger(JumpStationServiceImpl.class);

    /**
     * 获取站点List name-code
     *
     * @author weiyiming
     * @date 2025-12-02
     */
    @Override
    public List<Map<String, Object>> getStationList(String jumpType) {
        String tableName = dictDataService.selectDictByTypeAndLabel(jumpType, "WC");
        if (tableName == null || tableName.isEmpty()) {
            throw new ServiceException("跳站类型WC配置不完整: " + jumpType);
        }
        /*构建SQL查询语句 执行查询*/
        String sql = "SELECT WC AS stationCode, Description AS stationName FROM " + tableName.toUpperCase(Locale.ROOT) + " ORDER BY WC";
        try {
            return jdbcTemplate.queryForList(sql); // 执行查询并返回结果
        } catch (DataAccessException e) {
            throw new ServiceException("查询站点信息失败: " + e.getMessage());
        }
    }

    /**
     * 查询SN的信息
     *
     * @author weiyiming
     * @date 2025-12-03
     */
    @Override
    public List<SnInfoVO> list(List<String> snList, String jumpType) {
        if (snList == null || snList.isEmpty() || jumpType == null || jumpType.trim().isEmpty()) {
            throw new ServiceException("snList或jumpType不能为空!");
        }
        /*根据字典类型查询数据库名称F6|F3 利用循环先去查一个库的数据，查不到再查另外一个库 字典标签F6 value PTFIS-DB-71 F3 value IPTFIS-DB-70 不排除后续项目扩展到其他的DB*/
        List<SysDictData> dbList = dictDataService.selectDictDataByType("db_info");
        String tableName = dictDataService.selectDictByTypeAndLabel(jumpType, "SN");
        String mcbSnoList = String.join(",", Collections.nCopies(snList.size(), "?")); // 先把snList处理成用于McbSno IN查询的形式
        for (SysDictData dbInfo : dbList) { // 按顺序遍历数据库配置进行查询
            String dbIdentifier = dbInfo.getDictValue();
            StringBuilder snSql = new StringBuilder();
            /*因为sn表数据比较大,慎用select * 所以虽然使用in 也要加上top*/
            //sql.append("SELECT TOP 10000 * FROM [").append(dbIdentifier).append("].").append(tableName).append(" WHERE McbSno IN (").append(mcbSnoList).append(")");
            /*为了测试*/
            snSql.append("SELECT TOP 10000 * FROM ").append(tableName).append(" WHERE McbSno IN (").append(mcbSnoList).append(")");
            try {
                List<SnInfoVO> result = jdbcTemplate.query(
                        snSql.toString(),
                        snList.toArray(),
                        new BeanPropertyRowMapper<>(SnInfoVO.class)
                );
                if (!result.isEmpty()) { // 如果在当前数据库中查询到数据 则返回结果 不再查询其他数据库
                    /*检查model字段是否一致*/
                    String modelName = validateModelConsistency(result);
                    /*根据model获取SFC*/
                    String sfc = getSfcByModel(jumpType, modelName);
                    result.forEach(snInfo -> snInfo.setSfc(sfc));
                    return result;
                }
            } catch (DataAccessException e) {
                /*记录日志 但不中断流程 继续尝试下一个数据库*/
                logger.warn("查询数据库 {} 时发生错误: {}", dbIdentifier, e.getMessage());
            }
        }
        return new ArrayList<>(); // 如果所有数据库都没查到数据 返回空列表
    }

    /**
     * 验证SN列表中所有项目的model字段是否一致，并返回基准model值 防止不同model的SN进行跳站
     *
     * @author weiyiming
     * @date 2025-12-04
     */
    private String validateModelConsistency(List<SnInfoVO> result) {
        /*检查model为空的情况并收集对应的SN*/
        List<String> nullModelSnList = result.stream()
                .filter(snInfo -> snInfo.getModel() == null || snInfo.getModel().isEmpty())
                .map(SnInfoVO::getMcbSno)
                .collect(Collectors.toList());
        if (!nullModelSnList.isEmpty()) {
            throw new ServiceException("以下SN的机型为空: " + String.join(", ", nullModelSnList));
        }
        String baseModel = result.get(0).getModel();
        /*查找与基准model不一致的SN*/
        List<String> inconsistentSnList = result.stream()
                .filter(snInfo -> !Objects.equals(baseModel, snInfo.getModel()))
                .map(SnInfoVO::getMcbSno)
                .collect(Collectors.toList());
        if (!inconsistentSnList.isEmpty()) {
            throw new ServiceException("以下SN的机型与其他不一致: " + String.join(", ", inconsistentSnList) +
                    "。基准机型为: " + baseModel);
        }
        return baseModel;
    }

    /**
     * 根据model获取对应的SFC
     *
     * @author weiyiming
     * @date 2025-12-04
     */
    private String getSfcByModel(String jumpType, String modelName) {
        String sfcTableName = dictDataService.selectDictByTypeAndLabel(jumpType, "SFC");
        if (sfcTableName == null || sfcTableName.isEmpty()) {
            throw new ServiceException("跳站类型SFC配置不完整: " + jumpType);
        }
        String sfcSql = "SELECT Flow FROM " + sfcTableName + " WHERE Model = ?";
        try {
            List<String> sfcList = jdbcTemplate.queryForList(sfcSql, String.class, modelName);
            if (sfcList.isEmpty()) {
                throw new ServiceException("未找到model '" + modelName + "' 对应的SFC配置");
            } else if (sfcList.size() > 1) {
                throw new ServiceException("model '" + modelName + "' 对应多个SFC配置，请检查数据");
            }
            return sfcList.get(0);
        } catch (DataAccessException e) {
            throw new ServiceException("查询SFC信息失败: " + e.getMessage());
        }
    }

    /**
     * 执行跳站
     *
     * @author weiyiming
     * @date 2025-11-27
     */
    @Override
    public String execute(String input) {
        return "";
    }
}
