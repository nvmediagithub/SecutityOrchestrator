# Feature Module Structure

## Modular Architecture Overview

The SecurityOrchestrator follows a feature-first modular architecture where each business capability is organized as an independent module. Each module follows clean architecture principles with clear separation between domain, application, and infrastructure layers.

```
src/main/java/com/securityorchestrator/
├── core/                          # Shared core components
│   ├── domain/
│   ├── application/
│   └── infrastructure/
├── features/                      # Feature-specific modules
│   ├── bpmn-processing/
│   ├── api-testing/
│   ├── orchestration/
│   └── ai-test-generation/
└── shared/                        # Cross-cutting concerns
    ├── config/
    ├── security/
    ├── monitoring/
    └── common/
```

## Core Module

### Structure
```
core/
├── domain/
│   ├── model/
│   │   ├── entity/
│   │   │   ├── AggregateRoot.java
│   │   │   ├── Entity.java
│   │   │   └── ValueObject.java
│   │   ├── event/
│   │   │   ├── DomainEvent.java
│   │   │   └── EventPublisher.java
│   │   └── value/
│   │       ├── ValidationResult.java
│   │       ├── Version.java
│   │       └── Id.java
│   ├── service/
│   │   ├── DomainService.java
│   │   └── ValidationService.java
│   └── repository/
│       └── Repository.java
├── application/
│   ├── usecase/
│   │   ├── UseCase.java
│   │   └── Command.java
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   └── service/
│       └── ApplicationService.java
└── infrastructure/
    ├── config/
    │   ├── CoreConfiguration.java
    │   └── DatabaseConfiguration.java
    ├── persistence/
    │   ├── entity/
    │   └── repository/
    └── event/
        └── SpringEventPublisher.java
```

### Key Components
- **Base entities and value objects** for all features
- **Common domain services** (validation, ID generation)
- **Repository abstractions** and base implementations
- **Event publishing infrastructure**
- **Database configuration** and connection management

## BPMN Processing Feature

### Structure
```
features/bpmn-processing/
├── domain/
│   ├── model/
│   │   ├── Process.java
│   │   ├── FlowElement.java
│   │   ├── Task.java
│   │   ├── Gateway.java
│   │   ├── Event.java
│   │   ├── SequenceFlow.java
│   │   └── ExecutionContext.java
│   ├── service/
│   │   ├── ProcessValidator.java
│   │   ├── ProcessExecutor.java
│   │   ├── ElementResolver.java
│   │   └── VariableResolver.java
│   ├── repository/
│   │   ├── ProcessRepository.java
│   │   └── ExecutionRepository.java
│   └── event/
│       ├── ProcessCreatedEvent.java
│       ├── ProcessExecutedEvent.java
│       └── ProcessValidationFailedEvent.java
├── application/
│   ├── usecase/
│   │   ├── CreateProcessUseCase.java
│   │   ├── ExecuteProcessUseCase.java
│   │   ├── ValidateProcessUseCase.java
│   │   └── GetProcessUseCase.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CreateProcessRequest.java
│   │   │   └── ExecuteProcessRequest.java
│   │   └── response/
│   │       ├── ProcessResponse.java
│   │       └── ExecutionResultResponse.java
│   └── service/
│       └── ProcessApplicationService.java
└── infrastructure/
    ├── bpmn/
    │   ├── CamundaBpmnParser.java
    │   ├── BpmnValidator.java
    │   └── BpmnModelMapper.java
    ├── execution/
    │   ├── BpmnProcessExecutor.java
    │   ├── FlowElementExecutor.java
    │   ├── TaskExecutor.java
    │   └── GatewayExecutor.java
    ├── persistence/
    │   ├── entity/
    │   │   ├── ProcessEntity.java
    │   │   ├── FlowElementEntity.java
    │   │   └── ExecutionEntity.java
    │   ├── repository/
    │   │   ├── JpaProcessRepository.java
    │   │   └── JpaExecutionRepository.java
    │   └── mapper/
    │       └── ProcessMapper.java
    └── config/
        ├── BpmnProcessingConfiguration.java
        └── CamundaConfiguration.java
```

### Module Dependencies
- **Depends on**: core
- **Exposed interfaces**: ProcessRepository, ProcessExecutor
- **External libraries**: Camunda BPMN Model API

## API Testing Feature

### Structure
```
features/api-testing/
├── domain/
│   ├── model/
│   │   ├── ApiSpecification.java
│   │   ├── TestCase.java
│   │   ├── TestResult.java
│   │   ├── Parameter.java
│   │   ├── RequestBody.java
│   │   └── ResponseDefinition.java
│   ├── service/
│   │   ├── SpecificationParser.java
│   │   ├── TestCaseGenerator.java
│   │   ├── ResponseValidator.java
│   │   └── HttpClient.java
│   ├── repository/
│   │   ├── SpecificationRepository.java
│   │   ├── TestCaseRepository.java
│   │   └── TestResultRepository.java
│   └── event/
│       ├── SpecificationLoadedEvent.java
│       ├── TestCaseGeneratedEvent.java
│       └── TestExecutedEvent.java
├── application/
│   ├── usecase/
│   │   ├── LoadSpecificationUseCase.java
│   │   ├── GenerateTestCasesUseCase.java
│   │   ├── ExecuteTestCaseUseCase.java
│   │   ├── ValidateTestResultUseCase.java
│   │   └── GetTestResultsUseCase.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoadSpecificationRequest.java
│   │   │   └── ExecuteTestRequest.java
│   │   └── response/
│   │       ├── SpecificationResponse.java
│   │       ├── TestCaseResponse.java
│   │       └── TestResultResponse.java
│   └── service/
│       └── ApiTestingApplicationService.java
└── infrastructure/
    ├── openapi/
    │   ├── SwaggerSpecificationParser.java
    │   ├── OpenApiValidator.java
    │   └── SpecificationMapper.java
    ├── testing/
    │   ├── RestTemplateHttpClient.java
    │   ├── TestCaseGeneratorImpl.java
    │   ├── ResponseValidatorImpl.java
    │   └── HttpRequestBuilder.java
    ├── persistence/
    │   ├── entity/
    │   │   ├── SpecificationEntity.java
    │   │   ├── TestCaseEntity.java
    │   │   └── TestResultEntity.java
    │   ├── repository/
    │   │   ├── JpaSpecificationRepository.java
    │   │   ├── JpaTestCaseRepository.java
    │   │   └── JpaTestResultRepository.java
    │   └── mapper/
    │       ├── SpecificationMapper.java
    │       └── TestCaseMapper.java
    └── config/
        ├── ApiTestingConfiguration.java
        ├── HttpClientConfiguration.java
        └── OpenApiConfiguration.java
```

### Module Dependencies
- **Depends on**: core
- **Exposed interfaces**: SpecificationRepository, TestCaseRepository, HttpClient
- **External libraries**: Swagger Parser, JSON Schema Validator

## Orchestration Feature

### Structure
```
features/orchestration/
├── domain/
│   ├── model/
│   │   ├── Workflow.java
│   │   ├── ExecutionContext.java
│   │   ├── ExecutionStep.java
│   │   ├── StepResult.java
│   │   └── WorkflowConfiguration.java
│   ├── service/
│   │   ├── WorkflowEngine.java
│   │   ├── StepExecutor.java
│   │   ├── ContextManager.java
│   │   └── VariableResolver.java
│   ├── repository/
│   │   ├── WorkflowRepository.java
│   │   └── ExecutionRepository.java
│   └── event/
│       ├── WorkflowCreatedEvent.java
│       ├── WorkflowStartedEvent.java
│       ├── WorkflowCompletedEvent.java
│       └── StepExecutedEvent.java
├── application/
│   ├── usecase/
│   │   ├── CreateWorkflowUseCase.java
│   │   ├── ExecuteWorkflowUseCase.java
│   │   ├── GetWorkflowStatusUseCase.java
│   │   ├── CancelWorkflowUseCase.java
│   │   └── GetExecutionHistoryUseCase.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CreateWorkflowRequest.java
│   │   │   └── ExecuteWorkflowRequest.java
│   │   └── response/
│   │       ├── WorkflowResponse.java
│   │       ├── ExecutionStatusResponse.java
│   │       └── ExecutionHistoryResponse.java
│   └── service/
│       └── OrchestrationApplicationService.java
└── infrastructure/
    ├── workflow/
    │   ├── DefaultWorkflowEngine.java
    │   ├── ParallelStepExecutor.java
    │   ├── SequentialStepExecutor.java
    │   └── ConditionalStepExecutor.java
    ├── execution/
    │   ├── ExecutionContextManager.java
    │   ├── StepResultAggregator.java
    │   └── VariableResolverImpl.java
    ├── persistence/
    │   ├── entity/
    │   │   ├── WorkflowEntity.java
    │   │   ├── ExecutionEntity.java
    │   │   └── StepEntity.java
    │   ├── repository/
    │   │   ├── JpaWorkflowRepository.java
    │   │   └── JpaExecutionRepository.java
    │   └── mapper/
    │       ├── WorkflowMapper.java
    │       └── ExecutionMapper.java
    └── config/
        ├── OrchestrationConfiguration.java
        └── ExecutionConfiguration.java
```

### Module Dependencies
- **Depends on**: core, bpmn-processing, api-testing
- **Exposed interfaces**: WorkflowEngine, WorkflowRepository
- **External libraries**: Reactor (for reactive execution)

## LLM Feature Module

### Structure
```
features/llm-integration/
├── domain/
│   ├── entities/
│   │   ├── LLMProvider.java
│   │   ├── LLMModel.java
│   │   ├── AiModel.java
│   │   └── PerformanceMetrics.java
│   ├── valueobjects/
│   │   ├── ModelId.java
│   │   └── ModelStatus.java
│   ├── services/
│   ├── repository/
│   └── event/
├── application/
│   ├── usecase/
│   ├── dto/
│   │   └── llm/
│   │       ├── ChatCompletionRequest.java
│   │       ├── ChatCompletionResponse.java
│   │       ├── LLMConfigResponse.java
│   │       ├── LLMTestRequest.java
│   │       ├── LLMTestResponse.java
│   │       ├── LocalModelInfo.java
│   │       └── PerformanceReportResponse.java
│   └── service/
├── infrastructure/
│   ├── services/
│   │   ├── OpenRouterClient.java
│   │   └── LocalLLMService.java
│   ├── config/
│   │   └── LLMConfig.java
│   ├── persistence/
│   └── config/
└── presentation/
    ├── controllers/
    │   └── LLMController.java
    └── dto/
```

### Core Components

#### Domain Layer
**Entities:**
- `LLMProvider` - Provider enumeration (LOCAL, OPENROUTER)
- `LLMModel` - LLM model configuration and metadata
- `AiModel` - AI model entity for test generation
- `PerformanceMetrics` - Performance tracking and cost analysis

**Value Objects:**
- `ModelId` - Type-safe model identification
- `ModelStatus` - Model lifecycle states (LOADING, LOADED, ERROR, etc.)

#### Infrastructure Layer
**Services:**
- `OpenRouterClient` - Cloud LLM integration with OpenRouter API
- `LocalLLMService` - Local LLM integration with Ollama
- `LLMConfig` - Comprehensive configuration management

**Key Features:**
- Async processing with `@Async` annotations
- Circuit breaker patterns for resilience
- Connection pooling and retry logic
- Performance monitoring and cost tracking

#### Application Layer
**DTOs:**
- Chat completion request/response for API communication
- Configuration management DTOs
- Testing and validation DTOs
- Performance reporting DTOs

### Module Dependencies
- **Depends on**: core
- **Exposed interfaces**: LLMService, ModelManager, PerformanceTracker
- **External libraries**: WebFlux, RestTemplate, Jackson

## Integration with LLM Module
The LLM feature integrates with AI Test Generation to provide:
- **Intelligent Test Data Generation**: LLM-powered test case creation
- **Context-Aware Testing**: API schema and BPMN process analysis
- **Security-Focused Generation**: LLM-enhanced security test scenarios
- **Edge Case Detection**: AI-powered boundary condition identification

## AI Test Generation Feature

### Structure
```
features/ai-test-generation/
├── domain/
│   ├── model/
│   │   ├── AiModel.java
│   │   ├── GenerationRequest.java
│   │   ├── GeneratedData.java
│   │   ├── TestData.java
│   │   ├── EdgeCase.java
│   │   └── ModelMetadata.java
│   ├── service/
│   │   ├── ModelLoader.java
│   │   ├── DataGenerator.java
│   │   ├── ModelValidator.java
│   │   └── ModelManager.java
│   ├── repository/
│   │   ├── AiModelRepository.java
│   │   └── GenerationRepository.java
│   └── event/
│       ├── ModelLoadedEvent.java
│       ├── DataGeneratedEvent.java
│       └── GenerationFailedEvent.java
├── application/
│   ├── usecase/
│   │   ├── LoadModelUseCase.java
│   │   ├── GenerateTestDataUseCase.java
│   │   ├── ValidateGenerationUseCase.java
│   │   ├── GetModelInfoUseCase.java
│   │   └── ListAvailableModelsUseCase.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoadModelRequest.java
│   │   │   └── GenerateDataRequest.java
│   │   └── response/
│   │       ├── ModelResponse.java
│   │       ├── GeneratedDataResponse.java
│   │       └── GenerationResultResponse.java
│   └── service/
│       └── AiGenerationApplicationService.java
└── infrastructure/
    ├── ai/
    │   ├── onnx/
    │   │   ├── OnnxModelLoader.java
    │   │   ├── OnnxDataGenerator.java
    │   │   └── OnnxModelValidator.java
    │   ├── tensorflow/
    │   │   ├── TensorFlowModelLoader.java
    │   │   ├── TensorFlowDataGenerator.java
    │   │   └── TensorFlowModelValidator.java
    │   └── pytorch/
    │       ├── PyTorchModelLoader.java
    │       ├── PyTorchDataGenerator.java
    │       └── PyTorchModelValidator.java
    ├── processing/
    │   ├── DataPreprocessor.java
    │   ├── OutputProcessor.java
    │   ├── ValidationProcessor.java
    │   └── ModelInputBuilder.java
    ├── persistence/
    │   ├── entity/
    │   │   ├── AiModelEntity.java
    │   │   └── GenerationEntity.java
    │   ├── repository/
    │   │   ├── JpaAiModelRepository.java
    │   │   └── JpaGenerationRepository.java
    │   └── mapper/
    │       ├── AiModelMapper.java
    │       └── GenerationMapper.java
    └── config/
        ├── AiConfiguration.java
        ├── OnnxConfiguration.java
        └── ModelConfiguration.java
```

### Module Dependencies
- **Depends on**: core
- **Exposed interfaces**: DataGenerator, ModelManager
- **External libraries**: ONNX Runtime, DeepLearning4J

## Shared Components

### Structure
```
shared/
├── config/
│   ├── ApplicationConfiguration.java
│   ├── SecurityConfiguration.java
│   ├── MonitoringConfiguration.java
│   └── ExternalServiceConfiguration.java
├── security/
│   ├── FileUploadValidator.java
│   ├── InputSanitizer.java
│   ├── SecurityHeaders.java
│   └── ValidationFilter.java
├── monitoring/
│   ├── MetricsService.java
│   ├── HealthIndicator.java
│   ├── LoggingAspect.java
│   └── PerformanceMonitor.java
└── common/
    ├── exception/
    │   ├── DomainException.java
    │   ├── ApplicationException.java
    │   └── InfrastructureException.java
    ├── util/
    │   ├── IdGenerator.java
    │   ├── FileUtils.java
    │   ├── JsonUtils.java
    │   └── ValidationUtils.java
    └── web/
        ├── GlobalExceptionHandler.java
        ├── ApiResponse.java
        └── RequestLoggingFilter.java
```

## Module Communication

### Dependency Injection
```java
@Configuration
public class FeatureConfiguration {

    @Bean
    public ProcessRepository processRepository(ProcessJpaRepository jpaRepo, ProcessMapper mapper) {
        return new JpaProcessRepository(jpaRepo, mapper);
    }

    @Bean
    public WorkflowEngine workflowEngine(ProcessExecutor processExecutor,
                                       TestCaseExecutor testExecutor,
                                       ExecutionContextManager contextManager) {
        return new DefaultWorkflowEngine(processExecutor, testExecutor, contextManager);
    }
}
```

### Event-Driven Communication
```java
@Component
public class CrossFeatureEventHandler {

    private final WorkflowRepository workflowRepository;
    private final NotificationService notificationService;

    @EventListener
    public void handleProcessCreated(ProcessCreatedEvent event) {
        // Update workflows that depend on this process
        List<Workflow> affectedWorkflows = workflowRepository.findByProcessId(event.getProcessId());
        affectedWorkflows.forEach(workflow -> {
            workflow.markAsNeedingUpdate();
            workflowRepository.save(workflow);
        });

        notificationService.notifyWorkflowsUpdated(affectedWorkflows);
    }

    @EventListener
    public void handleTestCaseExecuted(TestCaseExecutedEvent event) {
        // Trigger dependent workflow steps
        List<Workflow> dependentWorkflows = workflowRepository.findByTestCaseId(event.getTestCaseId());
        dependentWorkflows.forEach(workflow -> {
            // Logic to advance workflow state based on test results
        });
    }
}
```

## Build Configuration

### Gradle Multi-Module Setup
```kotlin
// settings.gradle.kts
include("core")
include("features:bpmn-processing")
include("features:api-testing")
include("features:orchestration")
include("features:ai-test-generation")
include("shared")

// features/orchestration/build.gradle.kts
dependencies {
    implementation(project(":core"))
    implementation(project(":features:bpmn-processing"))
    implementation(project(":features:api-testing"))
    implementation(project(":shared"))

    // External dependencies
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("io.projectreactor:reactor-core")
}
```

This modular structure ensures:
- **High cohesion** within each feature module
- **Low coupling** between modules
- **Independent deployment** and testing capabilities
- **Clear dependency management**
- **Scalability** as new features are added