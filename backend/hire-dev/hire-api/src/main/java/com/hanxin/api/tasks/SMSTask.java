package com.hanxin.api.tasks;

import com.hanxin.api.retry.RetryComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Async
@Slf4j
public class SMSTask {

    @Autowired
    private RetryComponent retryComponent;

    public void sendSMSTask() {
        boolean res = retryComponent.sendSmsWithRetry();
        log.info("结果为: " + res);
    }
}
