package org.example.infrastructure.llm.testdata;

import org.example.infrastructure.llm.testdata.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Service for building specialized prompts for test data generation using LLM
 */
@Component
public class LLMTestDataPrompts {
    
    /**
     * Builds prompt for test data generation
     */
    public String buildGenerationPrompt(TestDataGenerationRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are an expert test data generator. Generate high-quality, realistic test data based on the following requirements:\n\n");
        
        // Basic requirements
        prompt.append("REQUIREMENTS:\n");
        prompt.append("- Data Type: ").append(request.getDataType()).append("\n");
        prompt.append("- Generation Scope: ").append(request.getGenerationScope()).append("\n");
        prompt.append("- Number of Records: ").append(request.getRecordCount()).append("\n");
        prompt.append("- Quality Level: ").append(request.getQualityLevel()).append("\n\n");
        
        // Add context if available
        if (request.getContext() != null && !request.getContext().isEmpty()) {
            prompt.append("CONTEXT:\n");
            for (Map.Entry<String, Object> entry : request.getContext().entrySet()) {
                prompt.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            prompt.append("\n");
        }
        
        // Add constraints if available
        if (request.getConstraints() != null && !request.getConstraints().isEmpty()) {
            prompt.append("CONSTRAINTS:\n");
            for (Map.Entry<String, Object> entry : request.getConstraints().entrySet()) {
                prompt.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            prompt.append("\n");
        }
        
        // Add required fields
        if (request.getRequiredFields() != null && !request.getRequiredFields().isEmpty()) {
            prompt.append("REQUIRED FIELDS:\n");
            for (String field : request.getRequiredFields()) {
                prompt.append("- ").append(field).append("\n");
            }
            prompt.append("\n");
        }
        
        // Add excluded fields
        if (request.getExcludeFields() != null && !request.getExcludeFields().isEmpty()) {
            prompt.append("EXCLUDE FIELDS:\n");
            for (String field : request.getExcludeFields()) {
                prompt.append("- ").append(field).append("\n");
            }
            prompt.append("\n");
        }
        
        // Quality requirements based on level
        prompt.append("QUALITY REQUIREMENTS:\n");
        prompt.append("Based on the quality level '").append(request.getQualityLevel()).append("', ensure:\n");
        
        switch (request.getQualityLevel().toUpperCase()) {
            case "BASIC":
                prompt.append("- Basic data realism and consistency\n");
                prompt.append("- Standard field validation\n");
                break;
            case "STANDARD":
                prompt.append("- Good data realism and business logic\n");
                prompt.append("- Enhanced validation and relationships\n");
                prompt.append("- Some data variety\n");
                break;
            case "PREMIUM":
                prompt.append("- High data realism with complex relationships\n");
                prompt.append("- Business rule compliance\n");
                prompt.append("- Data variety and edge cases\n");
                prompt.append("- Contextual accuracy\n");
                break;
            case "ENTERPRISE":
                prompt.append("- Maximum data realism and complexity\n");
                prompt.append("- Full business rule compliance\n");
                prompt.append("- Comprehensive data relationships\n");
                prompt.append("- Production-like data patterns\n");
                prompt.append("- Full validation coverage\n");
                break;
        }
        prompt.append("\n");
        
        // Output format
        prompt.append("OUTPUT FORMAT:\n");
        prompt.append("Provide the generated data in JSON array format. Each record should be a JSON object with the following structure:\n");
        prompt.append("{\n");
        prompt.append("  \"metadata\": {\n");
        prompt.append("    \"recordId\": \"unique_identifier\",\n");
        prompt.append("    \"generatedAt\": \"timestamp\",\n");
        prompt.append("    \"quality\": \"quality_assessment\"\n");
        prompt.append("  },\n");
        prompt.append("  \"data\": {\n");
        prompt.append("    // Generated data fields here\n");
        prompt.append("  }\n");
        prompt.append("}\n\n");
        
        // Special instructions
        prompt.append("INSTRUCTIONS:\n");
        prompt.append("1. Generate data that is realistic and follows business logic\n");
        prompt.append("2. Ensure data consistency within and across records\n");
        prompt.append("3. Use appropriate data types and formats\n");
        prompt.append("4. Include variety in data values where appropriate\n");
        prompt.append("5. Follow any specified constraints exactly\n");
        prompt.append("6. Validate that all required fields are present\n");
        prompt.append("7. Ensure data is suitable for testing purposes\n\n");
        
        prompt.append("Begin generation now:");
        
        return prompt.toString();
    }
    
    /**
     * Builds prompt for data quality analysis
     */
    public String buildQualityAnalysisPrompt(TestDataGenerationResult result) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are an expert data quality analyst. Analyze the quality of the following generated test data:\n\n");
        
        prompt.append("GENERATED DATA:\n");
        prompt.append(result.getGeneratedData()).append("\n\n");
        
        prompt.append("ANALYSIS CRITERIA:\n");
        prompt.append("Please evaluate the data quality based on:\n");
        prompt.append("1. COMPLETENESS - Are all required fields present and populated?\n");
        prompt.append("2. CONSISTENCY - Is the data internally consistent and logical?\n");
        prompt.append("3. REALISM - Does the data look realistic and business-appropriate?\n");
        prompt.append("4. COMPLIANCE - Does the data follow business rules and constraints?\n");
        prompt.append("5. VARIETY - Is there appropriate diversity in the data values?\n");
        prompt.append("6. VALIDATION - Are data types and formats correct?\n\n");
        
        prompt.append("OUTPUT FORMAT:\n");
        prompt.append("Provide your analysis in the following JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"overallScore\": <number_0_100>,\n");
        prompt.append("  \"qualityGrade\": \"<A/B/C/D/F>\",\n");
        prompt.append("  \"completenessScore\": <number_0_100>,\n");
        prompt.append("  \"consistencyScore\": <number_0_100>,\n");
        prompt.append("  \"realismScore\": <number_0_100>,\n");
        prompt.append("  \"complianceScore\": <number_0_100>,\n");
        prompt.append("  \"strengths\": [<list_of_strengths>],\n");
        prompt.append("  \"issues\": [<list_of_issues>],\n");
        prompt.append("  \"warnings\": [<list_of_warnings>],\n");
        prompt.append("  \"recommendations\": [<list_of_recommendations>]\n");
        prompt.append("}\n\n");
        
        prompt.append("Begin analysis:");
        
        return prompt.toString();
    }
    
    /**
     * Builds prompt for data validation
     */
    public String buildValidationPrompt(TestDataGenerationResult result, List<String> validationRules) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are a data validation expert. Validate the following generated test data against the specified rules:\n\n");
        
        prompt.append("GENERATED DATA:\n");
        prompt.append(result.getGeneratedData()).append("\n\n");
        
        prompt.append("VALIDATION RULES:\n");
        for (String rule : validationRules) {
            prompt.append("- ").append(rule).append("\n");
        }
        prompt.append("\n");
        
        prompt.append("OUTPUT FORMAT:\n");
        prompt.append("Provide validation results in the following JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"valid\": <true/false>,\n");
        prompt.append("  \"validationScore\": <number_0_100>,\n");
        prompt.append("  \"severity\": \"<LOW/MEDIUM/HIGH/CRITICAL>\",\n");
        prompt.append("  \"passedRules\": [<list_of_passed_rules>],\n");
        prompt.append("  \"failedRules\": [<list_of_failed_rules>],\n");
        prompt.append("  \"violations\": [<list_of_specific_violations>],\n");
        prompt.append("  \"warnings\": [<list_of_warnings>],\n");
        prompt.append("  \"suggestions\": [<list_of_suggestions>]\n");
        prompt.append("}\n\n");
        
        prompt.append("Begin validation:");
        
        return prompt.toString();
    }
    
    /**
     * Builds prompt for dependency-aware generation
     */
    public String buildDependencyAwarePrompt(TestDataGenerationRequest request, List<String> dependencies) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are an expert in generating test data with complex dependencies. Generate data that respects the following dependencies:\n\n");
        
        prompt.append("DEPENDENCIES:\n");
        for (String dependency : dependencies) {
            prompt.append("- ").append(dependency).append("\n");
        }
        prompt.append("\n");
        
        // Add base generation requirements
        prompt.append(buildGenerationPrompt(request));
        
        prompt.append("\nADDITIONAL REQUIREMENTS:\n");
        prompt.append("7. Ensure all specified dependencies are properly maintained\n");
        prompt.append("8. Generate data in dependency-aware order\n");
        prompt.append("9. Validate dependency relationships\n");
        prompt.append("10. Report any dependency violations\n\n");
        
        prompt.append("Begin dependency-aware generation:");
        
        return prompt.toString();
    }
    
    /**
     * Builds prompt for context-preserving generation
     */
    public String buildContextPreservingPrompt(TestDataGenerationRequest request, String context) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are an expert in generating context-aware test data. Preserve the following context during generation:\n\n");
        
        prompt.append("CONTEXT TO PRESERVE:\n");
        prompt.append(context).append("\n\n");
        
        // Add base generation requirements
        prompt.append(buildGenerationPrompt(request));
        
        prompt.append("\nCONTEXT PRESERVATION REQUIREMENTS:\n");
        prompt.append("7. Maintain context consistency throughout all generated data\n");
        prompt.append("8. Ensure data reflects the business context accurately\n");
        prompt.append("9. Preserve relationships and business logic from the context\n");
        prompt.append("10. Adapt data generation to fit the specific context\n\n");
        
        prompt.append("Begin context-preserving generation:");
        
        return prompt.toString();
    }
}