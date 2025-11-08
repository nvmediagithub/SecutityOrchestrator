package org.example.infrastructure.services.bpmn;

import org.springframework.stereotype.Service;

/**
 * Анализатор производительности BPMN процессов
 */
@Service
public class PerformanceProcessAnalyzer {
    
    /**
     * Анализирует производительность BPMN процесса
     */
    public PerformanceAnalysisResult analyzePerformance(BpmnParsedData parsedData, FlowAnalysisResult flowResult) {
        PerformanceAnalysisResult result = new PerformanceAnalysisResult();
        
        // Анализ сложности процесса
        analyzeComplexity(parsedData, flowResult, result);
        
        // Анализ узких мест
        analyzeBottlenecks(parsedData, flowResult, result);
        
        // Анализ циклических структур
        analyzeCyclicStructures(parsedData, result);
        
        // Общая оценка производительности
        assessPerformance(result);
        
        return result;
    }
    
    private void analyzeComplexity(BpmnParsedData parsedData, FlowAnalysisResult flowResult, PerformanceAnalysisResult result) {
        int elementCount = parsedData.getElements().size();
        int flowCount = parsedData.getFlows().size();
        int gatewayCount = (int) parsedData.getElements().stream()
            .filter(BpmnElement::isGateway)
            .count();
            
        // Вычисляем уровень сложности
        int complexityScore = elementCount + flowCount + (gatewayCount * 2);
        
        if (complexityScore > 100) {
            result.setComplexityLevel("CRITICAL");
        } else if (complexityScore > 50) {
            result.setComplexityLevel("HIGH");
        } else if (complexityScore > 20) {
            result.setComplexityLevel("MEDIUM");
        } else {
            result.setComplexityLevel("LOW");
        }
        
        // Добавляем предложения по оптимизации
        if (gatewayCount > 5) {
            result.addOptimizationSuggestion("Рассмотрите упрощение логики с большим количеством шлюзов");
        }
        if (flowCount > elementCount * 2) {
            result.addOptimizationSuggestion("Проверьте необходимость такого количества потоков");
        }
    }
    
    private void analyzeBottlenecks(BpmnParsedData parsedData, FlowAnalysisResult flowResult, PerformanceAnalysisResult result) {
        // Поиск элементов с большим количеством входящих потоков (potential bottlenecks)
        for (BpmnElement element : parsedData.getElements()) {
            if (element.getIncomingFlowCount() > 3) {
                result.addBottleneck("Element " + element.getName() + " has " + element.getIncomingFlowCount() + " incoming flows");
            }
        }
        
        // Поиск шлюзов с большим количеством выходящих потоков
        for (BpmnElement element : parsedData.getElements()) {
            if (element.isGateway() && element.getOutgoingFlowCount() > 5) {
                result.addBottleneck("Gateway " + element.getName() + " has " + element.getOutgoingFlowCount() + " outgoing flows");
            }
        }
    }
    
    private void analyzeCyclicStructures(BpmnParsedData parsedData, PerformanceAnalysisResult result) {
        // Простая эвристика для поиска циклических структур
        for (BpmnFlow flow : parsedData.getFlows()) {
            if (flow.getSourceElement().equals(flow.getTargetElement())) {
                result.addCyclicStructure("Self-loop found in flow " + flow.getId());
            }
        }
    }
    
    private void assessPerformance(PerformanceAnalysisResult result) {
        // Вычисляем балл производительности (0-100)
        int score = 100;
        
        if ("CRITICAL".equals(result.getComplexityLevel())) {
            score -= 40;
        } else if ("HIGH".equals(result.getComplexityLevel())) {
            score -= 25;
        } else if ("MEDIUM".equals(result.getComplexityLevel())) {
            score -= 10;
        }
        
        score -= result.getBottlenecks().size() * 5;
        score -= result.getCyclicStructures().size() * 10;
        
        score = Math.max(score, 0);
        result.setPerformanceScore(score);
        
        // Оценочные времена выполнения
        result.setAverageExecutionTime(score * 10); // Простая эвристика
        result.setMaxExecutionTime(score * 20);
    }
}