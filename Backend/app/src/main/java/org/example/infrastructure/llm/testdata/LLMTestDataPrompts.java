package org.example.infrastructure.llm.testdata;

import org.example.infrastructure.llm.testdata.dto.*;
import org.example.domain.model.testdata.enums.GenerationScope;
import org.example.domain.model.testdata.enums.DataType;
import org.example.domain.model.testdata.enums.DependencyType;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * LLM prompt builder for intelligent test data generation
 * Provides context-aware prompts for different test data types and scenarios
 */
@Service
public class LLMTestDataPrompts {
    
    // Base prompt templates for different data types
    private static final String BASE_GENERATION_PROMPT = """
        You are an expert test data generator. Generate realistic, high-quality test data for automated testing.
        
        Requirements:
        - Generate {recordCount} records of {dataType} data
        - Data must be realistic and business-appropriate
        - Include diverse, non-repetitive values
        - Ensure data consistency and referential integrity
        - Quality level: {qualityLevel}
        - Generation scope: {generationScope}
        {constraints}
        
        {businessContext}
        
        {openApiContext}
        {bpmnContext}
        {owaspContext}
        
        Return the data in a structured JSON format with the following structure:
        {formatExample}
        """;
    
    private static final String QUALITY_ANALYSIS_PROMPT = """
        Analyze the quality of the following test data and provide a detailed quality report.
        
        Generated Data:
        {generatedData}
        
        Analysis Criteria:
        1. Completeness (are all required fields populated?)
        2. Accuracy (are values realistic and correct?)
        3. Consistency (are related fields consistent?)
        4. Validity (do values meet business rules?)
        5. Uniqueness (are there duplicate issues?)
        6. Timeliness (are date values appropriate?)
        
        Provide a score from 0-100 and specific recommendations for improvement.
        """;
    
    private static final String VALIDATION_PROMPT = """
        Validate the following test data against the specified business rules and constraints.
        
        Test Data:
        {generatedData}
        
        Validation Rules:
        {validationRules}
        
        Check for:
        1. Field format compliance
        2. Business rule violations
        3. Data type consistency
        4. Referential integrity
        5. Constraint satisfaction
        
        Return validation results with specific violation details.
        """;
    
    // Business domain context templates
    private static final Map<String, String> BUSINESS_CONTEXTS = Map.of(
        "FINANCIAL", """
            Business Context: Financial Services
            - Generate realistic financial data (accounts, transactions, balances)
            - Use proper currency formats and valid account structures
            - Include compliance with financial regulations
            - Generate realistic credit scores, income levels, and risk profiles
            """,
        "ECOMMERCE", """
            Business Context: E-commerce Platform
            - Generate realistic customer and order data
            - Include valid product information, prices, and categories
            - Generate realistic shipping addresses and payment methods
            - Include order status progression and customer behavior patterns
            """,
        "HEALTHCARE", """
            Business Context: Healthcare System
            - Generate realistic patient and medical data
            - Use proper medical coding and terminology
            - Include realistic appointment scheduling and treatment data
            - Ensure HIPAA compliance and privacy considerations
            """,
        "BANKING", """
            Business Context: Banking System
            - Generate realistic banking data (accounts, transactions, customers)
            - Use proper banking formats and validation rules
            - Include regulatory compliance requirements
            - Generate realistic credit applications and loan data
            """
    );
    
    // OWASP security test data templates
    private static final Map<String, String> OWASP_CONTEXTS = Map.of(
        "SQL_INJECTION", """
            OWASP Context: SQL Injection Testing
            - Generate various SQL injection payloads
            - Include both simple and complex injection patterns
            - Add payload variations for different database types
            - Include time-based and boolean-based injection examples
            """,
        "XSS", """
            OWASP Context: Cross-Site Scripting Testing
            - Generate XSS payloads (reflected, stored, DOM-based)
            - Include various encoding and obfuscation techniques
            - Add payload variations for different contexts (HTML, JS, URL)
            - Include event handler and filter bypass examples
            """,
        "AUTH_BYPASS", """
            OWASP Context: Authentication Bypass Testing
            - Generate authentication bypass attempts
            - Include session management vulnerabilities
            - Add password brute force and credential stuffing examples
            - Include multi-factor authentication bypass attempts
            """,
        "DIR_TRAVERSAL", """
            OWASP Context: Directory Traversal Testing
            - Generate path traversal payloads
            - Include various encoding techniques
            - Add dot-dot-slash variations and encoding bypasses
            - Include null byte and Unicode normalization attacks
            """
    );
    
    // User profile context templates
    private static final Map<String, String> USER_PROFILES = Map.of(
        "CUSTOMER", """
            User Profile: End Customer
            - Generate typical customer data patterns
            - Include demographic information, preferences, and behavior
            - Use realistic names, addresses, and contact information
            - Generate typical customer journey data
            """,
        "BUSINESS_USER", """
            User Profile: Business User
            - Generate corporate and business-focused data
            - Include business addresses and organizational information
            - Use professional contact details and business patterns
            - Generate data appropriate for B2B scenarios
            """,
        "ADMIN", """
            User Profile: System Administrator
            - Generate administrative user data
            - Include high-privilege access patterns
            - Use secure but realistic admin credentials
            - Generate audit trail and administrative action data
            """,
        "ANONYMOUS", """
            User Profile: Anonymous User
            - Generate minimal or no identifying information
            - Include session-based and temporary data
            - Use generic or placeholder information
            - Generate public-facing data patterns
            """
    );
    
    /**
     * Builds the main generation prompt for test data creation
     */
    public String buildGenerationPrompt(TestDataGenerationRequest request) {
        // Add constraints
        String constraintsText = "";
        if (request.getConstraints() != null && !request.getConstraints().isEmpty()) {
            constraintsText = "Constraints:\n" + request.getConstraints().entrySet().stream()
                .map(entry -> "- " + entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));
        }
        
        // Add business context
        String businessContext = buildBusinessContext(request);
        
        // Add format example
        String formatExample = buildFormatExample(request);
        
        // Add OWASP context
        String owaspContext = buildOwaspContext(request);
        
        // Build final prompt
        String finalPrompt = BASE_GENERATION_PROMPT
            .replace("{recordCount}", String.valueOf(request.getRecordCount()))
            .replace("{dataType}", request.getDataType())
            .replace("{qualityLevel}", request.getQualityLevel())
            .replace("{generationScope}", request.getGenerationScope().toString())
            .replace("{constraints}", constraintsText)
            .replace("{businessContext}", businessContext)
            .replace("{formatExample}", formatExample)
            .replace("{openApiContext}", "")
            .replace("{bpmnContext}", "")
            .replace("{owaspContext}", owaspContext);
        
        return finalPrompt;
    }
    
    /**
     * Builds prompt for data quality analysis
     */
    public String buildQualityAnalysisPrompt(TestDataGenerationResult result) {
        return QUALITY_ANALYSIS_PROMPT
            .replace("{generatedData}", result.getGeneratedData());
    }
    
    /**
     * Builds prompt for data validation
     */
    public String buildValidationPrompt(TestDataGenerationResult result, List<String> validationRules) {
        String rules = validationRules != null ? 
            String.join("\n", validationRules) : "No specific rules provided";
        
        return VALIDATION_PROMPT
            .replace("{generatedData}", result.getGeneratedData())
            .replace("{validationRules}", rules);
    }
    
    /**
     * Builds context-aware generation prompt for OpenAPI integration
     */
    public String buildOpenApiPrompt(TestDataGenerationRequest request, String openApiSpec) {
        String basePrompt = buildGenerationPrompt(request);
        String openApiContext = """
            API Context: OpenAPI Specification
            OpenAPI Spec:
            {openApiSpec}
            
            Generate data that matches the API schema definitions.
            Ensure all required parameters are included.
            Use proper data types as defined in the schema.
            Include authentication tokens where required.
            """.replace("{openApiSpec}", openApiSpec);
        
        return basePrompt.replace("{openApiContext}", openApiContext);
    }
    
    /**
     * Builds context-aware generation prompt for BPMN integration
     */
    public String buildBpmnPrompt(TestDataGenerationRequest request, String bpmnProcess) {
        String basePrompt = buildGenerationPrompt(request);
        String bpmnContext = """
            Process Context: BPMN Business Process
            Process Definition:
            {bpmnProcess}
            
            Generate data that supports the business process workflow.
            Ensure data flow consistency across process steps.
            Include decision point data for gateways.
            Generate realistic process execution patterns.
            """.replace("{bpmnProcess}", bpmnProcess);
        
        return basePrompt.replace("{bpmnContext}", bpmnContext);
    }
    
    /**
     * Builds context-aware generation prompt for OWASP security testing
     */
    public String buildOwaspPrompt(TestDataGenerationRequest request, String owaspTestType) {
        String basePrompt = buildGenerationPrompt(request);
        String owaspContext = """
            Security Context: OWASP Testing
            Test Type: {owaspTestType}
            {owaspTestContext}
            
            Generate security test data for vulnerability assessment.
            Include both positive and negative test cases.
            Ensure data covers edge cases and attack vectors.
            """.replace("{owaspTestType}", owaspTestType)
            .replace("{owaspTestContext}", OWASP_CONTEXTS.getOrDefault(owaspTestType, ""));
        
        return basePrompt.replace("{owaspContext}", owaspContext);
    }
    
    /**
     * Builds prompt for dependency-aware data generation
     */
    public String buildDependencyPrompt(TestDataGenerationRequest request, Map<String, Object> existingData) {
        // Convert map to string representation
        String dataString = existingData.entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining(", "));
        
        String dependencyContext = """
            Dependency Context:
            Existing Data: {existingData}
            
            Generate new data that properly references existing data elements.
            Ensure referential integrity and data consistency.
            Maintain appropriate relationships between data entities.
            """.replace("{existingData}", dataString);
        
        String businessContext = buildBusinessContext(request) + "\n" + dependencyContext;
        
        return buildGenerationPrompt(request).replace("{businessContext}", businessContext);
    }
    
    // Helper methods
    private String buildBusinessContext(TestDataGenerationRequest request) {
        StringBuilder context = new StringBuilder();
        
        if (request.getBusinessDomain() != null) {
            String businessContext = BUSINESS_CONTEXTS.get(request.getBusinessDomain());
            if (businessContext != null) {
                context.append(businessContext);
            }
        }
        
        if (request.getUserProfile() != null) {
            String userContext = USER_PROFILES.get(request.getUserProfile());
            if (userContext != null) {
                context.append(userContext);
            }
        }
        
        if (request.getFinancialLimits() != null) {
            context.append("Financial Constraints: ").append(request.getFinancialLimits());
        }
        
        return context.toString().isEmpty() ? "" : "Business Context:\n" + context.toString();
    }
    
    private String buildOwaspContext(TestDataGenerationRequest request) {
        if (request.isSecurityTestData() && request.getOwaspTestType() != null) {
            return buildOwaspPrompt(request, request.getOwaspTestType())
                .replace(BASE_GENERATION_PROMPT, "")
                .replace("businessContext}", "securityContext}");
        }
        return "";
    }
    
    private String buildFormatExample(TestDataGenerationRequest request) {
        // Generate example format based on data type
        return switch (request.getDataType().toLowerCase()) {
            case "user", "customer", "person" -> """
                {
                  "records": [
                    {
                      "id": "string (unique identifier)",
                      "name": "string (realistic full name)",
                      "email": "string (valid email format)",
                      "phone": "string (realistic phone number)",
                      "address": "string (realistic address)",
                      "createdAt": "ISO 8601 timestamp"
                    }
                  ]
                }
                """;
            case "order", "transaction", "payment" -> """
                {
                  "records": [
                    {
                      "id": "string (unique order ID)",
                      "customerId": "string (reference to customer)",
                      "amount": "number (transaction amount)",
                      "currency": "string (currency code)",
                      "status": "string (valid status value)",
                      "timestamp": "ISO 8601 timestamp"
                    }
                  ]
                }
                """;
            default -> """
                {
                  "records": [
                    {
                      "id": "string (unique identifier)",
                      "value": "string (realistic test value)",
                      "metadata": "object (additional properties)",
                      "createdAt": "ISO 8601 timestamp"
                    }
                  ]
                }
                """;
        };
    }
    
    /**
     * Generates a context-aware data generation prompt for different integration scenarios
     */
    public String buildContextualPrompt(TestDataGenerationRequest request, String integrationType, Object contextData) {
        return switch (integrationType.toLowerCase()) {
            case "openapi" -> buildOpenApiPrompt(request, (String) contextData);
            case "bpmn" -> buildBpmnPrompt(request, (String) contextData);
            case "owasp" -> buildOwaspPrompt(request, (String) contextData);
            case "dependency" -> buildDependencyPrompt(request, (Map<String, Object>) contextData);
            default -> buildGenerationPrompt(request);
        };
    }
}