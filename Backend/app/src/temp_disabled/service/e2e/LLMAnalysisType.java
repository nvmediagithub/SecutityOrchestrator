package org.example.application.service.e2e;

/**
 * Типы анализа LLM для результатов тестирования
 */
public enum LLMAnalysisType {
    COMPREHENSIVE_ANALYSIS("Comprehensive analysis of all test results"),
    ERROR_ANALYSIS("Focused analysis of errors and issues"),
    PERFORMANCE_ANALYSIS("Performance-focused analysis"),
    SECURITY_ANALYSIS("Security assessment and vulnerability analysis"),
    QUALITY_ANALYSIS("Code quality and best practices analysis"),
    RECOMMENDATIONS("Actionable recommendations and next steps");
    
    private final String description;
    
    LLMAnalysisType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}