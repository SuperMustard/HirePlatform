package com.hanxin.api;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Slf4j
@Aspect
public class ServiceLogAspect {
    StopWatch stopWatch = new StopWatch();
    long begin = System.currentTimeMillis();

    @Around("execution(* com.hanxin.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long begin = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        String point = joinPoint.getTarget().getClass().getName()
                + "."
                + joinPoint.getSignature().getName();

        long end = System.currentTimeMillis();
        long takeTime = end - begin;

        if (takeTime > 300) {
            log.error("Method {} spends Too much run time {}", point, takeTime);
        } else if (takeTime > 200) {
            log.warn("Method {} spends long run time: {}", point, takeTime);
        } else {
            log.info("Method {} run time is: {}", point, takeTime);
        }

        return proceed;
    }

}
