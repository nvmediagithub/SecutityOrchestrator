package com.example.bpmn.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Comprehensive BPMN Analysis System Client
 * 
 * Provides full integration with SecurityOrchestrator's BPMN Analysis System
 * including process upload, analysis, AI integration, and compliance checking.
 */
@Slf4j
@Service
public class BPMNAnalysisClient {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final BPMNConfigProperties config;
    private final Duration requestTimeout;
    
    public BPMNAnalysisClient(RestTemplate restTemplate, 
                            ObjectMapper objectMapper,
                            BPMNConfigProperties config) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.config = config;
        this.requestTimeout = Duration.ofMillis(config.getTimeout());
        
        // Configure RestTemplate with timeout and error handling
        configureRestTemplate();
    }
    
    /**
     * Upload and analyze a BPMN process with comprehensive analysis
     */
    public ProcessAnalysisResult uploadAndAnalyzeProcess(String name, 
                                                        String description,
                                                        InputStream bpmnFile,
                                                        AnalysisOptions options) {
        try {
            log.info("Starting BPMN process analysis: {}", name);
            
            // Step 1: Upload the process
            ProcessUploadResponse uploadResponse = uploadProcess(name, description, bpmnFile);
            log.info("Process uploaded successfully: {}", uploadResponse.getProcessId());
            
            // Step 2: Start analysis
            String analysisId = startAnalysis(uploadResponse.getProcessId(), options);
            log.info("Analysis started: {}", analysisId);
            
            // Step 3: Wait for completion and get results
            return waitForAnalysisCompletion(analysisId, options.getMaxWaitTime());
            
        } catch (Exception e) {
            log.error("Failed to upload and analyze process: {}", name, e);
            throw new BPMNAnalysisException("Process analysis failed", e);
        }
    }
    
    /**
     * Upload BPMN process
     */
    public ProcessUploadResponse uploadProcess(String name, 
                                             String description,
                                             InputStream bpmnFile) {
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            if (config.getApiKey() != null) {
                headers.set("X-API-Key", config.getApiKey());
            }
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new InputStreamResource(bpmnFile));
            body.add("name", name);
            body.add("description", description != null ? description : "");
            
            HttpEntity<MultiValueMap<String, Object>> request = 
                new HttpEntity<>(body, headers);
            
            ResponseEntity<ProcessUploadResponse> response = restTemplate.postForEntity(
                config.getBaseUrl() + "/processes",
                request,
                ProcessUploadResponse.class
            );
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BPMNAnalysisException("Upload failed: " + response.getStatusCode());
            }
            
            return response.getBody();
            
        } catch (RestClientException e) {
            log.error("Failed to upload process", e);
            throw new BPMNAnalysisException("Upload failed", e);
        }
    }
    
    /**
     * Start security analysis
     */
    public String startAnalysis(String processId, AnalysisOptions options) {
        
        try {
            AnalysisRequest request = AnalysisRequest.builder()
                .processId(processId)
                .analysisType(options.getAnalysisType())
                .complianceStandards(options.getComplianceStandards())
                .configuration(AnalysisConfiguration.builder()
                    .includePerformance(options.isIncludePerformance())
                    .includeCompliance(options.isIncludeCompliance())
                    .includeThreatModeling(options.isIncludeThreatModeling())
                    .aiAssisted(options.isAiAssisted())
                    .customRules(options.getCustomRules())
                    .build())
                .build();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            if (config.getApiKey() != null) {
                headers.set("X-API-Key", config.getApiKey());
            }
            
            HttpEntity<AnalysisRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<AnalysisResponse> response = restTemplate.postForEntity(
                config.getBaseUrl() + "/analysis",
                entity,
                AnalysisResponse.class
            );
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BPMNAnalysisException("Analysis start failed: " + response.getStatusCode());
            }
            
            return response.getBody().getAnalysisId();
            
        } catch (RestClientException e) {
            log.error("Failed to start analysis for process: {}", processId, e);
            throw new BPMNAnalysisException("Analysis start failed", e);
        }
    }
    
    /**
     * Get analysis results
     */
    public AnalysisResult getAnalysisResult(String analysisId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (config.getApiKey() != null) {
                headers.set("X-API-Key", config.getApiKey());
            }
            
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<AnalysisResult> response = restTemplate.exchange(
                config.getBaseUrl() + "/analysis/" + analysisId,
                HttpMethod.GET,
                entity,
                AnalysisResult.class
            );
            
            return response.getBody();
            
        } catch (RestClientException e) {
            log.error("Failed to get analysis results: {}", analysisId, e);
            throw new BPMNAnalysisException("Failed to retrieve analysis results", e);
        }
    }
    
    /**
     * Get real-time analysis progress via WebSocket
     */
    public CompletableFuture<AnalysisResult> getAnalysisWithProgress(String analysisId,
                                                                   AnalysisProgressCallback callback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Setup WebSocket connection for progress updates
                setupWebSocketConnection(analysisId, callback);
                
                // Poll for completion
                return waitForAnalysisCompletion(analysisId);
                
            } catch (Exception e) {
                log.error("WebSocket setup failed for analysis: {}", analysisId, e);
                // Fallback to polling
                return waitForAnalysisCompletion(analysisId);
            }
        });
    }
    
    /**
     * Test AI provider connectivity
     */
    public AIProviderTestResult testAIProvider(String provider, String model) {
        try {
            AIProviderTestRequest request = AIProviderTestRequest.builder()
                .provider(provider)
                .model(model)
                .testPrompt("Test BPMN security analysis")
                .timeout(30)
                .build();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (config.getApiKey() != null) {
                headers.set("X-API-Key", config.getApiKey());
            }
            
            HttpEntity<AIProviderTestRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<AIProviderTestResult> response = restTemplate.postForEntity(
                config.getBaseUrl() + "/ai/test",
                entity,
                AIProviderTestResult.class
            );
            
            return response.getBody();
            
        } catch (RestClientException e) {
            log.error("AI provider test failed", e);
            throw new BPMNAnalysisException("AI provider test failed", e);
        }
    }
    
    /**
     * Generate security test cases
     */
    public TestGenerationResult generateTestCases(String processId, 
                                                 TestGenerationOptions options) {
        try {
            TestGenerationRequest request = TestGenerationRequest.builder()
                .processId(processId)
                .testTypes(options.getTestTypes())
                .coverage(options.getCoverage())
                .testFramework(options.getTestFramework())
                .outputFormat(options.getOutputFormat())
                .configuration(TestGenerationConfiguration.builder()
                    .includeEdgeCases(options.isIncludeEdgeCases())
                    .generateMockData(options.isGenerateMockData())
                    .includeNegativeTests(options.isIncludeNegativeTests())
                    .build())
                .build();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (config.getApiKey() != null) {
                headers.set("X-API-Key", config.getApiKey());
            }
            
            HttpEntity<TestGenerationRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<TestGenerationResult> response = restTemplate.postForEntity(
                config.getBaseUrl() + "/test-generation",
                entity,
                TestGenerationResult.class
            );
            
            return response.getBody();
            
        } catch (RestClientException e) {
            log.error("Test generation failed for process: {}", processId, e);
            throw new BPMNAnalysisException("Test generation failed", e);
        }
    }
    
    /**
     * Health check for the BPMN Analysis System
     */
    public BPMNSystemHealth getSystemHealth() {
        try {
            ResponseEntity<BPMNSystemHealth> response = restTemplate.getForEntity(
                config.getBaseUrl() + "/health",
                BPMNSystemHealth.class
            );
            
            return response.getBody();
            
        } catch (RestClientException e) {
            log.error("Health check failed", e);
            return BPMNSystemHealth.builder()
                .status("DOWN")
                .error(e.getMessage())
                .build();
        }
    }
    
    private void configureRestTemplate() {
        // Set default headers
        restTemplate.setInterceptors(Arrays.asList(
            (request, body, execution) -> {
                request.getHeaders().add("User-Agent", "BPMN-Client/1.0");
                return execution.execute(request, body);
            }
        ));
        
        // Set error handler
        restTemplate.setErrorHandler(new BPMNResponseErrorHandler());
    }
    
    private void setupWebSocketConnection(String analysisId, AnalysisProgressCallback callback) {
        // WebSocket implementation would go here
        // For now, this is a placeholder for the WebSocket setup
        log.info("Setting up WebSocket connection for analysis: {}", analysisId);
    }
    
    private ProcessAnalysisResult waitForAnalysisCompletion(String analysisId) {
        return waitForAnalysisCompletion(analysisId, 600); // 10 minutes default
    }
    
    private ProcessAnalysisResult waitForAnalysisCompletion(String analysisId, int maxWaitTimeSeconds) {
        int maxWaitTime = maxWaitTimeSeconds * 1000; // Convert to milliseconds
        int waitedTime = 0;
        int pollInterval = 5000; // 5 seconds
        
        while (waitedTime < maxWaitTime) {
            try {
                Thread.sleep(pollInterval);
                waitedTime += pollInterval;
                
                AnalysisResult result = getAnalysisResult(analysisId);
                
                if (result.getStatus() == AnalysisStatus.COMPLETED) {
                    return ProcessAnalysisResult.builder()
                        .analysisId(analysisId)
                        .processId(result.getProcessId())
                        .securityScore(result.getResults().getSecurityScore())
                        .riskLevel(result.getResults().getRiskLevel())
                        .totalFindings(result.getResults().getTotalFindings())
                        .findings(result.getResults().getFindings())
                        .recommendations(result.getResults().getRecommendations())
                        .complianceResults(result.getResults().getComplianceResults())
                        .aiInsights(result.getAiInsights())
                        .completedAt(result.getCompletedAt())
                        .build();
                        
                } else if (result.getStatus() == AnalysisStatus.FAILED) {
                    throw new BPMNAnalysisException("Analysis failed: " + result.getErrorMessage());
                }
                
                log.debug("Analysis {} in progress... ({}s elapsed)", 
                    analysisId, waitedTime / 1000);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BPMNAnalysisException("Analysis wait interrupted", e);
            }
        }
        
        throw new BPMNAnalysisException("Analysis timeout - analysis took longer than " + 
            maxWaitTimeSeconds + " seconds");
    }
    
    // Custom exception class
    public static class BPMNAnalysisException extends RuntimeException {
        public BPMNAnalysisException(String message) {
            super(message);
        }
        
        public BPMNAnalysisException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    // Custom error handler
    private static class BPMNResponseErrorHandler implements org.springframework.web.client.ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return !response.getStatusCode().is2xxSuccessful();
        }
        
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            String responseBody = new String(response.getBody().readAllBytes());
            throw new BPMNAnalysisException("HTTP " + response.getStatusCode() + ": " + responseBody);
        }
    }
}

// Supporting DTO classes
@lombok.Data
@lombok.Builder
class AnalysisOptions {
    private AnalysisType analysisType;
    private List<String> complianceStandards;
    private boolean includePerformance;
    private boolean includeCompliance;
    private boolean includeThreatModeling;
    private boolean aiAssisted;
    private List<String> customRules;
    private int maxWaitTime;
}

@lombok.Data
@lombok.Builder
class ProcessUploadResponse {
    private String processId;
    private String name;
    private String status;
    private ProcessElements elements;
    private ProcessValidation validation;
}

@lombok.Data
@lombok.Builder
class ProcessAnalysisResult {
    private String analysisId;
    private String processId;
    private Double securityScore;
    private String riskLevel;
    private Integer totalFindings;
    private List<SecurityFinding> findings;
    private List<SecurityRecommendation> recommendations;
    private Map<String, ComplianceResult> complianceResults;
    private AIInsights aiInsights;
    private java.time.LocalDateTime completedAt;
}

@lombok.Data
@lombok.Builder
class TestGenerationOptions {
    private List<String> testTypes;
    private String coverage;
    private String testFramework;
    private String outputFormat;
    private boolean includeEdgeCases;
    private boolean generateMockData;
    private boolean includeNegativeTests;
}

interface AnalysisProgressCallback {
    void onProgress(String analysisId, int progress, String currentStep);
    void onComplete(String analysisId, ProcessAnalysisResult result);
    void onError(String analysisId, String error);
}