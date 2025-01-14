package com.hanxin.controller;

import com.hanxin.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class HelloController {

    @Autowired
    private StuService stuService;

    @GetMapping("stu")
    public Object stu() {
        com.hanxin.pojo.Stu stu = new com.hanxin.pojo.Stu();

        stu.setAge(18);
        stu.setName("白雪女王");

        stuService.save(stu);

        return "OK!";
    }
}
