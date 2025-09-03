package com.fitness.activityservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMqConfig {
    
    @Bean
    Queue activityQueue(){
        // here the value is set to be true so that 
        // even when the rabbit mq restarts the messages remains there
        return new Queue("activity.queue", true);
    }

    @Bean
    MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
