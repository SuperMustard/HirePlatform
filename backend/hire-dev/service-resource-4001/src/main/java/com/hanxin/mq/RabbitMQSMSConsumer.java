package com.hanxin.mq;

import com.hanxin.api.mq.RabbitMQSMSConfig;
import com.hanxin.pojo.mq.SMSContentQO;
import com.hanxin.utils.GsonUtils;
import com.hanxin.utils.SMSUtils;
import com.hanxin.utils.SendEmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;

@Slf4j
@Component
public class RabbitMQSMSConsumer {
    @Autowired
    private SendEmailUtils sendEmailUtils;

    @RabbitListener(queues = {RabbitMQSMSConfig.SMS_QUEUE})
    public void watchQueue(String payload, Message message) {
        log.info("pay load = " + payload);
        String msg = payload;
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if (routingKey.equalsIgnoreCase(RabbitMQSMSConfig.ROUTING_KEY_SMS_SEND_LOGIN)) {
            SMSContentQO smsContentQO =  GsonUtils.stringToBean(msg, SMSContentQO.class);
            log.info("send code to: " + smsContentQO.getMobile());
            //sendEmailUtils.SendEmail("hanxin1101@outlook.com", smsContentQO.getContent());
        }
    }

}
