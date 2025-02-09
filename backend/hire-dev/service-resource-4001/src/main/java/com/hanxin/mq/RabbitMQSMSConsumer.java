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

import com.rabbitmq.client.Channel;

import java.io.IOException;

@Slf4j
@Component
public class RabbitMQSMSConsumer {
    @Autowired
    private SendEmailUtils sendEmailUtils;

    @RabbitListener(queues = {RabbitMQSMSConfig.SMS_QUEUE})
    public void watchQueue(Message message, Channel channel) throws Exception {
        try {
            String msg = new String(message.getBody());
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            if (routingKey.equalsIgnoreCase(RabbitMQSMSConfig.ROUTING_KEY_SMS_SEND_LOGIN)) {
                SMSContentQO smsContentQO = GsonUtils.stringToBean(msg, SMSContentQO.class);
                log.info("send code to: " + smsContentQO.getMobile());
                log.info("code is: " + smsContentQO.getContent());
                //sendEmailUtils.SendEmail("hanxin1101@outlook.com", smsContentQO.getContent());
            }

            /**
             * deliveryTag: 消息投递的标签
             * multiple: 批量确认所有消费者获得的消息
             */
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),
                    true);
        } catch (Exception e) {
            /**
             * requeue: true：重回队列 false：丢弃消息
             */
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),
                    true, //multiple
                    false); //requeue
        }
    }

}
