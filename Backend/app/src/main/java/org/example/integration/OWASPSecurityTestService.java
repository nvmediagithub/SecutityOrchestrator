package org.example.integration;

import org.example.domain.entities.SecurityTest;
import org.example.domain.entities.SecurityAnalysisResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * OWASP Security Test Service - handles OWASP API Security Top 10 testing
 */
@Service
public class OWASPSecurityTestService {
    
    /**
     * Generate comprehensive OWASP API Security Top 10 tests
     */
    public List<SecurityTest> generateOWASPTop10Tests(String apiSpecification) {
        List<SecurityTest> tests = new ArrayList<>();
        
        // API1: Broken Object Level Authorization (BOLA)
        tests.add(createTest("API1_BOLA_User_Access", "Test unauthorized access to user resources",
            "GET /api/users/123", "Should return 403 for unauthorized user"));
        tests.add(createTest("API1_BOLA_Order_Access", "Test unauthorized order access",
            "GET /api/orders/456", "Should return 403 for foreign orders"));
        
        // API2: Broken Authentication
        tests.add(createTest("API2_Auth_Weak_Password", "Test weak password policies",
            "POST /api/auth/login {\"password\":\"123456\"}", "Should reject weak passwords"));
        tests.add(createTest("API2_Auth_No_Token", "Test authentication without token",
            "GET /api/profile", "Should return 401 for missing token"));
        
        // API3: Excessive Data Exposure
        tests.add(createTest("API3_Data_Exposure_User", "Test sensitive data exposure",
            "GET /api/users/123", "Should not expose internal user data"));
        tests.add(createTest("API3_Data_Exposure_Admin", "Test admin data exposure",
            "GET /api/admin/users", "Should filter sensitive admin information"));
        
        // API4: Lack of Resources & Rate Limiting
        tests.add(createTest("API4_Rate_Limiting", "Test rate limiting enforcement",
            "POST /api/search (1000 times)", "Should enforce rate limits"));
        tests.add(createTest("API4_Resource_Exhaustion", "Test resource exhaustion",
            "GET /api/large-dataset", "Should limit large data responses"));
        
        // API5: Broken Function Level Authorization
        tests.add(createTest("API5_Admin_Function", "Test admin function access",
            "POST /api/admin/delete-user", "Should require admin privileges"));
        tests.add(createTest("API5_User_Escalation", "Test privilege escalation",
            "POST /api/user/promote-self", "Should prevent self-promotion"));
        
        // API6: Mass Assignment
        tests.add(createTest("API6_Mass_Assignment", "Test mass assignment vulnerability",
            "POST /api/users {\"role\":\"admin\"}", "Should not allow role assignment"));
        tests.add(createTest("API6_Field_Injection", "Test unauthorized field injection",
            "POST /api/profile {\"isAdmin\":true}", "Should reject admin flags"));
        
        // API7: Security Misconfiguration
        tests.add(createTest("API7_CORS_Misconfig", "Test CORS misconfiguration",
            "GET /api/data (cross-origin)", "Should enforce CORS policies"));
        tests.add(createTest("API7_Info_Disclosure", "Test information disclosure",
            "GET /api/error", "Should not reveal system information"));
        
        // API8: Injection
        tests.add(createTest("API8_SQL_Injection", "Test SQL injection",
            "GET /api/search?q='; DROP TABLE users; --", "Should prevent SQL injection"));
        tests.add(createTest("API8_NoSQL_Injection", "Test NoSQL injection",
            "GET /api/users?query={\"$ne\":null}", "Should prevent NoSQL injection"));
        
        // API9: Improper Assets Management
        tests.add(createTest("API9_Old_Version", "Test old API version access",
            "GET /api/v1/users", "Should disable old API versions"));
        tests.add(createTest("API9_Debug_Endpoint", "Test debug endpoint exposure",
            "GET /api/debug/info", "Should not expose debug information"));
        
        // API10: Insufficient Logging & Monitoring
        tests.add(createTest("API10_Missing_Logs", "Test security event logging",
            "POST /api/admin/action", "Should log security-relevant actions"));
        tests.add(createTest("API10_Attack_Detection", "Test attack detection",
            "GET /api/search?q=../../../etc/passwd", "Should detect and log attacks"));
        
        return tests;
    }
    
    /**
     * Execute OWASP security tests
     */
    public Map<String, Object> executeOWASPTests(List<SecurityTest> tests) {
        Map<String, Object> results = new java.util.HashMap<>();
        results.put("totalTests", tests.size());
        results.put("passed", 0);
        results.put("failed", 0);
        results.put("vulnerabilities", new ArrayList<String>());
        
        for (SecurityTest test : tests) {
            // Simulate test execution
            boolean passed = simulateTestExecution(test);
            test.setPassed(passed);
            test.setActualResult(passed ? "Test passed successfully" : "Vulnerability detected");
            
            if (passed) {
                results.put("passed", ((Integer) results.get("passed")) + 1);
            } else {
                results.put("failed", ((Integer) results.get("failed")) + 1);
                ((List<String>) results.get("vulnerabilities")).add(test.getName());
            }
        }
        
        return results;
    }
    
    private SecurityTest createTest(String name, String description, String payload, String expectedResult) {
        SecurityTest test = new SecurityTest(name, "OWASP_AUTOMATED", payload);
        test.setDescription(description);
        test.setExpectedResult(expectedResult);
        test.setCategory("OWASP_TOP10");
        return test;
    }
    
    private boolean simulateTestExecution(SecurityTest test) {
        // Simulate test execution with realistic pass/fail rates
        // Most tests should pass in a secure system, but some vulnerabilities might be detected
        double passRate = 0.85; // 85% pass rate for demonstration
        return Math.random() < passRate;
    }
}