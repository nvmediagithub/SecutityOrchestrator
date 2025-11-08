package org.example.infrastructure.services.datamanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for managing data masking rules and operations
 */
@Service
@Transactional
public class DataMaskingService {
    
    @Autowired
    private DataMaskingRuleRepository maskingRuleRepository;
    
    @Autowired
    private TestDataRepository testDataRepository;
    
    // Masking Rule Management Methods
    
    /**
     * Create a new masking rule
     */
    public DataMaskingRule createMaskingRule(String name, String fieldName, DataMaskingRule.MaskingType maskingType, String userId) {
        DataMaskingRule rule = new DataMaskingRule(name, fieldName, maskingType);
        rule.setCreatedBy(userId);
        rule.setEffectiveFrom(java.time.LocalDateTime.now());
        
        return maskingRuleRepository.save(rule);
    }
    
    /**
     * Get masking rule
     */
    public Optional<DataMaskingRule> getMaskingRule(String ruleId) {
        return maskingRuleRepository.findByRuleId(ruleId);
    }
    
    /**
     * Get all active masking rules
     */
    public List<DataMaskingRule> getActiveMaskingRules() {
        return maskingRuleRepository.findByIsActiveTrue();
    }
    
    /**
     * Apply masking to data content
     */
    public String maskData(String dataContent, String dataSetId) {
        if (dataContent == null || dataContent.trim().isEmpty()) {
            return dataContent;
        }
        
        List<DataMaskingRule> applicableRules = getApplicableRules(dataSetId);
        String maskedData = dataContent;
        
        for (DataMaskingRule rule : applicableRules) {
            maskedData = applyMaskingRule(maskedData, rule);
        }
        
        return maskedData;
    }
    
    /**
     * Mask PII patterns in data
     */
    public String maskPII(String dataContent) {
        if (dataContent == null) {
            return null;
        }
        
        String maskedContent = dataContent;
        
        // Mask email addresses
        maskedContent = maskPattern(maskedContent, 
            "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b", 
            "email@masked.com");
        
        // Mask phone numbers
        maskedContent = maskPattern(maskedContent, 
            "\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b", 
            "XXX-XXX-XXXX");
        
        // Mask SSN
        maskedContent = maskPattern(maskedContent, 
            "\\b\\d{3}-\\d{2}-\\d{4}\\b", 
            "XXX-XX-XXXX");
        
        // Mask credit cards
        maskedContent = maskPattern(maskedContent, 
            "\\b\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b", 
            "XXXX-XXXX-XXXX-XXXX");
        
        // Mask IP addresses
        maskedContent = maskPattern(maskedContent, 
            "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b", 
            "XXX.XXX.XXX.XXX");
        
        return maskedContent;
    }
    
    // Private Helper Methods
    
    private List<DataMaskingRule> getApplicableRules(String dataSetId) {
        List<DataMaskingRule> allRules = getActiveMaskingRules();
        return allRules.stream()
                .filter(rule -> isRuleApplicable(rule, dataSetId))
                .sorted(Comparator.comparing(DataMaskingRule::getPriority))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    private boolean isRuleApplicable(DataMaskingRule rule, String dataSetId) {
        // Check scope
        if (rule.isGlobalScope()) {
            return true;
        }
        if (rule.isDataSetSpecific() && rule.getAppliesToId() != null && rule.getAppliesToId().equals(dataSetId)) {
            return true;
        }
        return false;
    }
    
    private String applyMaskingRule(String data, DataMaskingRule rule) {
        switch (rule.getMaskingType()) {
            case STATIC_VALUE:
                return rule.getReplacementValue() != null ? rule.getReplacementValue() : data;
            case PATTERN_REPLACEMENT:
                if (rule.getMaskingPattern() != null && rule.getReplacementValue() != null) {
                    return maskPattern(data, rule.getMaskingPattern(), rule.getReplacementValue());
                }
                return data;
            default:
                return data;
        }
    }
    
    private String maskPattern(String data, String pattern, String replacement) {
        if (data == null || pattern == null || replacement == null) {
            return data;
        }
        
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(data);
        return matcher.replaceAll(replacement);
    }
}