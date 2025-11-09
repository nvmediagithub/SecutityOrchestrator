package org.example.infrastructure.services.bpmn;

import org.springframework.stereotype.Service;

/**
 * Анализатор безопасности BPMN процессов
 */
@Service
public class SecurityProcessAnalyzer {
    
    /**
     * Анализирует безопасность BPMN процесса
     */
    public SecurityAnalysisResult analyzeSecurity(BpmnParsedData parsedData) {
        SecurityAnalysisResult result = new SecurityAnalysisResult();
        
        // Анализ пользовательских задач
        analyzeUserTasks(parsedData, result);
        
        // Анализ сервисных задач
        analyzeServiceTasks(parsedData, result);
        
        // Анализ скриптовых задач
        analyzeScriptTasks(parsedData, result);
        
        // Общая оценка риска
        assessOverallRisk(result);
        
        return result;
    }
    
    private void analyzeUserTasks(BpmnParsedData parsedData, SecurityAnalysisResult result) {
        for (BpmnElement element : parsedData.getElements()) {
            if ("UserTask".equals(element.getType())) {
                String assignee = element.getAttributeValue("camunda:assignee");
                String candidateUsers = element.getAttributeValue("camunda:candidateUsers");
                
                if (assignee == null && candidateUsers == null) {
                    result.addVulnerability(new SecurityVulnerability(
                        "USER_TASK_" + element.getId(),
                        "Missing Assignment",
                        "User task " + element.getName() + " не имеет назначенных исполнителей",
                        "MEDIUM"
                    ));
                }
                
                result.addSecurityCheck(createSecurityCheck("USER_TASK", element.getName(), 
                    "Проверка назначения пользовательской задачи", true));
            }
        }
    }
    
    private void analyzeServiceTasks(BpmnParsedData parsedData, SecurityAnalysisResult result) {
        for (BpmnElement element : parsedData.getElements()) {
            if ("ServiceTask".equals(element.getType())) {
                String delegateExpression = element.getAttributeValue("camunda:delegateExpression");
                
                if (delegateExpression != null && delegateExpression.contains("${")) {
                    result.addVulnerability(new SecurityVulnerability(
                        "SERVICE_TASK_" + element.getId(),
                        "Expression Injection",
                        "Service task использует потенциально небезопасные выражения",
                        "HIGH"
                    ));
                }
                
                result.addSecurityCheck(createSecurityCheck("SERVICE_TASK", element.getName(), 
                    "Проверка безопасности сервисной задачи", true));
            }
        }
    }
    
    private void analyzeScriptTasks(BpmnParsedData parsedData, SecurityAnalysisResult result) {
        for (BpmnElement element : parsedData.getElements()) {
            if ("ScriptTask".equals(element.getType())) {
                String script = element.getAttributeValue("script");
                String scriptFormat = element.getAttributeValue("scriptFormat");
                
                if (script != null && script.contains("eval")) {
                    result.addVulnerability(new SecurityVulnerability(
                        "SCRIPT_TASK_" + element.getId(),
                        "Code Injection",
                        "Script task содержит потенциально опасный код",
                        "CRITICAL"
                    ));
                }
                
                result.addSecurityCheck(createSecurityCheck("SCRIPT_TASK", element.getName(), 
                    "Проверка безопасности скриптовой задачи", true));
            }
        }
    }
    
    private org.example.domain.entities.ProcessSecurityCheck createSecurityCheck(String checkType, 
                                                                                 String checkName, 
                                                                                 String description, 
                                                                                 boolean passed) {
        return org.example.domain.entities.ProcessSecurityCheck.createPassedCheck(
            null, checkType, checkName, description
        );
    }
    
    private void assessOverallRisk(SecurityAnalysisResult result) {
        long criticalCount = result.getVulnerabilities().stream()
            .filter(v -> "CRITICAL".equals(v.getSeverity()))
            .count();
        long highCount = result.getVulnerabilities().stream()
            .filter(v -> "HIGH".equals(v.getSeverity()))
            .count();
            
        if (criticalCount > 0) {
            result.setRiskLevel("CRITICAL");
        } else if (highCount > 0) {
            result.setRiskLevel("HIGH");
        } else if (!result.getVulnerabilities().isEmpty()) {
            result.setRiskLevel("MEDIUM");
        } else {
            result.setRiskLevel("LOW");
        }
        
        // Вычисляем балл безопасности (0-100)
        int score = 100;
        score -= criticalCount * 30;
        score -= highCount * 20;
        score -= result.getVulnerabilities().stream()
            .filter(v -> "MEDIUM".equals(v.getSeverity()))
            .count() * 10;
        score = Math.max(score, 0);
        
        result.setSecurityScore(score);
    }
}