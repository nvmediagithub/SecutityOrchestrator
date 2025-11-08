package org.example.infrastructure.services.datamanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Service for managing data privacy and compliance
 */
@Service
@Transactional
public class DataPrivacyService {
    
    @Autowired
    private DataPrivacyClassificationRepository privacyClassificationRepository;
    
    @Autowired
    private TestDataRepository testDataRepository;
    
    // Privacy Classification Methods
    
    /**
     * Classify data based on content analysis
     */
    public DataPrivacyClassification classifyData(String dataContent, String dataType) {
        DataPrivacyClassification classification = new DataPrivacyClassification();
        
        // Analyze content for PII patterns
        List<String> piiTypes = detectPII(dataContent);
        classification.setPiiTypes(piiTypes);
        
        // Determine privacy level
        DataPrivacyClassification.PrivacyLevel privacyLevel = determinePrivacyLevel(piiTypes, dataContent);
        classification.setPrivacyLevel(privacyLevel);
        
        // Determine data category
        DataPrivacyClassification.DataCategory dataCategory = determineDataCategory(dataType, dataContent);
        classification.setDataCategory(dataCategory);
        
        // Set compliance standards
        String complianceStandard = determineComplianceStandards(piiTypes, dataCategory);
        classification.setComplianceStandard(complianceStandard);
        
        // Set required actions
        List<String> requiredActions = determineRequiredActions(privacyLevel, piiTypes);
        classification.setRequiredActions(requiredActions);
        
        classification.setName("Auto-classified " + dataType + " data");
        classification.setDescription("Automatically classified based on content analysis");
        
        return privacyClassificationRepository.save(classification);
    }
    
    /**
     * Get privacy classification for data set
     */
    public Optional<DataPrivacyClassification> getClassification(String classificationId) {
        return privacyClassificationRepository.findByClassificationId(classificationId);
    }
    
    /**
     * Update privacy classification
     */
    public DataPrivacyClassification updateClassification(String classificationId, 
                                                        DataPrivacyClassification.PrivacyLevel privacyLevel,
                                                        List<String> piiTypes,
                                                        String userId) {
        DataPrivacyClassification classification = privacyClassificationRepository
                .findByClassificationId(classificationId)
                .orElseThrow(() -> new IllegalArgumentException("Classification not found: " + classificationId));
        
        classification.setPrivacyLevel(privacyLevel);
        classification.setPiiTypes(piiTypes);
        classification.setLastModifiedBy(userId);
        classification.setUpdatedAt(LocalDateTime.now());
        
        // Update compliance requirements
        String complianceStandard = determineComplianceStandards(piiTypes, classification.getDataCategory());
        classification.setComplianceStandard(complianceStandard);
        
        return privacyClassificationRepository.save(classification);
    }
    
    // PII Detection Methods
    
    /**
     * Detect PII in text content
     */
    public List<String> detectPII(String content) {
        List<String> piiTypes = new ArrayList<>();
        
        if (content == null || content.trim().isEmpty()) {
            return piiTypes;
        }
        
        // Email detection
        if (Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b").matcher(content).find()) {
            piiTypes.add("EMAIL");
        }
        
        // Phone number detection
        if (Pattern.compile("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b|\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b").matcher(content).find()) {
            piiTypes.add("PHONE");
        }
        
        // SSN detection
        if (Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b").matcher(content).find()) {
            piiTypes.add("SSN");
        }
        
        // Credit card detection
        if (Pattern.compile("\\b\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b").matcher(content).find()) {
            piiTypes.add("CREDIT_CARD");
        }
        
        // IP address detection
        if (Pattern.compile("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b").matcher(content).find()) {
            piiTypes.add("IP_ADDRESS");
        }
        
        // Address patterns (simplified)
        if (Pattern.compile("\\b\\d+\\s+[A-Za-z\\s]+\\s+(Street|St|Avenue|Ave|Road|Rd|Drive|Dr|Boulevard|Blvd)\\b").matcher(content).find()) {
            piiTypes.add("ADDRESS");
        }
        
        return piiTypes;
    }
    
    /**
     * Check if data contains sensitive information
     */
    public boolean containsSensitiveData(String content) {
        List<String> piiTypes = detectPII(content);
        return !piiTypes.isEmpty();
    }
    
    // Compliance Methods
    
    /**
     * Check compliance for data set
     */
    public ComplianceReport checkCompliance(String dataSetId) {
        Optional<TestDataSet> dataSet = testDataRepository.findByDataSetId(dataSetId);
        if (dataSet.isEmpty()) {
            throw new IllegalArgumentException("Data set not found: " + dataSetId);
        }
        
        TestDataSet set = dataSet.get();
        List<String> violations = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        
        // Check encryption requirement
        if (set.getIsSensitive() && !Boolean.TRUE.equals(set.getIsEncrypted())) {
            violations.add("Sensitive data is not encrypted");
            recommendations.add("Enable encryption for sensitive data");
        }
        
        // Check classification
        if (!set.hasPrivacyClassification()) {
            violations.add("Data lacks privacy classification");
            recommendations.add("Apply appropriate privacy classification");
        }
        
        // Check PII handling
        if (containsSensitiveData(set.getDataContent())) {
            List<String> piiTypes = detectPII(set.getDataContent());
            if (set.getIsSensitive() == null || !set.getIsSensitive()) {
                violations.add("PII detected but not marked as sensitive: " + piiTypes);
                recommendations.add("Mark data containing PII as sensitive");
            }
        }
        
        return new ComplianceReport(dataSetId, violations, recommendations);
    }
    
    /**
     * Generate compliance report for all data sets
     */
    public List<ComplianceReport> generateComplianceReport() {
        List<TestDataSet> allDataSets = testDataRepository.findAll();
        List<ComplianceReport> reports = new ArrayList<>();
        
        for (TestDataSet dataSet : allDataSets) {
            try {
                reports.add(checkCompliance(dataSet.getDataSetId()));
            } catch (Exception e) {
                // Log error but continue with other data sets
                reports.add(new ComplianceReport(dataSet.getDataSetId(), 
                        Arrays.asList("Error checking compliance: " + e.getMessage()), 
                        Arrays.asList("Fix data set and re-run compliance check")));
            }
        }
        
        return reports;
    }
    
    // Encryption Methods
    
    /**
     * Encrypt sensitive data
     */
    public String encryptSensitiveData(String data, String algorithm) {
        // Simplified implementation - in real scenario, use proper encryption
        return "ENCRYPTED[" + Base64.getEncoder().encodeToString(data.getBytes()) + "]";
    }
    
    /**
     * Decrypt sensitive data
     */
    public String decryptSensitiveData(String encryptedData) {
        // Simplified implementation - in real scenario, use proper decryption
        if (encryptedData.startsWith("ENCRYPTED[") && encryptedData.endsWith("]")) {
            String base64Data = encryptedData.substring(10, encryptedData.length() - 1);
            return new String(Base64.getDecoder().decode(base64Data));
        }
        return encryptedData; // Return as-is if not encrypted
    }
    
    /**
     * Check if data is encrypted
     */
    public boolean isEncrypted(String data) {
        return data != null && data.startsWith("ENCRYPTED[") && data.endsWith("]");
    }
    
    // Data Access Control Methods
    
    /**
     * Check if user can access data based on privacy classification
     */
    public boolean canUserAccessData(String userRole, String dataClassification) {
        // Simplified role-based access control
        Map<String, Set<String>> accessMatrix = new HashMap<>();
        accessMatrix.put("ADMIN", new HashSet<>(Arrays.asList("PUBLIC", "INTERNAL", "CONFIDENTIAL", "RESTRICTED")));
        accessMatrix.put("USER", new HashSet<>(Arrays.asList("PUBLIC", "INTERNAL")));
        accessMatrix.put("GUEST", new HashSet<>(Arrays.asList("PUBLIC")));
        
        Set<String> allowedClassifications = accessMatrix.get(userRole.toUpperCase());
        return allowedClassifications != null && allowedClassifications.contains(dataClassification.toUpperCase());
    }
    
    // Private Helper Methods
    
    private DataPrivacyClassification.PrivacyLevel determinePrivacyLevel(List<String> piiTypes, String content) {
        if (piiTypes.contains("SSN") || piiTypes.contains("CREDIT_CARD")) {
            return DataPrivacyClassification.PrivacyLevel.RESTRICTED;
        } else if (piiTypes.contains("EMAIL") || piiTypes.contains("PHONE") || piiTypes.contains("ADDRESS")) {
            return DataPrivacyClassification.PrivacyLevel.CONFIDENTIAL;
        } else if (piiTypes.contains("IP_ADDRESS")) {
            return DataPrivacyClassification.PrivacyLevel.INTERNAL;
        } else {
            return DataPrivacyClassification.PrivacyLevel.PUBLIC;
        }
    }
    
    private DataPrivacyClassification.DataCategory determineDataCategory(String dataType, String content) {
        if (content != null) {
            if (content.toLowerCase().contains("health") || content.toLowerCase().contains("medical")) {
                return DataPrivacyClassification.DataCategory.HEALTH_DATA;
            } else if (content.toLowerCase().contains("financial") || content.toLowerCase().contains("payment")) {
                return DataPrivacyClassification.DataCategory.FINANCIAL_DATA;
            } else if (content.toLowerCase().contains("location") || content.toLowerCase().contains("address")) {
                return DataPrivacyClassification.DataCategory.LOCATION_DATA;
            }
        }
        
        // Default based on data type
        switch (dataType != null ? dataType.toLowerCase() : "") {
            case "json":
            case "xml":
                return DataPrivacyClassification.DataCategory.PERSONAL_DATA;
            default:
                return DataPrivacyClassification.DataCategory.OTHER;
        }
    }
    
    private String determineComplianceStandards(List<String> piiTypes, DataPrivacyClassification.DataCategory dataCategory) {
        List<String> standards = new ArrayList<>();
        
        if (dataCategory == DataPrivacyClassification.DataCategory.PERSONAL_DATA || 
            !piiTypes.isEmpty()) {
            standards.add("GDPR");
        }
        
        if (dataCategory == DataPrivacyClassification.DataCategory.HEALTH_DATA || 
            piiTypes.contains("SSN")) {
            standards.add("HIPAA");
        }
        
        if (dataCategory == DataPrivacyClassification.DataCategory.FINANCIAL_DATA || 
            piiTypes.contains("CREDIT_CARD")) {
            standards.add("PCI-DSS");
        }
        
        return String.join(", ", standards);
    }
    
    private List<String> determineRequiredActions(DataPrivacyClassification.PrivacyLevel privacyLevel, List<String> piiTypes) {
        List<String> actions = new ArrayList<>();
        
        if (privacyLevel == DataPrivacyClassification.PrivacyLevel.RESTRICTED || 
            privacyLevel == DataPrivacyClassification.PrivacyLevel.CONFIDENTIAL) {
            actions.add("ENCRYPT");
        }
        
        if (!piiTypes.isEmpty()) {
            actions.add("ANONYMIZE");
        }
        
        actions.add("MASK");
        
        return actions;
    }
    
    // Inner classes for return types
    
    public static class ComplianceReport {
        private final String dataSetId;
        private final List<String> violations;
        private final List<String> recommendations;
        
        public ComplianceReport(String dataSetId, List<String> violations, List<String> recommendations) {
            this.dataSetId = dataSetId;
            this.violations = violations != null ? violations : new ArrayList<>();
            this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
        }
        
        // Getters
        public String getDataSetId() { return dataSetId; }
        public List<String> getViolations() { return violations; }
        public List<String> getRecommendations() { return recommendations; }
        
        public boolean isCompliant() {
            return violations.isEmpty();
        }
        
        public int getViolationCount() {
            return violations.size();
        }
        
        public int getRecommendationCount() {
            return recommendations.size();
        }
    }
}