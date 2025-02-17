package com.hanxin.controller;

import com.hanxin.result.CustomJSONResult;
import com.hanxin.service.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("resume")
public class ResumeController {

    final private ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /**
     * 初始化用户简历
     * @param userId
     * @return
     */
    @PostMapping("init")
    public CustomJSONResult init(@RequestParam("userId") String userId) {
        resumeService.initResume(userId);
        return CustomJSONResult.ok();
    }
}
