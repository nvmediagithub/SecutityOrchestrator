package org.example.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.HashMap;

/**
 * Configuration for LLM services
 */
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLMConfig {
    
    // OpenRouter settings
    private String openRouterApiKey;
    private String openRouterBaseUrl = "https://openrouter.ai/api/v1";
    private String openRouterReferer;
    private String openRouterAppName = "SecurityOrchestrator";
    private int openRouterTimeout = 30;
    
    // Local LLM settings
    private String localModelPath = "./models";
    private String localServerUrl = "http://localhost:11434";
    private int localServerPort = 11434;
    private int localTimeout = 300; // 5 minutes for model loading
    private int maxLocalModels = 3;
    
    // Performance settings
    private int maxRetries = 3;
    private int connectionPoolSize = 10;
    private int maxConcurrentRequests = 5;
    
    // Model configurations by name
    private Map<String, ModelConfig> models = new HashMap<>();
    
    public static class ModelConfig {
        private String provider = "LOCAL";
        private int contextWindow = 4096;
        private int maxTokens = 2048;
        private double temperature = 0.7;
        private double topP = 0.9;
        private double frequencyPenalty = 0.0;
        private double presencePenalty = 0.0;
        private Double sizeGB;
        
        // Getters and setters
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
        public int getContextWindow() { return contextWindow; }
        public void setContextWindow(int contextWindow) { this.contextWindow = contextWindow; }
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public double getTopP() { return topP; }
        public void setTopP(double topP) { this.topP = topP; }
        public double getFrequencyPenalty() { return frequencyPenalty; }
        public void setFrequencyPenalty(double frequencyPenalty) { this.frequencyPenalty = frequencyPenalty; }
        public double getPresencePenalty() { return presencePenalty; }
        public void setPresencePenalty(double presencePenalty) { this.presencePenalty = presencePenalty; }
        public Double getSizeGB() { return sizeGB; }
        public void setSizeGB(Double sizeGB) { this.sizeGB = sizeGB; }
    }
    
    // Getters and setters
    public String getOpenRouterApiKey() { return openRouterApiKey; }
    public void setOpenRouterApiKey(String openRouterApiKey) { this.openRouterApiKey = openRouterApiKey; }
    
    public String getOpenRouterBaseUrl() { return openRouterBaseUrl; }
    public void setOpenRouterBaseUrl(String openRouterBaseUrl) { this.openRouterBaseUrl = openRouterBaseUrl; }
    
    public String getOpenRouterReferer() { return openRouterReferer; }
    public void setOpenRouterReferer(String openRouterReferer) { this.openRouterReferer = openRouterReferer; }
    
    public String getOpenRouterAppName() { return openRouterAppName; }
    public void setOpenRouterAppName(String openRouterAppName) { this.openRouterAppName = openRouterAppName; }
    
    public int getOpenRouterTimeout() { return openRouterTimeout; }
    public void setOpenRouterTimeout(int openRouterTimeout) { this.openRouterTimeout = openRouterTimeout; }
    
    public String getLocalModelPath() { return localModelPath; }
    public void setLocalModelPath(String localModelPath) { this.localModelPath = localModelPath; }
    
    public String getLocalServerUrl() { return localServerUrl; }
    public void setLocalServerUrl(String localServerUrl) { this.localServerUrl = localServerUrl; }
    
    public int getLocalServerPort() { return localServerPort; }
    public void setLocalServerPort(int localServerPort) { this.localServerPort = localServerPort; }
    
    public int getLocalTimeout() { return localTimeout; }
    public void setLocalTimeout(int localTimeout) { this.localTimeout = localTimeout; }
    
    public int getMaxLocalModels() { return maxLocalModels; }
    public void setMaxLocalModels(int maxLocalModels) { this.maxLocalModels = maxLocalModels; }
    
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    
    public int getConnectionPoolSize() { return connectionPoolSize; }
    public void setConnectionPoolSize(int connectionPoolSize) { this.connectionPoolSize = connectionPoolSize; }
    
    public int getMaxConcurrentRequests() { return maxConcurrentRequests; }
    public void setMaxConcurrentRequests(int maxConcurrentRequests) { this.maxConcurrentRequests = maxConcurrentRequests; }
    
    public Map<String, ModelConfig> getModels() { return models; }
    public void setModels(Map<String, ModelConfig> models) { this.models = models; }
    
    // Helper methods
    public boolean hasOpenRouterKey() {
        return openRouterApiKey != null && !openRouterApiKey.trim().isEmpty();
    }
}