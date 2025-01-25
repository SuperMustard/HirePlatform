package com.hanxin.controller;

import com.hanxin.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("u")
@Slf4j
public class HelloController {

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
    public Object hello() {


        return "Hello User !!! OK!";
    }
}
