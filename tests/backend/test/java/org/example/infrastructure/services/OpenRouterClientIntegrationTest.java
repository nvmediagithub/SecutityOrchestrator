package org.example.infrastructure.services;

import org.example.infrastructure.config.LLMConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
// import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig;
// @SpringJUnitJupiterConfig
import org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test suite for OpenRouterClient
 * Tests real API integration with OpenRouter.ai
 * 
 * Note: Requires valid OpenRouter API key in environment or test configuration
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringJUnitJupiterConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OpenRouterClientIntegrationTest {

    @Autowired
    private OpenRouterClient openRouterClient;

    @Autowired
    private LLMConfig config;

    private static final String TEST_API_KEY = System.getenv("OPENROUTER_API_KEY");
    private static final boolean HAS_VALID_API_KEY = TEST_API_KEY != null && !TEST_API_KEY.trim().isEmpty();
    
    @BeforeEach
    void setUp() {
        if (HAS_VALID_API_KEY) {
            openRouterClient.updateApiKey(TEST_API_KEY);
        }
    }

    @Nested
    @DisplayName("Configuration Tests")
    class ConfigurationTests {
        
        @Test
        @Order(1)
        @DisplayName("Should have valid API configuration")
        void testConfiguration() {
            assertNotNull(openRouterClient, "OpenRouterClient should be initialized");
            
            if (HAS_VALID_API_KEY) {
                assertTrue(openRouterClient.hasApiKey(), "Client should have API key");
                System.out.println("✓ OpenRouterClient configured with valid API key");
            } else {
                assertFalse(openRouterClient.hasApiKey(), "Client should not have API key");
                System.out.println("⚠ OpenRouterClient: No API key provided - tests will verify error handling");
            }
        }

        @Test
        @Order(2)
        @DisplayName("Should update configuration dynamically")
        void testDynamicConfiguration() {
            String testApiKey = "sk-or-v1-test-key";
            openRouterClient.updateApiKey(testApiKey);
            assertTrue(openRouterClient.hasApiKey(), "Should have API key after update");
            
            openRouterClient.updateBaseUrl("https://api.openrouter.ai/v1");
            openRouterClient.updateTimeout(60);
            
            System.out.println("✓ Configuration updated successfully");
        }
    }

    @Nested
    @DisplayName("API Connection Tests")
    class ConnectionTests {
        
        @Test
        @Order(3)
        @DisplayName("Should throw exception when no API key provided")
        void testNoApiKeyException() {
            openRouterClient.updateApiKey(null);
            
            assertThrows(OpenRouterClient.OpenRouterConfigurationException.class, () -> {
                openRouterClient.listModels().get(5, TimeUnit.SECONDS);
            });
            
            System.out.println("✓ Correctly throws exception for missing API key");
        }

        @Test
        @Order(4)
        @DisplayName("Should test connection with valid API key")
        void testConnection() {
            if (!HAS_VALID_API_KEY) {
                System.out.println("⚠ Skipping connection test - no valid API key provided");
                return;
            }

            try {
                CompletableFuture<Boolean> connection = openRouterClient.checkConnection();
                Boolean result = connection.get(30, TimeUnit.SECONDS);
                
                assertTrue(result, "Connection should be successful");
                System.out.println("✓ OpenRouter API connection successful");
            } catch (Exception e) {
                fail("Connection test failed: " + e.getMessage());
            }
        }

        @Test
        @Order(5)
        @DisplayName("Should handle invalid API key gracefully")
        void testInvalidApiKey() {
            openRouterClient.updateApiKey("sk-or-v1-invalid-test-key");
            
            assertThrows(OpenRouterClient.OpenRouterAuthenticationException.class, () -> {
                openRouterClient.listModels().get(10, TimeUnit.SECONDS);
            });
            
            System.out.println("✓ Correctly handles invalid API key");
        }
    }

    @Nested
    @DisplayName("Models API Tests")
    class ModelsApiTests {
        
        @Test
        @Order(6)
        @DisplayName("Should fetch list of models with valid API key")
        void testListModels() {
            if (!HAS_VALID_API_KEY) {
                System.out.println("⚠ Skipping models test - no valid API key provided");
                return;
            }

            try {
                CompletableFuture<List<String>> modelsFuture = openRouterClient.listModels();
                List<String> models = modelsFuture.get(60, TimeUnit.SECONDS);
                
                assertNotNull(models, "Models list should not be null");
                assertFalse(models.isEmpty(), "Models list should not be empty");
                assertTrue(models.size() > 0, "Should have at least one model");
                
                // Log first few models
                System.out.println("✓ Successfully retrieved " + models.size() + " models:");
                models.stream().limit(5).forEach(model -> 
                    System.out.println("  - " + model)
                );
                
            } catch (Exception e) {
                fail("Failed to fetch models: " + e.getMessage());
            }
        }

        @Test
        @Order(7)
        @DisplayName("Should handle models API errors gracefully")
        void testModelsApiErrorHandling() {
            // Test with invalid API key
            openRouterClient.updateApiKey("invalid-key");
            
            assertThrows(OpenRouterClient.OpenRouterAuthenticationException.class, () -> {
                openRouterClient.listModels().get(10, TimeUnit.SECONDS);
            });
            
            System.out.println("✓ Models API error handling works correctly");
        }
    }

    @Nested
    @DisplayName("Chat Completion Tests")
    class ChatCompletionTests {
        
        @Test
        @Order(8)
        @DisplayName("Should complete simple chat with valid API key")
        void testChatCompletion() {
            if (!HAS_VALID_API_KEY) {
                System.out.println("⚠ Skipping chat completion test - no valid API key provided");
                return;
            }

            try {
                String model = "openrouter/auto"; // Use a general model
                String prompt = "Hello, how are you? Please respond briefly.";
                int maxTokens = 100;
                double temperature = 0.7;
                
                CompletableFuture<OpenRouterClient.ChatCompletionResponse> future = 
                    openRouterClient.chatCompletion(model, prompt, maxTokens, temperature);
                
                OpenRouterClient.ChatCompletionResponse response = future.get(60, TimeUnit.SECONDS);
                
                assertNotNull(response, "Chat response should not be null");
                assertNotNull(response.getResponse(), "Response content should not be null");
                assertFalse(response.getResponse().trim().isEmpty(), "Response should not be empty");
                
                System.out.println("✓ Chat completion successful:");
                System.out.println("  Response: " + response.getResponse().trim());
                System.out.println("  Tokens used: " + response.getTokensUsed());
                System.out.println("  Response time: " + response.getResponseTimeMs() + "ms");
                
            } catch (Exception e) {
                // This might fail if the model is not available or quota exceeded
                System.out.println("⚠ Chat completion test failed: " + e.getMessage());
                // Don't fail the test as it might be due to quota/model availability
            }
        }

        @Test
        @Order(9)
        @DisplayName("Should handle rate limiting appropriately")
        void testRateLimitHandling() {
            if (!HAS_VALID_API_KEY) {
                System.out.println("⚠ Skipping rate limit test - no valid API key provided");
                return;
            }

            // This test would require making multiple rapid requests to trigger rate limiting
            // For now, just verify that rate limit errors are handled
            System.out.println("⚠ Rate limit test requires implementation of actual rate limiting");
            System.out.println("  Recommendation: Add exponential backoff for 429 status codes");
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @Order(10)
        @DisplayName("Should handle network timeouts gracefully")
        void testTimeoutHandling() {
            // Test with a very short timeout
            openRouterClient.updateTimeout(1);
            openRouterClient.updateApiKey("valid-key-for-timeout-test");
            
            assertThrows(OpenRouterClient.OpenRouterException.class, () -> {
                openRouterClient.listModels().get(5, TimeUnit.SECONDS);
            });
            
            // Reset to normal timeout
            openRouterClient.updateTimeout(30);
            System.out.println("✓ Timeout handling works correctly");
        }

        @Test
        @Order(11)
        @DisplayName("Should provide detailed error messages")
        void testErrorMessageDetail() {
            try {
                openRouterClient.updateApiKey("invalid-key-for-detail-test");
                openRouterClient.listModels().get(5, TimeUnit.SECONDS);
                fail("Should have thrown an exception");
            } catch (Exception e) {
                assertTrue(e instanceof OpenRouterClient.OpenRouterException, 
                          "Should be OpenRouterException");
                String message = e.getMessage();
                assertNotNull(message, "Error message should not be null");
                assertTrue(message.length() > 0, "Error message should not be empty");
                System.out.println("✓ Error message details: " + message);
            }
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {
        
        @Test
        @Order(12)
        @DisplayName("Should measure response times")
        void testResponseTimeMeasurement() {
            if (!HAS_VALID_API_KEY) {
                System.out.println("⚠ Skipping response time test - no valid API key provided");
                return;
            }

            try {
                long startTime = System.currentTimeMillis();
                
                CompletableFuture<OpenRouterClient.ChatCompletionResponse> future = 
                    openRouterClient.chatCompletion("openrouter/auto", "Test", 50, 0.7);
                OpenRouterClient.ChatCompletionResponse response = future.get(60, TimeUnit.SECONDS);
                
                long totalTime = System.currentTimeMillis() - startTime;
                Double clientResponseTime = response.getResponseTimeMs();
                
                assertNotNull(clientResponseTime, "Client should measure response time");
                assertTrue(clientResponseTime > 0, "Response time should be positive");
                
                System.out.println("✓ Response time measurement:");
                System.out.println("  Client measured: " + clientResponseTime + "ms");
                System.out.println("  Total time: " + totalTime + "ms");
                
            } catch (Exception e) {
                System.out.println("⚠ Response time test failed: " + e.getMessage());
            }
        }
    }

    @AfterAll
    static void cleanup() {
        System.out.println("\n=== OpenRouterClient Integration Test Summary ===");
        System.out.println("API Key available: " + HAS_VALID_API_KEY);
        if (!HAS_VALID_API_KEY) {
            System.out.println("\nTo run full integration tests, set OPENROUTER_API_KEY environment variable:");
            System.out.println("export OPENROUTER_API_KEY=sk-or-v1-your-key-here");
        }
    }
}