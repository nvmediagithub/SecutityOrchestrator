# Infrastructure Layer Design

## Adapters and External Integrations

### BPMN Processing Infrastructure

#### BPMN Parser Adapter
```java
@Component
public class CamundaBpmnParser implements BpmnParser {

    private final BpmnModelInstanceFactory factory;

    @Override
    public Process parse(InputStream bpmnStream) {
        try {
            BpmnModelInstance modelInstance = Bpmn.readModelFromStream(bpmnStream);

            return convertToDomainModel(modelInstance);
        } catch (Exception e) {
            throw new BpmnParseException("Failed to parse BPMN file", e);
        }
    }

    private Process convertToDomainModel(BpmnModelInstance modelInstance) {
        Model model = modelInstance.getModel();
        Collection<RootElement> rootElements = model.getRootElements();

        Process process = null;
        for (RootElement rootElement : rootElements) {
            if (rootElement instanceof org.camunda.bpm.model.bpmn.instance.Process) {
                process = mapToProcess((org.camunda.bpm.model.bpmn.instance.Process) rootElement);
                break;
            }
        }

        return process;
    }

    private Process mapToProcess(org.camunda.bpm.model.bpmn.instance.Process camundaProcess) {
        List<FlowElement> elements = camundaProcess.getFlowElements().stream()
            .map(this::mapFlowElement)
            .collect(Collectors.toList());

        return Process.builder()
            .id(ProcessId.of(camundaProcess.getId()))
            .name(camundaProcess.getName())
            .elements(elements)
            .build();
    }
}
```

#### Process Execution Engine
```java
@Component
public class BpmnProcessExecutor implements ProcessExecutor {

    private final FlowElementExecutorFactory executorFactory;
    private final VariableResolver variableResolver;

    @Override
    public ExecutionResult execute(Process process, ExecutionContext context) {
        try {
            FlowElement startEvent = findStartEvent(process);
            ExecutionState state = new ExecutionState(startEvent);

            while (!state.isCompleted()) {
                FlowElement currentElement = state.getCurrentElement();
                ExecutionStep step = createExecutionStep(currentElement, context);

                StepResult result = executorFactory.getExecutor(currentElement.getType())
                    .execute(step);

                state = state.transition(result);
                context.addStep(step);
            }

            return ExecutionResult.success(context);
        } catch (Exception e) {
            return ExecutionResult.failure(e, context);
        }
    }

    private FlowElement findStartEvent(Process process) {
        return process.getElements().stream()
            .filter(element -> element.getType() == ElementType.START_EVENT)
            .findFirst()
            .orElseThrow(() -> new InvalidProcessException("No start event found"));
    }
}
```

### API Testing Infrastructure

#### OpenAPI Specification Parser
```java
@Component
public class SwaggerSpecificationParser implements SpecificationParser {

    private final ObjectMapper objectMapper;

    @Override
    public ApiSpecification parse(InputStream specStream) {
        try {
            OpenAPI openAPI = new OpenAPIV3Parser().readContents(
                IOUtils.toString(specStream, StandardCharsets.UTF_8)
            ).getOpenAPI();

            return convertToDomainModel(openAPI);
        } catch (Exception e) {
            throw new SpecificationParseException("Failed to parse OpenAPI specification", e);
        }
    }

    private ApiSpecification convertToDomainModel(OpenAPI openAPI) {
        Map<String, PathItem> paths = openAPI.getPaths().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> mapPathItem(entry.getValue())
            ));

        Components components = mapComponents(openAPI.getComponents());

        return ApiSpecification.builder()
            .id(SpecificationId.generate())
            .title(openAPI.getInfo().getTitle())
            .version(Version.parse(openAPI.getInfo().getVersion()))
            .openApiVersion(OpenApiVersion.fromString(openAPI.getOpenapi()))
            .paths(paths)
            .components(components)
            .servers(mapServers(openAPI.getServers()))
            .build();
    }
}
```

#### HTTP Client Adapter
```java
@Configuration
public class HttpClientConfiguration {

    @Bean
    public HttpClient httpClient(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(30))
            .additionalInterceptors(new LoggingInterceptor())
            .build();
    }
}

@Component
public class RestTemplateHttpClient implements HttpClient {

    private final RestTemplate restTemplate;
    private final ResponseExtractor responseExtractor;

    @Override
    public HttpResponse execute(HttpRequest request) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                request.getUrl(),
                request.getMethod(),
                createHttpEntity(request),
                String.class
            );

            return responseExtractor.extract(response);
        } catch (RestClientException e) {
            throw new HttpClientException("HTTP request failed", e);
        }
    }

    private HttpEntity<String> createHttpEntity(HttpRequest request) {
        HttpHeaders headers = new HttpHeaders();
        request.getHeaders().forEach(headers::set);

        return new HttpEntity<>(request.getBody(), headers);
    }
}
```

### Orchestration Infrastructure

#### Workflow Engine Implementation
```java
@Component
public class DefaultWorkflowEngine implements WorkflowEngine {

    private final ProcessExecutor processExecutor;
    private final TestCaseExecutor testExecutor;
    private final ExecutionContextManager contextManager;

    @Override
    public ExecutionResult execute(Workflow workflow) {
        ExecutionContext context = contextManager.create(workflow);

        try {
            // Execute BPMN process
            ExecutionResult processResult = processExecutor.execute(
                workflow.getProcess(),
                context
            );

            if (processResult.isSuccess()) {
                // Execute test cases based on process outcomes
                List<TestResult> testResults = executeTestCases(workflow, context);
                return combineResults(processResult, testResults);
            } else {
                return processResult;
            }

        } catch (Exception e) {
            contextManager.saveFailed(context, e);
            throw e;
        } finally {
            contextManager.save(context);
        }
    }

    private List<TestResult> executeTestCases(Workflow workflow, ExecutionContext context) {
        return workflow.getTestCases().stream()
            .map(testCase -> testExecutor.execute(testCase, context))
            .collect(Collectors.toList());
    }
}
```

### LLM Integration Infrastructure

#### LLM Configuration Management
```java
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLMConfig {
    
    // OpenRouter settings
    private String openRouterApiKey;
    private String openRouterBaseUrl = "https://openrouter.ai/api/v1";
    private int openRouterTimeout = 30;
    
    // Local LLM settings
    private String localServerUrl = "http://localhost:11434";
    private int localTimeout = 300;
    private int maxLocalModels = 3;
    
    // Performance settings
    private int maxRetries = 3;
    private int connectionPoolSize = 10;
    private int maxConcurrentRequests = 5;
    
    // Model configurations
    private Map<String, ModelConfig> models = new HashMap<>();
    
    public static class ModelConfig {
        private String provider = "LOCAL";
        private int contextWindow = 4096;
        private int maxTokens = 2048;
        private double temperature = 0.7;
        // ... additional configuration
    }
}
```

#### OpenRouter Client Implementation
```java
@Service
public class OpenRouterClient {
    
    private final LLMConfig config;
    private final RestTemplate restTemplate;
    private final CircuitBreaker circuitBreaker;
    
    @Async
    public CompletableFuture<ChatCompletionResponse> createChatCompletion(
            ChatCompletionRequest request) {
        
        return circuitBreaker.executeSupplier(() -> {
            try {
                HttpHeaders headers = createHeaders();
                HttpEntity<ChatCompletionRequest> entity =
                    new HttpEntity<>(request, headers);
                
                ResponseEntity<ChatCompletionResponse> response = restTemplate
                    .postForEntity(config.getOpenRouterBaseUrl() + "/chat/completions",
                                  entity,
                                  ChatCompletionResponse.class);
                
                return response.getBody();
                
            } catch (HttpClientErrorException e) {
                throw new OpenRouterException("OpenRouter request failed: " +
                    extractErrorMessage(e), e);
            }
        });
    }
    
    @Async
    public CompletableFuture<List<String>> listModels() {
        return circuitBreaker.executeSupplier(() -> {
            // Implementation for listing available models
        });
    }
    
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + config.getOpenRouterApiKey());
        headers.set("Content-Type", "application/json");
        headers.set("X-Title", config.getOpenRouterAppName());
        if (config.getOpenRouterReferer() != null) {
            headers.set("HTTP-Referer", config.getOpenRouterReferer());
        }
        return headers;
    }
}
```

#### Local LLM Service Implementation
```java
@Service
public class LocalLLMService {
    
    private final LLMConfig config;
    private final RestTemplate restTemplate;
    private final Map<String, Boolean> loadedModels = new ConcurrentHashMap<>();
    
    @Async
    public CompletableFuture<ChatCompletionResponse> generateCompletion(
            String model, String prompt, Map<String, Object> parameters) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!isOllamaRunning()) {
                    throw new LocalLLMNotRunningException("Ollama service is not running");
                }
                
                Map<String, Object> request = buildOllamaRequest(model, prompt, parameters);
                
                ResponseEntity<OllamaResponse> response = restTemplate
                    .postForEntity(config.getLocalServerUrl() + "/api/generate",
                                  request,
                                  OllamaResponse.class);
                
                return convertToChatResponse(response.getBody());
                
            } catch (Exception e) {
                throw new LocalLLMException("Local LLM request failed", e);
            }
        });
    }
    
    @Async
    public CompletableFuture<List<LocalModelInfo>> listLocalModels() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<OllamaModelsResponse> response = restTemplate
                    .getForEntity(config.getLocalServerUrl() + "/api/tags",
                                  OllamaModelsResponse.class);
                
                return response.getBody().getModels().stream()
                    .map(this::convertToLocalModelInfo)
                    .collect(Collectors.toList());
                    
            } catch (Exception e) {
                throw new LocalLLMException("Failed to list local models", e);
            }
        });
    }
    
    private boolean isOllamaRunning() {
        try {
            restTemplate.getForEntity(config.getLocalServerUrl() + "/api/tags", String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

#### Circuit Breaker Configuration
```java
@Configuration
public class LLMResilienceConfiguration {
    
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }
    
    @Bean
    public CircuitBreaker openRouterCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("openRouter",
            CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .build());
    }
    
    @Bean
    public CircuitBreaker localLLMCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("localLLM",
            CircuitBreakerConfig.custom()
                .failureRateThreshold(30)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .slidingWindowSize(5)
                .build());
    }
}
```

### AI Integration Infrastructure

#### ONNX Model Loader
```java
@Component
public class OnnxModelLoader implements ModelLoader {

    private final OrtEnvironment environment;

    @Override
    public AiModel load(Path modelPath) {
        try {
            OrtSession session = environment.createSession(modelPath.toString(),
                new OrtSession.SessionOptions());

            ModelMetadata metadata = extractMetadata(session);
            OnnxModel model = new OnnxModel(session, metadata);

            return AiModel.builder()
                .id(ModelId.generate())
                .name(extractName(modelPath))
                .type(ModelType.ONNX)
                .modelPath(modelPath)
                .metadata(metadata)
                .status(ModelStatus.LOADED)
                .build();

        } catch (OrtException e) {
            throw new ModelLoadException("Failed to load ONNX model", e);
        }
    }

    private ModelMetadata extractMetadata(OrtSession session) {
        Map<String, NodeInfo> inputInfo = session.getInputInfo();
        Map<String, NodeInfo> outputInfo = session.getOutputInfo();

        return new ModelMetadata(
            extractTensorInfo(inputInfo),
            extractTensorInfo(outputInfo)
        );
    }
}
```

#### Data Generator Implementation
```java
@Component
public class OnnxDataGenerator implements DataGenerator {

    private final ModelManager modelManager;
    private final DataProcessor dataProcessor;

    @Override
    public GeneratedData generate(GenerationRequest request) {
        AiModel model = modelManager.getLoadedModel(request.getModelId());

        // Prepare input tensors
        Map<String, OnnxTensor> inputs = prepareInputs(request);

        try {
            // Run inference
            OrtSession.Result result = model.getSession().run(inputs);

            // Process outputs
            GeneratedData data = processOutputs(result, request);

            // Validate generated data
            ValidationResult validation = validateGeneratedData(data, request);

            return data.withValidation(validation);

        } catch (OrtException e) {
            throw new GenerationException("AI model inference failed", e);
        }
    }

    private Map<String, OnnxTensor> prepareInputs(GenerationRequest request) {
        // Convert API specification and context to model inputs
        // Implementation depends on specific model requirements
        return dataProcessor.prepareModelInputs(request);
    }
}
```

## Persistence Layer

### Repository Implementations

#### JPA Repository for Processes
```java
@Repository
public class JpaProcessRepository implements ProcessRepository {

    private final ProcessJpaRepository jpaRepository;
    private final ProcessMapper mapper;

    @Override
    public Optional<Process> findById(ProcessId id) {
        return jpaRepository.findById(id.getValue())
            .map(mapper::toDomain);
    }

    @Override
    public Process save(Process process) {
        ProcessEntity entity = mapper.toEntity(process);
        ProcessEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Process> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
}
```

#### File System Repository for Specifications
```java
@Repository
public class FileSystemSpecificationRepository implements SpecificationRepository {

    private final Path basePath;
    private final SpecificationMapper mapper;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<ApiSpecification> findById(SpecificationId id) {
        Path specPath = basePath.resolve(id.getValue() + ".json");

        if (!Files.exists(specPath)) {
            return Optional.empty();
        }

        try {
            String content = Files.readString(specPath);
            SpecificationEntity entity = objectMapper.readValue(content, SpecificationEntity.class);
            return Optional.of(mapper.toDomain(entity));

        } catch (IOException e) {
            throw new SpecificationLoadException("Failed to load specification", e);
        }
    }

    @Override
    public ApiSpecification save(ApiSpecification spec) {
        try {
            SpecificationEntity entity = mapper.toEntity(spec);
            String content = objectMapper.writeValueAsString(entity);

            Path specPath = basePath.resolve(spec.getId().getValue() + ".json");
            Files.writeString(specPath, content);

            return spec;

        } catch (IOException e) {
            throw new SpecificationSaveException("Failed to save specification", e);
        }
    }
}
```

## External Service Integrations

### Configuration for External Services
```java
@Configuration
@ConfigurationProperties(prefix = "security.orchestrator")
public class OrchestratorConfiguration {

    private AiConfig ai = new AiConfig();
    private ApiConfig api = new ApiConfig();
    private PersistenceConfig persistence = new PersistenceConfig();

    public static class AiConfig {
        private Path modelDirectory = Path.of("models");
        private Duration modelLoadTimeout = Duration.ofMinutes(5);
        private Map<String, ModelConfig> models = new HashMap<>();
    }

    public static class ApiConfig {
        private Duration requestTimeout = Duration.ofSeconds(30);
        private int maxRetries = 3;
        private Duration retryDelay = Duration.ofSeconds(1);
    }

    public static class PersistenceConfig {
        private Path dataDirectory = Path.of("data");
        private boolean enableBackup = true;
        private Duration backupInterval = Duration.ofHours(24);
    }
}
```

### Health Checks and Monitoring
```java
@Component
public class InfrastructureHealthIndicator implements HealthIndicator {

    private final ModelManager modelManager;
    private final HttpClient httpClient;
    private final DataSource dataSource;

    @Override
    public Health health() {
        Health.Builder builder = Health.up();

        // Check AI models
        if (!modelManager.hasLoadedModels()) {
            builder.withDetail("ai-models", "No models loaded")
                   .status(Status.DOWN);
        }

        // Check HTTP client
        try {
            httpClient.ping();
            builder.withDetail("http-client", "OK");
        } catch (Exception e) {
            builder.withDetail("http-client", e.getMessage())
                   .status(Status.DOWN);
        }

        // Check database
        try {
            dataSource.getConnection().close();
            builder.withDetail("database", "OK");
        } catch (SQLException e) {
            builder.withDetail("database", e.getMessage())
                   .status(Status.DOWN);
        }

        return builder.build();
    }
}
```

## Error Handling and Resilience

### Circuit Breaker Configuration
```java
@Configuration
public class ResilienceConfiguration {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }

    @Bean
    public CircuitBreaker aiModelCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("aiModel",
            CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .build());
    }

    @Bean
    public CircuitBreaker httpClientCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("httpClient",
            CircuitBreakerConfig.custom()
                .failureRateThreshold(20)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build());
    }
}
```

This infrastructure layer design provides robust adapters for external systems while maintaining clean separation from the domain and application layers.