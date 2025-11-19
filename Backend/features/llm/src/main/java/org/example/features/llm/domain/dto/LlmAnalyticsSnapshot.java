package org.example.features.llm.domain.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Immutable snapshot describing the state of available LLM providers.
 */
public record LlmAnalyticsSnapshot(
    String activeProviderId,
    Map<String, ProviderSnapshot> providers,
    long switchCount,
    Instant lastSwitchAt
) {

    public record ProviderSnapshot(
        String id,
        String displayName,
        String mode,
        String baseUrl,
        boolean enabled,
        boolean requiresApiKey,
        String model
    ) {}
}
