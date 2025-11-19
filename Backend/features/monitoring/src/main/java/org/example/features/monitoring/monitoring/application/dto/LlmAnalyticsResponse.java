package org.example.features.monitoring.monitoring.application.dto;

import java.time.Instant;
import java.util.List;

/**
 * DTO returned to the frontend exposing LLM analytics information.
 */
public record LlmAnalyticsResponse(
    String activeProviderId,
    String activeProviderName,
    String activeProviderMode,
    long switches,
    Instant lastSwitchAt,
    List<LlmProviderResponse> providers
) {

    public record LlmProviderResponse(
        String id,
        String displayName,
        String mode,
        String baseUrl,
        boolean available,
        boolean requiresApiKey,
        boolean active
    ) {}
}
