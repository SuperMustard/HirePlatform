package com.hanxin.service;

import com.hanxin.pojo.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxin.pojo.bo.ModifyUserBO;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author hanxin
 * @since 2025-01-09
 */
public interface UsersService {
    public void modifyUserInfo(ModifyUserBO userBO);

    public Users getById(String uid);
}
