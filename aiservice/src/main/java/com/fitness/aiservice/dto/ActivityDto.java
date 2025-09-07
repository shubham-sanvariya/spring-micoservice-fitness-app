package com.fitness.aiservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ActivityDto(
        String id,
        String userId,
        String type,
        Integer duration,
        Integer caloriesburned,
        LocalDateTime startTime,

        Map<String, Object> additionalMetrics,

        LocalDateTime createdAt,

        LocalDateTime updatedAt) {

}
