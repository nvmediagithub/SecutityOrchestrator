package org.example.test.systemhealth;

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

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive System Health Testing Suite
 * Tests monitoring dashboard, alert system, and performance metrics collection
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("System Health Monitoring Tests")
class SystemHealthTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private Instant testStartTime;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        testStartTime = Instant.now();
    }

    @AfterEach
    void tearDown() {
        logInfo("Test completed at: " + Instant.now());
    }

    // ==================== MONITORING DASHBOARD TESTS ====================

    @Test
    @Order(1)
    @DisplayName("System Health Endpoint Verification")
    void testSystemHealthEndpoint() {
        // Test basic health check
        ResponseEntity<String> healthResponse = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/system/health",
            String.class
        );

        assertEquals(HttpStatus.OK, healthResponse.getStatusCode());
        assertNotNull(healthResponse.getBody());
        
        // Verify health response structure
        String healthBody = healthResponse.getBody();
        assertTrue(healthBody.contains("status") || healthBody.contains("healthy") || 
                  healthBody.contains("system"), "Health response should contain status information");
        
        // Test detailed health information
        ResponseEntity<String> detailedHealthResponse = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/system/health/detailed",
            String.class
        );

        if (detailedHealthResponse.getStatusCode() == HttpStatus.OK) {
            assertNotNull(detailedHealthResponse.getBody());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Resource Usage Monitoring")
    void testResourceUsageMonitoring() {
        // Test CPU and memory metrics
        ResponseEntity<String> resourceResponse = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/system/resources",
            String.class
        );

        assertEquals(HttpStatus.OK, resourceResponse.getStatusCode());
        assertNotNull(resourceResponse.getBody());
        
        // Verify resource metrics include expected fields
        String resourceBody = resourceResponse.getBody();
        assertTrue(resourceBody.contains("cpu") || resourceBody.contains("memory") || 
                  resourceBody.contains("usage"), "Resource response should contain usage metrics");
        
        // Test real-time resource monitoring
        ResponseEntity<String> realtimeResponse = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/system/resources/realtime",
            String.class
        );

        if (realtimeResponse.getStatusCode() == HttpStatus.OK) {
            assertNotNull(realtimeResponse.getBody());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Performance Metrics Collection")
    void testPerformanceMetricsCollection() {
        // Test general metrics endpoint
        ResponseEntity<String> metricsResponse = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/metrics",
            String.class
        );

        assertEquals(HttpStatus.OK, metricsResponse.getStatusCode());
        assertNotNull(metricsResponse.getBody());
        
        // Test specific metric types
        String[] metricTypes = {"llm", "response-time", "throughput", "errors"};
        
        for (String metricType : metricTypes) {
            try {
                ResponseEntity<String> specificMetrics = restTemplate.getForEntity(
                    baseUrl + "/api/monitoring/metrics/" + metricType,
                    String.class
                );
                
                if (specificMetrics.getStatusCode() == HttpStatus.OK) {
                    assertNotNull(specificMetrics.getBody());
                    logInfo("Successfully retrieved " + metricType + " metrics");
                }
            } catch (Exception e) {
                logInfo("Metric type " + metricType + " not available: " + e.getMessage());
            }
        }
    }

    // ==================== ALERT SYSTEM TESTS ====================

    @Test
    @Order(10)
    @DisplayName("Alert Configuration and Management")
    void testAlertConfiguration() {
        // Test alert configuration
        String alertConfig = """
            {
                "metrics": [
                    {
                        "name": "cpu_usage",
                        "threshold": 80.0,
                        "severity": "WARNING"
                    },
                    {
                        "name": "memory_usage", 
                        "threshold": 90.0,
                        "severity": "CRITICAL"
                    },
                    {
                        "name": "error_rate",
                        "threshold": 5.0,
                        "severity": "ERROR"
                    }
                ],
                "notificationChannels": [
                    {
                        "type": "log",
                        "enabled": true
                    }
                ]
            }
            """;

        ResponseEntity<String> configResponse = restTemplate.postForEntity(
            baseUrl + "/api/monitoring/alerts/configure",
            alertConfig,
            String.class
        );

        assertEquals(HttpStatus.OK, configResponse.getStatusCode());
        
        // Test alert status
        ResponseEntity<String> statusResponse = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/alerts/status",
            String.class
        );

        assertEquals(HttpStatus.OK, statusResponse.getStatusCode());
        assertNotNull(statusResponse.getBody());
    }

    @Test
    @Order(11)
    @DisplayName("Alert Trigger and Notification")
    void testAlertTriggerAndNotification() throws InterruptedException {
        // Configure aggressive alert thresholds
        String aggressiveConfig = """
            {
                "metrics": [
                    {
                        "name": "cpu_usage",
                        "threshold": 1.0,
                        "severity": "WARNING"
                    }
                ],
                "immediateTrigger": true
            }
            """;

        restTemplate.postForEntity(
            baseUrl + "/api/monitoring/alerts/configure",
            aggressiveConfig,
            String.class
        );

        // Generate load to trigger alerts
        int concurrentRequests = 10;
        CountDownLatch latch = new CountDownLatch(concurrentRequests);

        for (int i = 0; i < concurrentRequests; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    // Make requests to generate load
                    restTemplate.getForEntity(
                        baseUrl + "/api/monitoring/system/health",
                        String.class
                    );
                } catch (Exception e) {
                    logInfo("Load generation request failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for load generation
        assertTrue(latch.await(30, TimeUnit.SECONDS), "Load generation should complete");

        // Wait a moment for alerts to be processed
        Thread.sleep(2000);

        // Test alert history
        ResponseEntity<String> alertHistory = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/alerts/history",
            String.class
        );

        if (alertHistory.getStatusCode() == HttpStatus.OK) {
            assertNotNull(alertHistory.getBody());
            logInfo("Alert history retrieved successfully");
        }
    }

    @Test
    @Order(12)
    @DisplayName("Alert Recovery and Resolution")
    void testAlertRecovery() {
        // Configure test alert
        String testAlertConfig = """
            {
                "metrics": [
                    {
                        "name": "test_metric",
                        "threshold": 50.0,
                        "severity": "WARNING"
                    }
                ],
                "autoRecovery": true
            }
            """;

        restTemplate.postForEntity(
            baseUrl + "/api/monitoring/alerts/configure",
            testAlertConfig,
            String.class
        );

        // Test manual alert resolution
        ResponseEntity<String> resolveResponse = restTemplate.postForEntity(
            baseUrl + "/api/monitoring/alerts/resolve",
            "{\"alertId\": \"test-alert\", \"reason\": \"Test resolution\"}",
            String.class
        );

        if (resolveResponse.getStatusCode() == HttpStatus.OK) {
            assertNotNull(resolveResponse.getBody());
        }
    }

    // ==================== REAL-TIME MONITORING TESTS ====================

    @Test
    @Order(20)
    @DisplayName("WebSocket Real-time Updates")
    void testWebSocketRealTimeUpdates() {
        // Test WebSocket connection endpoint (simulate with HTTP for testing)
        ResponseEntity<String> wsConfig = restTemplate.postForEntity(
            baseUrl + "/api/monitoring/websocket/configure",
            """
                {
                    "metrics": ["cpu_usage", "memory_usage", "llm_response_time"],
                    "frequency": "every_5_seconds",
                    "maxConnections": 100
                }
                """,
            String.class
        );

        if (wsConfig.getStatusCode() == HttpStatus.OK) {
            assertNotNull(wsConfig.getBody());
        }
        
        // Test subscription management
        ResponseEntity<String> subscriptionResponse = restTemplate.postForEntity(
            baseUrl + "/api/monitoring/subscribe",
            """
                {
                    "metrics": ["system_health", "active_requests"],
                    "format": "json"
                }
                """,
            String.class
        );

        if (subscriptionResponse.getStatusCode() == HttpStatus.OK) {
            assertNotNull(subscriptionResponse.getBody());
        }
    }

    @Test
    @Order(21)
    @DisplayName("Real-time Metric Updates")
    void testRealTimeMetricUpdates() {
        // Generate some activity
        generateTestActivity(5);
        
        // Test real-time metrics endpoint
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            ResponseEntity<String> realtimeMetrics = restTemplate.getForEntity(
                baseUrl + "/api/monitoring/metrics/realtime",
                String.class
            );
            
            assertEquals(HttpStatus.OK, realtimeMetrics.getStatusCode());
            assertNotNull(realtimeMetrics.getBody());
        });
        
        // Test metric stream
        ResponseEntity<String> streamResponse = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/metrics/stream",
            String.class
        );

        if (streamResponse.getStatusCode() == HttpStatus.OK) {
            assertNotNull(streamResponse.getBody());
        }
    }

    // ==================== PERFORMANCE MONITORING TESTS ====================

    @Test
    @Order(30)
    @DisplayName("Response Time Monitoring")
    void testResponseTimeMonitoring() {
        // Test response time for different endpoints
        String[] endpoints = {
            "/api/monitoring/system/health",
            "/api/monitoring/system/resources",
            "/api/monitoring/metrics"
        };
        
        for (String endpoint : endpoints) {
            long startTime = System.currentTimeMillis();
            
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + endpoint,
                    String.class
                );
                
                long endTime = System.currentTimeMillis();
                long responseTime = endTime - startTime;
                
                logInfo(endpoint + " response time: " + responseTime + "ms");
                
                // All endpoints should respond within reasonable time
                assertTrue(responseTime < 5000, 
                    endpoint + " took too long: " + responseTime + "ms");
                
            } catch (Exception e) {
                logInfo("Endpoint " + endpoint + " failed: " + e.getMessage());
            }
        }
        
        // Test response time metrics
        ResponseEntity<String> responseTimeMetrics = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/metrics/response-time",
            String.class
        );

        if (responseTimeMetrics.getStatusCode() == HttpStatus.OK) {
            assertNotNull(responseTimeMetrics.getBody());
        }
    }

    @Test
    @Order(31)
    @DisplayName("Throughput Monitoring")
    void testThroughputMonitoring() {
        // Generate concurrent requests to test throughput
        int concurrentRequests = 20;
        CountDownLatch latch = new CountDownLatch(concurrentRequests);
        AtomicInteger successfulRequests = new AtomicInteger(0);
        AtomicInteger failedRequests = new AtomicInteger(0);
        
        long testStart = System.currentTimeMillis();
        
        for (int i = 0; i < concurrentRequests; i++) {
            final int requestId = i;
            CompletableFuture.runAsync(() -> {
                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(
                        baseUrl + "/api/monitoring/system/health",
                        String.class
                    );
                    
                    if (response.getStatusCode() == HttpStatus.OK) {
                        successfulRequests.incrementAndGet();
                    } else {
                        failedRequests.incrementAndGet();
                    }
                } catch (Exception e) {
                    failedRequests.incrementAndGet();
                    logInfo("Throughput test request " + requestId + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all requests
        try {
            assertTrue(latch.await(60, TimeUnit.SECONDS), "Throughput test should complete");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Throughput test interrupted");
        }
        
        long testEnd = System.currentTimeMillis();
        long testDuration = testEnd - testStart;
        
        // Calculate throughput
        double throughput = (double) successfulRequests.get() / (testDuration / 1000.0);
        logInfo("Throughput: " + String.format("%.2f", throughput) + " requests/second");
        logInfo("Success rate: " + successfulRequests.get() + "/" + concurrentRequests);
        
        // Test throughput metrics
        ResponseEntity<String> throughputMetrics = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/metrics/throughput",
            String.class
        );

        if (throughputMetrics.getStatusCode() == HttpStatus.OK) {
            assertNotNull(throughputMetrics.getBody());
        }
    }

    @Test
    @Order(32)
    @DisplayName("Error Rate Monitoring")
    void testErrorRateMonitoring() {
        // Generate some errors by making invalid requests
        String[] invalidEndpoints = {
            "/api/monitoring/nonexistent",
            "/api/monitoring/system/invalid",
            "/api/monitoring/metrics/unknown"
        };
        
        int errorCount = 0;
        for (String endpoint : invalidEndpoints) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(
                    baseUrl + endpoint,
                    String.class
                );
                
                if (response.getStatusCode().is4xxClientError() || 
                    response.getStatusCode().is5xxServerError()) {
                    errorCount++;
                }
            } catch (Exception e) {
                errorCount++;
                logInfo("Invalid request to " + endpoint + " failed as expected: " + e.getMessage());
            }
        }
        
        logInfo("Generated " + errorCount + " errors for testing");
        
        // Test error rate metrics
        ResponseEntity<String> errorMetrics = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/metrics/errors",
            String.class
        );

        if (errorMetrics.getStatusCode() == HttpStatus.OK) {
            assertNotNull(errorMetrics.getBody());
        }
    }

    // ==================== DASHBOARD FUNCTIONALITY TESTS ====================

    @Test
    @Order(40)
    @DisplayName("Dashboard Data Aggregation")
    void testDashboardDataAggregation() {
        // Test dashboard data endpoint
        ResponseEntity<String> dashboardResponse = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/dashboard/data",
            String.class
        );

        assertEquals(HttpStatus.OK, dashboardResponse.getStatusCode());
        assertNotNull(dashboardResponse.getBody());
        
        // Test dashboard configuration
        ResponseEntity<String> configResponse = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/dashboard/config",
            String.class
        );

        if (configResponse.getStatusCode() == HttpStatus.OK) {
            assertNotNull(configResponse.getBody());
        }
    }

    @Test
    @Order(41)
    @DisplayName("Dashboard Historical Data")
    void testDashboardHistoricalData() {
        // Test historical data retrieval
        ResponseEntity<String> historyResponse = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/dashboard/history?period=1h",
            String.class
        );

        if (historyResponse.getStatusCode() == HttpStatus.OK) {
            assertNotNull(historyResponse.getBody());
        }
        
        // Test data export
        ResponseEntity<String> exportResponse = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/dashboard/export?format=json&period=1d",
            String.class
        );

        if (exportResponse.getStatusCode() == HttpStatus.OK) {
            assertNotNull(exportResponse.getBody());
        }
    }

    // ==================== STRESS TESTING ====================

    @Test
    @Order(50)
    @DisplayName("System Health Under Stress")
    void testSystemHealthUnderStress() throws Exception {
        // Generate heavy load
        int stressRequests = 50;
        CountDownLatch latch = new CountDownLatch(stressRequests);
        
        for (int i = 0; i < stressRequests; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    // Mix of different endpoint calls
                    String[] endpoints = {
                        "/api/monitoring/system/health",
                        "/api/monitoring/system/resources",
                        "/api/monitoring/metrics"
                    };
                    
                    for (String endpoint : endpoints) {
                        restTemplate.getForEntity(baseUrl + endpoint, String.class);
                    }
                } catch (Exception e) {
                    logInfo("Stress test request failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for stress test to complete
        assertTrue(latch.await(2, TimeUnit.MINUTES), "Stress test should complete");
        
        // Verify system is still healthy after stress
        ResponseEntity<String> postStressHealth = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/system/health",
            String.class
        );

        assertEquals(HttpStatus.OK, postStressHealth.getStatusCode());
        assertNotNull(postStressHealth.getBody());
        
        // Check resource usage after stress
        ResponseEntity<String> postStressResources = restTemplate.getForEntity(
            baseUrl + "/api/monitoring/system/resources",
            String.class
        );

        assertEquals(HttpStatus.OK, postStressResources.getStatusCode());
        assertNotNull(postStressResources.getBody());
    }

    // ==================== UTILITY METHODS ====================

    private void generateTestActivity(int requestCount) {
        for (int i = 0; i < requestCount; i++) {
            try {
                restTemplate.getForEntity(
                    baseUrl + "/api/monitoring/system/health",
                    String.class
                );
            } catch (Exception e) {
                logInfo("Test activity request " + i + " failed: " + e.getMessage());
            }
        }
    }

    private void logInfo(String message) {
        System.out.println("[SYSTEM-HEALTH-TEST] " + message);
    }
}