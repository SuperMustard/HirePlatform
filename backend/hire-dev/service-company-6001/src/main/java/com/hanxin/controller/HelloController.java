package com.hanxin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("c")
public class HelloController {

    @GetMapping("Hello")
    public Object company() {
        return "Hello Company Service!";
    }
}
