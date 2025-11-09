package org.example.infrastructure.config;

import org.example.domain.entities.LLMProvider;
import org.example.infrastructure.services.ONNXInferenceEngine;
import org.example.infrastructure.services.ONNXModelService;
import org.example.infrastructure.services.ONNXProvider;
import org.example.infrastructure.services.OptimizedLLMOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for ONNX Runtime integration
 * Sets up ONNX model service, inference engine, and provider
 */
@Configuration
public class ONNXConfig {
    
    @Autowired
    private LLMConfig llmConfig;
    
    /**
     * Configure ONNX model service
     */
    @Bean
    public ONNXModelService onnxModelService() {
        Executor taskExecutor = threadPoolTaskExecutor();
        
        if (llmConfig.getOnnxModelPath() != null) {
            return new ONNXModelService(taskExecutor, llmConfig.getOnnxModelPath());
        } else {
            return new ONNXModelService(taskExecutor);
        }
    }
    
    /**
     * Configure ONNX inference engine
     */
    @Bean
    public ONNXInferenceEngine onnxInferenceEngine(ONNXModelService onnxModelService) {
        return new ONNXInferenceEngine(onnxModelService, threadPoolTaskExecutor());
    }
    
    /**
     * Configure ONNX provider
     */
    @Bean
    public ONNXProvider onnxProvider(ONNXModelService onnxModelService, 
                                    ONNXInferenceEngine onnxInferenceEngine) {
        return new ONNXProvider(onnxModelService, onnxInferenceEngine);
    }
    
    /**
     * Configure ONNX provider registration with LLM orchestrator
     */
    @Bean
    @Autowired
    public ONNXProviderRegistration onnxProviderRegistration(ONNXProvider onnxProvider,
                                                            OptimizedLLMOrchestrator orchestrator) {
        return new ONNXProviderRegistration(onnxProvider, orchestrator);
    }
    
    /**
     * Thread pool for ONNX operations
     */
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(llmConfig.getOnnxNumThreads());
        executor.setMaxPoolSize(llmConfig.getOnnxNumThreads() * 2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ONNX-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
    
    /**
     * Helper class to register ONNX provider with orchestrator
     */
    public static class ONNXProviderRegistration {
        
        public ONNXProviderRegistration(ONNXProvider onnxProvider, 
                                       OptimizedLLMOrchestrator orchestrator) {
            // Register the ONNX provider with the orchestrator
            orchestrator.registerProvider(LLMProvider.ONNX, onnxProvider);
            
            // Set ONNX as the active provider if no other providers are available
            if (orchestrator.getAvailableProviders().contains(LLMProvider.ONNX) &&
                orchestrator.getAvailableProviders().size() == 1) {
                orchestrator.setActiveProvider(LLMProvider.ONNX);
            }
        }
    }
}