# Application Layer Design

## Use Cases and Application Services

### BPMN Processing Feature

#### Process Management Use Cases
```java
public class CreateProcessUseCase {
    private final ProcessRepository processRepository;
    private final ProcessValidator processValidator;
    private final EventPublisher eventPublisher;

    public Process create(CreateProcessCommand command) {
        Process process = Process.builder()
            .id(ProcessId.generate())
            .name(command.getName())
            .elements(command.getElements())
            .build();

        ValidationResult validation = processValidator.validate(process);
        if (!validation.isValid()) {
            throw new ValidationException(validation.getErrors());
        }

        Process saved = processRepository.save(process);
        eventPublisher.publish(new ProcessCreatedEvent(saved.getId()));

        return saved;
    }
}

public class ExecuteProcessUseCase {
    private final ProcessExecutor processExecutor;
    private final ExecutionContextRepository contextRepository;

    public ExecutionResult execute(ExecuteProcessCommand command) {
        Process process = getProcess(command.getProcessId());
        ExecutionContext context = createExecutionContext(process, command.getVariables());

        ExecutionResult result = processExecutor.execute(process, context);
        contextRepository.save(context);

        return result;
    }
}
```

#### Commands and Queries
```java
public class CreateProcessCommand {
    private String name;
    private List<FlowElement> elements;
    private Map<String, Object> initialVariables;
}

public class ExecuteProcessCommand {
    private ProcessId processId;
    private Map<String, Object> variables;
    private ExecutionMode mode;
}

public class GetProcessQuery {
    private ProcessId processId;
}

public class ListProcessesQuery {
    private ProcessStatus status;
    private Pagination pagination;
}
```

### API Testing Feature

#### Test Case Management Use Cases
```java
public class GenerateTestCasesUseCase {
    private final SpecificationRepository specRepository;
    private final TestCaseGenerator generator;
    private final TestCaseRepository testCaseRepository;

    public List<TestCase> generate(GenerateTestCasesCommand command) {
        ApiSpecification spec = specRepository.findById(command.getSpecId())
            .orElseThrow(() -> new SpecificationNotFoundException());

        List<TestCase> testCases = generator.generate(spec);

        return testCaseRepository.saveAll(testCases);
    }
}

public class ExecuteTestCaseUseCase {
    private final TestCaseRepository testCaseRepository;
    private final HttpClient httpClient;
    private final ResponseValidator validator;

    public TestResult execute(ExecuteTestCaseCommand command) {
        TestCase testCase = testCaseRepository.findById(command.getTestCaseId())
            .orElseThrow(() -> new TestCaseNotFoundException());

        HttpRequest request = buildRequest(testCase, command.getTestData());
        HttpResponse response = httpClient.execute(request);

        ValidationResult validation = validator.validate(response, testCase.getExpectedResponses());
        TestResult result = createTestResult(testCase, response, validation);

        eventPublisher.publish(new TestCaseExecutedEvent(testCase.getId(), result));

        return result;
    }
}
```

#### Commands and Queries
```java
public class GenerateTestCasesCommand {
    private SpecificationId specId;
    private GenerationOptions options;
}

public class ExecuteTestCaseCommand {
    private TestCaseId testCaseId;
    private TestData testData;
}

public class GetTestResultsQuery {
    private TestCaseId testCaseId;
    private DateRange dateRange;
}
```

### Orchestration Feature

#### Workflow Management Use Cases
```java
public class CreateWorkflowUseCase {
    private final WorkflowRepository workflowRepository;
    private final ProcessRepository processRepository;
    private final TestCaseRepository testCaseRepository;

    public Workflow create(CreateWorkflowCommand command) {
        Process process = processRepository.findById(command.getProcessId())
            .orElseThrow(() -> new ProcessNotFoundException());

        List<TestCase> testCases = testCaseRepository.findAllByIds(command.getTestCaseIds());

        Workflow workflow = Workflow.builder()
            .id(WorkflowId.generate())
            .name(command.getName())
            .process(process)
            .testCases(testCases)
            .config(command.getConfig())
            .build();

        return workflowRepository.save(workflow);
    }
}

public class ExecuteWorkflowUseCase {
    private final WorkflowEngine workflowEngine;
    private final WorkflowRepository workflowRepository;

    public ExecutionResult execute(ExecuteWorkflowCommand command) {
        Workflow workflow = workflowRepository.findById(command.getWorkflowId())
            .orElseThrow(() -> new WorkflowNotFoundException());

        ExecutionResult result = workflowEngine.execute(workflow);

        workflow.updateStatus(result.getStatus());
        workflowRepository.save(workflow);

        return result;
    }
}
```

### AI Test Generation Feature

#### Data Generation Use Cases
```java
public class GenerateTestDataUseCase {
    private final AiModelRepository modelRepository;
    private final DataGenerator dataGenerator;
    private final SpecificationRepository specRepository;

    public GeneratedData generate(GenerateTestDataCommand command) {
        AiModel model = modelRepository.findById(command.getModelId())
            .orElseThrow(() -> new ModelNotFoundException());

        ApiSpecification spec = specRepository.findById(command.getSpecId())
            .orElseThrow(() -> new SpecificationNotFoundException());

        GenerationRequest request = new GenerationRequest(
            spec,
            command.getPath(),
            command.getOperation(),
            command.getContext()
        );

        return dataGenerator.generate(request);
    }
}

public class LoadAiModelUseCase {
    private final ModelLoader modelLoader;
    private final AiModelRepository modelRepository;

    public AiModel load(LoadModelCommand command) {
        AiModel model = modelLoader.load(command.getModelPath());

        ValidationResult validation = modelValidator.validate(model);
        if (!validation.isValid()) {
            throw new InvalidModelException(validation.getErrors());
        }

        return modelRepository.save(model);
    }
}
```

## Data Transfer Objects (DTOs)

### Request DTOs
```java
public class CreateProcessRequest {
    @NotBlank
    private String name;

    @NotEmpty
    @Valid
    private List<FlowElementDto> elements;

    private Map<String, Object> initialVariables;
}

public class ExecuteTestCaseRequest {
    @NotNull
    private TestCaseId testCaseId;

    private TestDataDto testData;
}

public class CreateWorkflowRequest {
    @NotBlank
    private String name;

    @NotNull
    private ProcessId processId;

    @NotEmpty
    private List<TestCaseId> testCaseIds;

    private WorkflowConfigurationDto config;
}
```

### Response DTOs
```java
public class ProcessResponse {
    private ProcessId id;
    private String name;
    private Version version;
    private ProcessStatus status;
    private List<FlowElementDto> elements;
    private Instant createdAt;
    private Instant updatedAt;
}

public class TestResultResponse {
    private TestExecutionId executionId;
    private TestStatus status;
    private int actualStatusCode;
    private String responseBody;
    private List<ValidationErrorDto> validationErrors;
    private long executionTimeMs;
    private Instant executedAt;
}

public class WorkflowExecutionResponse {
    private ExecutionId executionId;
    private WorkflowStatus status;
    private List<StepResultDto> stepResults;
    private Map<String, Object> finalVariables;
    private Duration totalExecutionTime;
    private Instant startedAt;
    private Instant completedAt;
}
```

## Application Services

### Orchestration Service
```java
@Service
public class WorkflowOrchestrationService {

    private final WorkflowRepository workflowRepository;
    private final ProcessExecutor processExecutor;
    private final TestCaseExecutor testExecutor;
    private final EventPublisher eventPublisher;

    @Transactional
    public ExecutionResult orchestrateWorkflow(WorkflowId workflowId) {
        Workflow workflow = workflowRepository.findById(workflowId)
            .orElseThrow(() -> new WorkflowNotFoundException());

        ExecutionContext context = new ExecutionContext(workflow);

        try {
            // Execute BPMN process
            ExecutionResult processResult = processExecutor.execute(
                workflow.getProcess(),
                context
            );

            // Execute test cases based on process flow
            List<TestResult> testResults = executeTestCases(workflow, context);

            ExecutionResult finalResult = combineResults(processResult, testResults);

            eventPublisher.publish(new WorkflowCompletedEvent(workflowId, finalResult));

            return finalResult;

        } catch (Exception e) {
            handleExecutionError(workflow, e);
            throw e;
        }
    }

    private List<TestResult> executeTestCases(Workflow workflow, ExecutionContext context) {
        return workflow.getTestCases().stream()
            .map(testCase -> executeTestCase(testCase, context))
            .collect(Collectors.toList());
    }

    private TestResult executeTestCase(TestCase testCase, ExecutionContext context) {
        TestData testData = resolveTestData(testCase, context);
        return testExecutor.execute(testCase, testData);
    }
}
```

### Validation Service
```java
@Service
public class ValidationApplicationService {

    private final ProcessValidator processValidator;
    private final SpecificationValidator specValidator;
    private final WorkflowValidator workflowValidator;

    public ValidationResult validateProcess(Process process) {
        return processValidator.validate(process);
    }

    public ValidationResult validateSpecification(ApiSpecification spec) {
        return specValidator.validate(spec);
    }

    public ValidationResult validateWorkflow(Workflow workflow) {
        return workflowValidator.validate(workflow);
    }

    public ValidationResult validateAll(ValidationRequest request) {
        List<ValidationError> allErrors = new ArrayList<>();

        if (request.getProcess() != null) {
            allErrors.addAll(validateProcess(request.getProcess()).getErrors());
        }

        if (request.getSpecification() != null) {
            allErrors.addAll(validateSpecification(request.getSpecification()).getErrors());
        }

        if (request.getWorkflow() != null) {
            allErrors.addAll(validateWorkflow(request.getWorkflow()).getErrors());
        }

        return new ValidationResult(allErrors.isEmpty(), allErrors);
    }
}
```

## Event Handling

### Application Event Handlers
```java
@Component
public class ProcessEventHandler {

    private final NotificationService notificationService;
    private final AuditService auditService;

    @EventListener
    public void handleProcessCreated(ProcessCreatedEvent event) {
        auditService.logProcessCreation(event.getProcessId());
        notificationService.notifyProcessCreated(event.getProcessId());
    }

    @EventListener
    public void handleProcessExecuted(ProcessExecutedEvent event) {
        auditService.logProcessExecution(event.getProcessId(), event.getResult());
    }
}

@Component
public class TestExecutionEventHandler {

    private final MetricsService metricsService;
    private final AlertService alertService;

    @EventListener
    public void handleTestCaseExecuted(TestCaseExecutedEvent event) {
        metricsService.recordTestExecution(event.getResult());
        if (event.getResult().getStatus() == TestStatus.FAILED) {
            alertService.sendTestFailureAlert(event.getTestCaseId(), event.getResult());
        }
    }
}
```

## Cross-Cutting Concerns

### Transaction Management
- All use cases are transactional
- Read operations use read-only transactions
- Write operations use required transactions
- Distributed transactions for multi-service operations

### Error Handling
- Domain exceptions for business rule violations
- Infrastructure exceptions for external service failures
- Application exceptions for use case failures
- Global exception handler for consistent error responses

### Logging and Monitoring
- Structured logging for all use case executions
- Performance metrics for critical operations
- Health checks for external service dependencies

This application layer design ensures clean separation between business logic and infrastructure concerns, while providing a clear API for the presentation layer.