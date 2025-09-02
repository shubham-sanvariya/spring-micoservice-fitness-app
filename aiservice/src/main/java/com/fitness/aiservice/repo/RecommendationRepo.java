package com.fitness.aiservice.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fitness.aiservice.model.Recommendation;
import java.util.List;
import java.util.Optional;


public interface RecommendationRepo extends MongoRepository<Recommendation,String >{
    List<Recommendation> findByUserId(String userId);

    Optional<Recommendation> findByActivityId(String activityId);
}
