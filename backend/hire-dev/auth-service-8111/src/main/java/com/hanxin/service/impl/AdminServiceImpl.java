package com.hanxin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanxin.pojo.Admin;
import com.hanxin.mapper.AdminMapper;
import com.hanxin.pojo.bo.AdminBO;
import com.hanxin.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxin.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务实现类
 * </p>
 *
 * @author hanxin
 * @since 2025-01-09
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin getAdminInfo(AdminBO adminBO) {
        Admin admin = getAdmin(adminBO.getUsername());
        return admin;

    }

    @Override
    public boolean adminLogin(AdminBO adminBO) {

        // get slat
        Admin admin = getAdmin(adminBO.getUsername());

        if (admin == null) {
            return false;
        } else {
            String slat = admin.getSlat();
            String md5Str = MD5Utils.encrypt(adminBO.getPassword(), slat);
            if (md5Str.equalsIgnoreCase(admin.getPassword())) {
                return true;
            }
        }

        return false;
    }

    private Admin getAdmin(String username) {
        Admin admin = adminMapper.selectOne(
                new QueryWrapper<Admin>()
                        .eq("username", username));

        return admin;
    }
}
