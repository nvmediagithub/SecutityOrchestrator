package org.example.infrastructure.services;

import org.example.domain.dto.llm.ChatCompletionRequest;
import org.example.domain.dto.llm.ChatCompletionResponse;
import org.example.domain.entities.LLMProvider;
import org.example.domain.entities.ONNXModel;
import org.example.domain.interfaces.ILLMProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * ONNX Provider implementation of ILLMProvider interface
 * Provides ONNX Runtime integration for the LLM orchestration system
 */
@Service
public class ONNXProvider implements ILLMProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(ONNXProvider.class);
    
    private final ONNXModelService modelService;
    private final ONNXInferenceEngine inferenceEngine;
    
    // Provider state
    private volatile boolean available = false;
    private volatile String statusMessage = "Initializing...";
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicLong lastRequestAt = new AtomicLong(0);
    private final ConcurrentHashMap<String, RequestMetrics> requestMetrics = new ConcurrentHashMap<>();
    
    @Autowired
    public ONNXProvider(ONNXModelService modelService, ONNXInferenceEngine inferenceEngine) {
        this.modelService = modelService;
        this.inferenceEngine = inferenceEngine;
        initialize();
    }
    
    private void initialize() {
        try {
            // Check if ONNX models are available
            List<String> availableModels = listModels().get();
            if (!availableModels.isEmpty()) {
                this.available = true;
                this.statusMessage = "ONNX provider is ready with " + availableModels.size() + " models";
                logger.info("ONNX Provider initialized successfully with {} models", availableModels.size());
            } else {
                this.statusMessage = "No ONNX models available";
                logger.warn("ONNX Provider initialized but no models are available");
            }
        } catch (Exception e) {
            this.statusMessage = "ONNX provider initialization failed: " + e.getMessage();
            logger.error("Failed to initialize ONNX Provider", e);
        }
    }
    
    @Override
    public LLMProvider getProviderType() {
        return LLMProvider.ONNX;
    }
    
    @Override
    public String getProviderName() {
        return "ONNX Runtime";
    }
    
    @Override
    public boolean isAvailable() {
        return available;
    }
    
    @Override
    public CompletableFuture<Boolean> testConnection() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                totalRequests.incrementAndGet();
                lastRequestAt.set(System.currentTimeMillis());
                
                // Test by checking if we can list available models
                List<String> models = listModels().get();
                successfulRequests.incrementAndGet();
                
                logger.debug("ONNX provider connection test successful, found {} models", models.size());
                return true;
                
            } catch (Exception e) {
                failedRequests.incrementAndGet();
                logger.error("ONNX provider connection test failed", e);
                return false;
            }
        });
    }
    
    @Override
    public CompletableFuture<List<String>> listModels() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return modelService.getAvailableModels().stream()
                    .map(ONNXModel::getName)
                    .collect(Collectors.toList());
            } catch (Exception e) {
                logger.error("Failed to list ONNX models", e);
                return Arrays.asList();
            }
        });
    }
    
    @Override
    public CompletableFuture<ChatCompletionResponse> executeCompletion(ChatCompletionRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            totalRequests.incrementAndGet();
            lastRequestAt.set(startTime);
            
            try {
                // Get model name, default to first available if not specified
                String originalModelName = request.getModelName();
                if (originalModelName == null || originalModelName.isEmpty()) {
                    List<String> availableModels = listModels().get();
                    if (availableModels.isEmpty()) {
                        throw new RuntimeException("No ONNX models available");
                    }
                    originalModelName = availableModels.get(0);
                }
                
                final String modelName = originalModelName;
                
                // Validate that the model is loaded
                List<ONNXModel> loadedModels = modelService.getLoadedModels();
                ONNXModel targetModel = loadedModels.stream()
                    .filter(model -> model.getName().equals(modelName) ||
                                   model.getModelId().toString().equals(modelName))
                    .findFirst()
                    .orElseGet(() -> {
                        // Try to load the model if not already loaded
                        try {
                            String modelPath = findModelPath(modelName);
                            if (modelPath != null) {
                                return modelService.loadModel(modelPath).get();
                            }
                        } catch (Exception e) {
                            logger.warn("Failed to load model: {}", modelName, e);
                        }
                        return null;
                    });
                
                if (targetModel == null) {
                    throw new RuntimeException("Model not found or cannot be loaded: " + modelName);
                }
                
                // Execute inference
                ChatCompletionResponse response = inferenceEngine
                    .generateCompletion(targetModel.getModelId().toString(), request)
                    .get();
                
                // Update metrics
                long responseTime = System.currentTimeMillis() - startTime;
                totalResponseTime.addAndGet(responseTime);
                successfulRequests.incrementAndGet();
                
                // Track request metrics
                requestMetrics.computeIfAbsent(modelName, k -> new RequestMetrics()).recordSuccess(responseTime);
                
                logger.debug("ONNX completion successful for model: {} ({}ms)", modelName, responseTime);
                return response;
                
            } catch (Exception e) {
                failedRequests.incrementAndGet();
                totalResponseTime.addAndGet(System.currentTimeMillis() - startTime);
                
                // Track failure metrics
                String modelName = request.getModelName() != null ? request.getModelName() : "unknown";
                requestMetrics.computeIfAbsent(modelName, k -> new RequestMetrics()).recordFailure();
                
                logger.error("ONNX completion failed for model: {}", modelName, e);
                throw new RuntimeException("ONNX inference failed: " + e.getMessage(), e);
            }
        });
    }
    
    @Override
    public ILLMProvider.LLMCapabilities getCapabilities() {
        return new ILLMProvider.LLMCapabilities(
            true,   // supportsStreaming - could be implemented with proper streaming
            false,  // supportsFunctionCalling - depends on model capabilities
            false,  // supportsVision - depends on model type
            8192,   // maxContextLength - varies by model, using conservative estimate
            Arrays.asList("onnx") // supportedFormats
        );
    }
    
    @Override
    public ILLMProvider.ProviderStatus getStatus() {
        return new ILLMProvider.ProviderStatus(
            available,
            isHealthy(),
            statusMessage,
            lastRequestAt.get(),
            calculateAverageResponseTime()
        );
    }
    
    @Override
    public void updateConfiguration(ILLMProvider.ProviderConfig config) {
        // ONNX provider doesn't need external configuration like API keys
        // This could be extended to update model paths, memory limits, etc.
        logger.info("ONNX provider configuration update requested (not implemented)");
    }
    
    @Override
    public ILLMProvider.ProviderMetrics getMetrics() {
        return new ILLMProvider.ProviderMetrics(
            totalRequests.get(),
            successfulRequests.get(),
            failedRequests.get(),
            calculateAverageResponseTime(),
            0.0, // ONNX models are free
            lastRequestAt.get()
        );
    }
    
    /**
     * Get provider-specific ONNX metrics
     */
    public ONNXProviderMetrics getONNXMetrics() {
        ONNXModelService.MemoryStats memoryStats = modelService.getMemoryStats();
        
        return new ONNXProviderMetrics(
            getMetrics(),
            memoryStats,
            modelService.getAvailableModels().size(),
            modelService.getLoadedModels().size(),
            new ConcurrentHashMap<>(requestMetrics)
        );
    }
    
    /**
     * Load a specific ONNX model
     */
    public CompletableFuture<String> loadModel(String modelName, String modelPath) {
        return modelService.registerModel(modelName, modelPath)
            .thenCompose(success -> {
                if (success) {
                    return modelService.loadModel(modelPath)
                        .thenApply(model -> "Model " + modelName + " loaded successfully");
                } else {
                    return CompletableFuture.failedFuture(
                        new RuntimeException("Failed to register model: " + modelName));
                }
            });
    }
    
    /**
     * Unload a specific ONNX model
     */
    public CompletableFuture<String> unloadModel(String modelName) {
        return modelService.unloadModel(modelName)
            .thenApply(success -> {
                if (success) {
                    return "Model " + modelName + " unloaded successfully";
                } else {
                    throw new RuntimeException("Failed to unload model: " + modelName);
                }
            });
    }
    
    /**
     * Get model performance metrics
     */
    public CompletableFuture<ONNXInferenceEngine.ModelPerformanceMetrics> getModelMetrics(String modelName) {
        return inferenceEngine.getPerformanceMetrics(modelName);
    }
    
    // Private helper methods
    
    private String findModelPath(String modelName) {
        return modelService.getAvailableModels().stream()
            .filter(model -> model.getName().equals(modelName))
            .map(ONNXModel::getFilePath)
            .findFirst()
            .orElse(null);
    }
    
    private boolean isHealthy() {
        // Consider the provider healthy if:
        // 1. It's available
        // 2. It has at least one loaded model
        // 3. The success rate is above 50%
        
        if (!available) return false;
        
        long total = totalRequests.get();
        if (total == 0) return true; // No requests yet, consider healthy
        
        double successRate = (double) successfulRequests.get() / total;
        boolean hasLoadedModels = !modelService.getLoadedModels().isEmpty();
        
        return hasLoadedModels && successRate > 0.5;
    }
    
    private double calculateAverageResponseTime() {
        long total = successfulRequests.get();
        if (total == 0) return 0.0;
        
        return (double) totalResponseTime.get() / total;
    }
    
    // Inner classes
    
    /**
     * Extended metrics for ONNX provider
     */
    public static class ONNXProviderMetrics {
        private final ILLMProvider.ProviderMetrics baseMetrics;
        private final ONNXModelService.MemoryStats memoryStats;
        private final int totalModels;
        private final int loadedModels;
        private final ConcurrentHashMap<String, RequestMetrics> modelMetrics;
        
        public ONNXProviderMetrics(ILLMProvider.ProviderMetrics baseMetrics,
                                 ONNXModelService.MemoryStats memoryStats,
                                 int totalModels, int loadedModels,
                                 ConcurrentHashMap<String, RequestMetrics> modelMetrics) {
            this.baseMetrics = baseMetrics;
            this.memoryStats = memoryStats;
            this.totalModels = totalModels;
            this.loadedModels = loadedModels;
            this.modelMetrics = modelMetrics;
        }
        
        public ILLMProvider.ProviderMetrics getBaseMetrics() { return baseMetrics; }
        public ONNXModelService.MemoryStats getMemoryStats() { return memoryStats; }
        public int getTotalModels() { return totalModels; }
        public int getLoadedModels() { return loadedModels; }
        public ConcurrentHashMap<String, RequestMetrics> getModelMetrics() { return modelMetrics; }
    }
    
    /**
     * Request metrics per model
     */
    public static class RequestMetrics {
        private final AtomicLong requestCount = new AtomicLong(0);
        private final AtomicLong successCount = new AtomicLong(0);
        private final AtomicLong failureCount = new AtomicLong(0);
        private final AtomicLong totalResponseTime = new AtomicLong(0);
        
        public void recordSuccess(long responseTime) {
            requestCount.incrementAndGet();
            successCount.incrementAndGet();
            totalResponseTime.addAndGet(responseTime);
        }
        
        public void recordFailure() {
            requestCount.incrementAndGet();
            failureCount.incrementAndGet();
        }
        
        public long getRequestCount() { return requestCount.get(); }
        public long getSuccessCount() { return successCount.get(); }
        public long getFailureCount() { return failureCount.get(); }
        public double getSuccessRate() {
            long requests = requestCount.get();
            return requests > 0 ? (double) successCount.get() / requests : 0.0;
        }
        public double getAverageResponseTime() {
            long successes = successCount.get();
            return successes > 0 ? (double) totalResponseTime.get() / successes : 0.0;
        }
    }
}