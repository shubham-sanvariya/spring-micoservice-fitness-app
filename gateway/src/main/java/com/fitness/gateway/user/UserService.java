package com.fitness.gateway.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {
        log.info("Calling User Validation API for userId: {}", userId);
            return userServiceWebClient
                    .get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class, e -> {
                        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                            throw new RuntimeException("User Not found: " + userId);
                        } else if (e.getStatusCode() == HttpStatus.BAD_GATEWAY) {
                            throw new RuntimeException("Invalid Request: " + userId);
                        }
                        throw new RuntimeException("something went wrong", e);
                    });
        
    }

    public Mono<UserResponse> registerUser(RegisterRequest request){
        log.info("Calling User Registeration API for userId: {}", request.email());
        log.info("REGISTER REQUEST BODY: {}", request);
        return userServiceWebClient
                .post()
                .uri("/api/users/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        throw new RuntimeException("Bad Request " + e.getMessage());
                    } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                        throw new RuntimeException("Internal server error" + e.getMessage());
                    }
                    throw new RuntimeException("something went wrong", e);
                }); 
    }
}
