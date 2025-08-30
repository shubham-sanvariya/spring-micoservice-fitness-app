package com.fitness.activityservice.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fitness.activityservice.model.Activity;

public interface ActivityRepo extends MongoRepository<Activity, String>{
    
}
