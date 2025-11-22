package com.ruoyi.system.service;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author: weiyiming
 * @CreateTime: 2025-11-21
 * @Description: 字符串工具
 */
public interface IStringToolService {

    String execute(String input);

    void downloadTemplate(HttpServletResponse response);
}