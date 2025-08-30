
package com.fitness.activityservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fitness.activityservice.model.ActivityType;

public record ActivityRequest(
        String userId,
        ActivityType type,
        Integer duration,
        Integer caloriesburned,
        LocalDateTime startTime,
        Map<String, Object> additionalMetrics
        ) {}