package com.fitness.activityservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fitness.activityservice.model.ActivityType;

public record ActivityResponse(
        String id,
        String userId,
        ActivityType type,
        Integer duration,
        Integer caloriesburned,
        LocalDateTime startTime,

        Map<String, Object> additionalMetrics,

        LocalDateTime createdAt,

        LocalDateTime updatedAt) {

}
