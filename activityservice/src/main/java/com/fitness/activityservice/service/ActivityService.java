package com.fitness.activityservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repo.ActivityRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepo activityRepo;

    public ActivityResponse trackActivity(ActivityRequest request) {
        Activity activity = Activity.builder()
                .userId(request.userId())
                .type(request.type())
                .duration(request.duration())
                .caloriesburned(request.caloriesburned())
                .startTime(request.startTime())
                .additionalMetrics(request.additionalMetrics())
                .build();

        Activity savedActivity = activityRepo.save(activity);

        return mapToResponse(savedActivity);
    }

    public List<ActivityResponse> getUsersActivities(String userId) {
        List<Activity> activities = activityRepo.findByUserId(userId);

        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
