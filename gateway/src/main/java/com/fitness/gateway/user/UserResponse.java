package com.fitness.gateway.user;

import java.time.LocalDateTime;

public record UserResponse(
                String id,
                String keycloakId,
                String email,
                String password,
                String firstName,
                String lastName,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {

}
