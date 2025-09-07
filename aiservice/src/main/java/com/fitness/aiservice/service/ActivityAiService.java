package com.fitness.aiservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.dto.ActivityDto;
import com.fitness.aiservice.model.Recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {

    private final GeminiService geminiService;

    public Recommendation generateRecommendation(ActivityDto activityDto) {
        String prompt = createPromptForActivity(activityDto);
        String aiResponse = geminiService.getAnswer(prompt);

        log.info("Response from AI: {} ", aiResponse);

        return processAiResponse(activityDto,aiResponse);
    }

    private Recommendation processAiResponse(ActivityDto activityDto, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);

            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();

            // log.info("Passed Response From AI: {} ", jsonContent);

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode anaysisNode = analysisJson.path("analysis");

            StringBuilder fullAnalysis = new StringBuilder();

            addAnalysisiSection(fullAnalysis, anaysisNode, "overall", "Overall:");
            addAnalysisiSection(fullAnalysis, anaysisNode, "pace", "Pace:");
            addAnalysisiSection(fullAnalysis, anaysisNode, "heartRate", "Heart Rate:");
            addAnalysisiSection(fullAnalysis, anaysisNode, "caloriesBurned", "Calories:");

            List<String> improvements = extractImprovements(analysisJson.path("improvements"));

            List<String> suggetions = extractSuggestions(analysisJson.path("suggestions"));

            List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));

            return Recommendation.builder()
                .activityId(activityDto.id())
                .userId(activityDto.userId())
                .activityType(activityDto.type())
                .recommendation(fullAnalysis.toString().trim())
                .improvements(improvements)
                .suggestions(suggetions)
                .safety(safety)
                .createdAt(LocalDateTime.now())
                .build();

        } catch (Exception e) {
            e.printStackTrace();
            return createDefaultRecommendation(activityDto);
        }
    }

    private Recommendation createDefaultRecommendation(ActivityDto activity) {
        return Recommendation.builder()
                .activityId(activity.id())
                .userId(activity.userId())
                .activityType(activity.type())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();

        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> {
                safety.add(item.asText());
            });

        }
        return safety.isEmpty() ? Collections.singletonList("Follow general safety guide") : safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionNode) {
        List<String> suggestions = new ArrayList<>();

        if (suggestionNode.isArray()) {
            suggestionNode.forEach(sug -> {
                String workout = sug.path("workout").asText();
                String description = sug.path("description").asText();
                suggestions.add(String.format("%s: %s", workout, description));
            });

        }
        return suggestions.isEmpty() ? Collections.singletonList("No specific suggestions provided") : suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementNode) {
        List<String> improvements = new ArrayList<>();

        if (improvementNode.isArray()) {
            improvementNode.forEach(imp -> {
                String area = imp.path("area").asText();
                String detail= imp.path("recommendation").asText();
                improvements.add(String.format("%s: %s", area, detail));
            });

        }
        return improvements.isEmpty() ? Collections.singletonList("No specific improvements provided") : improvements;
    }

    private void addAnalysisiSection(StringBuilder fullAnalysis, JsonNode anaysisNode, String key, String prefix) {
        if (!anaysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix)
                .append(anaysisNode.path(key).asText())
                .append("\n\n");
        }
    }

    private String createPromptForActivity(ActivityDto activityDto) {
        return String.format(
                """
                        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
                        {
                          "analysis": {
                            "overall": "Overall analysis here",
                            "pace": "Pace analysis here",
                            "heartRate": "Heart rate analysis here",
                            "caloriesBurned": "Calories analysis here"
                          },
                          "improvements": [
                            {
                              "area": "Area name",
                              "recommendation": "Detailed recommendation"
                            }
                          ],
                          "suggestions": [
                            {
                              "workout": "Workout name",
                              "description": "Detailed workout description"
                            }
                          ],
                          "safety": [
                            "Safety point 1",
                            "Safety point 2"
                          ]
                        }

                        Analyze this activity:
                        Activity Type: %s
                        Duration: %d minutes
                        Calories Burned: %d
                        Additional Metrics: %s

                        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
                        Ensure the response follows the EXACT JSON format shown above.
                        """,
                activityDto.type(),
                activityDto.duration(),
                activityDto.caloriesburned(),
                activityDto.additionalMetrics());
    }
}
