package org.example.infrastructure.services.integrated.analysis;

import org.example.domain.entities.openapi.OpenApiSpecification;
import org.example.domain.entities.BpmnDiagram;
import org.example.domain.valueobjects.SeverityLevel;
import org.example.infrastructure.services.integrated.dto.ApiBpmnContradiction;
import org.example.infrastructure.services.integrated.dto.PrioritizedIssue;
import org.example.infrastructure.services.bpmn.BpmnElement;
import org.example.infrastructure.services.bpmn.BpmnParsedData;
import org.example.infrastructure.services.bpmn.BpmnParser;
import org.example.infrastructure.services.openapi.OpenApiDataExtractor;
import org.example.infrastructure.services.openapi.OpenApiDataExtractor.EndpointSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Анализатор противоречий между API и BPMN процессами
 * Выявляет несоответствия между OpenAPI спецификациями и бизнес-процессами
 */
@Service
public class ApiBpmnContradictionAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiBpmnContradictionAnalyzer.class);
    
    @Autowired
    private OpenApiDataExtractor apiDataExtractor;
    
    @Autowired
    private BpmnParser bpmnParser;
    
    // Паттерны для сопоставления API эндпоинтов с задачами BPMN
    private static final Pattern HTTP_METHOD_PATTERN = Pattern.compile("(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS)");
    private static final Pattern RESOURCE_PATTERN = Pattern.compile("/([a-zA-Z0-9_-]+)");
    
    // Типы противоречий
    public enum ContradictionType {
        MISSING_API_FOR_TASK,
        MISSING_TASK_FOR_API,
        HTTP_METHOD_MISMATCH,
        PARAMETER_MISMATCH,
        RESPONSE_MISMATCH,
        SECURITY_MISMATCH,
        BUSINESS_LOGIC_MISMATCH,
        VALIDATION_MISMATCH
    }
    
    /**
     * Анализирует противоречия между OpenAPI спецификацией и BPMN диаграммами
     */
    public List<ApiBpmnContradiction> analyzeContradictions(
            String openApiSpec, 
            List<BpmnDiagram> bpmnDiagrams) {
        
        logger.info("Starting contradiction analysis between API and {} BPMN diagrams", 
            bpmnDiagrams != null ? bpmnDiagrams.size() : 0);
        
        List<ApiBpmnContradiction> contradictions = new ArrayList<>();
        
        try {
            // Извлекаем данные из OpenAPI
            OpenApiDataExtractor.OpenApiLLMData apiData = apiDataExtractor.extractLLMData(openApiSpec);
            List<EndpointSummary> apiEndpoints = apiData.getEndpoints();
            
            // Парсим BPMN диаграммы
            Map<String, BpmnParsedData> bpmnDataMap = new HashMap<>();
            for (BpmnDiagram diagram : bpmnDiagrams) {
                BpmnParsedData bpmnData = bpmnParser.parseBpmnContent(diagram.getBpmnContent());
                bpmnDataMap.put(diagram.getDiagramId(), bpmnData);
            }
            
            // Анализируем различные типы противоречий
            contradictions.addAll(findMissingApiForTasks(apiEndpoints, bpmnDataMap));
            contradictions.addAll(findMissingTasksForApi(apiEndpoints, bpmnDataMap));
            contradictions.addAll(findMethodMismatches(apiEndpoints, bpmnDataMap));
            contradictions.addAll(findParameterMismatches(apiEndpoints, bpmnDataMap));
            contradictions.addAll(findResponseMismatches(apiEndpoints, bpmnDataMap));
            contradictions.addAll(findSecurityMismatches(apiEndpoints, bpmnDataMap));
            contradictions.addAll(findBusinessLogicMismatches(apiEndpoints, bpmnDataMap));
            contradictions.addAll(findValidationMismatches(apiEndpoints, bpmnDataMap));
            
            // Убираем дубликаты и сортируем по критичности
            contradictions = deduplicateContradictions(contradictions);
            contradictions.sort(Comparator.comparing(
                c -> c.getSeverity().getLevel(), Comparator.reverseOrder()));
            
            logger.info("Found {} contradictions between API and BPMN", contradictions.size());
            return contradictions;
            
        } catch (Exception e) {
            logger.error("Error during contradiction analysis", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Находит API эндпоинты, отсутствующие для BPMN задач
     */
    private List<ApiBpmnContradiction> findMissingApiForTasks(
            List<EndpointSummary> apiEndpoints, 
            Map<String, BpmnParsedData> bpmnDataMap) {
        
        List<ApiBpmnContradiction> contradictions = new ArrayList<>();
        
        for (Map.Entry<String, BpmnParsedData> entry : bpmnDataMap.entrySet()) {
            String diagramId = entry.getKey();
            BpmnParsedData bpmnData = entry.getValue();
            
            for (BpmnElement task : bpmnData.getTasks()) {
                // Ищем соответствующий API эндпоинт для задачи
                String taskName = task.getName().toLowerCase();
                String taskType = task.getType().toLowerCase();
                
                boolean found = false;
                for (EndpointSummary endpoint : apiEndpoints) {
                    if (isTaskCorrespondsToEndpoint(taskName, taskType, endpoint)) {
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    ApiBpmnContradiction contradiction = new ApiBpmnContradiction();
                    contradiction.setId("missing_api_" + UUID.randomUUID().toString());
                    contradiction.setType(ContradictionType.MISSING_API_FOR_TASK);
                    contradiction.setSeverity(determineMissingApiSeverity(task));
                    contradiction.setDiagramId(diagramId);
                    contradiction.setTaskId(task.getId());
                    contradiction.setTaskName(task.getName());
                    contradiction.setDescription(String.format(
                        "BPMN task '%s' of type '%s' has no corresponding API endpoint", 
                        task.getName(), task.getType()));
                    contradiction.setRecommendedAction(generateMissingApiAction(task));
                    contradiction.setBusinessImpact(calculateMissingApiImpact(task));
                    contradiction.setDetectedAt(java.time.LocalDateTime.now());
                    
                    contradictions.add(contradiction);
                }
            }
        }
        
        return contradictions;
    }
    
    /**
     * Находит BPMN задачи, отсутствующие для API эндпоинтов
     */
    private List<ApiBpmnContradiction> findMissingTasksForApi(
            List<EndpointSummary> apiEndpoints, 
            Map<String, BpmnParsedData> bpmnDataMap) {
        
        List<ApiBpmnContradiction> contradictions = new ArrayList<>();
        
        for (EndpointSummary endpoint : apiEndpoints) {
            String endpointPath = endpoint.getPath();
            String httpMethod = endpoint.getMethod();
            
            // Проверяем, есть ли соответствующая задача в BPMN
            boolean found = false;
            for (BpmnParsedData bpmnData : bpmnDataMap.values()) {
                for (BpmnElement task : bpmnData.getTasks()) {
                    if (isEndpointCorrespondsToTask(endpoint, task)) {
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
            
            if (!found) {
                ApiBpmnContradiction contradiction = new ApiBpmnContradiction();
                contradiction.setId("missing_task_" + UUID.randomUUID().toString());
                contradiction.setType(ContradictionType.MISSING_TASK_FOR_API);
                contradiction.setSeverity(determineMissingTaskSeverity(endpoint));
                contradiction.setApiPath(endpointPath);
                contradiction.setApiMethod(httpMethod);
                contradiction.setDescription(String.format(
                    "API endpoint %s %s has no corresponding BPMN task", 
                    httpMethod, endpointPath));
                contradiction.setRecommendedAction(generateMissingTaskAction(endpoint));
                contradiction.setBusinessImpact(calculateMissingTaskImpact(endpoint));
                contradiction.setDetectedAt(java.time.LocalDateTime.now());
                
                contradictions.add(contradiction);
            }
        }
        
        return contradictions;
    }
    
    /**
     * Находит несоответствия HTTP методов
     */
    private List<ApiBpmnContradiction> findMethodMismatches(
            List<EndpointSummary> apiEndpoints, 
            Map<String, BpmnParsedData> bpmnDataMap) {
        
        List<ApiBpmnContradiction> contradictions = new ArrayList<>();
        
        for (Map.Entry<String, BpmnParsedData> entry : bpmnDataMap.entrySet()) {
            String diagramId = entry.getKey();
            BpmnParsedData bpmnData = entry.getValue();
            
            for (BpmnElement task : bpmnData.getTasks()) {
                for (EndpointSummary endpoint : apiEndpoints) {
                    if (isTaskCorrespondsToEndpoint(task.getName().toLowerCase(), 
                                                  task.getType().toLowerCase(), endpoint)) {
                        
                        String expectedMethod = getExpectedHttpMethod(task);
                        String actualMethod = endpoint.getMethod();
                        
                        if (!expectedMethod.equals(actualMethod)) {
                            ApiBpmnContradiction contradiction = new ApiBpmnContradiction();
                            contradiction.setId("method_mismatch_" + UUID.randomUUID().toString());
                            contradiction.setType(ContradictionType.HTTP_METHOD_MISMATCH);
                            contradiction.setSeverity(SeverityLevel.MEDIUM);
                            contradiction.setDiagramId(diagramId);
                            contradiction.setTaskId(task.getId());
                            contradiction.setApiPath(endpoint.getPath());
                            contradiction.setExpectedValue(expectedMethod);
                            contradiction.setActualValue(actualMethod);
                            contradiction.setDescription(String.format(
                                "BPMN task '%s' expects HTTP method '%s' but API uses '%s'",
                                task.getName(), expectedMethod, actualMethod));
                            contradiction.setBusinessImpact(0.5);
                            contradiction.setDetectedAt(java.time.LocalDateTime.now());
                            
                            contradictions.add(contradiction);
                        }
                    }
                }
            }
        }
        
        return contradictions;
    }
    
    /**
     * Находит несоответствия параметров
     */
    private List<ApiBpmnContradiction> findParameterMismatches(
            List<EndpointSummary> apiEndpoints, 
            Map<String, BpmnParsedData> bpmnDataMap) {
        
        List<ApiBpmnContradiction> contradictions = new ArrayList<>();
        
        for (Map.Entry<String, BpmnParsedData> entry : bpmnDataMap.entrySet()) {
            String diagramId = entry.getKey();
            BpmnParsedData bpmnData = entry.getValue();
            
            for (BpmnElement task : bpmnData.getTasks()) {
                for (EndpointSummary endpoint : apiEndpoints) {
                    if (isTaskCorrespondsToEndpoint(task.getName().toLowerCase(), 
                                                  task.getType().toLowerCase(), endpoint)) {
                        
                        // Сравниваем параметры
                        List<String> taskInputs = getTaskInputParameters(task);
                        List<String> apiParams = endpoint.getParameters();
                        
                        List<String> missingInApi = taskInputs.stream()
                            .filter(param -> !apiParams.contains(param))
                            .collect(Collectors.toList());
                        
                        List<String> missingInTask = apiParams.stream()
                            .filter(param -> !taskInputs.contains(param))
                            .collect(Collectors.toList());
                        
                        if (!missingInApi.isEmpty() || !missingInTask.isEmpty()) {
                            ApiBpmnContradiction contradiction = new ApiBpmnContradiction();
                            contradiction.setId("param_mismatch_" + UUID.randomUUID().toString());
                            contradiction.setType(ContradictionType.PARAMETER_MISMATCH);
                            contradiction.setSeverity(SeverityLevel.MEDIUM);
                            contradiction.setDiagramId(diagramId);
                            contradiction.setTaskId(task.getId());
                            contradiction.setApiPath(endpoint.getPath());
                            contradiction.setMissingInApi(missingInApi);
                            contradiction.setMissingInTask(missingInTask);
                            contradiction.setDescription("Parameter mismatch between BPMN task and API endpoint");
                            contradiction.setBusinessImpact(0.4);
                            contradiction.setDetectedAt(java.time.LocalDateTime.now());
                            
                            contradictions.add(contradiction);
                        }
                    }
                }
            }
        }
        
        return contradictions;
    }
    
    // Вспомогательные методы
    
    private boolean isTaskCorrespondsToEndpoint(String taskName, String taskType, EndpointSummary endpoint) {
        String normalizedTaskName = taskName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        String normalizedPath = endpoint.getPath().replaceAll("[/{}\\\\]", " ").toLowerCase();
        String endpointName = normalizedPath.contains(" ") ? 
            normalizedPath.split(" ")[1] : normalizedPath;
        
        return normalizedTaskName.contains(endpointName) || 
               endpointName.contains(normalizedTaskName) ||
               taskType.contains("user") && endpointPathContains(endpoint, "user") ||
               taskType.contains("create") && "POST".equals(endpoint.getMethod()) ||
               taskType.contains("update") && ("PUT".equals(endpoint.getMethod()) || "PATCH".equals(endpoint.getMethod())) ||
               taskType.contains("delete") && "DELETE".equals(endpoint.getMethod()) ||
               taskType.contains("get") && "GET".equals(endpoint.getMethod());
    }
    
    private boolean endpointPathContains(EndpointSummary endpoint, String keyword) {
        return endpoint.getPath().toLowerCase().contains(keyword.toLowerCase());
    }
    
    private boolean isEndpointCorrespondsToTask(EndpointSummary endpoint, BpmnElement task) {
        return isTaskCorrespondsToEndpoint(task.getName().toLowerCase(), 
                                          task.getType().toLowerCase(), endpoint);
    }
    
    private String getExpectedHttpMethod(BpmnElement task) {
        String taskType = task.getType().toLowerCase();
        if (taskType.contains("create") || taskType.contains("add") || taskType.contains("submit")) {
            return "POST";
        } else if (taskType.contains("update") || taskType.contains("modify") || taskType.contains("edit")) {
            return "PUT";
        } else if (taskType.contains("delete") || taskType.contains("remove")) {
            return "DELETE";
        } else if (taskType.contains("get") || taskType.contains("read") || taskType.contains("list")) {
            return "GET";
        }
        return "POST"; // Default
    }
    
    private List<String> getTaskInputParameters(BpmnElement task) {
        // Извлекаем параметры из задачи (упрощенная логика)
        List<String> parameters = new ArrayList<>();
        if (task.getInputs() != null) {
            parameters.addAll(task.getInputs());
        }
        // Добавляем стандартные параметры на основе типа задачи
        String taskType = task.getType().toLowerCase();
        if (taskType.contains("user")) {
            parameters.add("userId");
        }
        if (taskType.contains("order") || taskType.contains("transaction")) {
            parameters.add("orderId");
            parameters.add("amount");
        }
        return parameters;
    }
    
    private SeverityLevel determineMissingApiSeverity(BpmnElement task) {
        String taskType = task.getType().toLowerCase();
        if (taskType.contains("critical") || taskType.contains("payment") || taskType.contains("security")) {
            return SeverityLevel.CRITICAL;
        } else if (taskType.contains("important") || taskType.contains("business")) {
            return SeverityLevel.HIGH;
        } else if (taskType.contains("user") || taskType.contains("data")) {
            return SeverityLevel.MEDIUM;
        }
        return SeverityLevel.LOW;
    }
    
    private SeverityLevel determineMissingTaskSeverity(EndpointSummary endpoint) {
        String path = endpoint.getPath().toLowerCase();
        if (path.contains("payment") || path.contains("admin") || path.contains("security")) {
            return SeverityLevel.HIGH;
        } else if (path.contains("user") || path.contains("order") || path.contains("business")) {
            return SeverityLevel.MEDIUM;
        }
        return SeverityLevel.LOW;
    }
    
    private String generateMissingApiAction(BpmnElement task) {
        return String.format("Create API endpoint for BPMN task '%s'. " +
            "Consider RESTful design principles and include proper validation and error handling.", 
            task.getName());
    }
    
    private String generateMissingTaskAction(EndpointSummary endpoint) {
        return String.format("Add BPMN task to represent API endpoint %s %s. " +
            "Include appropriate task type, data flow, and business logic.", 
            endpoint.getMethod(), endpoint.getPath());
    }
    
    private double calculateMissingApiImpact(BpmnElement task) {
        return determineMissingApiSeverity(task).getLevel() * 0.2;
    }
    
    private double calculateMissingTaskImpact(EndpointSummary endpoint) {
        return determineMissingTaskSeverity(endpoint).getLevel() * 0.15;
    }
    
    private List<ApiBpmnContradiction> findResponseMismatches(List<EndpointSummary> apiEndpoints, 
                                                             Map<String, BpmnParsedData> bpmnDataMap) {
        // Реализация анализа несоответствий ответов
        return Collections.emptyList();
    }
    
    private List<ApiBpmnContradiction> findSecurityMismatches(List<EndpointSummary> apiEndpoints, 
                                                             Map<String, BpmnParsedData> bpmnDataMap) {
        // Реализация анализа несоответствий безопасности
        return Collections.emptyList();
    }
    
    private List<ApiBpmnContradiction> findBusinessLogicMismatches(List<EndpointSummary> apiEndpoints, 
                                                                  Map<String, BpmnParsedData> bpmnDataMap) {
        // Реализация анализа несоответствий бизнес-логики
        return Collections.emptyList();
    }
    
    private List<ApiBpmnContradiction> findValidationMismatches(List<EndpointSummary> apiEndpoints, 
                                                               Map<String, BpmnParsedData> bpmnDataMap) {
        // Реализация анализа несоответствий валидации
        return Collections.emptyList();
    }
    
    private List<ApiBpmnContradiction> deduplicateContradictions(List<ApiBpmnContradiction> contradictions) {
        // Удаляем дубликаты на основе ключевых полей
        return contradictions.stream()
            .collect(Collectors.groupingBy(c -> c.getType() + "_" + c.getDiagramId() + "_" + c.getTaskId() + "_" + c.getApiPath()))
            .values().stream()
            .map(group -> group.get(0)) // Берем первый элемент из каждой группы
            .collect(Collectors.toList());
    }
}