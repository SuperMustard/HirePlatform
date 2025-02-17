package com.hanxin.api.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitResumeMQConfig {

    public static final String INIT_RESUME_EXCHANGE = "init_resume_exchange";

    public static final String INIT_RESUME_QUEUE = "init_resume_queue";

    public static final String ROUTING_KEY_INIT_RESUME_LOGIN = "init.resume.display";


    // create exchange
    @Bean(INIT_RESUME_EXCHANGE)
    public Exchange exchange() {
        return ExchangeBuilder
                .topicExchange(INIT_RESUME_EXCHANGE)
                .durable(true)
                .build();
    }

    //create queue
    @Bean(INIT_RESUME_QUEUE)
    public Queue queue() {
        return QueueBuilder
                .durable(INIT_RESUME_QUEUE)
                .build();
    }

    //create binding
    @Bean
    public Binding initResumeBinding(@Qualifier(INIT_RESUME_EXCHANGE) Exchange exchange,
                                     @Qualifier(INIT_RESUME_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("init.resume.#")
                .noargs();
    }
}
