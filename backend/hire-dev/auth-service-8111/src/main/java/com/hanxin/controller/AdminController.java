package com.hanxin.controller;

import com.google.gson.Gson;
import com.hanxin.api.intercept.JWTCurrentUserInterceptor;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.pojo.Admin;
import com.hanxin.pojo.bo.AdminBO;
import com.hanxin.pojo.vo.AdminVO;
import com.hanxin.result.CustomJSONResult;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.service.impl.AdminServiceImpl;
import com.hanxin.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("admin")
public class AdminController extends BaseInfoProperties {
    @Autowired
    AdminServiceImpl adminService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("login")
    public CustomJSONResult login(@Valid @RequestBody AdminBO adminBO) {
        boolean isExist = adminService.adminLogin(adminBO);
        if (!isExist) {
            return CustomJSONResult.errorCustom(ResponseStatusEnum.ADMIN_LOGIN_ERROR);
        }

        Admin admin = adminService.getAdminInfo(adminBO);
        String adminToken = jwtUtils.createJWTWithPrefix(new Gson().toJson(admin), TOKEN_ADMIN_PREFIX);

        return CustomJSONResult.ok(adminToken);
    }

    @GetMapping("info")
    public CustomJSONResult info() {

        Admin admin = JWTCurrentUserInterceptor.adminUser.get();

        AdminVO adminVO = new AdminVO();
        BeanUtils.copyProperties(admin, adminVO);

        return CustomJSONResult.ok(adminVO);
    }

    @PostMapping("logout")
    public CustomJSONResult logout() {
        return CustomJSONResult.ok();
    }
}
