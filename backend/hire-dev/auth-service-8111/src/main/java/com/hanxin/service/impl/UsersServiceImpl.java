package com.hanxin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanxin.api.feign.WorkMicroServiceFeign;
import com.hanxin.api.mq.InitResumeMQConfig;
import com.hanxin.enums.Sex;
import com.hanxin.enums.ShowWhichName;
import com.hanxin.enums.UserRole;
import com.hanxin.mq.InitResumeMQProducerHandler;
import com.hanxin.pojo.Users;
import com.hanxin.mapper.UsersMapper;
import com.hanxin.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxin.utils.DesensitizationUtil;
import com.hanxin.utils.GsonUtils;
import com.hanxin.utils.LocalDateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
@Slf4j
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private WorkMicroServiceFeign workMicroServiceFeign;

    private static final String USER_FACE1 = "http://122.152.205.72:88/group1/M00/00/05/CpoxxF6ZUySASMbOAABBAXhjY0Y649.png";

    @Override
    public Users queryEmailIsExist(String email) {

        Users user = usersMapper.selectOne(new QueryWrapper<Users>()
                .eq("email", email));

        return user;
    }

    @Override
    public Users queryMobileIsExist(String mobile) {

        Users user = usersMapper.selectOne(new QueryWrapper<Users>()
                .eq("mobile", mobile));

        return user;
    }

    @Transactional
    @Override
    public Users createUsers(String mobile) {

        Users user = new Users();

        user.setEmail("");
        user.setMobile(mobile);
        user.setNickname("用户" + DesensitizationUtil.commonDisplay(mobile));
        user.setRealName("测试");
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
        user.setDescription("努力生存");

        // 我参加工作的日期，默认使用注册当天的日期
        user.setStartWorkDate(LocalDate.now());
        user.setPosition("开心程序员");
        user.setRole(UserRole.CANDIDATE.type);
        user.setHrInWhichCompanyId("");

        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        usersMapper.insert(user);

//        CustomJSONResult result =  workMicroServiceFeign.init(user.getId());
//        if (result.getStatus() != 200) {
//            String xid = RootContext.getXID();
//
//            if (StringUtils.isNotBlank(xid)) {
//                try {
//                    GlobalTransactionContext.reload(xid).rollback();
//                } catch (TransactionException e) {
//                    e.printStackTrace();
//                }finally {
//                    ExceptionWrapper.display(ResponseStatusEnum.USER_STATUS_ERROR);
//                }
//
//            }
//        }

        return user;
    }

    @Autowired
    public RabbitTemplate rabbitTemplate;

    @Autowired
    public InitResumeMQProducerHandler producerHandler;

    @Transactional
    @Override
    public Users createUsersAndInitResumeMQ(String mobile) {
        Users user = createUsers(mobile);

        producerHandler.saveLocalMsg(InitResumeMQConfig.INIT_RESUME_EXCHANGE,
                InitResumeMQConfig.ROUTING_KEY_INIT_RESUME_LOGIN,
                user.getId());

//        rabbitTemplate.convertAndSend(
//                InitResumeMQConfig.INIT_RESUME_EXCHANGE,
//                InitResumeMQConfig.ROUTING_KEY_INIT_RESUME_LOGIN,
//                user.getId());

        return user;
    }
}
