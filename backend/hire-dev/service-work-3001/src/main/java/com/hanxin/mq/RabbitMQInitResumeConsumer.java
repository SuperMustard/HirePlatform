package com.hanxin.mq;

import com.hanxin.api.mq.InitResumeMQConfig;
import com.hanxin.api.mq.RabbitMQSMSConfig;
import com.hanxin.pojo.mq.SMSContentQO;
import com.hanxin.service.ResumeService;
import com.hanxin.utils.GsonUtils;
import com.hanxin.utils.SendEmailUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQInitResumeConsumer {
    @Autowired
    private ResumeService resumeService;

    @RabbitListener(queues = {InitResumeMQConfig.INIT_RESUME_QUEUE})
    public void watchQueue(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        String userId = msg.split(",")[0];
        String recordId = msg.split(",")[1];

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        try {
            if (routingKey.equalsIgnoreCase(InitResumeMQConfig.ROUTING_KEY_INIT_RESUME_LOGIN)) {
                resumeService.initResume(userId, recordId);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
                log.info("Manal ack, message id: {}", recordId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), true,true);
        }

    }

}
