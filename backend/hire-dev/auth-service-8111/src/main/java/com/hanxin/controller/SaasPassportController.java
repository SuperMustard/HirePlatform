package com.hanxin.controller;

import com.google.gson.Gson;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.pojo.Users;
import com.hanxin.result.CustomJSONResult;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.service.UsersService;
import com.hanxin.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("saas")
@Slf4j
public class SaasPassportController extends BaseInfoProperties {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsersService usersService;

    @PostMapping("getQRToken")
    public CustomJSONResult getQRToken() {
        String qrToken = UUID.randomUUID().toString();

        // save qrtoken into redis
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken, qrToken, 5*60);
        // mark this qrToken to unread
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken, "0", 5*60);

        //return this qrToken to front-end
        return CustomJSONResult.ok(qrToken);
    }

    @PostMapping("scanCode")
    public CustomJSONResult scanCode(String qrToken, HttpServletRequest request) {

        // check qrToken
        if (StringUtils.isBlank(qrToken))
            return CustomJSONResult.errorCustom(ResponseStatusEnum.FAILED);

        // get qrToken from redis
        String redisQRToken = redis.get(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken);
        if (!redisQRToken.equalsIgnoreCase(qrToken))
            return CustomJSONResult.errorCustom(ResponseStatusEnum.FAILED);

        // get userId and userJWT from header
        String headerUserId = request.getHeader("appUserId");
        String headerUserToken = request.getHeader("appUserToken");

        // check headerUserId + headerUserToken
        if (StringUtils.isBlank(headerUserId) || StringUtils.isBlank(headerUserToken))
            return CustomJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);

        // check JWT
        String userJson = jwtUtils.checkJWT(headerUserToken.split("@")[1]);
        if (StringUtils.isBlank(userJson))
            return CustomJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);

        // create login token
        String preToken = UUID.randomUUID().toString();

        //redis rewrite qrToken
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken, preToken, 5*60);

        //mark the qrToken disable
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken, "1," + preToken, 5*60);

        // return preToken to client
        return CustomJSONResult.ok(preToken);
    }

    //used to detect whether this QR code has been read
    @PostMapping("codeHasBeenRead")
    public CustomJSONResult codeHasBeenRead(String qrToken) {

        String readStr = redis.get(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken);
        List list = new ArrayList();

        if (StringUtils.isNotBlank(readStr)) {

            String[] readArr = readStr.split(",");
            if (readArr.length >= 2) {
                list.add(Integer.valueOf(readArr[0]));
                list.add(readArr[1]);
            } else {
                list.add(0);
            }

            return CustomJSONResult.ok(list);
        } else {
            return CustomJSONResult.ok(list);
        }
    }

    /**
     * 4. using preToken to check if it is allowed to login
     * ps: can use websocket or netty to communicate with front-end
     * @param userId
     * @param qrToken
     * @param preToken
     * @return
     */
    @PostMapping("goQRLogin")
    public CustomJSONResult goQRLogin(String userId,
                                     String qrToken,
                                     String preToken) {

        String preTokenRedisArr = redis.get(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken);

        if (StringUtils.isNotBlank(preTokenRedisArr)) {
            String preTokenRedis = preTokenRedisArr.split(",")[1];
            if (preTokenRedis.equalsIgnoreCase(preToken)) {
                // 根据用户id获得用户信息
                Users hrUser = usersService.getById(userId);
                if (hrUser == null) {
                    return CustomJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
                }

                // save user info into redis, so that front end can get user info later
                // ps: if we use websocket, we can directly message the front-end userinfo
                redis.set(REDIS_SAAS_USER_INFO + ":temp:" + preToken, new Gson().toJson(hrUser),5*60);
            }
        }

        return CustomJSONResult.ok();
    }

    @PostMapping("logout")
    public CustomJSONResult logout(@RequestParam String userId,
                                   HttpServletRequest request) throws Exception {

        // 后端只需要清除用户的token信息即可，前端也需要清除相关的用户信息
        //redis.del(REDIS_USER_TOKEN + ":" + userId);

        return CustomJSONResult.ok();
    }
}


