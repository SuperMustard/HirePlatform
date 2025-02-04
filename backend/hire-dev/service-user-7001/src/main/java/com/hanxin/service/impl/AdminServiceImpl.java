package com.hanxin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.exceptions.ExceptionWrapper;
import com.hanxin.mapper.AdminMapper;
import com.hanxin.pojo.Admin;
import com.hanxin.pojo.bo.CreateAdminBO;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.service.AdminService;
import com.hanxin.utils.MD5Utils;
import com.hanxin.utils.PagedGridResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 慕聘网运营管理系统的admin账户表，仅登录，不提供注册 服务实现类
 * </p>
 *
 * @author hanxin
 * @since 2025-01-09
 */
@Service
public class AdminServiceImpl extends BaseInfoProperties implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Transactional
    @Override
    public void createAdmin(CreateAdminBO createAdminBO) {

        // admin账号判断是否存在，如果存在，则禁止账号分配
        Admin admin = getSelfAdmin(createAdminBO.getUsername());
        // 优雅异常解耦完美体现
        if (admin != null) ExceptionWrapper.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);

        // 创建账号
        Admin newAdmin = new Admin();
        BeanUtils.copyProperties(createAdminBO, newAdmin);

        // 生成随机数字或者英文字母结合的盐
        String slat = (int)((Math.random() * 9 + 1) * 100000) + "";
        String pwd = MD5Utils.encrypt(createAdminBO.getPassword(), slat);
        newAdmin.setPassword(pwd);
        newAdmin.setSlat(slat);

        newAdmin.setCreateTime(LocalDateTime.now());
        newAdmin.setUpdatedTime(LocalDateTime.now());

        adminMapper.insert(newAdmin);
    }

    @Override
    public PagedGridResult getAdminList(String accountName,
                                        Integer page,
                                        Integer limit) {

        PageHelper.startPage(page, limit);

        List<Admin> adminList = adminMapper.selectList(
                new QueryWrapper<Admin>()
                        .like("username", accountName)
        );

        return setterPagedGrid(adminList, page);
    }

    @Transactional
    @Override
    public void deleteAdmin(String username) {

        int res = adminMapper.delete(
                new QueryWrapper<Admin>()
                        .eq("username", username)
                        .ne("username", "admin")
        );

        if (res == 0) ExceptionWrapper.display(ResponseStatusEnum.ADMIN_DELETE_ERROR);
    }

    private Admin getSelfAdmin(String username) {
        Admin admin = adminMapper.selectOne(
                new QueryWrapper<Admin>()
                        .eq("username", username)
        );
        return admin;
    }
}
