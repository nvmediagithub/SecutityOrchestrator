# API Contracts and Integration Points

## REST API Specification

The SecurityOrchestrator exposes a RESTful API following OpenAPI 3.0 standards. The API is organized around the core features with clear resource hierarchies and consistent response formats.

### Base API Information
```yaml
openapi: 3.0.3
info:
  title: SecurityOrchestrator API
  version: 1.0.0
  description: API for orchestrating BPMN workflows and API testing
servers:
  - url: http://localhost:8080/api/v1
    description: Local development server
```

### Common Response Formats

#### Success Response
```json
{
  "success": true,
  "data": {
    "id": "resource-id",
    "createdAt": "2023-11-05T11:22:33.169Z",
    "updatedAt": "2023-11-05T11:22:33.169Z"
  },
  "meta": {
    "requestId": "req-123",
    "timestamp": "2023-11-05T11:22:33.169Z"
  }
}
```

#### Error Response
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid request parameters",
    "details": [
      {
        "field": "name",
        "message": "Name is required"
      }
    ]
  },
  "meta": {
    "requestId": "req-123",
    "timestamp": "2023-11-05T11:22:33.169Z"
  }
}
```

### BPMN Processing API

#### Process Management
```yaml
paths:
  /processes:
    post:
      summary: Create a new BPMN process
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                  description: BPMN 2.0 XML file
                name:
                  type: string
                  description: Optional process name override
      responses:
        '201':
          description: Process created successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProcessResponse'

    get:
      summary: List all processes
      parameters:
        - name: status
          in: query
          schema:
            type: string
            enum: [ACTIVE, INACTIVE, ARCHIVED]
        - name: page
          in: query
          schema:
            type: integer
            minimum: 0
            default: 0
        - name: size
          in: query
          schema:
            type: integer
            minimum: 1
            maximum: 100
            default: 20
      responses:
        '200':
          description: List of processes
          content:
            application/json:
              schema:
                type: object
                properties:
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/ProcessSummary'
                  meta:
                    $ref: '#/components/schemas/PaginationMeta'

  /processes/{processId}:
    get:
      summary: Get process details
      parameters:
        - name: processId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Process details
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProcessResponse'

    put:
      summary: Update process
      parameters:
        - name: processId
          in: path
          required: true
          schema:
            type: string
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProcessUpdateRequest'
      responses:
        '200':
          description: Process updated
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProcessResponse'

    delete:
      summary: Delete process
      parameters:
        - name: processId
          in: path
          required: true
          schema:
            type: string
      responses:
        '204':
          description: Process deleted

  /processes/{processId}/execute:
    post:
      summary: Execute a BPMN process
      parameters:
        - name: processId
          in: path
          required: true
          schema:
            type: string
      requestBody:
        required: false
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ProcessExecutionRequest'
      responses:
        '200':
          description: Process execution result
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ExecutionResultResponse'
```

### API Testing API

#### Specification Management
```yaml
paths:
  /specifications:
    post:
      summary: Load OpenAPI specification
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                  description: OpenAPI 3.0+ JSON/YAML file
                name:
                  type: string
                  description: Optional specification name
      responses:
        '201':
          description: Specification loaded successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/SpecificationResponse'

    get:
      summary: List all specifications
      responses:
        '200':
          description: List of specifications
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/SpecificationSummary'

  /specifications/{specId}/test-cases:
    post:
      summary: Generate test cases from specification
      parameters:
        - name: specId
          in: path
          required: true
          schema:
            type: string
      requestBody:
        required: false
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/TestCaseGenerationRequest'
      responses:
        '201':
          description: Test cases generated
          content:
            application/json:
              schema:
                type: object
                properties:
                  generated:
                    type: integer
                  testCases:
                    type: array
                    items:
                      $ref: '#/components/schemas/TestCaseResponse'

    get:
      summary: Get test cases for specification
      parameters:
        - name: specId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: List of test cases
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/TestCaseSummary'

  /test-cases/{testCaseId}/execute:
    post:
      summary: Execute a test case
      parameters:
        - name: testCaseId
          in: path
          required: true
          schema:
            type: string
      requestBody:
        required: false
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/TestExecutionRequest'
      responses:
        '200':
          description: Test execution result
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TestResultResponse'
```

### Orchestration API

#### Workflow Management
```yaml
paths:
  /workflows:
    post:
      summary: Create a new workflow
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/WorkflowCreationRequest'
      responses:
        '201':
          description: Workflow created successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/WorkflowResponse'

    get:
      summary: List all workflows
      parameters:
        - name: status
          in: query
          schema:
            type: string
            enum: [DRAFT, ACTIVE, RUNNING, COMPLETED, FAILED]
      responses:
        '200':
          description: List of workflows
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/WorkflowSummary'

  /workflows/{workflowId}:
    get:
      summary: Get workflow details
      parameters:
        - name: workflowId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Workflow details
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/WorkflowResponse'

  /workflows/{workflowId}/execute:
    post:
      summary: Execute a workflow
      parameters:
        - name: workflowId
          in: path
          required: true
          schema:
            type: string
      requestBody:
        required: false
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/WorkflowExecutionRequest'
      responses:
        '202':
          description: Workflow execution started
          headers:
            Location:
              schema:
                type: string
              description: URL to check execution status
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ExecutionStartedResponse'

  /workflows/{workflowId}/status:
    get:
      summary: Get workflow execution status
      parameters:
        - name: workflowId
          in: path
          required: true
          schema:
            type: string
      responses:
        '200':
          description: Current execution status
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ExecutionStatusResponse'
```

### AI Test Generation API

#### Model Management
```yaml
paths:
  /ai/models:
    post:
      summary: Load an AI model
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                  description: AI model file (ONNX, TensorFlow, PyTorch)
                name:
                  type: string
                  description: Model name
                type:
                  type: string
                  enum: [ONNX, TENSORFLOW, PYTORCH]
      responses:
        '201':
          description: Model loaded successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ModelResponse'

    get:
      summary: List loaded models
      responses:
        '200':
          description: List of loaded models
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/ModelSummary'

  /ai/generate:
    post:
      summary: Generate test data using AI
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/DataGenerationRequest'
      responses:
        '200':
          description: Generated test data
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/GeneratedDataResponse'
```

## WebSocket Integration

### Real-time Execution Monitoring
```javascript
// WebSocket endpoint: /ws/executions
const socket = new WebSocket('ws://localhost:8080/ws/executions');

socket.onmessage = (event) => {
  const update = JSON.parse(event.data);
  switch(update.type) {
    case 'EXECUTION_STARTED':
      // Handle execution start
      break;
    case 'STEP_COMPLETED':
      // Handle step completion
      updateExecutionProgress(update.stepId, update.result);
      break;
    case 'EXECUTION_COMPLETED':
      // Handle execution completion
      showFinalResults(update.results);
      break;
    case 'EXECUTION_FAILED':
      // Handle execution failure
      showError(update.error);
      break;
  }
};

// Send subscription message
socket.send(JSON.stringify({
  type: 'SUBSCRIBE',
  executionId: 'exec-123'
}));
```

## Data Schemas

### Common Schemas
```yaml
components:
  schemas:
    PaginationMeta:
      type: object
      properties:
        page:
          type: integer
        size:
          type: integer
        totalElements:
          type: integer
        totalPages:
          type: integer

    ErrorDetail:
      type: object
      properties:
        field:
          type: string
        message:
          type: string
        code:
          type: string

    ApiError:
      type: object
      properties:
        code:
          type: string
        message:
          type: string
        details:
          type: array
          items:
            $ref: '#/components/schemas/ErrorDetail'

    ApiResponse:
      type: object
      properties:
        success:
          type: boolean
        data:
          type: object
        error:
          $ref: '#/components/schemas/ApiError'
        meta:
          type: object
          properties:
            requestId:
              type: string
            timestamp:
              type: string
              format: date-time
```

## Integration Points

### External System Integrations

#### Test Target APIs
- **Integration Method**: HTTP REST calls during test execution
- **Authentication**: API key, OAuth2, Basic Auth (configurable per test case)
- **Rate Limiting**: Configurable delays between requests
- **Timeout**: Configurable request timeouts
- **SSL Validation**: Configurable certificate validation

#### File System Integration
- **BPMN Files**: Local file upload and parsing
- **OpenAPI Specs**: Local file upload and parsing
- **AI Models**: Local model file loading
- **Reports**: Local file generation and storage

#### Database Integration
- **Type**: H2 (file-based) for local storage
- **Entities**: Processes, specifications, test cases, workflows, execution results
- **Migrations**: Flyway for schema management
- **Backup**: Automatic local backups

### Cross-Feature Integration

#### BPMN + API Testing
```java
// Workflow execution integrates BPMN and API testing
public ExecutionResult executeWorkflow(Workflow workflow) {
    Process process = workflow.getProcess();

    // Execute BPMN process
    ExecutionResult processResult = processExecutor.execute(process, context);

    // Extract test case IDs from process variables
    List<TestCaseId> testCaseIds = extractTestCaseIds(context);

    // Execute relevant API tests
    List<TestResult> testResults = testCaseIds.stream()
        .map(id -> testExecutor.execute(findTestCase(id), context))
        .collect(Collectors.toList());

    return combineResults(processResult, testResults);
}
```

#### AI + API Testing
```java
// AI-generated data enhances API testing
public TestResult executeTestWithAiData(TestCase testCase) {
    // Generate test data using AI if needed
    TestData aiData = aiGenerator.generate(testCase.getSpecification());

    // Merge with existing test data
    TestData combinedData = mergeTestData(testCase.getTestData(), aiData);

    // Execute test with enhanced data
    return httpClient.execute(buildRequest(testCase, combinedData));
}
```

## Security Headers and CORS

### Security Configuration
```java
@Configuration
public class WebSecurityConfiguration {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

### File Upload Security
- **File Type Validation**: Only allow BPMN XML, OpenAPI JSON/YAML, AI model files
- **File Size Limits**: Configurable maximum file sizes
- **Content Validation**: Parse and validate file content before processing
- **Path Traversal Protection**: Sanitize file names and paths

## Monitoring and Health Checks

### Health Endpoints
```yaml
paths:
  /actuator/health:
    get:
      summary: System health check
      responses:
        '200':
          description: System is healthy
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/HealthResponse'

  /actuator/metrics:
    get:
      summary: Application metrics
      responses:
        '200':
          description: Application metrics
          content:
            application/json:
              schema:
                type: object
```

This API contract provides a comprehensive interface for all SecurityOrchestrator features while maintaining clean separation and consistent interaction patterns.