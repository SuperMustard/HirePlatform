package com.hanxin.api;

import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Slf4j
@Aspect
public class SeataTransactionAspect {

//    @Before("execution(* com.hanxin.service.impl..*.*(..))")
//    public void beginTransaction(JoinPoint joinPoint) throws Throwable {
//        // get or create global transaction
//        GlobalTransaction gt = GlobalTransactionContext.getCurrentOrCreate();
//        gt.begin();
//    }
//
//    @AfterThrowing(
//            throwing = "throwable",
//            pointcut = "execution(* com.hanxin.service.impl..*.*(..))")
//    public void seataRollBack(Throwable throwable) throws Throwable {
//
//        String xid = RootContext.getXID();
//
//        if (StringUtils.isNotBlank(xid)) {
//            GlobalTransactionContext.reload(xid).rollback();
//        }
//    }

}
