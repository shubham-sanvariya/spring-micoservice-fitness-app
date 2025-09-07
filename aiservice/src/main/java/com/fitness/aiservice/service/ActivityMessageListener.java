package com.fitness.aiservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fitness.aiservice.dto.ActivityDto;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repo.RecommendationRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAiService aiService;
    private final RecommendationRepo recommendationRepo;
    
    @RabbitListener(queues = "activity.queue")
    public void proccessActivity(ActivityDto activityDto){
        log.info("Received activity for processing: {}", activityDto.id());

        // log.info("Generated Recommendation: {}",aiService.generateRecommendation(activityDto));

        Recommendation recommendation = aiService.generateRecommendation(activityDto);

        recommendationRepo.save(recommendation);
    }
}
