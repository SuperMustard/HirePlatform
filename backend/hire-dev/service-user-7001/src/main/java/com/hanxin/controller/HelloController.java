package com.hanxin.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.google.gson.Gson;
import com.hanxin.api.intercept.JWTCurrentUserInterceptor;
import com.hanxin.base.BaseInfoProperties;
import com.hanxin.pojo.Users;
import com.hanxin.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("u")
@Slf4j
public class HelloController extends BaseInfoProperties {

    @Autowired
    private StuService stuService;

    @Value("${server.port}")
    private String port;

    @GetMapping("stu")
    public Object stu() {
        com.hanxin.pojo.Stu stu = new com.hanxin.pojo.Stu();

        stu.setAge(18);
        stu.setName("白雪女王");

        stuService.save(stu);
        log.info("current port is: " + port);

        return "OK!";
    }

    @GetMapping("hello")
    public Object hello(HttpServletRequest request) {
        String userJson = request.getHeader(APP_USER_JSON);
        Users jwtUser = new Gson().fromJson(userJson, Users.class);
        log.info(jwtUser.toString());

        Users currentUser = JWTCurrentUserInterceptor.currentUser.get();
        log.info("Current User Interceptor: " + currentUser.toString());

        return "Hello User !!! OK!";
    }
}
