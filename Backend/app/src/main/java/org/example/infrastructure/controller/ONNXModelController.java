package org.example.infrastructure.controller;

import org.example.domain.dto.common.BaseResponse;
import org.example.domain.entities.ONNXModel;
import org.example.domain.valueobjects.ModelStatus;
import org.example.infrastructure.services.ONNXInferenceEngine;
import org.example.infrastructure.services.ONNXModelService;
import org.example.infrastructure.services.ONNXProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST API controller for ONNX model management
 * Provides endpoints for loading, unloading, and managing ONNX models
 */
@RestController
@RequestMapping("/api/llm/onnx")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ONNXModelController {
    
    private static final Logger logger = LoggerFactory.getLogger(ONNXModelController.class);
    
    @Autowired
    private ONNXModelService modelService;
    
    @Autowired
    private ONNXInferenceEngine inferenceEngine;
    
    @Autowired
    private ONNXProvider onnxProvider;
    
    /**
     * Generic response with data
     */
    private static class DataResponse<T> extends BaseResponse {
        private T data;
        
        public DataResponse(BaseResponse.ResponseStatus status, String message) {
            super(status, message);
        }
        
        public DataResponse(BaseResponse.ResponseStatus status, String message, T data) {
            super(status, message);
            this.data = data;
        }
        
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
    
    /**
     * Get list of all available ONNX models
     */
    @GetMapping("/models")
    public CompletableFuture<ResponseEntity<DataResponse<List<ONNXModel>>>> getAvailableModels() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ONNXModel> models = modelService.getAvailableModels();
                
                DataResponse<List<ONNXModel>> response = new DataResponse<>(
                    BaseResponse.ResponseStatus.SUCCESS,
                    "Retrieved " + models.size() + " available ONNX models",
                    models
                );
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Failed to get available ONNX models", e);
                return ResponseEntity.internalServerError().body(
                    new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                     "Failed to retrieve models: " + e.getMessage())
                );
            }
        });
    }
    
    /**
     * Get list of currently loaded ONNX models
     */
    @GetMapping("/models/loaded")
    public CompletableFuture<ResponseEntity<DataResponse<List<ONNXModel>>>> getLoadedModels() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ONNXModel> models = modelService.getLoadedModels();
                
                DataResponse<List<ONNXModel>> response = new DataResponse<>(
                    BaseResponse.ResponseStatus.SUCCESS,
                    "Retrieved " + models.size() + " loaded ONNX models",
                    models
                );
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Failed to get loaded ONNX models", e);
                return ResponseEntity.internalServerError().body(
                    new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                     "Failed to retrieve loaded models: " + e.getMessage())
                );
            }
        });
    }
    
    /**
     * Load an ONNX model
     */
    @PostMapping("/load")
    public CompletableFuture<ResponseEntity<DataResponse<ONNXModel>>> loadModel(
            @RequestParam String modelPath) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Loading ONNX model from: {}", modelPath);
                
                ONNXModel model = modelService.loadModel(modelPath).get();
                
                DataResponse<ONNXModel> response = new DataResponse<>(
                    BaseResponse.ResponseStatus.SUCCESS,
                    "ONNX model loaded successfully: " + model.getName(),
                    model
                );
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Failed to load ONNX model: {}", modelPath, e);
                return ResponseEntity.badRequest().body(
                    new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                     "Failed to load model: " + e.getMessage())
                );
            }
        });
    }
    
    /**
     * Unload an ONNX model
     */
    @PostMapping("/unload")
    public CompletableFuture<ResponseEntity<DataResponse<String>>> unloadModel(
            @RequestParam String modelId) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Unloading ONNX model: {}", modelId);
                
                boolean success = modelService.unloadModel(modelId).get();
                
                if (success) {
                    DataResponse<String> response = new DataResponse<>(
                        BaseResponse.ResponseStatus.SUCCESS,
                        "ONNX model unloaded successfully: " + modelId,
                        modelId
                    );
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.badRequest().body(
                        new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                         "Model was not loaded: " + modelId)
                    );
                }
            } catch (Exception e) {
                logger.error("Failed to unload ONNX model: {}", modelId, e);
                return ResponseEntity.badRequest().body(
                    new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                     "Failed to unload model: " + e.getMessage())
                );
            }
        });
    }
    
    /**
     * Get model status
     */
    @GetMapping("/status/{modelId}")
    public CompletableFuture<ResponseEntity<DataResponse<Map<String, Object>>>> getModelStatus(
            @PathVariable String modelId) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                ModelStatus status = modelService.getModelStatus(modelId);
                ONNXModelService.ModelMetadata metadata = modelService.getModelMetadata(modelId);
                
                Map<String, Object> statusInfo = new HashMap<>();
                statusInfo.put("modelId", modelId);
                statusInfo.put("status", status.name());
                statusInfo.put("isLoaded", status == ModelStatus.LOADED);
                statusInfo.put("metadata", metadata);
                
                DataResponse<Map<String, Object>> response = new DataResponse<>(
                    BaseResponse.ResponseStatus.SUCCESS,
                    "Model status retrieved",
                    statusInfo
                );
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Failed to get model status: {}", modelId, e);
                return ResponseEntity.internalServerError().body(
                    new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                     "Failed to get model status: " + e.getMessage())
                );
            }
        });
    }
    
    /**
     * Run inference on a model
     */
    @PostMapping("/inference")
    public CompletableFuture<ResponseEntity<DataResponse<String>>> runInference(
            @RequestParam String modelId,
            @RequestParam String prompt,
            @RequestParam(defaultValue = "100") Integer maxTokens,
            @RequestParam(defaultValue = "0.7") Double temperature) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Running inference on model: {} with prompt: {}", modelId, prompt);
                
                String result = inferenceEngine.testModel(modelId, prompt).get();
                
                DataResponse<String> response = new DataResponse<>(
                    BaseResponse.ResponseStatus.SUCCESS,
                    "Inference completed successfully",
                    result
                );
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Failed to run inference on model: {}", modelId, e);
                return ResponseEntity.badRequest().body(
                    new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                     "Inference failed: " + e.getMessage())
                );
            }
        });
    }
    
    /**
     * Get memory usage statistics
     */
    @GetMapping("/memory")
    public CompletableFuture<ResponseEntity<DataResponse<ONNXModelService.MemoryStats>>> getMemoryStats() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ONNXModelService.MemoryStats stats = modelService.getMemoryStats();
                
                DataResponse<ONNXModelService.MemoryStats> response = new DataResponse<>(
                    BaseResponse.ResponseStatus.SUCCESS,
                    "Memory statistics retrieved",
                    stats
                );
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Failed to get memory statistics", e);
                return ResponseEntity.internalServerError().body(
                    new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                     "Failed to get memory stats: " + e.getMessage())
                );
            }
        });
    }
    
    /**
     * Clean up unused models
     */
    @PostMapping("/cleanup")
    public CompletableFuture<ResponseEntity<DataResponse<Integer>>> cleanupUnusedModels() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Cleaning up unused ONNX models");
                
                Integer cleanedCount = modelService.cleanupUnusedModels().get();
                
                DataResponse<Integer> response = new DataResponse<>(
                    BaseResponse.ResponseStatus.SUCCESS,
                    "Cleaned up " + cleanedCount + " unused models",
                    cleanedCount
                );
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Failed to cleanup unused models", e);
                return ResponseEntity.internalServerError().body(
                    new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                     "Cleanup failed: " + e.getMessage())
                );
            }
        });
    }
    
    /**
     * Register a new model
     */
    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<DataResponse<String>>> registerModel(
            @RequestParam String name,
            @RequestParam String filePath) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Registering ONNX model: {} from {}", name, filePath);
                
                Boolean success = modelService.registerModel(name, filePath).get();
                
                if (success) {
                    DataResponse<String> response = new DataResponse<>(
                        BaseResponse.ResponseStatus.SUCCESS,
                        "Model registered successfully: " + name,
                        name
                    );
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.badRequest().body(
                        new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                         "Failed to register model: " + name)
                    );
                }
            } catch (Exception e) {
                logger.error("Failed to register model: {}", name, e);
                return ResponseEntity.badRequest().body(
                    new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                     "Registration failed: " + e.getMessage())
                );
            }
        });
    }
    
    /**
     * Get ONNX provider metrics
     */
    @GetMapping("/metrics")
    public CompletableFuture<ResponseEntity<DataResponse<ONNXProvider.ONNXProviderMetrics>>> getProviderMetrics() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ONNXProvider.ONNXProviderMetrics metrics = onnxProvider.getONNXMetrics();
                
                DataResponse<ONNXProvider.ONNXProviderMetrics> response = new DataResponse<>(
                    BaseResponse.ResponseStatus.SUCCESS,
                    "Provider metrics retrieved",
                    metrics
                );
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Failed to get provider metrics", e);
                return ResponseEntity.internalServerError().body(
                    new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                     "Failed to get metrics: " + e.getMessage())
                );
            }
        });
    }
    
    /**
     * Get health status of ONNX provider
     */
    @GetMapping("/health")
    public CompletableFuture<ResponseEntity<DataResponse<Map<String, Object>>>> getHealthStatus() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                boolean available = onnxProvider.isAvailable();
                var status = onnxProvider.getStatus();
                var metrics = onnxProvider.getMetrics();
                
                Map<String, Object> healthInfo = new HashMap<>();
                healthInfo.put("available", available);
                healthInfo.put("healthy", status.isHealthy());
                healthInfo.put("statusMessage", status.getStatusMessage());
                healthInfo.put("totalRequests", metrics.getTotalRequests());
                healthInfo.put("successRate", metrics.getSuccessRate());
                healthInfo.put("averageResponseTime", metrics.getAverageResponseTime());
                healthInfo.put("lastCheckedAt", status.getLastCheckedAt());
                
                DataResponse<Map<String, Object>> response = new DataResponse<>(
                    BaseResponse.ResponseStatus.SUCCESS,
                    "Health status retrieved",
                    healthInfo
                );
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Failed to get health status", e);
                return ResponseEntity.internalServerError().body(
                    new DataResponse<>(BaseResponse.ResponseStatus.ERROR,
                                     "Health check failed: " + e.getMessage())
                );
            }
        });
    }
}