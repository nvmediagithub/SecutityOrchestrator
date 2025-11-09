package org.example.infrastructure.services.integration.bpmn;

import org.example.domain.dto.integration.BpmnContextExtractionRequest;
import org.example.domain.entities.BpmnDiagram;
import org.example.infrastructure.services.bpmn.BpmnAnalysisService;
import org.example.infrastructure.services.integration.llm.LlmAnalysisService;
import org.example.infrastructure.services.integration.status.ExtractionStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Specialized extractor for BPMN task data
 */
@Service
public class TaskDataExtractor {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskDataExtractor.class);
    
    @Autowired
    private BpmnAnalysisService bpmnAnalysisService;
    
    @Autowired
    private LlmAnalysisService llmAnalysisService;
    
    @Autowired
    private ExtractionStatusService statusService;
    
    @Autowired
    private Executor bpmnExtractionExecutor;
    
    @Async
    public CompletableFuture<BpmnContextOrchestrator.PartialExtractionResult> extract(
            BpmnContextExtractionRequest request, String extractionId) {
        
        logger.info("Starting task data extraction for diagram: {}", request.getDiagramId());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                statusService.updateExtractionStatus(extractionId, "TASK_DATA_EXTRACTION");
                
                BpmnDiagram diagram = createBpmnDiagram(request);
                BpmnAnalysisService.BpmnAnalysisResult analysisResult = analyzeBpmnDiagram(diagram);
                String prompt = buildTaskDataPrompt(diagram, analysisResult);
                String llmResponse = llmAnalysisService.executeAnalysis(prompt, "task_data");
                Map<String, Object> taskData = parseTaskDataResponse(llmResponse);
                
                BpmnContextOrchestrator.PartialExtractionResult result = new BpmnContextOrchestrator.PartialExtractionResult();
                result.setExtractionType("TASK_DATA");
                result.setTaskData(taskData);
                result.setProcessingTimeMs(System.currentTimeMillis() - extractionId.hashCode());
                result.setTimestamp(LocalDateTime.now());
                
                logger.info("Task data extraction completed for diagram: {}", request.getDiagramId());
                return result;
                
            } catch (Exception e) {
                logger.error("Task data extraction failed for diagram: {}", request.getDiagramId(), e);
                throw new RuntimeException("Task data extraction failed", e);
            }
        }, bpmnExtractionExecutor);
    }
    
    private BpmnAnalysisService.BpmnAnalysisResult analyzeBpmnDiagram(BpmnDiagram diagram) {
        try {
            return bpmnAnalysisService.analyzeBpmnDiagram(diagram.getDiagramId(), diagram).get();
        } catch (Exception e) {
            logger.warn("BPMN analysis failed, proceeding with basic analysis", e);
            return null;
        }
    }
    
    private BpmnDiagram createBpmnDiagram(BpmnContextExtractionRequest request) {
        BpmnDiagram diagram = new BpmnDiagram();
        diagram.setDiagramId(request.getDiagramId());
        diagram.setBpmnContent(request.getBpmnContent());
        diagram.setCreatedAt(LocalDateTime.now());
        return diagram;
    }
    
    private String buildTaskDataPrompt(BpmnDiagram diagram, BpmnAnalysisService.BpmnAnalysisResult analysisResult) {
        return String.format("""
            Извлеки данные задач из данной BPMN диаграммы:

            1. Входные данные задач (Task Inputs):
               - Идентификатор задачи
               - Имя поля
               - Тип данных
               - Источник данных
               - Валидационные правила

            2. Выходные данные задач (Task Outputs):
               - Идентификатор задачи
               - Имя поля
               - Тип данных
               - Назначение
               - Формат

            3. Зависимости данных (Data Dependencies):
               - Связи между задачами
               - Потоки данных
               - Трансформации

            BPMN Диаграмма: %s
            Результаты анализа: %s

            Ответь в формате JSON со структурой:
            {
              "taskInputs": [...],
              "taskOutputs": [...],
              "dataDependencies": [...]
            }
            """, diagram.getBpmnContent(), 
            analysisResult != null ? analysisResult.toString() : "N/A");
    }
    
    private Map<String, Object> parseTaskDataResponse(String llmResponse) {
        Map<String, Object> result = new HashMap<>();
        result.put("rawResponse", llmResponse);
        result.put("parsed", true);
        result.put("extractionType", "TASK_DATA");
        result.put("extractionTime", LocalDateTime.now().toString());
        return result;
    }
}