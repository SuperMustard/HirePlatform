package com.hanxin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("w")
public class HelloController {

    @GetMapping("hello")
    public Object company() {
        return "Hello Work Service!";
    }

    @Value("${server.port}")
    private String port;

    @GetMapping("port")
    public Object port() {
        return "port = " + port;
    }
}
