package com.fitness.aiservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMqConfig {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Bean
    Queue activityQueue(){
        // here the value is set to be true so that 
        // even when the rabbit mq restarts the messages remains there
        return new Queue(queue, true);
    }

    @Bean
    DirectExchange activityExchange(){
        return new DirectExchange(exchange);
    }

    @Bean
    Binding activityBinding(Queue activityQueue, DirectExchange activityExchange){
        return  BindingBuilder.bind(activityQueue).to(activityExchange).with(routingKey);
    }

    @Bean
    MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
