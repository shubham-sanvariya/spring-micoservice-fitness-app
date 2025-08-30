package com.fitness.userservice.service;

import org.springframework.stereotype.Service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepo userRepo;

    public UserResponse register(RegisterRequest request){
        if (userRepo.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exist");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        User savedUser = userRepo.save(user);
        UserResponse userResponse = new UserResponse(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getPassword(),
            savedUser.getFirstName(),
            savedUser.getLastName(),
            savedUser.getCreatedAt(),
            savedUser.getUpdatedAt()
        );

        return userResponse;
    }
}
