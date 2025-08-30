package com.fitness.userservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitness.userservice.model.User;


public interface UserRepo extends JpaRepository<User, String>{
    boolean existsByEmail(String email);   
}
