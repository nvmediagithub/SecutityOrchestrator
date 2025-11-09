package org.example.infrastructure.services;

import org.example.domain.entities.SecurityAnalysisResult;
import org.example.domain.entities.SecurityTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * LLM Security Analysis Service - integrates with CodeLlama for OWASP API Security analysis
 */
@Service
public class LLMSecurityAnalysisService {
    
    private static final String CODE_LLAMA_ENDPOINT = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "codellama:7b";
    
    @Autowired
    private RestTemplate restTemplate;
    
    private final Map<String, List<String>> owaspTestTemplates;
    private final Map<String, String> securityPatterns;
    
    public LLMSecurityAnalysisService() {
        this.owaspTestTemplates = initializeOWASPTemplates();
        this.securityPatterns = initializeSecurityPatterns();
    }
    
    /**
     * Analyze API endpoint for OWASP Security vulnerabilities using LLM
     */
    public CompletableFuture<SecurityAnalysisResult> analyzeSecurityVulnerability(
            String endpointDescription,
            String httpMethod,
            String requestBody,
            String responseBody) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                SecurityAnalysisResult result = new SecurityAnalysisResult(endpointDescription, null);
                result.setAnalysisType("LLM_Security_Analysis");
                result.setLlmModel(MODEL_NAME);
                
                // Generate security analysis prompt
                String prompt = generateSecurityAnalysisPrompt(endpointDescription, httpMethod, requestBody, responseBody);
                
                // Call CodeLlama for analysis
                String llmResponse = callCodeLlama(prompt);
                
                // Parse LLM response
                SecurityAnalysisResult parsedResult = parseLLMResponse(llmResponse, result);
                
                // Generate security tests
                List<SecurityTest> tests = generateSecurityTests(parsedResult);
                parsedResult.setGeneratedTests(tests);
                
                // Calculate confidence score
                parsedResult.setConfidenceScore(calculateConfidenceScore(parsedResult));
                
                return parsedResult;
                
            } catch (Exception e) {
                SecurityAnalysisResult errorResult = new SecurityAnalysisResult(endpointDescription, null);
                errorResult.setDescription("Error in LLM analysis: " + e.getMessage());
                errorResult.setSeverity("UNKNOWN");
                return errorResult;
            }
        });
    }
    
    /**
     * Generate OWASP API Security Top 10 tests using LLM
     */
    public CompletableFuture<List<SecurityTest>> generateOWASPTests(String apiSpecification) {
        return CompletableFuture.supplyAsync(() -> {
            List<SecurityTest> tests = new ArrayList<>();
            
            // Generate tests for each OWASP category
            for (SecurityAnalysisResult.OWASPCategory category : SecurityAnalysisResult.OWASPCategory.values()) {
                try {
                    String testPrompt = generateOWASPCategoryPrompt(apiSpecification, category);
                    String llmResponse = callCodeLlama(testPrompt);
                    List<SecurityTest> categoryTests = parseOWASPTestsFromLLM(llmResponse, category);
                    tests.addAll(categoryTests);
                } catch (Exception e) {
                    System.err.println("Error generating tests for category " + category + ": " + e.getMessage());
                }
            }
            
            return tests;
        });
    }
    
    /**
     * Analyze text description for security issues using pattern matching and LLM
     */
    public CompletableFuture<List<SecurityAnalysisResult>> analyzeTextDescription(String description) {
        return CompletableFuture.supplyAsync(() -> {
            List<SecurityAnalysisResult> results = new ArrayList<>();
            
            // Pattern-based analysis
            List<SecurityAnalysisResult> patternResults = performPatternBasedAnalysis(description);
            results.addAll(patternResults);
            
            // LLM-based deep analysis
            String llmPrompt = generateTextAnalysisPrompt(description);
            try {
                String llmResponse = callCodeLlama(llmPrompt);
                List<SecurityAnalysisResult> llmResults = parseTextAnalysisFromLLM(llmResponse);
                results.addAll(llmResults);
            } catch (Exception e) {
                System.err.println("Error in LLM text analysis: " + e.getMessage());
            }
            
            return results;
        });
    }
    
    private String generateSecurityAnalysisPrompt(String endpoint, String method, String request, String response) {
        return String.format("""
            Analyze the following API endpoint for OWASP API Security Top 10 vulnerabilities:
            
            Endpoint: %s
            HTTP Method: %s
            Request Body: %s
            Response Body: %s
            
            Provide analysis in JSON format:
            {
                "category": "OWASP_CATEGORY_NAME",
                "severity": "LOW|MEDIUM|HIGH|CRITICAL",
                "description": "detailed vulnerability description",
                "recommendation": "specific remediation steps",
                "codeSnippet": "example vulnerable code",
                "confidence": 0.95
            }
            
            Focus on: Authorization flaws, Authentication weaknesses, Data exposure, 
            Rate limiting issues, Function level authorization, Mass assignment, 
            Security misconfigurations, Injection attacks, Improper asset management, 
            and Insufficient logging.
            """, endpoint, method, request, response);
    }
    
    private String callCodeLlama(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL_NAME);
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            requestBody.put("options", Map.of("temperature", 0.1, "top_p", 0.9));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                CODE_LLAMA_ENDPOINT,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            return extractTextFromResponse(response.getBody());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to call CodeLlama: " + e.getMessage(), e);
        }
    }
    
    private String extractTextFromResponse(String responseBody) {
        try {
            // Parse Ollama response format
            org.json.JSONObject jsonResponse = new org.json.JSONObject(responseBody);
            return jsonResponse.optString("response", "");
        } catch (Exception e) {
            return responseBody;
        }
    }
    
    private SecurityAnalysisResult parseLLMResponse(String llmResponse, SecurityAnalysisResult result) {
        try {
            // Simple parsing - in production, use more robust JSON parsing
            if (llmResponse.contains("API1") || llmResponse.contains("Object Level Authorization")) {
                result.setOwaspCategory(SecurityAnalysisResult.OWASPCategory.API1_BROKEN_OBJECT_LEVEL_AUTHORIZATION);
            } else if (llmResponse.contains("API2") || llmResponse.contains("Authentication")) {
                result.setOwaspCategory(SecurityAnalysisResult.OWASPCategory.API2_BROKEN_USER_AUTHENTICATION);
            } else if (llmResponse.contains("API3") || llmResponse.contains("Data Exposure")) {
                result.setOwaspCategory(SecurityAnalysisResult.OWASPCategory.API3_EXCESSIVE_DATA_EXPOSURE);
            } else if (llmResponse.contains("API4") || llmResponse.contains("Rate Limiting")) {
                result.setOwaspCategory(SecurityAnalysisResult.OWASPCategory.API4_LACK_OF_RESOURCES_AND_RATE_LIMITING);
            } else if (llmResponse.contains("API5") || llmResponse.contains("Function Level")) {
                result.setOwaspCategory(SecurityAnalysisResult.OWASPCategory.API5_BROKEN_FUNCTION_LEVEL_AUTHORIZATION);
            } else if (llmResponse.contains("API6") || llmResponse.contains("Mass Assignment")) {
                result.setOwaspCategory(SecurityAnalysisResult.OWASPCategory.API6_MASS_ASSIGNMENT);
            } else if (llmResponse.contains("API7") || llmResponse.contains("Misconfiguration")) {
                result.setOwaspCategory(SecurityAnalysisResult.OWASPCategory.API7_SECURITY_MISCONFIGURATION);
            } else if (llmResponse.contains("API8") || llmResponse.contains("Injection")) {
                result.setOwaspCategory(SecurityAnalysisResult.OWASPCategory.API8_INJECTION);
            } else if (llmResponse.contains("API9") || llmResponse.contains("Asset Management")) {
                result.setOwaspCategory(SecurityAnalysisResult.OWASPCategory.API9_IMPROPER_ASSETS_MANAGEMENT);
            } else if (llmResponse.contains("API10") || llmResponse.contains("Logging")) {
                result.setOwaspCategory(SecurityAnalysisResult.OWASPCategory.API10_INSUFFICIENT_LOGGING_AND_MONITORING);
            }
            
            // Extract severity
            if (llmResponse.contains("CRITICAL")) {
                result.setSeverity("CRITICAL");
            } else if (llmResponse.contains("HIGH")) {
                result.setSeverity("HIGH");
            } else if (llmResponse.contains("MEDIUM")) {
                result.setSeverity("MEDIUM");
            } else {
                result.setSeverity("LOW");
            }
            
            result.setDescription(llmResponse);
            result.setRecommendation("Implement security controls based on OWASP guidelines");
            
        } catch (Exception e) {
            result.setDescription("Error parsing LLM response: " + e.getMessage());
        }
        
        return result;
    }
    
    private List<SecurityTest> generateSecurityTests(SecurityAnalysisResult result) {
        List<SecurityTest> tests = new ArrayList<>();
        
        if (result.getOwaspCategory() != null) {
            String categoryName = result.getOwaspCategory().name();
            List<String> testTemplates = getTestTemplatesForCategory(result.getOwaspCategory());
            
            for (String template : testTemplates) {
                SecurityTest test = new SecurityTest(
                    categoryName + " Test",
                    "AUTOMATED",
                    template
                );
                test.setCategory(categoryName);
                test.setDescription("Generated test for " + categoryName);
                tests.add(test);
            }
        }
        
        return tests;
    }
    
    private double calculateConfidenceScore(SecurityAnalysisResult result) {
        double score = 0.5; // Base score
        
        // Adjust based on severity
        switch (result.getSeverity()) {
            case "CRITICAL": score += 0.3; break;
            case "HIGH": score += 0.2; break;
            case "MEDIUM": score += 0.1; break;
            default: break;
        }
        
        // Adjust based on category
        if (result.getOwaspCategory() != null) {
            score += 0.2;
        }
        
        return Math.min(score, 1.0);
    }
    
    private List<SecurityTest> parseOWASPTestsFromLLM(String llmResponse, SecurityAnalysisResult.OWASPCategory category) {
        List<SecurityTest> tests = new ArrayList<>();
        // Implementation for parsing OWASP-specific tests
        return tests;
    }
    
    private List<SecurityAnalysisResult> performPatternBasedAnalysis(String description) {
        List<SecurityAnalysisResult> results = new ArrayList<>();
        
        // Pattern matching for common security issues
        for (Map.Entry<String, String> pattern : securityPatterns.entrySet()) {
            if (description.toLowerCase().contains(pattern.getKey().toLowerCase())) {
                SecurityAnalysisResult result = new SecurityAnalysisResult(description, null);
                result.setDescription(pattern.getValue());
                result.setSeverity("MEDIUM");
                result.setAnalysisType("Pattern_Based_Analysis");
                results.add(result);
            }
        }
        
        return results;
    }
    
    private String generateTextAnalysisPrompt(String description) {
        return String.format("""
            Analyze the following API description for potential security vulnerabilities:
            
            Description: %s
            
            Identify:
            1. Authentication weaknesses
            2. Authorization flaws
            3. Data exposure risks
            4. Input validation issues
            5. Security configuration problems
            
            Provide specific findings with severity levels.
            """, description);
    }
    
    private List<SecurityAnalysisResult> parseTextAnalysisFromLLM(String llmResponse) {
        List<SecurityAnalysisResult> results = new ArrayList<>();
        // Implementation for parsing text analysis results
        return results;
    }
    
    private String generateOWASPCategoryPrompt(String apiSpec, SecurityAnalysisResult.OWASPCategory category) {
        return String.format("""
            Generate security tests for OWASP API Security category: %s
            
            API Specification: %s
            
            Create specific test cases with payloads and expected results.
            """, category, apiSpec);
    }
    
    private List<String> getTestTemplatesForCategory(SecurityAnalysisResult.OWASPCategory category) {
        return owaspTestTemplates.getOrDefault(category.name(), Arrays.asList("default test"));
    }
    
    private Map<String, List<String>> initializeOWASPTemplates() {
        Map<String, List<String>> templates = new HashMap<>();
        
        templates.put("API1_BROKEN_OBJECT_LEVEL_AUTHORIZATION", Arrays.asList(
            "GET /api/users/{id} - Test with different user IDs",
            "POST /api/orders/{orderId}/cancel - Test unauthorized cancellation"
        ));
        
        templates.put("API2_BROKEN_USER_AUTHENTICATION", Arrays.asList(
            "POST /api/login - Test weak password policies",
            "GET /api/profile - Test without authentication tokens"
        ));
        
        templates.put("API3_EXCESSIVE_DATA_EXPOSURE", Arrays.asList(
            "GET /api/user/{id} - Check for sensitive data in response",
            "POST /api/search - Test response data exposure"
        ));
        
        // Add more templates for other categories...
        
        return templates;
    }
    
    private Map<String, String> initializeSecurityPatterns() {
        Map<String, String> patterns = new HashMap<>();
        
        patterns.put("password", "Potential password storage/handling issue");
        patterns.put("admin", "Administrative access pattern detected");
        patterns.put("api_key", "API key exposure risk");
        patterns.put("token", "Token management vulnerability");
        patterns.put("auth", "Authentication mechanism in use");
        
        return patterns;
    }
}