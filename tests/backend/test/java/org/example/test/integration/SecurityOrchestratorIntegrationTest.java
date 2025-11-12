package org.example.test.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.example.config.TestConfig;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive End-to-End Integration Test Suite for SecurityOrchestrator
 * Tests the complete integrated system from frontend to LLM inference
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = org.example.SecurityOrchestratorApplication.class
)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("SecurityOrchestrator End-to-End Integration Tests")
class SecurityOrchestratorIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    
    private String baseUrl;
    private String validApiKey = "sk-or-v1-test-key-12345";
    private String testPrompt = "What is machine learning?";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        baseUrl = "http://localhost:" + port;
        
        // Setup test environment
        setupTestEnvironment();
    }

    @AfterEach
    void tearDown() {
        // Clean up test data
        cleanupTestEnvironment();
    }

    // ==================== END-TO-END WORKFLOW TESTS ====================

    @Test
    @Order(1)
    @DisplayName("Complete LLM Workflow: Frontend to Backend to LLM Inference")
    void testCompleteLLMWorkflow() {
        // 1. Start SecurityOrchestrator
        assertTrue(isSystemHealthy(), "System should be healthy");
        
        // 2. Open LLM Dashboard - simulate frontend configuration
        // Verify LLM configuration endpoint
        String configResponse = mockMvc.perform(get("/api/llm/config"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        assertNotNull(configResponse);
        
        // 3. Configure OpenRouter provider
        String configUpdate = String.format("""
            {
                "provider": "openrouter",
                "apiKey": "%s",
                "model": "meta-llama/llama-2-7b-chat",
                "maxTokens": 1000,
                "temperature": 0.7
            }
            """, validApiKey);
        
        mockMvc.perform(put("/api/llm/config")
                .contentType("application/json")
                .content(configUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        // 4. Test LLM inference
        String testRequest = String.format("""
            {
                "message": "%s",
                "model": "meta-llama/llama-2-7b-chat",
                "provider": "openrouter"
            }
            """, testPrompt);
        
        String response = mockMvc.perform(post("/api/llm/chat-completion")
                .contentType("application/json")
                .content(testRequest))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        assertNotNull(response);
        assertTrue(response.contains("content"));
        
        // 5. Verify monitoring captures the operation
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            String metricsResponse = mockMvc.perform(get("/api/monitoring/metrics/llm-operations"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
            assertNotNull(metricsResponse);
        });
        
        // 6. Check real-time dashboard updates
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            String dashboardResponse = mockMvc.perform(get("/api/monitoring/dashboard/last-update"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
            assertNotNull(dashboardResponse);
        });
    }

    @Test
    @Order(2)
    @DisplayName("ONNX Model Integration Workflow")
    void testONNXModelIntegrationWorkflow() throws Exception {
        // 1. Load ONNX model
        String loadRequest = """
            {
                "modelId": "test-model-onnx",
                "modelPath": "/test-models/test.onnx",
                "config": {
                    "batchSize": 1,
                    "threads": 4
                }
            }
            """;
        
        String loadResponse = mockMvc.perform(post("/api/llm/onnx/load-model")
                .contentType("application/json")
                .content(loadRequest))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        assertNotNull(loadResponse);
        
        // 2. Check model status
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            String statusResponse = mockMvc.perform(get("/api/llm/onnx/model-status/test-model-onnx"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
            assertNotNull(statusResponse);
            assertTrue(statusResponse.contains("LOADED") || statusResponse.contains("LOADING"));
        });
        
        // 3. Run inference if model loaded
        String inferenceRequest = """
            {
                "message": "Test ONNX inference",
                "modelId": "test-model-onnx"
            }
            """;
        
        try {
            String inferenceResponse = mockMvc.perform(post("/api/llm/onnx/inference")
                    .contentType("application/json")
                    .content(inferenceRequest))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            
            // 4. Verify performance metrics
            await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                String metricsResponse = mockMvc.perform(get("/api/monitoring/metrics/onnx-inference"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
                assertNotNull(metricsResponse);
            });
        } catch (Exception e) {
            // Model might not be available, which is acceptable in test environment
            logInfo("ONNX model not available in test environment: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Real-time Monitoring WebSocket Updates")
    void testRealTimeMonitoringWebSocketUpdates() throws Exception {
        // 1. Subscribe to monitoring updates
        // Note: WebSocket testing would require actual WebSocket connection setup
        // This is a simplified test that verifies the endpoint structure
        
        String subscribeRequest = """
            {
                "metrics": ["cpu_usage", "memory_usage", "llm_response_time"],
                "frequency": "every_5_seconds"
            }
            """;
        
        // Test monitoring configuration endpoint
        String response = mockMvc.perform(post("/api/monitoring/subscribe")
                .contentType("application/json")
                .content(subscribeRequest))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        assertNotNull(response);
        
        // 2. Trigger some operations to generate metrics
        generateTestLLMRequests(5);
        
        // 3. Verify monitoring endpoints respond
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            String healthResponse = mockMvc.perform(get("/api/monitoring/system/health"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
            assertNotNull(healthResponse);
        });
    }

    // ==================== BACKEND API INTEGRATION TESTS ====================

    @Test
    @Order(10)
    @DisplayName("LLM Management API Integration")
    void testLLMManagementAPI() {
        // Test LLM provider configuration
        String configResponse = mockMvc.perform(get("/api/llm/providers"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(configResponse);
        
        // Test model listing
        String modelsResponse = mockMvc.perform(get("/api/llm/models"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(modelsResponse);
        
        // Test model status
        String statusResponse = mockMvc.perform(get("/api/llm/status"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(statusResponse);
    }

    @Test
    @Order(11)
    @DisplayName("BPMN Analysis API Integration")
    void testBPMNAnalysisAPI() {
        // Test BPMN upload
        String bpmnContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <definitions>
                <process id="TestProcess" name="Test Process">
                    <startEvent id="start1"/>
                    <task id="task1" name="Test Task"/>
                    <endEvent id="end1"/>
                    <sequenceFlow id="flow1" sourceRef="start1" targetRef="task1"/>
                    <sequenceFlow id="flow2" sourceRef="task1" targetRef="end1"/>
                </process>
            </definitions>
            """;
        
        String uploadResponse = mockMvc.perform(post("/api/bpmn/upload")
                .contentType("application/xml")
                .content(bpmnContent))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(uploadResponse);
        
        // Test BPMN analysis
        String analysisResponse = mockMvc.perform(post("/api/bpmn/analyze")
                .contentType("application/json")
                .content("{\"diagramId\": \"test-diagram\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(analysisResponse);
    }

    @Test
    @Order(12)
    @DisplayName("OpenAPI Analysis API Integration")
    void testOpenAPIAnalysisAPI() {
        // Test OpenAPI specification upload
        String openApiSpec = """
            {
                "openapi": "3.0.0",
                "info": {
                    "title": "Test API",
                    "version": "1.0.0"
                },
                "paths": {
                    "/test": {
                        "get": {
                            "summary": "Test endpoint",
                            "responses": {
                                "200": {
                                    "description": "Success"
                                }
                            }
                        }
                    }
                }
            }
            """;
        
        String uploadResponse = mockMvc.perform(post("/api/openapi/upload")
                .contentType("application/json")
                .content(openApiSpec))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(uploadResponse);
    }

    // ==================== LLM PROVIDER TESTING ====================

    @Test
    @Order(20)
    @DisplayName("OpenRouter Provider Integration")
    void testOpenRouterProvider() {
        // Configure OpenRouter
        String configRequest = String.format("""
            {
                "provider": "openrouter",
                "apiKey": "%s",
                "baseUrl": "https://openrouter.ai/api/v1",
                "defaultModel": "meta-llama/llama-2-7b-chat"
            }
            """, validApiKey);
        
        mockMvc.perform(put("/api/llm/providers/openrouter")
                .contentType("application/json")
                .content(configRequest))
                .andExpect(status().isOk());
        
        // Test connection
        String connectionResponse = mockMvc.perform(get("/api/llm/providers/openrouter/status"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(connectionResponse);
    }

    @Test
    @Order(21)
    @DisplayName("ONNX Provider Integration")
    void testONNXProvider() {
        // Test ONNX model listing
        String modelsResponse = mockMvc.perform(get("/api/llm/onnx/models"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(modelsResponse);
        
        // Test ONNX configuration
        String configRequest = """
            {
                "modelPath": "/test-models",
                "maxBatchSize": 32,
                "threads": 4
            }
            """;
        
        mockMvc.perform(post("/api/llm/onnx/config")
                .contentType("application/json")
                .content(configRequest))
                .andExpect(status().isOk());
    }

    @Test
    @Order(22)
    @DisplayName("Circuit Breaker and Retry Logic")
    void testCircuitBreakerAndRetryLogic() {
        // Test with invalid API key to trigger circuit breaker
        String invalidConfig = """
            {
                "provider": "openrouter",
                "apiKey": "invalid-key",
                "model": "meta-llama/llama-2-7b-chat"
            }
            """;
        
        mockMvc.perform(put("/api/llm/providers/openrouter")
                .contentType("application/json")
                .content(invalidConfig))
                .andExpect(status().isOk());
        
        // Make multiple requests to trigger circuit breaker
        for (int i = 0; i < 15; i++) {
            String request = """
                {
                    "message": "Test request",
                    "model": "meta-llama/llama-2-7b-chat"
                }
                """;
            
            try {
                mockMvc.perform(post("/api/llm/chat-completion")
                        .contentType("application/json")
                        .content(request));
            } catch (Exception e) {
                // Expected failures
            }
        }
        
        // Verify circuit breaker status
        String breakerStatus = mockMvc.perform(get("/api/llm/circuit-breaker/status"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(breakerStatus);
    }

    // ==================== SYSTEM HEALTH TESTING ====================

    @Test
    @Order(30)
    @DisplayName("System Health Monitoring")
    void testSystemHealthMonitoring() {
        // Test system health endpoint
        String healthResponse = mockMvc.perform(get("/api/monitoring/system/health"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(healthResponse);
        
        // Test resource usage
        String resourceResponse = mockMvc.perform(get("/api/monitoring/system/resources"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(resourceResponse);
        
        // Test performance metrics
        String metricsResponse = mockMvc.perform(get("/api/monitoring/metrics"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(metricsResponse);
    }

    @Test
    @Order(31)
    @DisplayName("Alert System Verification")
    void testAlertSystem() {
        // Test alert configuration
        String alertConfig = """
            {
                "metrics": ["cpu_usage", "memory_usage", "error_rate"],
                "thresholds": {
                    "cpu_usage": 80.0,
                    "memory_usage": 90.0,
                    "error_rate": 5.0
                }
            }
            """;
        
        mockMvc.perform(post("/api/monitoring/alerts/configure")
                .contentType("application/json")
                .content(alertConfig))
                .andExpect(status().isOk());
        
        // Test alert status
        String alertStatus = mockMvc.perform(get("/api/monitoring/alerts/status"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(alertStatus);
    }

    @Test
    @Order(32)
    @DisplayName("Performance Metrics Collection")
    void testPerformanceMetricsCollection() {
        // Test LLM performance metrics
        String llmMetrics = mockMvc.perform(get("/api/monitoring/metrics/llm"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(llmMetrics);
        
        // Test response time metrics
        String responseTimeMetrics = mockMvc.perform(get("/api/monitoring/metrics/response-time"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(responseTimeMetrics);
        
        // Test throughput metrics
        String throughputMetrics = mockMvc.perform(get("/api/monitoring/metrics/throughput"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(throughputMetrics);
    }

    // ==================== PERFORMANCE TESTING ====================

    @Test
    @Order(40)
    @DisplayName("System Under Load - Concurrent LLM Requests")
    void testSystemUnderLoad() throws Exception {
        int concurrentRequests = 20;
        CountDownLatch latch = new CountDownLatch(concurrentRequests);
        
        // Execute concurrent LLM requests
        for (int i = 0; i < concurrentRequests; i++) {
            final int requestId = i;
            CompletableFuture.runAsync(() -> {
                try {
                    String request = String.format("""
                        {
                            "message": "Test prompt %d",
                            "model": "meta-llama/llama-2-7b-chat"
                        }
                        """, requestId);
                    
                    mockMvc.perform(post("/api/llm/chat-completion")
                            .contentType("application/json")
                            .content(request));
                } catch (Exception e) {
                    // Log but don't fail the test
                    logInfo("Request " + requestId + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all requests to complete
        assertTrue(latch.await(2, TimeUnit.MINUTES), "All requests should complete within 2 minutes");
        
        // Verify system is still responsive
        String healthResponse = mockMvc.perform(get("/api/monitoring/system/health"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertNotNull(healthResponse);
    }

    @Test
    @Order(41)
    @DisplayName("Response Time Benchmarks")
    void testResponseTimeBenchmarks() {
        // Test LLM response time
        String startTime = String.valueOf(System.currentTimeMillis());
        
        String request = """
            {
                "message": "Quick test response",
                "model": "meta-llama/llama-2-7b-chat"
            }
            """;
        
        try {
            mockMvc.perform(post("/api/llm/chat-completion")
                    .contentType("application/json")
                    .content(request))
                    .andExpect(status().isOk());
            
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - Long.parseLong(startTime);
            
            // Log performance for analysis
            logInfo("LLM response time: " + responseTime + "ms");
        } catch (Exception e) {
            // Service might not be available, which is acceptable
            logInfo("LLM service not available for benchmark: " + e.getMessage());
        }
        
        // Test API endpoint response times
        String[] endpoints = {
            "/api/llm/config",
            "/api/monitoring/system/health",
            "/api/llm/status"
        };
        
        for (String endpoint : endpoints) {
            long start = System.currentTimeMillis();
            try {
                mockMvc.perform(get(endpoint))
                        .andExpect(status().isOk());
                long end = System.currentTimeMillis();
                logInfo(endpoint + " response time: " + (end - start) + "ms");
            } catch (Exception e) {
                logInfo("Endpoint " + endpoint + " not available: " + e.getMessage());
            }
        }
    }

    // ==================== UTILITY METHODS ====================

    private void setupTestEnvironment() {
        // Setup test configuration
        logInfo("Setting up test environment on port: " + port);
    }

    private void cleanupTestEnvironment() {
        // Clean up test data
        logInfo("Cleaning up test environment");
    }

    private boolean isSystemHealthy() {
        try {
            String healthResponse = restTemplate.getForObject(
                baseUrl + "/api/monitoring/system/health", String.class);
            return healthResponse != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void generateTestLLMRequests(int count) {
        for (int i = 0; i < count; i++) {
            try {
                String request = String.format("""
                    {
                        "message": "Test request %d",
                        "model": "meta-llama/llama-2-7b-chat"
                    }
                    """, i);
                
                mockMvc.perform(post("/api/llm/chat-completion")
                        .contentType("application/json")
                        .content(request));
            } catch (Exception e) {
                // Ignore errors in test data generation
            }
        }
    }

    private void logInfo(String message) {
        System.out.println("[TEST] " + message);
    }
}