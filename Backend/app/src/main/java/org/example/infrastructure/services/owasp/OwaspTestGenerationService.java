package org.example.infrastructure.services.owasp;

import org.example.domain.entities.SecurityTest;
import org.example.domain.valueobjects.OwaspTestCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Главный сервис для генерации OWASP API Security тестов
 * Координирует работу специализированных генераторов по OWASP категориям
 */
@Service
@Transactional
public class OwaspTestGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(OwaspTestGenerationService.class);
    
    // Специализированные генераторы по OWASP категориям
    @Autowired
    private AuthenticationTestGenerator authenticationGenerator;
    
    @Autowired
    private AuthorizationTestGenerator authorizationGenerator;
    
    @Autowired
    private BusinessLogicTestGenerator businessLogicGenerator;
    
    @Autowired
    private DataValidationTestGenerator dataValidationGenerator;
    
    @Autowired
    private SecurityConfigurationTestGenerator securityConfigGenerator;
    
    @Autowired
    private DosAttackTestGenerator dosAttackGenerator;
    
    @Autowired
    private SqlInjectionTestGenerator sqlInjectionGenerator;
    
    @Autowired
    private ApiInventoryTestGenerator apiInventoryGenerator;
    
    public OwaspTestGenerationService() {
        logger.info("Initializing OwaspTestGenerationService");
    }
    
    /**
     * Generate comprehensive security test suite for an OpenAPI specification
     */
    public CompletableFuture<List<SecurityTest>> generateOpenApiTests(String openApiSpecId, String userId) {
        logger.info("Generating OWASP security tests for OpenAPI specification: {}", openApiSpecId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<SecurityTest> generatedTests = new ArrayList<>();
                
                // Генерируем тесты по каждой OWASP категории
                List<CompletableFuture<List<SecurityTest>>> categoryGenerationTasks = Arrays.asList(
                    // A01: Broken Object Level Authorization
                    authorizationGenerator.generateA01Tests(openApiSpecId, userId),
                    // A02: Broken Authentication
                    authenticationGenerator.generateA02Tests(openApiSpecId, userId),
                    // A03: Broken Object Property Level Authorization
                    authorizationGenerator.generateA03Tests(openApiSpecId, userId),
                    // A04: Unrestricted Resource Consumption
                    dosAttackGenerator.generateA04Tests(openApiSpecId, userId),
                    // A05: Broken Function Level Authorization
                    authorizationGenerator.generateA05Tests(openApiSpecId, userId),
                    // A06: Unrestricted Access to Sensitive Business Workflows
                    businessLogicGenerator.generateA06Tests(openApiSpecId, userId),
                    // A07: Server Side Request Forgery
                    securityConfigGenerator.generateA07Tests(openApiSpecId, userId),
                    // A08: Security Misconfiguration
                    securityConfigGenerator.generateA08Tests(openApiSpecId, userId),
                    // A09: Improper Inventory Management
                    apiInventoryGenerator.generateA09Tests(openApiSpecId, userId),
                    // A10: Unsafe Consumption of APIs
                    securityConfigGenerator.generateA10Tests(openApiSpecId, userId)
                );
                
                // Ожидаем все задачи и собираем результаты
                CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                    categoryGenerationTasks.toArray(new CompletableFuture[0])
                );
                
                return allTasks.thenApply(v -> {
                    List<SecurityTest> allTests = new ArrayList<>();
                    for (CompletableFuture<List<SecurityTest>> task : categoryGenerationTasks) {
                        try {
                            allTests.addAll(task.get());
                        } catch (Exception e) {
                            logger.warn("Failed to generate tests for category", e);
                        }
                    }
                    
                    // Удаляем дубликаты
                    List<SecurityTest> uniqueTests = allTests.stream()
                            .collect(Collectors.groupingBy(test -> 
                                    test.getTestName() + "|" + test.getTargetEndpoint()))
                            .values()
                            .stream()
                            .map(group -> group.get(0))
                            .collect(Collectors.toList());
                    
                    logger.info("Generated {} unique security tests for OpenAPI specification: {}", 
                               uniqueTests.size(), openApiSpecId);
                    
                    return uniqueTests;
                }).join();
                
            } catch (Exception e) {
                logger.error("Error generating tests for OpenAPI specification: {}", openApiSpecId, e);
                throw new RuntimeException("Failed to generate security tests", e);
            }
        });
    }
    
    /**
     * Generate security tests for BPMN processes
     */
    public CompletableFuture<List<SecurityTest>> generateBpmnTests(String bpmnProcessId, String userId) {
        logger.info("Generating OWASP security tests for BPMN process: {}", bpmnProcessId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<SecurityTest> generatedTests = new ArrayList<>();
                
                // Генерируем процесс-специфичные тесты
                List<CompletableFuture<List<SecurityTest>>> bpmnTestTasks = Arrays.asList(
                    businessLogicGenerator.generateWorkflowSecurityTests(bpmnProcessId, userId),
                    authorizationGenerator.generateBusinessProcessAuthorizationTests(bpmnProcessId, userId),
                    securityConfigGenerator.generateProcessSecurityTests(bpmnProcessId, userId),
                    dataValidationGenerator.generateDataValidationTests(bpmnProcessId, userId)
                );
                
                // Ожидаем все задачи
                CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                    bpmnTestTasks.toArray(new CompletableFuture[0])
                );
                
                return allTasks.thenApply(v -> {
                    List<SecurityTest> allTests = new ArrayList<>();
                    for (CompletableFuture<List<SecurityTest>> task : bpmnTestTasks) {
                        try {
                            allTests.addAll(task.get());
                        } catch (Exception e) {
                            logger.warn("Failed to generate BPMN tests", e);
                        }
                    }
                    
                    logger.info("Generated {} security tests for BPMN process: {}", 
                               allTests.size(), bpmnProcessId);
                    return allTests;
                }).join();
                
            } catch (Exception e) {
                logger.error("Error generating tests for BPMN process: {}", bpmnProcessId, e);
                throw new RuntimeException("Failed to generate BPMN security tests", e);
            }
        });
    }
    
    /**
     * Generate security tests based on LLM analysis and consistency checking
     */
    public CompletableFuture<List<SecurityTest>> generateLLMBasedTests(String analysisId, 
                                                                       List<OwaspTestCategory> categories, 
                                                                       String userId) {
        logger.info("Generating LLM-based security tests for analysis: {} with categories: {}", 
                   analysisId, categories);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<SecurityTest> generatedTests = new ArrayList<>();
                
                // Генерируем тесты на основе LLM анализа
                if (categories.contains(OwaspTestCategory.A01_2023_BROKEN_OBJECT_LEVEL_AUTHORIZATION) ||
                    categories.contains(OwaspTestCategory.A05_2023_BROKEN_FUNCTION_LEVEL_AUTHORIZATION)) {
                    generatedTests.addAll(authorizationGenerator.generateLLMBasedTests(analysisId, userId).join());
                }
                
                // Генерируем тесты из выявленных несоответствий
                generatedTests.addAll(businessLogicGenerator.generateTestsFromInconsistencies(analysisId, userId).join());
                
                // Генерируем контекстно-зависимые тесты
                generatedTests.addAll(dataValidationGenerator.generateContextAwareTests(analysisId, categories, userId).join());
                
                logger.info("Generated {} LLM-based security tests for analysis: {}", 
                           generatedTests.size(), analysisId);
                
                return generatedTests;
                
            } catch (Exception e) {
                logger.error("Error generating LLM-based tests for analysis: {}", analysisId, e);
                throw new RuntimeException("Failed to generate LLM-based security tests", e);
            }
        });
    }
    
    /**
     * Generate targeted security tests for specific OWASP category
     */
    public CompletableFuture<List<SecurityTest>> generateCategoryTests(String openApiSpecId, 
                                                                       OwaspTestCategory category, 
                                                                       String userId) {
        logger.info("Generating targeted tests for category: {} and specification: {}", 
                   category.getCategoryCode(), openApiSpecId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                CompletableFuture<List<SecurityTest>> testGeneration;
                
                switch (category) {
                    case A01_2023_BROKEN_OBJECT_LEVEL_AUTHORIZATION:
                    case A05_2023_BROKEN_FUNCTION_LEVEL_AUTHORIZATION:
                        testGeneration = authorizationGenerator.generateCategoryTests(category, openApiSpecId, userId);
                        break;
                    case A02_2023_BROKEN_AUTHENTICATION:
                        testGeneration = authenticationGenerator.generateCategoryTests(category, openApiSpecId, userId);
                        break;
                    case A03_2023_BROKEN_OBJECT_PROPERTY_LEVEL_AUTHORIZATION:
                        testGeneration = authorizationGenerator.generateCategoryTests(category, openApiSpecId, userId);
                        break;
                    case A04_2023_UNRESTRICTED_RESOURCE_CONSUMPTION:
                        testGeneration = dosAttackGenerator.generateCategoryTests(category, openApiSpecId, userId);
                        break;
                    case A06_2023_UNRESTRICTED_ACCESS_TO_SENSITIVE_BUSINESS_WORKFLOWS:
                        testGeneration = businessLogicGenerator.generateCategoryTests(category, openApiSpecId, userId);
                        break;
                    case A07_2023_SERVER_SIDE_REQUEST_FORGERY:
                    case A08_2023_SECURITY_MISCONFIGURATION:
                    case A10_2023_UNSAFE_CONSUMPTION_OF_APIS:
                        testGeneration = securityConfigGenerator.generateCategoryTests(category, openApiSpecId, userId);
                        break;
                    case A09_2023_IMPROPER_INVENTORY_MANAGEMENT:
                        testGeneration = apiInventoryGenerator.generateCategoryTests(category, openApiSpecId, userId);
                        break;
                    default:
                        testGeneration = CompletableFuture.completedFuture(new ArrayList<>());
                }
                
                List<SecurityTest> tests = testGeneration.join();
                logger.info("Generated {} tests for category: {}", tests.size(), category.getCategoryCode());
                return tests;
                
            } catch (Exception e) {
                logger.error("Error generating tests for category: {}", category, e);
                throw new RuntimeException("Failed to generate category tests", e);
            }
        });
    }
    
    /**
     * Generate comprehensive test suite combining all sources
     */
    public CompletableFuture<List<SecurityTest>> generateComprehensiveTestSuite(String openApiSpecId, 
                                                                               String bpmnProcessId,
                                                                               String llmAnalysisId,
                                                                               String userId) {
        logger.info("Generating comprehensive test suite for: OpenAPI={}, BPMN={}, LLM={}", 
                   openApiSpecId, bpmnProcessId, llmAnalysisId);
        
        CompletableFuture<List<SecurityTest>> openApiTests = generateOpenApiTests(openApiSpecId, userId);
        CompletableFuture<List<SecurityTest>> bpmnTests = generateBpmnTests(bpmnProcessId, userId);
        CompletableFuture<List<SecurityTest>> llmTests = generateLLMBasedTests(llmAnalysisId, 
                                                                              Arrays.asList(OwaspTestCategory.values()), 
                                                                              userId);
        
        return CompletableFuture.allOf(openApiTests, bpmnTests, llmTests)
                .thenApply(v -> {
                    List<SecurityTest> allTests = new ArrayList<>();
                    allTests.addAll(openApiTests.join());
                    allTests.addAll(bpmnTests.join());
                    allTests.addAll(llmTests.join());
                    
                    // Remove duplicates based on test name and target endpoint
                    List<SecurityTest> uniqueTests = allTests.stream()
                            .collect(Collectors.groupingBy(test -> 
                                    test.getTestName() + "|" + test.getTargetEndpoint()))
                            .values()
                            .stream()
                            .map(group -> group.get(0)) // Take first occurrence
                            .collect(Collectors.toList());
                    
                    logger.info("Generated comprehensive test suite with {} unique tests", uniqueTests.size());
                    return uniqueTests;
                });
    }
    
    /**
     * Get test generation statistics
     */
    public Map<String, Object> getTestGenerationStats(String userId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalCategories", OwaspTestCategory.values().length);
        stats.put("totalTests", 0); // Будет обновлено после интеграции с репозиториями
        stats.put("criticalTests", 0); // Будет обновлено после интеграции с репозиториями
        stats.put("completedTests", 0); // Будет обновлено после интеграции с репозиториями
        stats.put("completionRate", 0.0); // Будет обновлено после интеграции с репозиториями
        stats.put("message", "Statistics will be available after full integration with repositories");
        
        return stats;
    }
    
    /**
     * Get available OWASP categories
     */
    public List<OwaspTestCategory> getAvailableCategories() {
        return Arrays.asList(OwaspTestCategory.values());
    }
    
    /**
     * Check if service is ready for test generation
     */
    public boolean isReady() {
        return authenticationGenerator != null && 
               authorizationGenerator != null && 
               businessLogicGenerator != null &&
               dataValidationGenerator != null &&
               securityConfigGenerator != null &&
               dosAttackGenerator != null &&
               sqlInjectionGenerator != null &&
               apiInventoryGenerator != null;
    }
}
