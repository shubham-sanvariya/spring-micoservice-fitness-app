package com.fitness.activityservice.service;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {

    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId) {
        log.info("Calling User Validation API for userId: {}", userId);
        try {
            return userServiceWebClient
                    .get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("User Not found: " + userId);
            }
            else if (e.getStatusCode() == HttpStatus.BAD_GATEWAY) {
                throw new RuntimeException("Invalid Request: " + userId);
            }
            throw new RuntimeException("something went wrong",e);
        }
    }
}
