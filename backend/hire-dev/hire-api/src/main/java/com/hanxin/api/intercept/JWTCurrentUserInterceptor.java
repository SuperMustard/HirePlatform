package com.hanxin.api.intercept;

import com.google.gson.Gson;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.pojo.Admin;
import com.hanxin.pojo.Users;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JWTCurrentUserInterceptor extends BaseInfoProperties implements HandlerInterceptor {
    public static ThreadLocal<Users> currentUser = new ThreadLocal<>();
    public static ThreadLocal<Admin> adminUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String appUserJson = request.getHeader(APP_USER_JSON);
        String saasUserJson = request.getHeader(SAAS_USER_JSON);
        String adminUserJson = request.getHeader(ADMIN_USER_JSON);

        if (StringUtils.isNotBlank(appUserJson)
                || StringUtils.isNotBlank(saasUserJson)) {
            Users appUser = new Gson().fromJson(appUserJson, Users.class);
            currentUser.set(appUser);
        }

        if (StringUtils.isNotBlank(adminUserJson)) {
            Admin admin = new Gson().fromJson(adminUserJson, Admin.class);
            adminUser.set(admin);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
