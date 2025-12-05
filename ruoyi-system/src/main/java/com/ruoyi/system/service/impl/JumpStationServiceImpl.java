package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.dto.ChangeWcLogDTO;
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
        if (snList == null || snList.isEmpty()) {
            throw new ServiceException("sn不能为空!");
        }
        if (jumpType == null || jumpType.trim().isEmpty()) {
            throw new ServiceException("jumpType不能为空!");
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
                List<SnInfoVO> result = jdbcTemplate.query(snSql.toString(), snList.toArray(), new BeanPropertyRowMapper<>(SnInfoVO.class));
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
     * 执行跳站
     *
     * @author weiyiming
     * @date 2025-12-05
     */
    @Override
    public String execute(List<String> snList, String station, String jumpType, String remark) {
        List<SysDictData> dbList = dictDataService.selectDictDataByType("db_info");
        String tableName = dictDataService.selectDictByTypeAndLabel(jumpType, "SN");
        String logTableName = dictDataService.selectDictByTypeAndLabel(jumpType, "LOG");
        if (tableName == null || tableName.isEmpty()) {
            throw new ServiceException("跳站类型SN配置不完整: " + jumpType);
        }
        if (logTableName == null || logTableName.isEmpty()) {
            throw new ServiceException("跳站类型LOG配置不完整: " + jumpType);
        }
        String mcbSnoList = String.join(",", Collections.nCopies(snList.size(), "?"));
        for (SysDictData dbInfo : dbList) {
            String dbIdentifier = dbInfo.getDictValue();
            StringBuilder snSql = new StringBuilder();
            snSql.append("SELECT TOP 10000 * FROM ").append(tableName).append(" WHERE McbSno IN (").append(mcbSnoList).append(")");
            try {
                List<SnInfoVO> result = jdbcTemplate.query(snSql.toString(), snList.toArray(), new BeanPropertyRowMapper<>(SnInfoVO.class));
                if (!result.isEmpty()) {
                    return executeJumpInDatabase(result, station, jumpType, remark, dbIdentifier, tableName, logTableName);
                }
            } catch (DataAccessException e) {
                logger.warn("查询数据库 {} 时发生错误: {}", dbIdentifier, e.getMessage());
            }
        }
        throw new ServiceException("未找到有效的SN信息");
    }

    /**
     * 在指定数据库中执行跳站操作
     */
    private String executeJumpInDatabase(List<SnInfoVO> snInfoList, String station, String jumpType,
                                         String remark, String dbIdentifier, String tableName, String logTableName) {
        int successCount = 0;
        List<String> failedSnList = new ArrayList<>();
        for (SnInfoVO snInfo : snInfoList) {
            String sn = snInfo.getMcbSno();
            String originalWc = snInfo.getNwc(); // 原始站点 跳站前的站点
            int snoId = snInfo.getSnoId(); // 从SnInfoVO中获取SnoId（SNO表的主键）用来log表插入值
            try {
                String updateSql = "UPDATE " + tableName +
                        " SET NWC = ?, udt = GETDATE() " +
                        "WHERE McbSno = ?";
                int updatedRows = jdbcTemplate.update(updateSql, station, sn);
                if (updatedRows > 0) {
                    successCount++;
                    // 记录日志 - 使用SN表的主键ID作为LOG表的SnoId字段
                    String logSql = "INSERT INTO " + logTableName +
                            " (SnoId, McbSno, Original_WC, Dest_WC, Reason, Creator, Cdt) " +
                            "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
                    jdbcTemplate.update(logSql, snoId, sn, originalWc, station, remark, "MESTools");
                } else {
                    failedSnList.add(sn);
                }
            } catch (DataAccessException e) {
                logger.error("跳站操作失败，SN: " + sn + " 数据库: " + dbIdentifier, e);
                failedSnList.add(sn);
            }
        }
        // 构造返回结果
        StringBuilder resultMsg = new StringBuilder();
        resultMsg.append("成功处理 ").append(successCount).append(" 个SN");
        if (!failedSnList.isEmpty()) {
            resultMsg.append("，失败SN: ").append(String.join(", ", failedSnList));
        }
        return resultMsg.toString();
    }

    /**
     * 撤销跳站操作
     *
     * @author weiyiming
     * @date 2025-12-05
     */
    @Override
    public String undoExecute(List<String> snList, String station, String jumpType, String remark) {
        // 获取数据库列表
        List<SysDictData> dbList = dictDataService.selectDictDataByType("db_info");
        String tableName = dictDataService.selectDictByTypeAndLabel(jumpType, "SN");
        String logTableName = dictDataService.selectDictByTypeAndLabel(jumpType, "LOG");
        if (tableName == null || tableName.isEmpty()) {
            throw new ServiceException("跳站类型SN配置不完整: " + jumpType);
        }
        if (logTableName == null || logTableName.isEmpty()) {
            throw new ServiceException("跳站类型LOG配置不完整: " + jumpType);
        }
        String mcbSnoList = String.join(",", Collections.nCopies(snList.size(), "?"));
        for (SysDictData dbInfo : dbList) {
            String dbIdentifier = dbInfo.getDictValue();
            StringBuilder snSql = new StringBuilder();
            snSql.append("SELECT TOP 10000 * FROM ").append(tableName).append(" WHERE McbSno IN (").append(mcbSnoList).append(")");
            try {
                List<SnInfoVO> result = jdbcTemplate.query(snSql.toString(), snList.toArray(), new BeanPropertyRowMapper<>(SnInfoVO.class));
                if (!result.isEmpty()) {
                    // 在找到数据的同一个数据库中执行撤销跳站操作
                    return undoJumpInDatabase(result, station, jumpType, remark, dbIdentifier, tableName, logTableName);
                }
            } catch (DataAccessException e) {
                logger.warn("查询数据库 {} 时发生错误: {}", dbIdentifier, e.getMessage());
            }
        }
        throw new ServiceException("未找到有效的SN信息");
    }

    /**
     * 在指定数据库中执行撤销跳站操作
     */
    private String undoJumpInDatabase(List<SnInfoVO> snInfoList, String station, String jumpType,
                                      String remark, String dbIdentifier, String tableName, String logTableName) {
        int successCount = 0;
        List<String> failedSnList = new ArrayList<>();
        for (SnInfoVO snInfo : snInfoList) {
            String sn = snInfo.getMcbSno();
            ChangeWcLogDTO logInfo = getLatestLogInfoBySn(sn, logTableName); // 从日志表中获取原始站点 - 根据McbSno获取最新的一条记录
            if (logInfo == null) {
                failedSnList.add(sn + "(未找到日志信息)");
                continue;
            }
            /*验证当前站点是否与目标站点一致*/
            if (!station.equals(logInfo.getDestWc())) {
                failedSnList.add(sn + "(当前站点与目标站点不匹配)");
                continue;
            }
            String originalWc = logInfo.getOriginalWc();
            int logId = logInfo.getId(); // 获取日志记录的ID 用于后续删除
            int snoId = snInfo.getSnoId();
            try {
                String updateSql = "UPDATE " + tableName +
                        " SET NWC = ?, udt = GETDATE() " +
                        "WHERE McbSno = ?";
                int updatedRows = jdbcTemplate.update(updateSql, originalWc, sn);
                if (updatedRows > 0) {
                    successCount++;
                    /*记录撤销日志*/
                    String logSql = "INSERT INTO " + logTableName +
                            " (SnoId, McbSno, Original_WC, Dest_WC, Reason, Creator, Cdt) " +
                            "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
                    jdbcTemplate.update(logSql, snoId, sn, station, originalWc, "撤销操作: " + remark, "System");
                    /*删除原始的跳站日志记录*/
                    String deleteLogSql = "DELETE FROM " + logTableName + " WHERE Id = ?";
                    jdbcTemplate.update(deleteLogSql, logId);
                } else {
                    failedSnList.add(sn);
                }
            } catch (DataAccessException e) {
                logger.error("撤销跳站操作失败，SN: " + sn + " 数据库: " + dbIdentifier, e);
                failedSnList.add(sn);
            }
        }
        StringBuilder resultMsg = new StringBuilder();
        resultMsg.append("成功撤销 ").append(successCount).append(" 个SN的跳站操作");
        if (!failedSnList.isEmpty()) {
            resultMsg.append("，失败SN: ").append(String.join(", ", failedSnList));
        }
        return resultMsg.toString();
    }

    /**
     * 从日志表中获取最新的日志信息（根据SN）
     *
     * @author weiyiming
     * @date 2025-12-05
     */
    private ChangeWcLogDTO getLatestLogInfoBySn(String sn, String logTableName) {
        try {
            String sql = "SELECT TOP 1 Id, SnoId, Original_WC, Dest_WC FROM " + logTableName +
                    " WHERE McbSno = ? " +
                    "ORDER BY Cdt DESC";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                ChangeWcLogDTO logInfo = new ChangeWcLogDTO();
                logInfo.setId(rs.getInt("Id")); // 获取日志记录的主键ID
                logInfo.setSnoId(rs.getInt("SnoId"));
                logInfo.setOriginalWc(rs.getString("Original_WC"));
                logInfo.setDestWc(rs.getString("Dest_WC"));
                return logInfo;
            }, sn);
        } catch (DataAccessException e) {
            logger.error("查询日志信息失败，SN: " + sn, e);
            return null;
        }
    }

    /**
     * 验证SN列表中所有项目的model字段是否一致，并返回基准model值 防止不同model的SN进行跳站
     *
     * @author weiyiming
     * @date 2025-12-04
     */
    private String validateModelConsistency(List<SnInfoVO> result) {
        /*检查model为空的情况并收集对应的SN*/
        List<String> nullModelSnList = result.stream().filter(snInfo -> snInfo.getModel() == null || snInfo.getModel().isEmpty()).map(SnInfoVO::getMcbSno).collect(Collectors.toList());
        if (!nullModelSnList.isEmpty()) {
            throw new ServiceException("以下SN的机型为空: " + String.join(", ", nullModelSnList));
        }
        String baseModel = result.get(0).getModel();
        /*查找与基准model不一致的SN*/
        List<String> inconsistentSnList = result.stream().filter(snInfo -> !Objects.equals(baseModel, snInfo.getModel())).map(SnInfoVO::getMcbSno).collect(Collectors.toList());
        if (!inconsistentSnList.isEmpty()) {
            throw new ServiceException("以下SN的机型与其他不一致: " + String.join(", ", inconsistentSnList) + "。基准机型为: " + baseModel);
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
}
