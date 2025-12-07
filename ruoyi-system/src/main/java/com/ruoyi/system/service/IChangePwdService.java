package com.ruoyi.system.service;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-12-07
 * @Description: 修改FIS账号密码
 */
public interface IChangePwdService {
    boolean changePwd(String fisNumber, String factory, String password);
}
