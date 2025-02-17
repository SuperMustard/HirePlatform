package com.hanxin.mq;

import com.hanxin.pojo.MqLocalMsgRecord;
import com.hanxin.service.MqLocalMsgRecordService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class InitResumeMQProducerHandler {
    @Autowired
    private MqLocalMsgRecordService recordService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private ThreadLocal<List<String>> msgIdsThreadLocal = new ThreadLocal<List<String>>();

    public void saveLocalMsg(String targetExchange, String routingKey, String msgContent) {
        MqLocalMsgRecord record = new MqLocalMsgRecord();
        record.setTargetExchange(targetExchange);
        record.setRoutingKey(routingKey);
        record.setMsgContent(msgContent);
        record.setCreatedTime(LocalDateTime.now());
        record.setUpdatedTime(LocalDateTime.now());

        recordService.save(record);

        List<String> msgIds = msgIdsThreadLocal.get();
        if (CollectionUtils.isEmpty(msgIds)) {
            msgIds = new ArrayList<>();
        }

        msgIds.add(record.getId());
        msgIdsThreadLocal.set(msgIds);
    }

    /**
     * send messages in thread local
     */
    public void sendAllLocalMsg() {
        List<String> msgIds = msgIdsThreadLocal.get();
        if (CollectionUtils.isEmpty(msgIds)) {
            return;
        }

        List<MqLocalMsgRecord> msgRecords = recordService.getBatchLocalMsgRecordList(msgIds);
        for (MqLocalMsgRecord record: msgRecords) {
            rabbitTemplate.convertAndSend(
                    record.getTargetExchange(),
                    record.getRoutingKey(),
                    record.getMsgContent() + "," + record.getId());
        }
    }
}
