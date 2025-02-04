package com.hanxin.service;

import com.hanxin.pojo.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxin.pojo.bo.AdminBO;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author hanxin
 * @since 2025-01-09
 */
public interface AdminService extends IService<Admin> {
    public boolean adminLogin(AdminBO adminBO);

    public Admin getAdminInfo(AdminBO adminBO);
}
