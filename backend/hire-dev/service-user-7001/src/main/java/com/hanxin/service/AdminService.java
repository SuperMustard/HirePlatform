package com.hanxin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxin.pojo.Admin;
import com.hanxin.pojo.bo.AdminBO;
import com.hanxin.pojo.bo.CreateAdminBO;
import com.hanxin.utils.PagedGridResult;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author hanxin
 * @since 2025-01-09
 */
public interface AdminService {
    /**
     * 创建admin账号
     * @param createAdminBO
     */
    public void createAdmin(CreateAdminBO createAdminBO);

    /**
     * 查询admin列表
     * @param accountName
     * @param page
     * @param limit
     * @return
     */
    public PagedGridResult getAdminList(String accountName,
                                        Integer page,
                                        Integer limit);

    /**
     * 删除admin账号
     * @param username
     */
    public void deleteAdmin(String username);
}
