# Domain Model Specification

## Core Domain Entities

### BPMN Processing Feature

#### Process Entity
```java
public class Process {
    private ProcessId id;
    private String name;
    private Version version;
    private List<FlowElement> elements;
    private ProcessStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
```

#### FlowElement Hierarchy
```java
public abstract class FlowElement {
    protected ElementId id;
    protected String name;
    protected ElementType type;
}

public class Task extends FlowElement {
    private TaskType taskType;
    private Map<String, Object> properties;
}

public class Gateway extends FlowElement {
    private GatewayType gatewayType;
    private List<SequenceFlow> incoming;
    private List<SequenceFlow> outgoing;
}

public class Event extends FlowElement {
    private EventType eventType;
    private EventDefinition definition;
}
```

### API Testing Feature

#### ApiSpecification Entity
```java
public class ApiSpecification {
    private SpecificationId id;
    private String title;
    private Version version;
    private OpenApiVersion openApiVersion;
    private Map<String, PathItem> paths;
    private Components components;
    private List<Server> servers;
}
```

#### TestCase Entity
```java
public class TestCase {
    private TestCaseId id;
    private String name;
    private HttpMethod method;
    private String path;
    private Map<String, Parameter> parameters;
    private RequestBody requestBody;
    private List<ResponseDefinition> expectedResponses;
    private TestData testData;
}
```

#### TestResult Value Object
```java
public class TestResult {
    private TestExecutionId executionId;
    private TestStatus status;
    private HttpStatusCode actualStatusCode;
    private String responseBody;
    private Map<String, ValidationError> validationErrors;
    private Duration executionTime;
    private Instant executedAt;
}
```

### Orchestration Feature

#### Workflow Entity
```java
public class Workflow {
    private WorkflowId id;
    private String name;
    private Process process;
    private List<TestCase> testCases;
    private WorkflowConfiguration config;
    private WorkflowStatus status;
}
```

#### Execution Context
```java
public class ExecutionContext {
    private ExecutionId id;
    private Workflow workflow;
    private Map<String, Object> variables;
    private ExecutionState currentState;
    private List<ExecutionStep> steps;
    private Instant startedAt;
    private Instant completedAt;
}
```

### AI Test Generation Feature

#### Model Entity
```java
public class AiModel {
    private ModelId id;
    private String name;
    private ModelType type;
    private Path modelPath;
    private ModelMetadata metadata;
    private ModelStatus status;
}
```

#### GenerationRequest Value Object
```java
public class GenerationRequest {
    private RequestId id;
    private ApiSpecification spec;
    private PathItem path;
    private Operation operation;
    private GenerationContext context;
}
```

#### GeneratedData Value Object
```java
public class GeneratedData {
    private List<TestData> testDataSets;
    private List<EdgeCase> edgeCases;
    private GenerationMetadata metadata;
    private ValidationResult validation;
}
```

## Domain Services

### BPMN Domain Services
```java
public interface ProcessValidator {
    ValidationResult validate(Process process);
}

public interface ProcessExecutor {
    ExecutionResult execute(Process process, ExecutionContext context);
}

public interface ElementResolver {
    FlowElement resolve(ElementId id, Process process);
}
```

### API Testing Domain Services
```java
public interface SpecificationParser {
    ApiSpecification parse(InputStream input);
}

public interface TestCaseGenerator {
    List<TestCase> generate(ApiSpecification spec);
}

public interface ResponseValidator {
    ValidationResult validate(Response response, ResponseDefinition expected);
}
```

### Orchestration Domain Services
```java
public interface WorkflowEngine {
    ExecutionResult execute(Workflow workflow);
}

public interface StepExecutor {
    StepResult execute(ExecutionStep step, ExecutionContext context);
}

public interface VariableResolver {
    Object resolve(String expression, ExecutionContext context);
}
```

### AI Generation Domain Services
```java
public interface ModelLoader {
    AiModel load(Path modelPath);
}

public interface DataGenerator {
    GeneratedData generate(GenerationRequest request);
}

public interface ModelValidator {
    ValidationResult validate(AiModel model);
}
```

## Value Objects

### Common Value Objects
```java
public class Version {
    private int major;
    private int minor;
    private int patch;
}

public class ValidationResult {
    private boolean valid;
    private List<ValidationError> errors;
}

public class ValidationError {
    private String field;
    private String message;
    private ErrorSeverity severity;
}
```

### BPMN Value Objects
```java
public class SequenceFlow {
    private FlowId id;
    private ElementId sourceRef;
    private ElementId targetRef;
    private Expression condition;
}

public class ElementId {
    private String value;
}

public class ProcessId {
    private String value;
}
```

### API Testing Value Objects
```java
public class Parameter {
    private String name;
    private ParameterLocation location;
    private Schema schema;
    private boolean required;
}

public class RequestBody {
    private Schema schema;
    private boolean required;
    private Map<String, MediaType> content;
}
```

## Domain Events

```java
public class ProcessCreatedEvent {
    private ProcessId processId;
    private Instant occurredAt;
}

public class TestCaseExecutedEvent {
    private TestCaseId testCaseId;
    private TestResult result;
    private Instant occurredAt;
}

public class WorkflowCompletedEvent {
    private WorkflowId workflowId;
    private ExecutionResult result;
    private Instant occurredAt;
}
```

## Repositories

### Abstract Repository Interfaces
```java
public interface ProcessRepository {
    Optional<Process> findById(ProcessId id);
    List<Process> findAll();
    Process save(Process process);
    void delete(ProcessId id);
}

public interface TestCaseRepository {
    Optional<TestCase> findById(TestCaseId id);
    List<TestCase> findBySpecification(SpecificationId specId);
    TestCase save(TestCase testCase);
}

public interface WorkflowRepository {
    Optional<Workflow> findById(WorkflowId id);
    Workflow save(Workflow workflow);
    List<Workflow> findByStatus(WorkflowStatus status);
}
```

## Domain Rules and Invariants

### Process Invariants
- A process must have at least one start event
- All sequence flows must connect valid elements
- Process names must be unique within a workspace

### Test Case Invariants
- Test cases must reference valid API operations
- Parameters must match the operation definition
- Expected responses must be defined in the specification

### Workflow Invariants
- Workflows must reference existing processes and test cases
- Execution contexts must maintain variable scope
- Steps must execute in dependency order

This domain model provides the foundation for implementing clean architecture principles, ensuring that business logic remains independent of infrastructure concerns.