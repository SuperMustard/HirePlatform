package com.hanxin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("r")
public class HelloController {

    @GetMapping("hello")
    public Object company() {
        return "Hello Resource Service!";
    }
}
