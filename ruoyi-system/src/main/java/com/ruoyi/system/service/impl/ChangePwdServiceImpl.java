package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.service.IChangePwdService;
import com.ruoyi.system.service.ISysDictDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-12-07
 * @Description: 修改FIS用户密码
 */
@Service
public class ChangePwdServiceImpl implements IChangePwdService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ISysDictDataService dictDataService;

    private static final Logger logger = LoggerFactory.getLogger(ChangePwdServiceImpl.class);

    @Override
    public boolean changePwd(String fisNumber, String factory, String password) {
        //根据factory的值F6 F3作为label从字典db_info中获取对应的value值 作为DB的信息   从fis_pwd_table字典获取lebel为FISPWDTABLE的value作为tableName
        // 获取数据库配置
        String dbConfig = dictDataService.selectDictByTypeAndLabel("db_info", factory);
        if (dbConfig == null || dbConfig.isEmpty()) {
            throw new ServiceException("未找到" + factory + "工厂的数据库配置信息");
        }
        // 获取表名
        String tableName = dictDataService.selectDictByTypeAndLabel("fis_pwd_table", "FISPWDTABLE");
        if (tableName == null || tableName.isEmpty()) {
            throw new ServiceException("未找到FIS密码表配置信息");
        }
        try {
            String checkSql = "SELECT COUNT(1) FROM " + tableName + " WHERE fis_number = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, fisNumber);
            if (count == null || count == 0) {
                throw new ServiceException("FIS账号 [" + fisNumber + "] 不存在，无法修改密码");
            }
            if (count > 1) {
                throw new ServiceException("FIS账号 [" + fisNumber + "] 存在多条记录，无法修改密码");
            }
            String updateSql = "UPDATE " + tableName + " SET password = ?, Udt = GETDATE() WHERE fis_number = ?";
            int result = jdbcTemplate.update(updateSql, password, fisNumber);
            return result > 0;
        } catch (DataAccessException e) {
            logger.error("数据库操作异常: {}", e.getMessage());
            if (e.getMessage().contains("Invalid object name")) {
                throw new ServiceException("数据库表 [" + tableName + "] 不存在");
            }
            throw new ServiceException("数据库操作失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("密码修改异常: {}", e.getMessage());
            // ServiceException直接抛出，其他异常包装为ServiceException
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            throw new ServiceException("密码修改失败: " + e.getMessage());
        }
    }
}