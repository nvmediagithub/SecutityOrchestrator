package org.example.infrastructure.services.integration.llm;

import org.example.infrastructure.services.OpenRouterClient;
import org.example.infrastructure.services.LocalLLMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for LLM analysis operations
 * Handles both OpenRouter and local LLM services with fallback logic
 */
@Service
public class LlmAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(LlmAnalysisService.class);
    
    @Autowired
    private OpenRouterClient openRouterClient;
    
    @Autowired
    private LocalLLMService localLLMService;
    
    private static final long EXTRACTION_TIMEOUT_MINUTES = 30;
    
    /**
     * Executes LLM analysis with fallback to different providers
     */
    public String executeAnalysis(String prompt, String analysisType) {
        try {
            // Try OpenRouter first (external models)
            if (openRouterClient.hasApiKey()) {
                logger.debug("Using OpenRouter for {} BPMN analysis", analysisType);
                return openRouterClient.chatCompletion(getPreferredModel(), prompt, 2000, 0.3)
                    .get(EXTRACTION_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.warn("OpenRouter failed for {} BPMN analysis, trying local model", analysisType, e);
        }
        
        try {
            // Fallback to local model
            if (localLLMService.checkOllamaConnection() != null) {
                logger.debug("Using local LLM for {} BPMN analysis", analysisType);
                return localLLMService.localChatCompletion(getPreferredLocalModel(), prompt, 2000, 0.3)
                    .get(EXTRACTION_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                    .getResponse();
            }
        } catch (Exception e) {
            logger.error("All LLM providers failed for {} BPMN analysis", analysisType, e);
            throw new RuntimeException("No LLM provider available for BPMN analysis", e);
        }
        
        throw new RuntimeException("No LLM providers available");
    }
    
    private String getPreferredModel() {
        return "anthropic/claude-3-sonnet";
    }
    
    private String getPreferredLocalModel() {
        return "llama3.1:8b";
    }
}