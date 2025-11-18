package org.example.infrastructure.services.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.model.testdata.ContextAwareData;
import org.example.domain.model.testdata.DataDependency;
import org.example.domain.model.testdata.DataFlowChain;
import org.example.domain.model.testdata.enums.DataType;
import org.example.domain.model.testdata.enums.DependencyType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Service for generating context-aware test data considering dependencies between API steps
 * Integrates with BPMN processes to understand data flow and dependencies
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestDataGenerator {

    /**
     * Generate context-aware test data considering BPMN workflow dependencies
     */
    public Map<String, Object> generateContextAwareData(String testCaseId, String bpmnId, Map<String, Object> constraints) {
        log.info("Generating context-aware test data for test case: {} with BPMN: {}", testCaseId, bpmnId);
        
        Map<String, Object> testData = new HashMap<>();
        
        try {
            // Analyze data dependencies based on BPMN process
            List<DataDependency> dependencies = analyzeDataDependencies(bpmnId);
            
            // Generate data for each step considering dependencies
            Map<String, Object> stepData = generateStepData(testCaseId, bpmnId, dependencies, constraints);
            
            // Create data flow chain
            DataFlowChain dataFlowChain = createDataFlowChain(dependencies, stepData);
            
            testData.put("testCaseId", testCaseId);
            testData.put("bpmnId", bpmnId);
            testData.put("stepData", stepData);
            testData.put("dataFlowChain", dataFlowChain);
            testData.put("dependencies", dependencies);
            testData.put("constraints", constraints);
            testData.put("generatedAt", LocalDateTime.now());
            
            log.info("Generated context-aware test data for test case: {}", testCaseId);
            return testData;
            
        } catch (Exception e) {
            log.error("Error generating test data", e);
            throw new RuntimeException("Failed to generate test data", e);
        }
    }

    /**
     * Analyze data dependencies from BPMN process
     */
    public List<DataDependency> analyzeDataDependencies(String bpmnId) {
        log.info("Analyzing data dependencies for BPMN: {}", bpmnId);
        
        List<DataDependency> dependencies = new ArrayList<>();
        
        // Example dependencies based on typical banking/financial workflows
        switch (bpmnId.toLowerCase()) {
            case "bonus_payment":
            case "01_bonus_payment":
                dependencies.addAll(Arrays.asList(
                    createDependency("user_id", "user_id", DependencyType.PASS_THROUGH),
                    createDependency("auth_token", "auth_token", DependencyType.GENERATED),
                    createDependency("account_id", "account_id", DependencyType.FETCHED),
                    createDependency("balance", "balance", DependencyType.FETCHED),
                    createDependency("payment_id", "payment_id", DependencyType.GENERATED),
                    createDependency("transaction_status", "status", DependencyType.VERIFIED)
                ));
                break;
                
            case "credit_application":
            case "02_credit_application":
                dependencies.addAll(Arrays.asList(
                    createDependency("applicant_data", "applicant", DependencyType.INPUT),
                    createDependency("application_id", "app_id", DependencyType.GENERATED),
                    createDependency("credit_score", "score", DependencyType.FETCHED),
                    createDependency("approval_status", "status", DependencyType.DETERMINED),
                    createDependency("terms", "terms", DependencyType.GENERATED)
                ));
                break;
                
            case "mobile_payment":
            case "04_mobile_payment":
                dependencies.addAll(Arrays.asList(
                    createDependency("mobile_number", "phone", DependencyType.INPUT),
                    createDependency("payment_amount", "amount", DependencyType.INPUT),
                    createDependency("payment_token", "token", DependencyType.GENERATED),
                    createDependency("transaction_id", "txn_id", DependencyType.GENERATED),
                    createDependency("receipt", "receipt", DependencyType.GENERATED)
                ));
                break;
                
            case "utility_payment":
            case "18_utility_payment":
                dependencies.addAll(Arrays.asList(
                    createDependency("provider_id", "provider", DependencyType.INPUT),
                    createDependency("account_number", "acct_num", DependencyType.INPUT),
                    createDependency("bill_amount", "amount", DependencyType.FETCHED),
                    createDependency("payment_reference", "ref", DependencyType.GENERATED),
                    createDependency("payment_status", "status", DependencyType.VERIFIED)
                ));
                break;
                
            default:
                // Generic dependencies for unknown processes
                dependencies.addAll(Arrays.asList(
                    createDependency("request_id", "req_id", DependencyType.GENERATED),
                    createDependency("auth_token", "token", DependencyType.GENERATED),
                    createDependency("response_data", "data", DependencyType.FETCHED),
                    createDependency("status", "status", DependencyType.VERIFIED)
                ));
        }
        
        return dependencies;
    }

    /**
     * Generate data for each step considering dependencies
     */
    private Map<String, Object> generateStepData(String testCaseId, String bpmnId, 
                                               List<DataDependency> dependencies, 
                                               Map<String, Object> constraints) {
        Map<String, Object> stepData = new HashMap<>();
        
        // Parse BPMN steps and generate appropriate data
        List<String> bpmnSteps = getBpmnSteps(bpmnId);
        
        for (int i = 0; i < bpmnSteps.size(); i++) {
            String step = bpmnSteps.get(i);
            Map<String, Object> stepDataMap = new HashMap<>();
            
            // Generate data based on step type
            if (step.contains("Auth") || step.contains("auth")) {
                stepDataMap.putAll(generateAuthData(constraints));
            } else if (step.contains("Payment") || step.contains("payment")) {
                stepDataMap.putAll(generatePaymentData(constraints));
            } else if (step.contains("Account") || step.contains("account")) {
                stepDataMap.putAll(generateAccountData(constraints));
            } else if (step.contains("Balance") || step.contains("balance")) {
                stepDataMap.putAll(generateBalanceData(constraints));
            } else {
                stepDataMap.putAll(generateGenericStepData(constraints));
            }
            
            stepData.put("step_" + (i + 1), stepDataMap);
        }
        
        return stepData;
    }

    /**
     * Generate authentication data
     */
    private Map<String, Object> generateAuthData(Map<String, Object> constraints) {
        Map<String, Object> authData = new HashMap<>();
        
        authData.put("username", generateUsername());
        authData.put("password", generatePassword());
        authData.put("client_id", generateClientId());
        authData.put("client_secret", generateClientSecret());
        authData.put("grant_type", "password");
        authData.put("scope", "read write");
        
        // Apply constraints if provided
        if (constraints.containsKey("userRole")) {
            authData.put("role", constraints.get("userRole"));
        }
        
        return authData;
    }

    /**
     * Generate payment data
     */
    private Map<String, Object> generatePaymentData(Map<String, Object> constraints) {
        Map<String, Object> paymentData = new HashMap<>();
        
        paymentData.put("amount", generateAmount(constraints));
        paymentData.put("currency", constraints.getOrDefault("currency", "RUB"));
        paymentData.put("from_account", generateAccountNumber());
        paymentData.put("to_account", generateAccountNumber());
        paymentData.put("description", generatePaymentDescription());
        paymentData.put("payment_type", "transfer");
        
        return paymentData;
    }

    /**
     * Generate account data
     */
    private Map<String, Object> generateAccountData(Map<String, Object> constraints) {
        Map<String, Object> accountData = new HashMap<>();
        
        accountData.put("account_id", generateAccountId());
        accountData.put("account_number", generateAccountNumber());
        accountData.put("account_type", constraints.getOrDefault("accountType", "savings"));
        accountData.put("currency", "RUB");
        accountData.put("is_active", true);
        accountData.put("opened_date", LocalDateTime.now().minusYears(1).toString());
        
        return accountData;
    }

    /**
     * Generate balance data
     */
    private Map<String, Object> generateBalanceData(Map<String, Object> constraints) {
        Map<String, Object> balanceData = new HashMap<>();
        
        balanceData.put("available_balance", generateBalance());
        balanceData.put("current_balance", generateBalance());
        balanceData.put("reserved_amount", 0.0);
        balanceData.put("last_updated", LocalDateTime.now().toString());
        
        return balanceData;
    }

    /**
     * Generate generic step data
     */
    private Map<String, Object> generateGenericStepData(Map<String, Object> constraints) {
        Map<String, Object> genericData = new HashMap<>();
        
        genericData.put("request_id", generateRequestId());
        genericData.put("timestamp", LocalDateTime.now().toString());
        genericData.put("status", "pending");
        genericData.put("metadata", Map.of("source", "test_generator"));
        
        return genericData;
    }

    /**
     * Create data flow chain from dependencies
     */
    private DataFlowChain createDataFlowChain(List<DataDependency> dependencies, Map<String, Object> stepData) {
        DataFlowChain dataFlowChain = new DataFlowChain();
        dataFlowChain.setId("chain_" + System.currentTimeMillis());
        dataFlowChain.setDependencies(dependencies);
        dataFlowChain.setStepData(stepData);
        dataFlowChain.setCreatedAt(LocalDateTime.now());
        
        return dataFlowChain;
    }

    /**
     * Get BPMN steps for a process
     */
    private List<String> getBpmnSteps(String bpmnId) {
        // This would typically fetch from BPMN service
        // For now, return mock steps based on process type
        switch (bpmnId.toLowerCase()) {
            case "bonus_payment":
            case "01_bonus_payment":
                return Arrays.asList(
                    "Authentication: POST /auth/bank-token",
                    "Get Accounts: GET /accounts",
                    "Check Balance: GET /accounts/{account_id}/balances",
                    "Initiate Payment: POST /payments",
                    "Check Payment Status: GET /payments/{payment_id}"
                );
            case "credit_application":
            case "02_credit_application":
                return Arrays.asList(
                    "Submit Application: POST /credit/applications",
                    "Validate Data: POST /credit/validate",
                    "Credit Check: GET /credit/score",
                    "Review Application: POST /credit/review",
                    "Decision: GET /credit/decision"
                );
            default:
                return Arrays.asList(
                    "Step 1: Initialize",
                    "Step 2: Process",
                    "Step 3: Validate",
                    "Step 4: Complete"
                );
        }
    }

    // Helper methods for data generation
    private String generateUsername() {
        return "testuser_" + System.currentTimeMillis() % 10000;
    }

    private String generatePassword() {
        return "TestPass123!";
    }

    private String generateClientId() {
        return "client_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateClientSecret() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private Double generateAmount(Map<String, Object> constraints) {
        Double min = constraints.containsKey("minAmount") ? 
            Double.valueOf(constraints.get("minAmount").toString()) : 100.0;
        Double max = constraints.containsKey("maxAmount") ? 
            Double.valueOf(constraints.get("maxAmount").toString()) : 10000.0;
        return min + Math.random() * (max - min);
    }

    private String generateAccountNumber() {
        return "40817810" + String.format("%010d", System.currentTimeMillis() % 1000000000L);
    }

    private String generatePaymentDescription() {
        return "Test payment " + System.currentTimeMillis();
    }

    private String generateAccountId() {
        return "acc_" + UUID.randomUUID().toString().substring(0, 12);
    }

    private Double generateBalance() {
        return 10000.0 + Math.random() * 90000.0;
    }

    private String generateRequestId() {
        return "req_" + System.currentTimeMillis();
    }

    /**
     * Create a data dependency
     */
    private DataDependency createDependency(String sourceField, String targetField, DependencyType type) {
        DataDependency dependency = new DataDependency();
        dependency.setId(UUID.randomUUID().toString());
        dependency.setSourceField(sourceField);
        dependency.setTargetField(targetField);
        dependency.setDependencyType(type);
        dependency.setCreatedAt(LocalDateTime.now());
        
        return dependency;
    }
}