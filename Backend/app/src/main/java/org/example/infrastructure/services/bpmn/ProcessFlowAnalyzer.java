package org.example.infrastructure.services.bpmn;

import org.springframework.stereotype.Service;

/**
 * Анализатор потоков BPMN процесса
 */
@Service
public class ProcessFlowAnalyzer {
    
    /**
     * Анализирует потоки BPMN процесса
     */
    public FlowAnalysisResult analyzeFlows(BpmnParsedData parsedData) {
        FlowAnalysisResult result = new FlowAnalysisResult();
        
        // Подсчет типов потоков
        result.setTotalFlows(parsedData.getFlows().size());
        
        long sequenceCount = parsedData.getFlows().stream()
            .filter(flow -> flow.isSequenceFlow())
            .count();
        result.setSequenceFlows((int) sequenceCount);
        
        long messageCount = parsedData.getFlows().stream()
            .filter(flow -> flow.isMessageFlow())
            .count();
        result.setMessageFlows((int) messageCount);
        
        long conditionalCount = parsedData.getFlows().stream()
            .filter(flow -> flow.isConditional())
            .count();
        result.setConditionalFlows((int) conditionalCount);
        
        // Поиск недостижимых элементов
        for (BpmnElement element : parsedData.getElements()) {
            if (element.getIncomingFlowCount() == 0 && !element.isStartEvent()) {
                result.addUnreachableElement(element.getId());
            }
        }
        
        // Поиск потенциальных dead paths
        detectDeadPaths(parsedData, result);
        
        // Вычисление сложности потоков
        int flowComplexity = calculateFlowComplexity(parsedData);
        result.setFlowComplexity(flowComplexity);
        
        return result;
    }
    
    private void detectDeadPaths(BpmnParsedData parsedData, FlowAnalysisResult result) {
        // Простая эвристика для поиска dead paths
        for (BpmnFlow flow : parsedData.getFlows()) {
            BpmnElement targetElement = parsedData.findElementById(flow.getTargetElement());
            if (targetElement != null && targetElement.getOutgoingFlowCount() == 0 && !targetElement.isEndEvent()) {
                result.addDeadPath("Flow " + flow.getId() + " leads to element without outgoing flows");
            }
        }
    }
    
    private int calculateFlowComplexity(BpmnParsedData parsedData) {
        int complexity = 0;
        
        // Сложность на основе количества потоков
        complexity += parsedData.getFlows().size();
        
        // Сложность на основе условных потоков
        long conditionalFlows = parsedData.getFlows().stream()
            .filter(BpmnFlow::isConditional)
            .count();
        complexity += conditionalFlows * 2;
        
        // Сложность на основе шлюзов
        long gateways = parsedData.getElements().stream()
            .filter(BpmnElement::isGateway)
            .count();
        complexity += gateways * 3;
        
        return Math.min(complexity, 50);
    }
}