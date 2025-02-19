package com.hanxin.controller;

import com.google.gson.Gson;
import com.hanxin.api.intercept.JWTCurrentUserInterceptor;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.pojo.Users;
import com.hanxin.pojo.bo.ModifyUserBO;
import com.hanxin.pojo.vo.UsersVO;
import com.hanxin.result.CustomJSONResult;
import com.hanxin.service.StuService;
import com.hanxin.service.UsersService;
import com.hanxin.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("userinfo")
@Slf4j
public class UserInfoController extends BaseInfoProperties {

    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("modify")
    public CustomJSONResult modify(@RequestBody ModifyUserBO userBO) {
        usersService.modifyUserInfo(userBO);

        //return new user info help front end to refresh
        UsersVO usersVO = getUserInfo(userBO.getUserId());

       return CustomJSONResult.ok(usersVO);
    }

    private UsersVO getUserInfo(String userId) {
        // 查询获得用户的最新信息
        Users latestUser = usersService.getById(userId);

        // 重新生成并且覆盖原来的token
        String uToken = jwtUtils.createJWTWithPrefix(new Gson().toJson(latestUser),
                TOKEN_USER_PREFIX);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(latestUser, usersVO);
        usersVO.setUserToken(uToken);

        return usersVO;
    }
}
