package com.fitness.aiservice.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeminiService {
    
    @Value("${GEMINI_API_URL}")
    private String geminiApiUrl;

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.build();
    }

    public String getAnswer(String question){
        Map<String , Object> requestBody = Map.of(
            "contents",
            new Object[]{
                Map.of("parts", new Object[] {
                    Map.of("text", question)
                })
            }
        );

        String response = webClient.post()
            .uri(geminiApiUrl)
            .headers((headers) -> {
                headers.set("Content-Type", "application/json");
                headers.set("X-goog-api-key", geminiApiKey);
            })
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return response;
    }
}
