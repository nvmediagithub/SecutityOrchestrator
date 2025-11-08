package org.example.web.controllers;

import org.example.domain.entities.LLMProvider;
import org.example.domain.entities.LLMModel;
import org.example.domain.entities.PerformanceMetrics;
import org.example.domain.dto.llm.*;
import org.example.infrastructure.config.LLMConfig;
import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.example.domain.dto.ApiResponse;
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
            return ResponseEntity.ok(ApiResponse.success(providers, "providers-retrieved"));
        } catch (Exception e) {
            logger.error("Failed to get providers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("PROVIDERS_ERROR", "Failed to get providers: " + e.getMessage()), "error"));
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
                        .body(ApiResponse.error(new ApiResponse.ApiError("UNKNOWN_PROVIDER", "Unknown provider: " + providerName), "error"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("Provider updated successfully", "provider-updated"));
        } catch (Exception e) {
            logger.error("Failed to update provider", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("PROVIDER_UPDATE_ERROR", "Failed to update provider: " + e.getMessage()), "error"));
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
            
            return ResponseEntity.ok(ApiResponse.success(models, "models-retrieved"));
        } catch (Exception e) {
            logger.error("Failed to get models", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("MODELS_ERROR", "Failed to get models: " + e.getMessage()), "error"));
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
            return ResponseEntity.ok(ApiResponse.success("Model added successfully", "model-added"));
        } catch (Exception e) {
            logger.error("Failed to add model", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("MODEL_ADD_ERROR", "Failed to add model: " + e.getMessage()), "error"));
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
            return ResponseEntity.ok(ApiResponse.success("Model updated successfully", "model-updated"));
        } catch (Exception e) {
            logger.error("Failed to update model", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("MODEL_UPDATE_ERROR", "Failed to update model: " + e.getMessage()), "error"));
        }
    }
    
    /**
     * Delete model configuration
     */
    @DeleteMapping("/models/{modelName}")
    public ResponseEntity<ApiResponse<String>> deleteModel(@PathVariable String modelName) {
        try {
            logger.info("Model deleted: {}", modelName);
            return ResponseEntity.ok(ApiResponse.success("Model deleted successfully", "model-deleted"));
        } catch (Exception e) {
            logger.error("Failed to delete model", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("MODEL_DELETE_ERROR", "Failed to delete model: " + e.getMessage()), "error"));
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
            
            return ResponseEntity.ok(ApiResponse.success(response, "config-retrieved"));
        } catch (Exception e) {
            logger.error("Failed to get configuration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("CONFIG_ERROR", "Failed to get configuration: " + e.getMessage()), "error"));
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
            return ResponseEntity.ok(ApiResponse.success("Configuration updated successfully", "config-updated"));
        } catch (Exception e) {
            logger.error("Failed to update configuration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("CONFIG_UPDATE_ERROR", "Failed to update configuration: " + e.getMessage()), "error"));
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
            return ResponseEntity.ok(ApiResponse.success(statuses, "status-retrieved"));
        } catch (Exception e) {
            logger.error("Failed to get status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("STATUS_ERROR", "Failed to get status: " + e.getMessage()), "error"));
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
                    .body(ApiResponse.error(new ApiResponse.ApiError("INVALID_REQUEST", "modelName is required"), "error"));
            }
            
            String provider = request.getModelName().contains(":") ?
                request.getModelName().split(":")[0] : "openrouter";
            
            LLMTestResponse response;
            
            if ("local".equalsIgnoreCase(provider)) {
                response = testLocalModel(request);
            } else {
                response = testOpenRouterModel(request);
            }
            
            return ResponseEntity.ok(ApiResponse.success(response, "test-completed"));
        } catch (Exception e) {
            logger.error("LLM test failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("TEST_ERROR", "LLM test failed: " + e.getMessage()), "error"));
        }
    }
    
    // ==================== OPENROUTER MODELS ====================
    
    /**
     * List OpenRouter models
     */
    @GetMapping("/openrouter/models")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listOpenRouterModels() {
        try {
            logger.info("Getting OpenRouter models list");
            Map<String, Object> response = new HashMap<>();
            
            // Mock OpenRouter models for demo
            List<Map<String, Object>> models = Arrays.asList(
                createModelInfo("gpt-4", "GPT-4", "OpenAI", 8192, true),
                createModelInfo("gpt-3.5-turbo", "GPT-3.5 Turbo", "OpenAI", 4096, true),
                createModelInfo("claude-3-opus", "Claude 3 Opus", "Anthropic", 200000, true),
                createModelInfo("claude-3-sonnet", "Claude 3 Sonnet", "Anthropic", 200000, true),
                createModelInfo("llama-3.1-70b", "Llama 3.1 70B", "Meta", 131072, true)
            );
            
            response.put("models", models);
            response.put("total", models.size());
            response.put("provider", "openrouter");
            
            return ResponseEntity.ok(ApiResponse.success(response, "openrouter-models-retrieved"));
        } catch (Exception e) {
            logger.error("Failed to list OpenRouter models", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("OPENROUTER_MODELS_ERROR", "Failed to list OpenRouter models: " + e.getMessage()), "error"));
        }
    }
    
    // ==================== LOCAL MODELS ====================
    
    /**
     * List local models
     */
    @GetMapping("/local/models")
    public ResponseEntity<ApiResponse<List<LocalModelInfo>>> listLocalModels() {
        try {
            CompletableFuture<List<LocalModelInfo>> future = localLLMService.listLocalModels();
            List<LocalModelInfo> models = future.get();
            return ResponseEntity.ok(ApiResponse.success(models, "local-models-retrieved"));
        } catch (Exception e) {
            logger.error("Failed to list local models", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("LOCAL_MODELS_ERROR", "Failed to list local models: " + e.getMessage()), "error"));
        }
    }
    
    /**
     * Load a local model
     */
    @PostMapping("/local/models/load")
    public ResponseEntity<ApiResponse<String>> loadLocalModel(@RequestBody Map<String, String> request) {
        try {
            String modelName = request.get("modelName");
            if (modelName == null || modelName.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(new ApiResponse.ApiError("INVALID_REQUEST", "modelName is required"), "error"));
            }
            
            CompletableFuture<Boolean> future = localLLMService.loadModel(modelName);
            future.get();
            
            return ResponseEntity.ok(ApiResponse.success("Model loaded successfully", "model-loaded"));
        } catch (Exception e) {
            logger.error("Failed to load local model", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("MODEL_LOAD_ERROR", "Failed to load local model: " + e.getMessage()), "error"));
        }
    }
    
    /**
     * Unload a local model
     */
    @PostMapping("/local/models/unload")
    public ResponseEntity<ApiResponse<String>> unloadLocalModel(@RequestBody Map<String, String> request) {
        try {
            String modelName = request.get("modelName");
            if (modelName == null || modelName.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(new ApiResponse.ApiError("INVALID_REQUEST", "modelName is required"), "error"));
            }
            
            CompletableFuture<Boolean> future = localLLMService.unloadModel(modelName);
            future.get();
            
            return ResponseEntity.ok(ApiResponse.success("Model unloaded successfully", "model-unloaded"));
        } catch (Exception e) {
            logger.error("Failed to unload local model", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("MODEL_UNLOAD_ERROR", "Failed to unload local model: " + e.getMessage()), "error"));
        }
    }
    
    // ==================== CHAT COMPLETION ====================
    
    /**
     * Chat completion endpoint
     */
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<OpenRouterClient.ChatCompletionResponse>> chatCompletion(
            @RequestBody Map<String, Object> request) {
        try {
            String model = (String) request.get("model");
            String prompt = (String) request.get("prompt");
            int maxTokens = request.get("maxTokens") != null ? (Integer) request.get("maxTokens") : 2048;
            double temperature = request.get("temperature") != null ? (Double) request.get("temperature") : 0.7;
            
            if (model == null || prompt == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(new ApiResponse.ApiError("INVALID_REQUEST", "model and prompt are required"), "error"));
            }
            
            CompletableFuture<OpenRouterClient.ChatCompletionResponse> future = 
                openRouterClient.chatCompletion(model, prompt, maxTokens, temperature);
            
            OpenRouterClient.ChatCompletionResponse response = future.get();
            return ResponseEntity.ok(ApiResponse.success(response, "chat-completed"));
        } catch (Exception e) {
            logger.error("Chat completion failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("CHAT_ERROR", "Chat completion failed: " + e.getMessage()), "error"));
        }
    }
    
    /**
     * Local chat completion endpoint
     */
    @PostMapping("/local/chat")
    public ResponseEntity<ApiResponse<LocalLLMService.ChatCompletionResponse>> localChatCompletion(
            @RequestBody Map<String, Object> request) {
        try {
            String model = (String) request.get("model");
            String prompt = (String) request.get("prompt");
            int maxTokens = request.get("maxTokens") != null ? (Integer) request.get("maxTokens") : 2048;
            double temperature = request.get("temperature") != null ? (Double) request.get("temperature") : 0.7;
            
            if (model == null || prompt == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(new ApiResponse.ApiError("INVALID_REQUEST", "model and prompt are required"), "error"));
            }
            
            CompletableFuture<LocalLLMService.ChatCompletionResponse> future = 
                localLLMService.localChatCompletion(model, prompt, maxTokens, temperature);
            
            LocalLLMService.ChatCompletionResponse response = future.get();
            return ResponseEntity.ok(ApiResponse.success(response, "local-chat-completed"));
        } catch (Exception e) {
            logger.error("Local chat completion failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("LOCAL_CHAT_ERROR", "Local chat completion failed: " + e.getMessage()), "error"));
        }
    }
    
    // ==================== METRICS ====================
    
    /**
     * Get performance metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<Map<String, PerformanceMetrics>>> getMetrics() {
        try {
            Map<String, PerformanceMetrics> metrics = new HashMap<>();
            
            // Mock metrics for demonstration
            PerformanceMetrics openRouterMetrics = new PerformanceMetrics(
                LLMProvider.OPENROUTER,
                150,
                145,
                5,
                1250.0,
                15000,
                3.33, // error rate
                96.67 // uptime
            );
            
            PerformanceMetrics localMetrics = new PerformanceMetrics(
                LLMProvider.LOCAL,
                75,
                74,
                1,
                850.0,
                7500,
                1.33, // error rate
                98.67 // uptime
            );
            
            metrics.put("openrouter", openRouterMetrics);
            metrics.put("local", localMetrics);
            
            return ResponseEntity.ok(ApiResponse.success(metrics, "metrics-retrieved"));
        } catch (Exception e) {
            logger.error("Failed to get metrics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(new ApiResponse.ApiError("METRICS_ERROR", "Failed to get metrics: " + e.getMessage()), "error"));
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
        return ResponseEntity.ok(ApiResponse.success("LLM Controller is healthy", "health-check"));
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
        return ResponseEntity.ok(ApiResponse.success(response, "test-echo"));
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
    
    /**
     * Helper method to create model information map
     */
    private Map<String, Object> createModelInfo(String id, String name, String provider, int contextWindow, boolean available) {
        Map<String, Object> model = new HashMap<>();
        model.put("id", id);
        model.put("name", name);
        model.put("provider", provider);
        model.put("contextWindow", contextWindow);
        model.put("available", available);
        return model;
    }
}