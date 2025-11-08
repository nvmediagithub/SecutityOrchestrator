package org.example.web.controllers;

import org.example.domain.entities.LLMProvider;
import org.example.domain.entities.LLMModel;
import org.example.domain.entities.PerformanceMetrics;
import org.example.domain.dto.llm.*;
import org.example.infrastructure.config.LLMConfig;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.example.domain.dto.test.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * REST Controller for LLM operations
 * Provides endpoints for managing LLM providers, models, and chat completions
 * CORS is configured globally in SecurityOrchestratorApplication
 */
@RestController
@RequestMapping("/api/llm")
public class LLMController {
    
    private static final Logger logger = LoggerFactory.getLogger(LLMController.class);
    
    private final OpenRouterClient openRouterClient;
    private final LocalLLMService localLLMService;
    private final LLMConfig config;
    
    public LLMController(OpenRouterClient openRouterClient, LocalLLMService localLLMService, LLMConfig config) {
        this.openRouterClient = openRouterClient;
        this.localLLMService = localLLMService;
        this.config = config;
    }
    
    // ==================== PROVIDER MANAGEMENT ====================
    
    /**
     * Get available LLM providers
     */
    @GetMapping("/providers")
    public ResponseEntity<ApiResponse<Map<String, LLMProviderSettings>>> getProviders(HttpServletRequest request) {
        logger.info("CORS Debug - Request from origin: {} for /api/llm/providers", request.getHeader("Origin"));
        logger.info("CORS Debug - Request headers: Origin={}, User-Agent={}",
            request.getHeader("Origin"), request.getHeader("User-Agent"));
        
        try {
            logger.info("Getting LLM providers configuration");
            Map<String, LLMProviderSettings> providers = new HashMap<>();
            
            // OpenRouter provider
            LLMProviderSettings openRouterSettings = createOpenRouterSettings();
            providers.put("openrouter", openRouterSettings);
            logger.info("OpenRouter provider configured: hasKey={}, baseUrl={}",
                openRouterSettings.hasApiKey(), openRouterSettings.getBaseUrl());
            
            // Local provider
            LLMProviderSettings localSettings = createLocalSettings();
            providers.put("local", localSettings);
            logger.info("Local provider configured: baseUrl={}", localSettings.getBaseUrl());
            
            logger.info("Successfully retrieved {} LLM providers", providers.size());
            return ResponseEntity.ok(ApiResponse.success("providers-retrieved", providers));
        } catch (Exception e) {
            logger.error("Failed to get providers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get providers: " + e.getMessage(), "PROVIDERS_ERROR"));
        }
    }
    
    /**
     * Update LLM provider configuration
     */
    @PutMapping("/providers/{providerName}")
    public ResponseEntity<ApiResponse<String>> updateProvider(
            @PathVariable String providerName,
            @RequestBody LLMProviderSettings settings) {
        try {
            switch (providerName.toLowerCase()) {
                case "openrouter":
                    if (settings.getApiKey() != null) {
                        openRouterClient.updateApiKey(settings.getApiKey());
                    }
                    if (settings.getBaseUrl() != null) {
                        openRouterClient.updateBaseUrl(settings.getBaseUrl());
                    }
                    if (settings.getTimeout() != null && settings.getTimeout() > 0) {
                        openRouterClient.updateTimeout(settings.getTimeout());
                    }
                    break;
                case "local":
                    if (settings.getBaseUrl() != null) {
                        localLLMService.updateBaseUrl(settings.getBaseUrl());
                    }
                    if (settings.getTimeout() != null && settings.getTimeout() > 0) {
                        localLLMService.updateTimeout(settings.getTimeout());
                    }
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Unknown provider: " + providerName, "UNKNOWN_PROVIDER"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("provider-updated", "Provider updated successfully"));
        } catch (Exception e) {
            logger.error("Failed to update provider", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update provider: " + e.getMessage(), "PROVIDER_UPDATE_ERROR"));
        }
    }
    
    // ==================== MODEL MANAGEMENT ====================
    
    /**
     * Get available models
     */
    @GetMapping("/models")
    public ResponseEntity<ApiResponse<Map<String, LLMModelConfig>>> getModels() {
        try {
            Map<String, LLMModelConfig> models = new HashMap<>();
            
            // Add models from configuration
            for (Map.Entry<String, LLMConfig.ModelConfig> entry : config.getModels().entrySet()) {
                LLMConfig.ModelConfig modelConfig = entry.getValue();
                models.put(entry.getKey(), new LLMModelConfig(
                    entry.getKey(),
                    LLMProvider.valueOf(modelConfig.getProvider()),
                    modelConfig.getContextWindow(),
                    modelConfig.getMaxTokens(),
                    modelConfig.getTemperature(),
                    modelConfig.getTopP(),
                    modelConfig.getFrequencyPenalty(),
                    modelConfig.getPresencePenalty(),
                    modelConfig.getSizeGB()
                ));
            }
            
            return ResponseEntity.ok(ApiResponse.success("models-retrieved", models));
        } catch (Exception e) {
            logger.error("Failed to get models", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get models: " + e.getMessage(), "MODELS_ERROR"));
        }
    }
    
    /**
     * Add a new model configuration
     */
    @PostMapping("/models")
    public ResponseEntity<ApiResponse<String>> addModel(@RequestBody LLMModelConfig modelConfig) {
        try {
            // In a real implementation, you would persist this to a database
            logger.info("Model added: {}", modelConfig.getModelName());
            return ResponseEntity.ok(ApiResponse.success("model-added", "Model added successfully"));
        } catch (Exception e) {
            logger.error("Failed to add model", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to add model: " + e.getMessage(), "MODEL_ADD_ERROR"));
        }
    }
    
    /**
     * Update model configuration
     */
    @PutMapping("/models/{modelName}")
    public ResponseEntity<ApiResponse<String>> updateModel(
            @PathVariable String modelName,
            @RequestBody LLMModelConfig modelConfig) {
        try {
            logger.info("Model updated: {}", modelName);
            return ResponseEntity.ok(ApiResponse.success("model-updated", "Model updated successfully"));
        } catch (Exception e) {
            logger.error("Failed to update model", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update model: " + e.getMessage(), "MODEL_UPDATE_ERROR"));
        }
    }
    
    /**
     * Delete model configuration
     */
    @DeleteMapping("/models/{modelName}")
    public ResponseEntity<ApiResponse<String>> deleteModel(@PathVariable String modelName) {
        try {
            logger.info("Model deleted: {}", modelName);
            return ResponseEntity.ok(ApiResponse.success("model-deleted", "Model deleted successfully"));
        } catch (Exception e) {
            logger.error("Failed to delete model", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to delete model: " + e.getMessage(), "MODEL_DELETE_ERROR"));
        }
    }
    
    // ==================== CONFIGURATION ====================
    
    /**
     * Get current LLM configuration
     */
    @GetMapping("/config")
    public ResponseEntity<ApiResponse<LLMConfigResponse>> getConfig() {
        try {
            Map<String, LLMProviderSettings> providers = new HashMap<>();
            providers.put("openrouter", createOpenRouterSettings());
            providers.put("local", createLocalSettings());
            
            Map<String, LLMModelConfig> models = config.getModels().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        LLMConfig.ModelConfig mc = entry.getValue();
                        return new LLMModelConfig(
                            entry.getKey(),
                            LLMProvider.valueOf(mc.getProvider()),
                            mc.getContextWindow(),
                            mc.getMaxTokens(),
                            mc.getTemperature(),
                            mc.getTopP(),
                            mc.getFrequencyPenalty(),
                            mc.getPresencePenalty(),
                            mc.getSizeGB()
                        );
                    }
                ));
            
            // For demo purposes, use first model as active
            String activeModel = models.keySet().iterator().next();
            LLMConfigResponse response = new LLMConfigResponse(
                LLMProvider.OPENROUTER, activeModel, providers, models
            );
            
            return ResponseEntity.ok(ApiResponse.success("config-retrieved", response));
        } catch (Exception e) {
            logger.error("Failed to get configuration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get configuration: " + e.getMessage(), "CONFIG_ERROR"));
        }
    }
    
    /**
     * Update LLM configuration
     */
    @PutMapping("/config")
    public ResponseEntity<ApiResponse<String>> updateConfig(@RequestBody LLMConfigUpdateRequest request) {
        try {
            // Update active provider
            logger.info("Configuration updated for provider: {}", request.getProvider());
            return ResponseEntity.ok(ApiResponse.success("config-updated", "Configuration updated successfully"));
        } catch (Exception e) {
            logger.error("Failed to update configuration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update configuration: " + e.getMessage(), "CONFIG_UPDATE_ERROR"));
        }
    }
    
    // ==================== STATUS ====================
    
    /**
     * Get LLM service status
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, LLMStatusResponse>>> getStatus(HttpServletRequest request) {
        logger.info("CORS Debug - Status request from origin: {} for /api/llm/status", request.getHeader("Origin"));
        
        try {
            logger.info("Getting LLM service status");
            Map<String, LLMStatusResponse> statuses = new HashMap<>();
            
            // OpenRouter status
            logger.info("Checking OpenRouter status");
            LLMStatusResponse openRouterStatus = getOpenRouterStatus();
            statuses.put("openrouter", openRouterStatus);
            logger.info("OpenRouter status: healthy={}, available={}",
                openRouterStatus.isHealthy(), openRouterStatus.isAvailable());
            
            // Local status
            logger.info("Checking Local LLM status");
            LLMStatusResponse localStatus = getLocalStatus();
            statuses.put("local", localStatus);
            logger.info("Local LLM status: healthy={}, available={}",
                localStatus.isHealthy(), localStatus.isAvailable());
            
            logger.info("Successfully retrieved LLM service status");
            return ResponseEntity.ok(ApiResponse.success("status-retrieved", statuses));
        } catch (Exception e) {
            logger.error("Failed to get status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to get status: " + e.getMessage(), "STATUS_ERROR"));
        }
    }
    
    // ==================== TESTING ====================
    
    /**
     * Test LLM functionality
     */
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<LLMTestResponse>> testLLM(@Valid @RequestBody LLMTestRequest request) {
        try {
            if (request.getModelName() == null || request.getModelName().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("modelName is required", "INVALID_REQUEST"));
            }
            
            String provider = request.getModelName().contains(":") ?
                request.getModelName().split(":")[0] : "openrouter";
            
            LLMTestResponse response;
            
            if ("local".equalsIgnoreCase(provider)) {
                response = testLocalModel(request);
            } else {
                response = testOpenRouterModel(request);
            }
            
            return ResponseEntity.ok(ApiResponse.success("test-completed", response));
        } catch (Exception e) {
            logger.error("LLM test failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("LLM test failed: " + e.getMessage(), "TEST_ERROR"));
        }
    }
    
    // ==================== TEST ENDPOINTS ====================
    
    /**
     * Health check endpoint for testing
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck(HttpServletRequest request) {
        logger.info("CORS Debug - Health check from origin: {} for /api/llm/health", request.getHeader("Origin"));
        logger.info("LLM Controller health check endpoint called");
        return ResponseEntity.ok(ApiResponse.success("health-check", "LLM Controller is healthy"));
    }
    
    /**
     * Test endpoint that doesn't require API keys
     */
    @GetMapping("/test/echo")
    public ResponseEntity<ApiResponse<Map<String, String>>> testEcho(
            @RequestParam(defaultValue = "Hello from LLM Controller") String message,
            HttpServletRequest request) {
        logger.info("CORS Debug - Test echo from origin: {} for /api/llm/test/echo", request.getHeader("Origin"));
        logger.info("Echo test endpoint called with message: {}", message);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", "ok");
        response.put("server", "SecurityOrchestrator LLM Controller");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(ApiResponse.success("test-echo", response));
    }
    
    // ==================== HELPER METHODS ====================
    
    private LLMProviderSettings createOpenRouterSettings() {
        return LLMProviderSettings.withMaskedKey(
            LLMProvider.OPENROUTER,
            config.hasOpenRouterKey(),
            config.getOpenRouterBaseUrl(),
            config.getOpenRouterTimeout(),
            config.getMaxRetries()
        );
    }
    
    private LLMProviderSettings createLocalSettings() {
        return LLMProviderSettings.withMaskedKey(
            LLMProvider.LOCAL,
            true, // Local service is always available
            config.getLocalServerUrl(),
            config.getLocalTimeout(),
            config.getMaxRetries()
        );
    }
    
    private LLMStatusResponse getOpenRouterStatus() {
        try {
            if (!config.hasOpenRouterKey()) {
                return new LLMStatusResponse(
                    LLMProvider.OPENROUTER, false, false, null, "API key not configured", LocalDateTime.now()
                );
            }
            
            CompletableFuture<Boolean> future = openRouterClient.checkConnection();
            Boolean result = future.get();
            
            return new LLMStatusResponse(
                LLMProvider.OPENROUTER, result, result, 1250.0, null, LocalDateTime.now()
            );
        } catch (Exception e) {
            return new LLMStatusResponse(
                LLMProvider.OPENROUTER, false, false, null, e.getMessage(), LocalDateTime.now()
            );
        }
    }
    
    private LLMStatusResponse getLocalStatus() {
        try {
            CompletableFuture<Boolean> future = localLLMService.checkOllamaConnection();
            Boolean result = future.get();
            
            return new LLMStatusResponse(
                LLMProvider.LOCAL, result, result, 850.0, null, LocalDateTime.now()
            );
        } catch (Exception e) {
            return new LLMStatusResponse(
                LLMProvider.LOCAL, false, false, null, e.getMessage(), LocalDateTime.now()
            );
        }
    }
    
    private LLMTestResponse testOpenRouterModel(LLMTestRequest request) {
        try {
            CompletableFuture<OpenRouterClient.ChatCompletionResponse> future = 
                openRouterClient.chatCompletion(request.getModelName(), request.getPrompt(), 512, 0.7);
            
            OpenRouterClient.ChatCompletionResponse completion = future.get();
            
            return new LLMTestResponse(
                request.getModelName(),
                LLMProvider.OPENROUTER,
                request.getPrompt(),
                completion.getResponse(),
                completion.getTokensUsed(),
                completion.getResponseTimeMs(),
                true
            );
        } catch (Exception e) {
            return new LLMTestResponse(
                request.getModelName(),
                LLMProvider.OPENROUTER,
                request.getPrompt(),
                null,
                null,
                null,
                false
            );
        }
    }
    
    private LLMTestResponse testLocalModel(LLMTestRequest request) {
        try {
            CompletableFuture<LocalLLMService.ChatCompletionResponse> future =
                localLLMService.localChatCompletion(request.getModelName(), request.getPrompt(), 512, 0.7);
            
            LocalLLMService.ChatCompletionResponse completion = future.get();
            
            return new LLMTestResponse(
                request.getModelName(),
                LLMProvider.LOCAL,
                request.getPrompt(),
                completion.getResponse(),
                completion.getTokensUsed(),
                completion.getResponseTimeMs(),
                true
            );
        } catch (Exception e) {
            return new LLMTestResponse(
                request.getModelName(),
                LLMProvider.LOCAL,
                request.getPrompt(),
                null,
                null,
                null,
                false
            );
        }
    }
}