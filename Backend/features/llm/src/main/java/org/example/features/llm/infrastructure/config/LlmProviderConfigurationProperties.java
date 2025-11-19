package org.example.features.llm.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * POJO representation of the YAML based LLM provider configuration file.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmProviderConfigurationProperties {

    private String activeProvider;
    private List<ProviderEntry> providers = new ArrayList<>();

    public String getActiveProvider() {
        return activeProvider;
    }

    public void setActiveProvider(String activeProvider) {
        this.activeProvider = activeProvider;
    }

    public List<ProviderEntry> getProviders() {
        return providers;
    }

    public void setProviders(List<ProviderEntry> providers) {
        this.providers = providers;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProviderEntry {
        private String id;
        private String displayName;
        private String mode;
        private String baseUrl;
        private String apiKey;
        private String model;
        private boolean enabled = true;
        private Map<String, Object> metadata;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }
    }
}
