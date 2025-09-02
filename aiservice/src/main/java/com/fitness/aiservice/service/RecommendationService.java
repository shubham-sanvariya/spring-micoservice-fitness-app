package com.fitness.aiservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repo.RecommendationRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    
    private final RecommendationRepo repository;

    public List<Recommendation> getUserRecommendation(String userId){
        return repository.findByUserId(userId);
    }

    public Recommendation getActivityRecommendation(String  activityId){
        return repository.findByActivityId(activityId)
            .orElseThrow(() -> new RuntimeException("No Recommendation found for this activity: " + activityId));
    }
}
