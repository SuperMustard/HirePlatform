package com.hanxin.api.retry;

import com.hanxin.exceptions.ExceptionWrapper;
import com.hanxin.result.ResponseStatusEnum;
import com.hanxin.utils.SMSUtilsRetry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class RetryComponent {
    @Retryable(value = IllegalAccessError.class, //指定异常重试
            maxAttempts = 5, //重试次数
            backoff = @Backoff(
                    delay = 1000L,
                    multiplier = 2)) //重试间隔为1秒，每次重试，重试间隔时间翻倍
    public boolean sendSmsWithRetry() {
        log.info("当前时间 Time = {}", LocalDateTime.now());
        return SMSUtilsRetry.sendSMS();
    }

    @Recover //达到最大重试次数，或抛出其他无法处理异常的时候的最终方法
    public boolean recover() {
        ExceptionWrapper.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
        return false;
    }
}
