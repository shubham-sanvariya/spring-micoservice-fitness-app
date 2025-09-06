package com.fitness.aiservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fitness.aiservice.dto.ActivityDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {
    
    @RabbitListener(queues = "activity.queue")
    public void proccessActivity(ActivityDto activityDto){
        log.info("Received activity for processing: {}", activityDto.id());


    }
}
