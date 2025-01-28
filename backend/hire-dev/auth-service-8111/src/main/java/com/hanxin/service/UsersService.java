package com.hanxin.service;

import com.hanxin.pojo.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author hanxin
 * @since 2025-01-09
 */
public interface UsersService extends IService<Users> {
    public Users queryEmailIsExist(String email);

    public Users queryMobileIsExist(String mobile);

    public Users createUsers(String email);
}
