package com.hanxin.controller;

import com.hanxin.base.BaseInfoProperties;
import com.hanxin.pojo.bo.CreateAdminBO;
import com.hanxin.pojo.bo.ResetPwdBO;
import com.hanxin.result.CustomJSONResult;
import com.hanxin.service.AdminService;
import com.hanxin.utils.PagedGridResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("admininfo")
@Slf4j
public class AdminInfoController extends BaseInfoProperties {

    @Autowired
    private AdminService adminService;

    @PostMapping("create")
    public CustomJSONResult create(@Valid @RequestBody CreateAdminBO createAdminBO) {
        adminService.createAdmin(createAdminBO);
        return CustomJSONResult.ok();
    }

    @PostMapping("list")
    public CustomJSONResult list(String accountName,
                                Integer page,
                                Integer limit) {

        if (page == null) page = 1;
        if (limit == null) page = 10;

        PagedGridResult listResult = adminService.getAdminList(accountName,
                page,
                limit);

        return CustomJSONResult.ok(listResult);
    }

    @PostMapping("delete")
    public CustomJSONResult delete(String username) {
        adminService.deleteAdmin(username);
        return CustomJSONResult.ok();
    }

    @PostMapping("resetPwd")
    public CustomJSONResult resetPwd(@RequestBody ResetPwdBO resetPwdBO) {

        // resetPwdBO 校验
        // adminService 重置密码

        resetPwdBO.modifyPwd();
        return CustomJSONResult.ok();
    }

}

