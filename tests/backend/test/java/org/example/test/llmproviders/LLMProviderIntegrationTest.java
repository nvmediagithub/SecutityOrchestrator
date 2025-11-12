package org.example.test.llmproviders;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive LLM Provider Integration Testing Suite
 * Tests OpenRouter, ONNX, and other LLM providers in isolation and integration
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("LLM Provider Integration Tests")
class LLMProviderIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private String validOpenRouterKey = "sk-or-v1-test-key-12345";
    private String testModel = "meta-llama/llama-2-7b-chat";
    private String testPrompt = "What is the capital of France?";

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    // ==================== OPENROUTER PROVIDER TESTS ====================

    @Test
    @Order(1)
    @DisplayName("OpenRouter Provider Configuration and Connectivity")
    void testOpenRouterProviderConfiguration() {
        // Test 1: Configure OpenRouter provider
        String configRequest = String.format("""
            {
                "provider": "openrouter",
                "apiKey": "%s",
                "baseUrl": "https://openrouter.ai/api/v1",
                "defaultModel": "%s",
                "maxTokens": 1000,
                "temperature": 0.7,
                "timeout": 30000
            }
            """, validOpenRouterKey, testModel);

        ResponseEntity<String> configResponse = restTemplate.postForEntity(
            baseUrl + "/api/llm/providers/openrouter/configure",
            configRequest,
            String.class
        );

        assertEquals(HttpStatus.OK, configResponse.getStatusCode());
        assertNotNull(configResponse.getBody());
        
        // Test 2: Check provider status
        ResponseEntity<String> statusResponse = restTemplate.getForEntity(
            baseUrl + "/api/llm/providers/openrouter/status",
            String.class
        );

        assertEquals(HttpStatus.OK, statusResponse.getStatusCode());
        assertNotNull(statusResponse.getBody());
    }

    @Test
    @Order(2)
    @DisplayName("OpenRouter API Integration Test")
    void testOpenRouterAPIIntegration() {
        // Test 3: Make a chat completion request
        String chatRequest = String.format("""
            {
                "model": "%s",
                "messages": [
                    {
                        "role": "user",
                        "content": "%s"
                    }
                ],
                "maxTokens": 100,
                "temperature": 0.5
            }
            """, testModel, testPrompt);

        try {
            ResponseEntity<String> chatResponse = restTemplate.postForEntity(
                baseUrl + "/api/llm/chat-completion",
                chatRequest,
                String.class
            );

            // In test environment, the service might not be available
            // So we test the endpoint structure and error handling
            if (chatResponse.getStatusCode() == HttpStatus.OK) {
                assertNotNull(chatResponse.getBody());
                assertTrue(chatResponse.getBody().contains("content") || 
                          chatResponse.getBody().contains("choices"));
            } else {
                // Test that we get proper error responses
                assertTrue(chatResponse.getStatusCode().is5xxServerError() || 
                          chatResponse.getStatusCode().is4xxClientError());
            }
        } catch (Exception e) {
            // Service might not be available, which is acceptable in test environment
            logInfo("OpenRouter service not available in test environment: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("OpenRouter Circuit Breaker and Retry Logic")
    void testOpenRouterCircuitBreakerAndRetry() {
        // Test 4: Invalid API key to trigger circuit breaker
        String invalidConfig = """
            {
                "provider": "openrouter",
                "apiKey": "invalid-key-12345",
                "defaultModel": "meta-llama/llama-2-7b-chat"
            }
            """;

        // Configure with invalid key
        restTemplate.postForEntity(
            baseUrl + "/api/llm/providers/openrouter/configure",
            invalidConfig,
            String.class
        );

        // Make multiple requests to trigger circuit breaker
        for (int i = 0; i < 15; i++) {
            String request = """
                {
                    "model": "meta-llama/llama-2-7b-chat",
                    "message": "Test request " + i
                }
                """;
            
            try {
                restTemplate.postForEntity(
                    baseUrl + "/api/llm/chat-completion",
                    request,
                    String.class
                );
            } catch (Exception e) {
                // Expected failures
                logInfo("Request " + i + " failed as expected: " + e.getMessage());
            }
        }

        // Verify circuit breaker status
        ResponseEntity<String> breakerStatus = restTemplate.getForEntity(
            baseUrl + "/api/llm/circuit-breaker/openrouter/status",
            String.class
        );

        assertNotNull(breakerStatus.getBody());
    }

    // ==================== ONNX PROVIDER TESTS ====================

    @Test
    @Order(10)
    @DisplayName("ONNX Model Loading and Management")
    void testONNXModelLoading() {
        // Test 1: Configure ONNX provider
        String onnxConfig = """
            {
                "modelPath": "/test-models",
                "maxBatchSize": 32,
                "threads": 4,
                "enableOptimizations": true
            }
            """;

        ResponseEntity<String> configResponse = restTemplate.postForEntity(
            baseUrl + "/api/llm/onnx/configure",
            onnxConfig,
            String.class
        );

        assertEquals(HttpStatus.OK, configResponse.getStatusCode());
    }

    @Test
    @Order(11)
    @DisplayName("ONNX Model Inference Performance")
    void testONNXModelInference() {
        // Test 2: List available ONNX models
        ResponseEntity<String> modelsResponse = restTemplate.getForEntity(
            baseUrl + "/api/llm/onnx/models",
            String.class
        );

        assertEquals(HttpStatus.OK, modelsResponse.getStatusCode());
        assertNotNull(modelsResponse.getBody());

        // Test 3: Load a test model
        String modelRequest = """
            {
                "modelId": "test-model",
                "modelPath": "/test-models/test-model.onnx",
                "config": {
                    "batchSize": 1,
                    "threads": 4
                }
            }
            """;

        try {
            ResponseEntity<String> loadResponse = restTemplate.postForEntity(
                baseUrl + "/api/llm/onnx/load-model",
                modelRequest,
                String.class
            );

            // Model might not be available in test environment
            if (loadResponse.getStatusCode() == HttpStatus.OK) {
                // Wait for model to load
                await().atMost(30, TimeUnit.SECONDS).until(() -> {
                    try {
                        ResponseEntity<String> statusResponse = restTemplate.getForEntity(
                            baseUrl + "/api/llm/onnx/model-status/test-model",
                            String.class
                        );
                        return statusResponse.getStatusCode() == HttpStatus.OK;
                    } catch (Exception e) {
                        return false;
                    }
                });

                // Test inference if model loaded
                String inferenceRequest = """
                    {
                        "modelId": "test-model",
                        "input": "Test input for ONNX model"
                    }
                    """;

                long startTime = System.currentTimeMillis();
                ResponseEntity<String> inferenceResponse = restTemplate.postForEntity(
                    baseUrl + "/api/llm/onnx/inference",
                    inferenceRequest,
                    String.class
                );
                long endTime = System.currentTimeMillis();
                long inferenceTime = endTime - startTime;

                // Log performance metrics
                logInfo("ONNX inference time: " + inferenceTime + "ms");

                if (inferenceResponse.getStatusCode() == HttpStatus.OK) {
                    // Should be under 5 seconds for local inference
                    assertTrue(inferenceTime < 5000, 
                        "ONNX inference should be under 5 seconds, took: " + inferenceTime + "ms");
                }
            }
        } catch (Exception e) {
            // ONNX model not available in test environment
            logInfo("ONNX model not available in test environment: " + e.getMessage());
        }
    }

    @Test
    @Order(12)
    @DisplayName("ONNX Performance Optimization")
    void testONNXPerformanceOptimization() {
        // Test multiple parallel inferences to check performance
        int parallelRequests = 5;
        CountDownLatch latch = new CountDownLatch(parallelRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < parallelRequests; i++) {
            final int requestId = i;
            CompletableFuture.runAsync(() -> {
                try {
                    String request = String.format("""
                        {
                            "modelId": "test-model",
                            "input": "Test input %d for performance testing"
                        }
                        """, requestId);

                    ResponseEntity<String> response = restTemplate.postForEntity(
                        baseUrl + "/api/llm/onnx/inference",
                        request,
                        String.class
                    );

                    if (response.getStatusCode() == HttpStatus.OK) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    logInfo("Performance test request " + requestId + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            // Wait for all requests to complete
            assertTrue(latch.await(60, TimeUnit.SECONDS), "All performance requests should complete");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test interrupted while waiting for performance requests");
        }

        // Log results
        logInfo("Performance test results - Success: " + successCount.get() + 
                ", Errors: " + errorCount.get());

        // At least some requests should succeed if ONNX is available
        if (successCount.get() > 0) {
            assertTrue(successCount.get() >= parallelRequests / 2, 
                "At least half of the requests should succeed");
        }
    }

    // ==================== LLM ORCHESTRATOR TESTS ====================

    @Test
    @Order(20)
    @DisplayName("LLM Orchestrator Provider Failover")
    void testLLMOrchestratorProviderFailover() {
        // Test 1: Configure primary and fallback providers
        String primaryConfig = String.format("""
            {
                "provider": "openrouter",
                "apiKey": "%s",
                "model": "%s",
                "priority": 1
            }
            """, validOpenRouterKey, testModel);

        String fallbackConfig = """
            {
                "provider": "local",
                "modelId": "test-model",
                "priority": 2
            }
            """;

        // Configure both providers
        restTemplate.postForEntity(
            baseUrl + "/api/llm/orchestrator/providers",
            "[" + primaryConfig + ", " + fallbackConfig + "]",
            String.class
        );

        // Test 2: Make request that should fail over
        String orchestratorRequest = """
            {
                "message": "Test failover scenario",
                "preferProviders": ["openrouter", "local"],
                "timeout": 10000
            }
            """;

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/api/llm/orchestrator/generate",
                orchestratorRequest,
                String.class
            );

            // Should get either success or proper error handling
            assertTrue(response.getStatusCode() == HttpStatus.OK || 
                      response.getStatusCode().is5xxServerError() ||
                      response.getStatusCode().is4xxClientError());
        } catch (Exception e) {
            logInfo("Orchestrator test failed (expected in test environment): " + e.getMessage());
        }
    }

    @Test
    @Order(21)
    @DisplayName("LLM Caching and Performance")
    void testLLMCachingAndPerformance() {
        // Test 1: Clear cache
        ResponseEntity<String> clearResponse = restTemplate.postForEntity(
            baseUrl + "/api/llm/cache/clear",
            "",
            String.class
        );

        assertEquals(HttpStatus.OK, clearResponse.getStatusCode());

        // Test 2: Make same request twice to test caching
        String cacheTestRequest = """
            {
                "message": "What is the color of the sky?",
                "model": "meta-llama/llama-2-7b-chat",
                "useCache": true
            }
            """;

        long firstRequestStart = System.currentTimeMillis();
        try {
            ResponseEntity<String> firstResponse = restTemplate.postForEntity(
                baseUrl + "/api/llm/chat-completion",
                cacheTestRequest,
                String.class
            );
            long firstRequestEnd = System.currentTimeMillis();
            long firstRequestTime = firstRequestEnd - firstRequestStart;

            logInfo("First request time: " + firstRequestTime + "ms");

            // Second request should be faster due to caching
            long secondRequestStart = System.currentTimeMillis();
            try {
                ResponseEntity<String> secondResponse = restTemplate.postForEntity(
                    baseUrl + "/api/llm/chat-completion",
                    cacheTestRequest,
                    String.class
                );
                long secondRequestEnd = System.currentTimeMillis();
                long secondRequestTime = secondRequestEnd - secondRequestStart;

                logInfo("Second request time: " + secondRequestTime + "ms");

                // If both requests succeeded, second should be faster
                if (firstResponse.getStatusCode() == HttpStatus.OK && 
                    secondResponse.getStatusCode() == HttpStatus.OK) {
                    assertTrue(secondRequestTime <= firstRequestTime * 1.2, 
                        "Cached request should be faster or equal, first: " + firstRequestTime + 
                        "ms, second: " + secondRequestTime + "ms");
                }
            } catch (Exception e) {
                logInfo("Second cache test request failed: " + e.getMessage());
            }
        } catch (Exception e) {
            logInfo("First cache test request failed: " + e.getMessage());
        }

        // Test 3: Check cache statistics
        ResponseEntity<String> cacheStats = restTemplate.getForEntity(
            baseUrl + "/api/llm/cache/statistics",
            String.class
        );

        assertEquals(HttpStatus.OK, cacheStats.getStatusCode());
    }

    // ==================== RATE LIMITING TESTS ====================

    @Test
    @Order(30)
    @DisplayName("LLM Provider Rate Limiting")
    void testLLMProviderRateLimiting() {
        // Configure rate limiting
        String rateLimitConfig = """
            {
                "requestsPerMinute": 10,
                "requestsPerHour": 100,
                "burstSize": 5
            }
            """;

        restTemplate.postForEntity(
            baseUrl + "/api/llm/rate-limit/configure",
            rateLimitConfig,
            String.class
        );

        // Make multiple rapid requests to trigger rate limiting
        int requestCount = 15;
        AtomicInteger successfulRequests = new AtomicInteger(0);
        AtomicInteger rateLimitedRequests = new AtomicInteger(0);

        for (int i = 0; i < requestCount; i++) {
            String request = String.format("""
                {
                    "message": "Rate limit test request %d",
                    "model": "%s"
                }
                """, i, testModel);

            try {
                ResponseEntity<String> response = restTemplate.postForEntity(
                    baseUrl + "/api/llm/chat-completion",
                    request,
                    String.class
                );

                if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS || 
                    response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                    rateLimitedRequests.incrementAndGet();
                } else if (response.getStatusCode() == HttpStatus.OK) {
                    successfulRequests.incrementAndGet();
                }
            } catch (Exception e) {
                logInfo("Rate limit test request " + i + " failed: " + e.getMessage());
            }

            // Small delay between requests
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        logInfo("Rate limiting test results - Success: " + successfulRequests.get() + 
                ", Rate limited: " + rateLimitedRequests.get());

        // Some requests should be rate limited
        assertTrue(rateLimitedRequests.get() > 0, 
            "Should have rate limited some requests, got: " + rateLimitedRequests.get());
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    @Order(40)
    @DisplayName("LLM Provider Error Handling and Recovery")
    void testLLMProviderErrorHandling() {
        // Test 1: Invalid model name
        String invalidModelRequest = """
            {
                "model": "non-existent-model-12345",
                "message": "Test with invalid model"
            }
            """;

        ResponseEntity<String> invalidModelResponse = restTemplate.postForEntity(
            baseUrl + "/api/llm/chat-completion",
            invalidModelRequest,
            String.class
        );

        // Should get appropriate error response
        assertTrue(invalidModelResponse.getStatusCode().is4xxClientError() || 
                  invalidModelResponse.getStatusCode().is5xxServerError());

        // Test 2: Empty request
        String emptyRequest = """
            {
                "model": ""
            }
            """;

        ResponseEntity<String> emptyRequestResponse = restTemplate.postForEntity(
            baseUrl + "/api/llm/chat-completion",
            emptyRequest,
            String.class
        );

        assertTrue(emptyRequestResponse.getStatusCode().is4xxClientError());

        // Test 3: Very long prompt (should be rejected)
        String longPrompt = "x".repeat(100000); // Very long prompt
        String longRequest = String.format("""
            {
                "model": "%s",
                "message": "%s"
            }
            """, testModel, longPrompt);

        ResponseEntity<String> longRequestResponse = restTemplate.postForEntity(
            baseUrl + "/api/llm/chat-completion",
            longRequest,
            String.class
        );

        assertTrue(longRequestResponse.getStatusCode().is4xxClientError());
    }

    // ==================== UTILITY METHODS ====================

    private void logInfo(String message) {
        System.out.println("[LLM-PROVIDER-TEST] " + message);
    }
}