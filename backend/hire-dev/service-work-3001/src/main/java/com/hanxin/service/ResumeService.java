package com.hanxin.service;

public interface ResumeService {

    public void initResume(String userId);

    /**
     * 用户注册的时候初始化简历
     * @param userId
     */
    public void initResume(String userId, String msgId);
}
