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
import org.apache.commons.lang3.StringUtils;
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
        UsersVO usersVO = getUserInfo(userBO.getUserId(), true);

       return CustomJSONResult.ok(usersVO);
    }

    private UsersVO getUserInfo(String userId, boolean needJWT) {
        // 查询获得用户的最新信息
        Users latestUser = usersService.getById(userId);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(latestUser, usersVO);

        if (needJWT) {
            // 重新生成并且覆盖原来的token
            String uToken = jwtUtils.createJWTWithPrefix(new Gson().toJson(latestUser),
                    TOKEN_USER_PREFIX);
            usersVO.setUserToken(uToken);
        }

        return usersVO;
    }


    /**
     * 根据企业id，查询绑定的hr数量有多少
     * @param companyId
     * @return
     */
    @PostMapping("getCountsByCompanyId")
    public CustomJSONResult getCountsByCompanyId(
            @RequestParam("companyId") String companyId) {

        String hrCountsStr = redis.get(REDIS_COMPANY_HR_COUNTS + ":" + companyId);
        Long hrCounts = 0l;
        if (StringUtils.isBlank(hrCountsStr)) {

            hrCounts = usersService.getCountsByCompanyId(companyId);
            redis.set(REDIS_COMPANY_HR_COUNTS + ":" + companyId,
                    hrCounts + "",
                    1 * 60);
            // FIXME: 此处有缓存击穿的风险，思考结合业务，怎么处理更好？
        } else {
            hrCounts = Long.valueOf(hrCountsStr);
        }

        return CustomJSONResult.ok(hrCounts);
    }

    /**
     * 绑定企业和hr用户的关系
     * @param hrUserId
     * @param realname
     * @param companyId
     * @return
     */
    @PostMapping("bindingHRToCompany")
    public CustomJSONResult bindingHRToCompany(
            @RequestParam("hrUserId") String hrUserId,
            @RequestParam("realname") String realname,
            @RequestParam("companyId") String companyId) {

        usersService.updateUserCompanyId(hrUserId,
                realname,
                companyId);

        Users hrUser = usersService.getById(hrUserId);

        return CustomJSONResult.ok(hrUser.getMobile());
    }

    /**
     * 刷新用户信息，传递最新的用户信息以及刷新token给前端
     * @param userId
     * @return
     */
    @PostMapping("freshUserInfo")
    public CustomJSONResult freshUserInfo(@RequestParam("userId") String userId) {
        UsersVO usersVO = getUserInfo(userId, true);
        return CustomJSONResult.ok(usersVO);
    }

    /**
     * 获得用户信息
     * @param userId
     * @return
     */
    @PostMapping("get")
    public CustomJSONResult get(@RequestParam("userId") String userId) {
        UsersVO usersVO = getUserInfo(userId, false);
        return CustomJSONResult.ok(usersVO);
    }
}
