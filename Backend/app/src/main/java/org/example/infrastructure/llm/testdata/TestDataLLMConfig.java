package org.example.infrastructure.llm.testdata;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Test Data LLM services
 */
@Component
@ConfigurationProperties(prefix = "app.test-data.llm")
public class TestDataLLMConfig {
    
    // Provider settings
    private String defaultProvider = "LOCAL";
    private boolean enableOpenRouter = true;
    private boolean enableLocal = true;
    
    // OpenRouter settings
    private String openRouterModel = "anthropic/claude-3-sonnet";
    private int openRouterMaxTokens = 2000;
    private double openRouterTemperature = 0.7;
    private int openRouterTimeout = 30;
    
    // Local LLM settings
    private String localModel = "llama3.1:8b";
    private int localMaxTokens = 1500;
    private double localTemperature = 0.8;
    private int localTimeout = 60;
    
    // Generation settings
    private int defaultMaxRetries = 3;
    private int defaultBatchSize = 100;
    private long cacheTimeoutMinutes = 60;
    private int maxConcurrentGenerations = 3;
    
    // Quality settings
    private boolean enableQualityValidation = true;
    private boolean enableContextPreservation = true;
    private boolean enableDependencyAnalysis = true;
    private int minDataQualityScore = 70;
    
    // Caching settings
    private boolean enableCaching = true;
    private int cacheSize = 1000;
    private long cacheExpirationHours = 24;
    
    // Prompt settings
    private int maxPromptLength = 4000;
    private boolean includeExamples = true;
    private boolean includeConstraints = true;
    
    // Fallback settings
    private boolean enableFallback = true;
    private String fallbackProvider = "LOCAL";
    private int fallbackTimeout = 120;
    
    // Getters and Setters
    public String getDefaultProvider() { return defaultProvider; }
    public void setDefaultProvider(String defaultProvider) { this.defaultProvider = defaultProvider; }
    
    public boolean isEnableOpenRouter() { return enableOpenRouter; }
    public void setEnableOpenRouter(boolean enableOpenRouter) { this.enableOpenRouter = enableOpenRouter; }
    
    public boolean isEnableLocal() { return enableLocal; }
    public void setEnableLocal(boolean enableLocal) { this.enableLocal = enableLocal; }
    
    public String getOpenRouterModel() { return openRouterModel; }
    public void setOpenRouterModel(String openRouterModel) { this.openRouterModel = openRouterModel; }
    
    public int getOpenRouterMaxTokens() { return openRouterMaxTokens; }
    public void setOpenRouterMaxTokens(int openRouterMaxTokens) { this.openRouterMaxTokens = openRouterMaxTokens; }
    
    public double getOpenRouterTemperature() { return openRouterTemperature; }
    public void setOpenRouterTemperature(double openRouterTemperature) { this.openRouterTemperature = openRouterTemperature; }
    
    public int getOpenRouterTimeout() { return openRouterTimeout; }
    public void setOpenRouterTimeout(int openRouterTimeout) { this.openRouterTimeout = openRouterTimeout; }
    
    public String getLocalModel() { return localModel; }
    public void setLocalModel(String localModel) { this.localModel = localModel; }
    
    public int getLocalMaxTokens() { return localMaxTokens; }
    public void setLocalMaxTokens(int localMaxTokens) { this.localMaxTokens = localMaxTokens; }
    
    public double getLocalTemperature() { return localTemperature; }
    public void setLocalTemperature(double localTemperature) { this.localTemperature = localTemperature; }
    
    public int getLocalTimeout() { return localTimeout; }
    public void setLocalTimeout(int localTimeout) { this.localTimeout = localTimeout; }
    
    public int getDefaultMaxRetries() { return defaultMaxRetries; }
    public void setDefaultMaxRetries(int defaultMaxRetries) { this.defaultMaxRetries = defaultMaxRetries; }
    
    public int getDefaultBatchSize() { return defaultBatchSize; }
    public void setDefaultBatchSize(int defaultBatchSize) { this.defaultBatchSize = defaultBatchSize; }
    
    public long getCacheTimeoutMinutes() { return cacheTimeoutMinutes; }
    public void setCacheTimeoutMinutes(long cacheTimeoutMinutes) { this.cacheTimeoutMinutes = cacheTimeoutMinutes; }
    
    public int getMaxConcurrentGenerations() { return maxConcurrentGenerations; }
    public void setMaxConcurrentGenerations(int maxConcurrentGenerations) { this.maxConcurrentGenerations = maxConcurrentGenerations; }
    
    public boolean isEnableQualityValidation() { return enableQualityValidation; }
    public void setEnableQualityValidation(boolean enableQualityValidation) { this.enableQualityValidation = enableQualityValidation; }
    
    public boolean isEnableContextPreservation() { return enableContextPreservation; }
    public void setEnableContextPreservation(boolean enableContextPreservation) { this.enableContextPreservation = enableContextPreservation; }
    
    public boolean isEnableDependencyAnalysis() { return enableDependencyAnalysis; }
    public void setEnableDependencyAnalysis(boolean enableDependencyAnalysis) { this.enableDependencyAnalysis = enableDependencyAnalysis; }
    
    public int getMinDataQualityScore() { return minDataQualityScore; }
    public void setMinDataQualityScore(int minDataQualityScore) { this.minDataQualityScore = minDataQualityScore; }
    
    public boolean isEnableCaching() { return enableCaching; }
    public void setEnableCaching(boolean enableCaching) { this.enableCaching = enableCaching; }
    
    public int getCacheSize() { return cacheSize; }
    public void setCacheSize(int cacheSize) { this.cacheSize = cacheSize; }
    
    public long getCacheExpirationHours() { return cacheExpirationHours; }
    public void setCacheExpirationHours(long cacheExpirationHours) { this.cacheExpirationHours = cacheExpirationHours; }
    
    public int getMaxPromptLength() { return maxPromptLength; }
    public void setMaxPromptLength(int maxPromptLength) { this.maxPromptLength = maxPromptLength; }
    
    public boolean isIncludeExamples() { return includeExamples; }
    public void setIncludeExamples(boolean includeExamples) { this.includeExamples = includeExamples; }
    
    public boolean isIncludeConstraints() { return includeConstraints; }
    public void setIncludeConstraints(boolean includeConstraints) { this.includeConstraints = includeConstraints; }
    
    public boolean isEnableFallback() { return enableFallback; }
    public void setEnableFallback(boolean enableFallback) { this.enableFallback = enableFallback; }
    
    public String getFallbackProvider() { return fallbackProvider; }
    public void setFallbackProvider(String fallbackProvider) { this.fallbackProvider = fallbackProvider; }
    
    public int getFallbackTimeout() { return fallbackTimeout; }
    public void setFallbackTimeout(int fallbackTimeout) { this.fallbackTimeout = fallbackTimeout; }
}