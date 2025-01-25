package com.hanxin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanxin.enums.Sex;
import com.hanxin.enums.ShowWhichName;
import com.hanxin.enums.UserRole;
import com.hanxin.pojo.Users;
import com.hanxin.mapper.UsersMapper;
import com.hanxin.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxin.utils.DesensitizationUtil;
import com.hanxin.utils.LocalDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
    @Autowired
    private UsersMapper usersMapper;

    private static final String USER_FACE1 = "http://122.152.205.72:88/group1/M00/00/05/CpoxxF6ZUySASMbOAABBAXhjY0Y649.png";

    @Override
    public Users queryEmailIsExist(String email) {

        Users user = usersMapper.selectOne(new QueryWrapper<Users>()
                .eq("email", email));

        return user;
    }

    @Transactional
    @Override
    public Users createUsers(String email) {

        Users user = new Users();

        user.setEmail(email);
        user.setMobile("13123193614");
        user.setNickname("白雪女王" + DesensitizationUtil.commonDisplay("13123193614"));
        user.setRealName("白雪");
        user.setShowWhichName(ShowWhichName.nickname.type);


        user.setSex(Sex.secret.type);
        user.setFace(USER_FACE1);

        LocalDate birthday = LocalDateUtils.parseLocalDate("1980-01-01",
                        LocalDateUtils.DATE_PATTERN);
        user.setBirthday(birthday);

        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setDescription("招收贱狗");

        // 我参加工作的日期，默认使用注册当天的日期
        user.setStartWorkDate(LocalDate.now());
        user.setPosition("SM女王");
        user.setRole(UserRole.CANDIDATE.type);
        user.setHrInWhichCompanyId("");

        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        usersMapper.insert(user);

        return user;
    }
}
