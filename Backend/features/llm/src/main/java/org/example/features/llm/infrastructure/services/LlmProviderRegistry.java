package org.example.features.llm.infrastructure.services;

import org.example.features.llm.domain.dto.LlmAnalyticsSnapshot;
import org.example.features.llm.infrastructure.config.LlmProviderConfigLoader;
import org.example.features.llm.infrastructure.config.LlmProviderConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Keeps track of available LLM providers and the currently selected provider.
 */
@Component
public class LlmProviderRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmProviderRegistry.class);

    private final Map<String, ProviderDescriptor> providers;
    private final AtomicLong switchCount = new AtomicLong(0);
    private volatile String activeProviderId;
    private volatile Instant lastSwitchAt = Instant.now();

    public LlmProviderRegistry(LlmProviderConfigLoader loader) {
        LlmProviderConfigurationProperties configuration = loader.loadConfiguration();
        this.providers = Collections.unmodifiableMap(configuration.getProviders().stream()
            .filter(p -> StringUtils.hasText(p.getId()))
            .collect(Collectors.toMap(
                LlmProviderConfigurationProperties.ProviderEntry::getId,
                ProviderDescriptor::new,
                (a, b) -> b,
                LinkedHashMap::new
            )));

        this.activeProviderId = resolveInitialProvider(configuration.getActiveProvider());
        LOGGER.info("LLM provider registry initialized. Active provider: {}", this.activeProviderId);
    }

    private String resolveInitialProvider(String preferred) {
        if (StringUtils.hasText(preferred) && providers.containsKey(preferred)) {
            return preferred;
        }
        return providers.keySet().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No LLM providers configured"));
    }

    public synchronized boolean switchTo(String providerId) {
        if (!providers.containsKey(providerId)) {
            return false;
        }
        if (!providerId.equals(activeProviderId)) {
            activeProviderId = providerId;
            switchCount.incrementAndGet();
            lastSwitchAt = Instant.now();
            LOGGER.info("Active LLM provider switched to {}", providerId);
        }
        return true;
    }

    public Optional<ProviderDescriptor> getActiveProvider() {
        return Optional.ofNullable(providers.get(activeProviderId));
    }

    public LlmAnalyticsSnapshot snapshot() {
        Map<String, LlmAnalyticsSnapshot.ProviderSnapshot> providerSnapshots = providers.values().stream()
            .collect(Collectors.toUnmodifiableMap(
                ProviderDescriptor::id,
                provider -> new LlmAnalyticsSnapshot.ProviderSnapshot(
                    provider.id(),
                    provider.displayName(),
                    provider.mode(),
                    provider.baseUrl(),
                    provider.enabled(),
                    provider.requiresApiKey(),
                    provider.model()
                )
            ));

        return new LlmAnalyticsSnapshot(
            activeProviderId,
            providerSnapshots,
            switchCount.get(),
            lastSwitchAt
        );
    }

    public record ProviderDescriptor(
        String id,
        String displayName,
        String mode,
        String baseUrl,
        String apiKey,
        String model,
        boolean enabled
    ) {
        ProviderDescriptor(LlmProviderConfigurationProperties.ProviderEntry entry) {
            this(
                entry.getId(),
                entry.getDisplayName(),
                entry.getMode(),
                entry.getBaseUrl(),
                entry.getApiKey(),
                entry.getModel(),
                entry.isEnabled()
            );
        }

        public boolean requiresApiKey() {
            return StringUtils.hasText(apiKey);
        }
    }
}
