package com.hanxin.api.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQSMSConfig {

    public static final String SMS_EXCHANGE = "sms_exchange";

    public static final String SMS_QUEUE = "sms_queue";

    public static final String ROUTING_KEY_SMS_SEND_LOGIN = "hanxin.sms.send.login";


    // create exchange
    @Bean(SMS_EXCHANGE)
    public Exchange exchange() {
        return ExchangeBuilder
                .topicExchange(SMS_EXCHANGE)
                .durable(true)
                .build();
    }

    //create queue
    @Bean(SMS_QUEUE)
    public Queue queue() {
//        return new Queue(SMS_QUEUE);
        return QueueBuilder
                .durable(SMS_QUEUE)
//                .withArgument("x-message-ttl", 30*1000)
//                .withArgument("x-dead-letter-exchange",
//                        RabbitMQSMSConfig_Dead.SMS_EXCHANGE_DEAD)
//                .withArgument("x-dead-letter-routing-key",
//                        RabbitMQSMSConfig_Dead.ROUTING_KEY_SMS_DEAD)
//                .withArgument("x-max-length", 6)
                .build();
    }

    //create binding
    @Bean
    public Binding smsBinding(@Qualifier(SMS_EXCHANGE) Exchange exchange,
                              @Qualifier(SMS_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("hanxin.sms.#")
                .noargs();
    }
}
