package org.example.features.monitoring.monitoring.application.mappers;

import org.example.features.llm.domain.dto.LlmAnalyticsSnapshot;
import org.example.features.monitoring.monitoring.application.dto.LlmAnalyticsResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class LlmAnalyticsMapper {

    public LlmAnalyticsResponse map(LlmAnalyticsSnapshot snapshot) {
        Map<String, LlmAnalyticsSnapshot.ProviderSnapshot> providerSnapshotMap = snapshot.providers();
        List<LlmAnalyticsResponse.LlmProviderResponse> providers = new ArrayList<>(providerSnapshotMap.size());

        LlmAnalyticsSnapshot.ProviderSnapshot activeSnapshot =
            providerSnapshotMap.get(snapshot.activeProviderId());

        providerSnapshotMap.forEach((id, provider) ->
            providers.add(new LlmAnalyticsResponse.LlmProviderResponse(
                provider.id(),
                provider.displayName(),
                provider.mode(),
                provider.baseUrl(),
                provider.enabled(),
                provider.requiresApiKey(),
                id.equals(snapshot.activeProviderId())
            ))
        );

        return new LlmAnalyticsResponse(
            snapshot.activeProviderId(),
            activeSnapshot != null ? activeSnapshot.displayName() : null,
            activeSnapshot != null ? activeSnapshot.mode() : null,
            snapshot.switchCount(),
            snapshot.lastSwitchAt(),
            providers
        );
    }
}
