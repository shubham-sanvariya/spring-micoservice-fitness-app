package com.fitness.gateway.config.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter{

    private final UserService userService;

    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain){
        String userId = exchange
        .getRequest()
        .getHeaders()
        .getFirst("X-User-ID");
        String token = exchange
        .getRequest()
        .getHeaders()
        .getFirst("Authorization");

        RegisterRequest request = getUserDetails(token);
        if (userId == null) {
            userId = request.keycloakId();
        }

        if (userId != null & token != null) {
            String finalUserId = userId;
            return userService.validateUser(userId)
             .flatMap(exist -> {
                if (!exist) {
                    // Register User

                    if (request != null) {
                       return userService.registerUser(request);
                    }else{

                        return Mono.empty();
                    }
                }else{
                    log.info("User already exist, Skipping sync.");
                    return Mono.empty();
                }
             })
             .then(Mono.defer(() -> {
                ServerHttpRequest mutatedRequest = exchange
                .getRequest().mutate().header("X-User-ID", finalUserId)
                .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());
             }));
        }

        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {
        try {
            String tokenWithouBearer = token.replace("Bearer ", "");

            SignedJWT signedJWT = SignedJWT.parse(tokenWithouBearer);
            
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest(
                claims.getStringClaim("sub"),
                claims.getStringClaim("email"),
                "dummy@123123",
                claims.getStringClaim("given_name"),
                claims.getStringClaim("family_name")
            );

            return registerRequest;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
