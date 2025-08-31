package com.fitness.activityservice.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fitness.activityservice.model.Activity;
import java.util.List;


public interface ActivityRepo extends MongoRepository<Activity, String>{
    List<Activity> findByUserId(String userId);
}
