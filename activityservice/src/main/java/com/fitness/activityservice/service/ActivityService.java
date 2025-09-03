package com.fitness.activityservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repo.ActivityRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepo activityRepo;
    private final UserValidationService validationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest request) {
        boolean isValidUser = validationService.validateUser(request.userId());

        if (!isValidUser) {
            throw new RuntimeException("Invalid User: " + request.userId());
        }
        Activity activity = Activity.builder()
                .userId(request.userId())
                .type(request.type())
                .duration(request.duration())
                .caloriesburned(request.caloriesburned())
                .startTime(request.startTime())
                .additionalMetrics(request.additionalMetrics())
                .build();

        Activity savedActivity = activityRepo.save(activity);

        // publish to rabbitMQ for AI processing

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
        } catch (Exception e) {
            log.error("Failed to publish activity to RabbitMQ : ", e);
            // TODO: handle exception
        }

        return mapToResponse(savedActivity);
    }

    public List<ActivityResponse> getUsersActivities(String userId) {
        List<Activity> activities = activityRepo.findByUserId(userId);

        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getActivityById(String activityId){
        return activityRepo.findById(activityId)
            .map(this::mapToResponse)
            .orElseThrow(() -> new RuntimeException("Activity not found with id: " + activityId));
    }

    private ActivityResponse mapToResponse(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getUserId(),
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesburned(),
                activity.getStartTime(),
                activity.getAdditionalMetrics(),
                activity.getCreatedAt(),
                activity.getUpdatedAt());
    }
}
