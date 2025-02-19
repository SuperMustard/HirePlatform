package com.hanxin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.exceptions.ExceptionWrapper;
import com.hanxin.pojo.Users;
import com.hanxin.mapper.UsersMapper;
import com.hanxin.pojo.bo.ModifyUserBO;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author hanxin
 * @since 2025-01-09
 */
@Service
public class UsersServiceImpl extends BaseInfoProperties implements UsersService {

    @Autowired
    private UsersMapper usersMapper;

    @Transactional
    @Override
    public void modifyUserInfo(ModifyUserBO userBO) {
        String userId = userBO.getUserId();
        if (StringUtils.isBlank(userId)) {
            ExceptionWrapper.display(ResponseStatusEnum.USER_INFO_UPDATED_ERROR);
        }

        Users pendingUser = new Users();
        pendingUser.setId(userId);
        pendingUser.setUpdatedTime(LocalDateTime.now());

        BeanUtils.copyProperties(userBO, pendingUser);

        usersMapper.updateById(pendingUser);
    }

    @Override
    public Users getById(String uid) {
        return usersMapper.selectById(uid);
    }
}
