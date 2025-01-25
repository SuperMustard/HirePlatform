package com.hanxin.controller;

import com.google.gson.Gson;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.pojo.Users;
import com.hanxin.pojo.bo.RegistLoginBO;
import com.hanxin.pojo.vo.UsersVO;
import com.hanxin.result.CustomJSONResult;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.service.UsersService;
import com.hanxin.utils.IPUtil;
import com.hanxin.utils.JwtUtils;
import com.hanxin.utils.SendEmailUtils;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("passport")
@Slf4j
public class PassportController extends BaseInfoProperties {

    @Autowired
    private SendEmailUtils sendEmailUtils;

    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("getEmailCode")
    public CustomJSONResult getEmailCode(String email, HttpServletRequest request) {
        if (StringUtil.isNullOrEmpty(email)) {
            return CustomJSONResult.error();
        }

        //using ip address to limit code sending times
        String userIp = IPUtil.getRequestIp(request);
        log.info(userIp);
        redis.setnx60s(EMAIL_CODE + ":" + userIp, email);

        String code = (int)((Math.random() * 9 + 1) * 100000) + "";
//        sendEmailUtils.SendEmail(email, code);
        log.info("Email Code is: " + code);
//
        // store code into redis
        log.info(EMAIL_CODE + ":" + email);
        redis.set(EMAIL_CODE + ":" + email, code, 30 * 60);

        return CustomJSONResult.ok();
    }

    @PostMapping("login")
    public CustomJSONResult login(@Valid @RequestBody RegistLoginBO registLoginBO, HttpServletRequest request) {
        String code = registLoginBO.getCode();
        String email = registLoginBO.getEmail();

        log.info(EMAIL_CODE + ":" + email);
        String redisCode = redis.get(EMAIL_CODE + ":" + email);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code)) {
            return CustomJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        Users user = usersService.queryEmailIsExist(email);
        if (user == null) {
            user = usersService.createUsers(email);
        }

        //save user token into redis
//        String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID().toString();
//        redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken);

        String jwt = jwtUtils.createJWTWithPrefix(new Gson().toJson(user), 1000L, TOKEN_USER_PREFIX);

        redis.del(EMAIL_CODE + ":" + email);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(jwt);

        return CustomJSONResult.ok(usersVO);
    }

    @PostMapping("logout")
    public CustomJSONResult logout(@RequestParam String userId,
                                  HttpServletRequest request) throws Exception {

        // 后端只需要清除用户的token信息即可，前端也需要清除相关的用户信息
        redis.del(REDIS_USER_TOKEN + ":" + userId);

        return CustomJSONResult.ok();
    }
}
