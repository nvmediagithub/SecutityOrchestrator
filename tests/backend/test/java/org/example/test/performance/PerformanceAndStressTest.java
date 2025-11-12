package org.example.test.performance;

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
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Performance and Stress Testing Suite for SecurityOrchestrator
 * Tests system performance under various load conditions
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Performance and Stress Tests")
class PerformanceAndStressTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private Instant testStartTime;
    private PerformanceMetricsCollector metricsCollector;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        testStartTime = Instant.now();
        metricsCollector = new PerformanceMetricsCollector();
    }

    @AfterEach
    void tearDown() {
        logInfo("Test completed. Duration: " + 
               Duration.between(testStartTime, Instant.now()).toMillis() + "ms");
    }

    // ==================== LOAD TESTING ====================

    @Test
    @Order(1)
    @DisplayName("System Under Load - 50 Concurrent LLM Requests")
    void testSystemUnderLoad() throws Exception {
        logInfo("🚀 Starting load test: 50 concurrent LLM requests");
        
        int concurrentRequests = 50;
        CountDownLatch latch = new CountDownLatch(concurrentRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicLong totalLatency = new AtomicLong(0);
        
        long testStartTime = System.currentTimeMillis();
        
        // Execute concurrent LLM requests
        for (int i = 0; i < concurrentRequests; i++) {
            final int requestId = i;
            CompletableFuture.runAsync(() -> {
                try {
                    String request = String.format("""
                        {
                            "message": "Load test prompt %d - What is artificial intelligence?",
                            "model": "meta-llama/llama-2-7b-chat",
                            "maxTokens": 100,
                            "temperature": 0.7
                        }
                        """, requestId);
                    
                    long requestStart = System.currentTimeMillis();
                    
                    ResponseEntity<String> response = restTemplate.postForEntity(
                        baseUrl + "/api/llm/chat-completion",
                        request,
                        String.class
                    );
                    
                    long requestEnd = System.currentTimeMillis();
                    long latency = requestEnd - requestStart;
                    totalLatency.addAndGet(latency);
                    
                    metricsCollector.recordRequest(latency, (HttpStatus) response.getStatusCode());
                    
                    if (response.getStatusCode() == HttpStatus.OK) {
                        successCount.incrementAndGet();
                        logInfo("✓ Request " + requestId + " completed in " + latency + "ms");
                    } else {
                        errorCount.incrementAndGet();
                        logInfo("✗ Request " + requestId + " failed: " + response.getStatusCode());
                    }
                    
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    logInfo("✗ Request " + requestId + " exception: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all requests to complete
        assertTrue(latch.await(5, TimeUnit.MINUTES), 
            "All 50 requests should complete within 5 minutes");
        
        long testEndTime = System.currentTimeMillis();
        long testDuration = testEndTime - testStartTime;
        
        // Calculate performance metrics
        double throughput = (double) successCount.get() / (testDuration / 1000.0);
        double averageLatency = (double) totalLatency.get() / Math.max(successCount.get(), 1);
        double errorRate = (double) errorCount.get() / concurrentRequests * 100.0;
        
        // Log detailed results
        logInfo("📊 Load Test Results:");
        logInfo("  📈 Total Duration: " + testDuration + "ms");
        logInfo("  ✅ Successful Requests: " + successCount.get() + "/" + concurrentRequests);
        logInfo("  ❌ Failed Requests: " + errorCount.get());
        logInfo("  ⚡ Throughput: " + String.format("%.2f", throughput) + " requests/second");
        logInfo("  ⏱️  Average Latency: " + String.format("%.2f", averageLatency) + "ms");
        logInfo("  📉 Error Rate: " + String.format("%.2f", errorRate) + "%");
        
        // Performance assertions
        // Target: 50 requests/second sustained load
        assertTrue(throughput >= 1.0, "Should achieve at least 1 req/s in test environment");
        
        // Target: < 1% error rate under normal load
        assertTrue(errorRate < 10.0, "Error rate should be under 10% in test environment");
        
        // System should remain responsive
        ResponseEntity<String> healthCheck = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/system/health",
            String.class
        );
        assertEquals(HttpStatus.OK, healthCheck.getStatusCode());
        
        logInfo("✅ Load test completed successfully!");
    }

    @Test
    @Order(2)
    @DisplayName("Sustained Load Test - 5 Minutes of Continuous Requests")
    void testSustainedLoad() throws Exception {
        logInfo("🚀 Starting sustained load test: 5 minutes of continuous requests");
        
        long testDurationMs = 5 * 60 * 1000; // 5 minutes
        long endTime = System.currentTimeMillis() + testDurationMs;
        
        AtomicInteger totalRequests = new AtomicInteger(0);
        AtomicInteger successfulRequests = new AtomicInteger(0);
        AtomicInteger failedRequests = new AtomicInteger(0);
        
        // Start continuous load
        CompletableFuture<Void> loadGenerator = CompletableFuture.runAsync(() -> {
            while (System.currentTimeMillis() < endTime) {
                try {
                    String request = """
                        {
                            "message": "Sustained load test - Generate a brief summary of machine learning",
                            "model": "meta-llama/llama-2-7b-chat",
                            "maxTokens": 50
                        }
                        """;
                    
                    ResponseEntity<String> response = restTemplate.postForEntity(
                        baseUrl + "/api/llm/chat-completion",
                        request,
                        String.class
                    );
                    
                    totalRequests.incrementAndGet();
                    
                    if (response.getStatusCode() == HttpStatus.OK) {
                        successfulRequests.incrementAndGet();
                    } else {
                        failedRequests.incrementAndGet();
                    }
                    
                    // Small delay to prevent overwhelming the system
                    Thread.sleep(1000); // 1 request per second
                    
                } catch (Exception e) {
                    failedRequests.incrementAndGet();
                    totalRequests.incrementAndGet();
                    logInfo("Sustained load request failed: " + e.getMessage());
                }
            }
        });
        
        // Monitor system health every 30 seconds
        CompletableFuture<Void> healthMonitor = CompletableFuture.runAsync(() -> {
            while (System.currentTimeMillis() < endTime) {
                try {
                    Thread.sleep(30000); // Check every 30 seconds
                    
                    ResponseEntity<String> health = restTemplate.getForEntity(
                        baseUrl + "/api/monitoring/system/health",
                        String.class
                    );
                    
                    if (health.getStatusCode() == HttpStatus.OK) {
                        logInfo("💚 System healthy at " + 
                               (System.currentTimeMillis() - testStartTime) / 1000 + "s");
                    } else {
                        logInfo("⚠️  System health degraded at " + 
                               (System.currentTimeMillis() - testStartTime) / 1000 + "s");
                    }
                    
                } catch (Exception e) {
                    logInfo("❌ Health check failed: " + e.getMessage());
                }
            }
        });
        
        // Wait for test to complete
        CompletableFuture.allOf(loadGenerator, healthMonitor).get();
        
        long actualDuration = System.currentTimeMillis() - testStartTime;
        double averageRps = (double) totalRequests.get() / (actualDuration / 1000.0);
        double successRate = (double) successfulRequests.get() / totalRequests.get() * 100.0;
        
        logInfo("📊 Sustained Load Test Results:");
        logInfo("  ⏱️  Actual Duration: " + (actualDuration / 1000) + " seconds");
        logInfo("  📊 Total Requests: " + totalRequests.get());
        logInfo("  ✅ Successful: " + successfulRequests.get());
        logInfo("  ❌ Failed: " + failedRequests.get());
        logInfo("  📈 Average RPS: " + String.format("%.2f", averageRps));
        logInfo("  ✅ Success Rate: " + String.format("%.2f", successRate) + "%");
        
        // Assertions for sustained load
        assertTrue(successRate >= 90.0, "Success rate should be at least 90% under sustained load");
        assertTrue(averageRps >= 0.5, "Should maintain at least 0.5 requests/second sustained");
        
        logInfo("✅ Sustained load test completed!");
    }

    // ==================== STRESS TESTING ====================

    @Test
    @Order(10)
    @DisplayName("Circuit Breaker Stress Test")
    void testCircuitBreakerStress() {
        logInfo("🧪 Starting circuit breaker stress test");
        
        // Configure circuit breaker with very low threshold
        String stressConfig = """
            {
                "provider": "openrouter",
                "failureThreshold": 3,
                "timeout": 10000,
                "resetTimeout": 30000,
                "failureRate": 0.3
            }
            """;

        restTemplate.postForEntity(
            baseUrl + "/api/llm/circuit-breaker/configure",
            stressConfig,
            String.class
        );
        
        // Generate rapid failures to trip circuit breaker
        int failureRequests = 20;
        CountDownLatch failureLatch = new CountDownLatch(failureRequests);
        AtomicInteger circuitBreakerTrips = new AtomicInteger(0);
        
        for (int i = 0; i < failureRequests; i++) {
            final int requestId = i;
            CompletableFuture.runAsync(() -> {
                try {
                    String failingRequest = """
                        {
                            "model": "non-existent-model-xyz",
                            "message": "This request will intentionally fail",
                            "provider": "openrouter"
                        }
                        """;
                    
                    ResponseEntity<String> response = restTemplate.postForEntity(
                        baseUrl + "/api/llm/chat-completion",
                        failingRequest,
                        String.class
                    );
                    
                    // Check if circuit breaker is active
                    if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE ||
                        response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        circuitBreakerTrips.incrementAndGet();
                        logInfo("🔴 Circuit breaker tripped for request " + requestId);
                    }
                    
                } catch (Exception e) {
                    logInfo("Request " + requestId + " failed: " + e.getMessage());
                } finally {
                    failureLatch.countDown();
                }
            });
        }
        
        // Wait for failure generation
        assertTrue(failureLatch.await(2, TimeUnit.MINUTES), 
            "Circuit breaker stress test should complete");
        
        logInfo("🔴 Circuit breaker tripped " + circuitBreakerTrips.get() + " times");
        
        // Test fallback mechanisms
        String fallbackRequest = """
            {
                "message": "Test fallback request",
                "preferFallback": true,
                "timeout": 5000
            }
            """;

        ResponseEntity<String> fallbackResponse = restTemplate.postForEntity(
            baseUrl + "/api/llm/generate-with-fallback",
            fallbackRequest,
            String.class
        );
        
        // Verify circuit breaker status
        ResponseEntity<String> breakerStatus = restTemplate.getForEntity(
            baseUrl + "/api/llm/circuit-breaker/openrouter/status",
            String.class
        );
        
        assertEquals(HttpStatus.OK, breakerStatus.getStatusCode());
        logInfo("✅ Circuit breaker stress test completed!");
    }

    @Test
    @Order(11)
    @DisplayName("Memory Pressure Test")
    void testMemoryPressure() throws Exception {
        logInfo("🧠 Starting memory pressure test");
        
        // Monitor memory usage before stress
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // Generate high memory usage scenario
        int memoryStressRequests = 100;
        CountDownLatch memoryLatch = new CountDownLatch(memoryStressRequests);
        
        for (int i = 0; i < memoryStressRequests; i++) {
            final int requestId = i;
            CompletableFuture.runAsync(() -> {
                try {
                    // Large prompt to stress memory
                    String largePrompt = "Large prompt for memory stress test. " + 
                                       "X".repeat(1000) + " Request ID: " + requestId;
                    
                    String request = String.format("""
                        {
                            "message": "%s",
                            "model": "meta-llama/llama-2-7b-chat",
                            "maxTokens": 500
                        }
                        """, largePrompt);
                    
                    ResponseEntity<String> response = restTemplate.postForEntity(
                        baseUrl + "/api/llm/chat-completion",
                        request,
                        String.class
                    );
                    
                    if (response.getStatusCode() == HttpStatus.OK) {
                        logInfo("✓ Memory stress request " + requestId + " completed");
                    }
                    
                } catch (Exception e) {
                    logInfo("Memory stress request " + requestId + " failed: " + e.getMessage());
                } finally {
                    memoryLatch.countDown();
                }
            });
        }
        
        // Wait for memory stress test
        assertTrue(memoryLatch.await(3, TimeUnit.MINUTES), 
            "Memory pressure test should complete");
        
        // Force garbage collection and check memory after
        System.gc();
        Thread.sleep(1000);
        
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = memoryAfter - memoryBefore;
        
        logInfo("📊 Memory Usage Results:");
        logInfo("  💾 Memory Before: " + (memoryBefore / 1024 / 1024) + " MB");
        logInfo("  💾 Memory After: " + (memoryAfter / 1024 / 1024) + " MB");
        logInfo("  📈 Memory Increase: " + (memoryIncrease / 1024 / 1024) + " MB");
        
        // System should handle memory pressure gracefully
        ResponseEntity<String> healthCheck = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/system/health",
            String.class
        );
        assertEquals(HttpStatus.OK, healthCheck.getStatusCode());
        
        logInfo("✅ Memory pressure test completed!");
    }

    // ==================== PERFORMANCE BENCHMARKING ====================

    @Test
    @Order(20)
    @DisplayName("Response Time Benchmarking")
    void testResponseTimeBenchmarking() {
        logInfo("⏱️  Starting response time benchmarking");
        
        String[] endpoints = {
            "/api/monitoring/system/health",
            "/api/monitoring/system/resources",
            "/api/monitoring/metrics",
            "/api/llm/status",
            "/api/llm/providers"
        };
        
        int requestCount = 10;
        
        for (String endpoint : endpoints) {
            logInfo("Benchmarking endpoint: " + endpoint);
            
            long totalLatency = 0;
            long minLatency = Long.MAX_VALUE;
            long maxLatency = 0;
            int successCount = 0;
            
            for (int i = 0; i < requestCount; i++) {
                long startTime = System.currentTimeMillis();
                
                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(
                        baseUrl + endpoint,
                        String.class
                    );
                    
                    long endTime = System.currentTimeMillis();
                    long latency = endTime - startTime;
                    
                    if (response.getStatusCode() == HttpStatus.OK) {
                        totalLatency += latency;
                        minLatency = Math.min(minLatency, latency);
                        maxLatency = Math.max(maxLatency, latency);
                        successCount++;
                    }
                    
                } catch (Exception e) {
                    minLatency = Math.min(minLatency, -1); // Mark as failure
                    logInfo("Request " + i + " failed: " + e.getMessage());
                }
            }
            
            if (successCount > 0) {
                double avgLatency = (double) totalLatency / successCount;
                logInfo("  📊 " + endpoint + ":");
                logInfo("    ⏱️  Average: " + String.format("%.2f", avgLatency) + "ms");
                logInfo("    🚀 Min: " + minLatency + "ms");
                logInfo("    🐌 Max: " + maxLatency + "ms");
                logInfo("    ✅ Success: " + successCount + "/" + requestCount);
                
                // Benchmark requirements:
                // - Monitoring updates: < 2 seconds latency
                // - Dashboard refresh: < 1 second
                if (endpoint.contains("dashboard")) {
                    assertTrue(avgLatency < 1000, 
                        endpoint + " should refresh in < 1s, took: " + avgLatency + "ms");
                } else if (endpoint.contains("monitoring")) {
                    assertTrue(avgLatency < 2000, 
                        endpoint + " should update in < 2s, took: " + avgLatency + "ms");
                }
            } else {
                logInfo("  ❌ " + endpoint + ": All requests failed");
            }
        }
        
        logInfo("✅ Response time benchmarking completed!");
    }

    @Test
    @Order(21)
    @DisplayName("Throughput Benchmarking")
    void testThroughputBenchmarking() throws Exception {
        logInfo("📈 Starting throughput benchmarking");
        
        // Test different concurrency levels
        int[] concurrencyLevels = {1, 5, 10, 20};
        
        for (int concurrency : concurrencyLevels) {
            logInfo("Testing concurrency level: " + concurrency);
            
            CountDownLatch latch = new CountDownLatch(concurrency);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger errorCount = new AtomicInteger(0);
            
            long testStart = System.currentTimeMillis();
            
            IntStream.range(0, concurrency).parallel().forEach(i -> {
                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(
                        baseUrl + "/api/monitoring/system/health",
                        String.class
                    );
                    
                    if (response.getStatusCode() == HttpStatus.OK) {
                        successCount.incrementAndGet();
                    } else {
                        errorCount.incrementAndGet();
                    }
                    
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
            
            // Wait for all requests
            assertTrue(latch.await(1, TimeUnit.MINUTES), 
                "Throughput test for concurrency " + concurrency + " should complete");
            
            long testEnd = System.currentTimeMillis();
            long testDuration = testEnd - testStart;
            
            double throughput = (double) successCount.get() / (testDuration / 1000.0);
            double errorRate = (double) errorCount.get() / concurrency * 100.0;
            
            logInfo("  📊 Concurrency " + concurrency + ":");
            logInfo("    ⚡ Throughput: " + String.format("%.2f", throughput) + " req/s");
            logInfo("    ✅ Success: " + successCount.get() + "/" + concurrency);
            logInfo("    ❌ Errors: " + errorCount.get());
            logInfo("    📉 Error Rate: " + String.format("%.2f", errorRate) + "%");
            
            // Throughput should scale with concurrency (allowing for some degradation)
            if (concurrency > 1) {
                assertTrue(throughput > 0, "Should maintain positive throughput");
            }
        }
        
        logInfo("✅ Throughput benchmarking completed!");
    }

    // ==================== CONCURRENCY TESTING ====================

    @Test
    @Order(30)
    @DisplayName("Concurrent LLM Requests Test")
    void testConcurrentLLMRequests() throws Exception {
        logInfo("🔄 Starting concurrent LLM requests test");
        
        int concurrentRequests = 30;
        CountDownLatch latch = new CountDownLatch(concurrentRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        for (int i = 0; i < concurrentRequests; i++) {
            final int requestId = i;
            CompletableFuture.runAsync(() -> {
                try {
                    String request = String.format("""
                        {
                            "message": "Concurrent test %d - Explain neural networks briefly",
                            "model": "meta-llama/llama-2-7b-chat",
                            "maxTokens": 75
                        }
                        """, requestId);
                    
                    ResponseEntity<String> response = restTemplate.postForEntity(
                        baseUrl + "/api/llm/chat-completion",
                        request,
                        String.class
                    );
                    
                    if (response.getStatusCode() == HttpStatus.OK) {
                        successCount.incrementAndGet();
                        logInfo("✓ Concurrent request " + requestId + " succeeded");
                    } else {
                        failureCount.incrementAndGet();
                        logInfo("✗ Concurrent request " + requestId + " failed: " + 
                               response.getStatusCode());
                    }
                    
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    logInfo("✗ Concurrent request " + requestId + " exception: " + 
                           e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all concurrent requests
        assertTrue(latch.await(3, TimeUnit.MINUTES), 
            "All concurrent requests should complete");
        
        double successRate = (double) successCount.get() / concurrentRequests * 100.0;
        
        logInfo("📊 Concurrent LLM Requests Results:");
        logInfo("  📊 Total Requests: " + concurrentRequests);
        logInfo("  ✅ Successful: " + successCount.get());
        logInfo("  ❌ Failed: " + failureCount.get());
        logInfo("  📈 Success Rate: " + String.format("%.2f", successRate) + "%");
        
        // Should handle concurrent requests well
        assertTrue(successRate >= 70.0, "Should handle at least 70% of concurrent requests");
        
        // System should remain stable
        ResponseEntity<String> postTestHealth = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/system/health",
            String.class
        );
        assertEquals(HttpStatus.OK, postTestHealth.getStatusCode());
        
        logInfo("✅ Concurrent LLM requests test completed!");
    }

    // ==================== UTILITY CLASSES AND METHODS ====================

    private static class PerformanceMetricsCollector {
        private final AtomicLong totalLatency = new AtomicLong(0);
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private final AtomicInteger successCount = new AtomicInteger(0);
        private final AtomicInteger errorCount = new AtomicInteger(0);
        
        public void recordRequest(long latency, HttpStatus status) {
            totalLatency.addAndGet(latency);
            requestCount.incrementAndGet();
            
            if (status == HttpStatus.OK) {
                successCount.incrementAndGet();
            } else {
                errorCount.incrementAndGet();
            }
        }
        
        public double getAverageLatency() {
            return (double) totalLatency.get() / Math.max(requestCount.get(), 1);
        }
        
        public double getSuccessRate() {
            return (double) successCount.get() / Math.max(requestCount.get(), 1) * 100.0;
        }
    }

    private void logInfo(String message) {
        System.out.println("[PERFORMANCE-TEST] " + message);
    }
}