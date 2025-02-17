package com.hanxin;

import com.hanxin.mq.InitResumeMQProducerHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;

@Component
public class MyTransactionManager extends DataSourceTransactionManager {

    @Autowired
    private InitResumeMQProducerHandler producerHandler;

    public MyTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        try {
            super.doCommit(status);
        } finally {
            // 事务提交之后，发送ThreadLocal中的消息（从当前线程中获得未发送的消息id）
            producerHandler.sendAllLocalMsg();
        }

    }
}
