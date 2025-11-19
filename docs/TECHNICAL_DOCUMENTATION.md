---

## 8. Integration Guides

### BPMN File Specifications

#### BPMN 2.0 Support
The SecurityOrchestrator supports BPMN 2.0 XML files with specific requirements for security testing workflows.

**Supported BPMN Elements:**
- Start Events (None, Message, Timer, Conditional)
- End Events (None, Message, Error, Terminate)
- Tasks (User, Service, Business Rule, Script)
- Gateways (Exclusive, Inclusive, Parallel, Event-based)
- Sequence Flows (Normal, Conditional, Default)
- Sub-processes (Embedded, Call Activity)

**BPMN File Requirements:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                  xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                  xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
                  id="Definitions_1"
                  targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="Process_1" isExecutable="true">
    <!-- BPMN elements go here -->
  </bpmn:process>
</bpmn:definitions>
```

### OpenAPI Integration Examples

#### OpenAPI 3.0+ Specification Support
```yaml
openapi: 3.0.3
info:
  title: SecurityOrchestrator API
  version: 1.0.0
  description: API for security testing workflows

servers:
  - url: https://api.example.com/v1
    description: Production server

security:
  - bearerAuth: []
  - apiKeyAuth: []

paths:
  /users:
    get:
      summary: Get users
      security:
        - bearerAuth: []
      parameters:
        - name: limit
          in: query
          schema:
            type: integer
            minimum: 1
            maximum: 100
            default: 20
      responses:
        '200':
          description: Successful response
          content:
            application/json:
              schema:
                type: object
                properties:
                  users:
                    type: array
                    items:
                      $ref: '#/components/schemas/User'
```

### AI Model Integration Examples

#### ONNX Model Integration
```java
@Service
public class OnnxModelIntegrationService {

    private final OrtEnvironment environment = OrtEnvironment.getEnvironment();

    public AiModel loadOnnxModel(Path modelPath) throws ModelLoadException {
        try {
            OrtSession session = environment.createSession(modelPath.toString());
            ModelMetadata metadata = extractMetadata(session);

            AiModel model = AiModel.builder()
                .id(ModelId.generate())
                .name(extractModelName(modelPath))
                .type(ModelType.ONNX)
                .path(modelPath)
                .metadata(metadata)
                .status(ModelStatus.LOADED)
                .build();

            return model;
        } catch (OrtException e) {
            throw new ModelLoadException("Failed to load ONNX model: " + modelPath, e);
        }
    }
}
```

---

## 9. Troubleshooting Guide

### Common Issues and Solutions

#### File Upload Problems

**Issue: "File size exceeds maximum limit"**
```
Error: File size exceeds maximum limit of 10MB
```
**Solutions:**
1. Check file size: `ls -lh filename`
2. Compress large BPMN files if possible
3. Split large specifications into multiple files
4. Increase limits in configuration (not recommended for production)

#### Database Connection Issues

**Issue: "Connection refused" to H2 database**
```
Error: Connection refused: connect to localhost:9092
```
**Solutions:**
1. Check if application is running: `ps aux | grep securityorchestrator`
2. Verify H2 console access: http://localhost:8080/h2-console
3. Check database file permissions: `ls -la data/`
4. Restart application to recreate database

### Diagnostic Tools and Commands

#### Application Health Checks
```bash
# Check application health
curl http://localhost:8080/actuator/health

# View application metrics
curl http://localhost:8080/actuator/metrics
```

---

## README Quick Start

# SecurityOrchestrator

An intelligent platform that orchestrates end-to-end security testing workflows by combining BPMN process definitions, OpenAPI specifications, and AI-powered test data generation.

## ✨ Features

- **BPMN Workflow Processing**: Parse and execute BPMN 2.0 business processes
- **OpenAPI Integration**: Generate comprehensive API security tests
- **AI-Powered Testing**: Intelligent test data generation using local ML models
- **Real-time Monitoring**: WebSocket-based execution monitoring
- **Local-First Security**: All processing happens locally, no external data sharing

## 🚀 Quick Start

### Prerequisites

- **Java 21+** - Download from [OpenJDK](https://openjdk.java.net/)
- **Git** - Version control system

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/securityorchestrator.git
   cd securityorchestrator
   ```

2. **Build the application**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the application**
   - Web Interface: http://localhost:8080
   - Health Check: http://localhost:8080/actuator/health

### First Security Test

1. **Upload a BPMN Process**
   ```bash
   curl -X POST http://localhost:8080/api/v1/processes \
     -F "file=@examples/credit-approval.bpmn"
   ```

2. **Upload an OpenAPI Specification**
   ```bash
   curl -X POST http://localhost:8080/api/v1/specifications \
     -F "file=@examples/api-spec.yaml"
   ```

3. **Create and Execute a Workflow**
   ```bash
   # Create workflow
   curl -X POST http://localhost:8080/api/v1/workflows \
     -H "Content-Type: application/json" \
     -d '{"name": "Security Test", "processId": "process-123"}'

   # Execute workflow
   curl -X POST http://localhost:8080/api/v1/workflows/{workflow-id}/execute
   ```

## 📋 System Requirements

### Minimum Requirements
- **OS**: Windows 10+, macOS Monterey+, Ubuntu 20.04+
- **CPU**: Quad-core processor
- **RAM**: 8 GB
- **Storage**: 20 GB available space
- **Java**: OpenJDK 21+

## 🔧 Configuration

### Environment Variables

```bash
# Server Configuration
SERVER_PORT=8080

# Database (Production)
DATABASE_URL=jdbc:postgresql://localhost:5432/securityorchestrator
DATABASE_USERNAME=securityorchestrator
DATABASE_PASSWORD=secure_password
```

### Integration Examples

#### BPMN Process Template
```xml
<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL">
  <bpmn:process id="SecurityTestProcess" isExecutable="true">
    <bpmn:startEvent id="StartEvent_1" name="Start Security Test">
      <bpmn:outgoing>SequenceFlow_1</bpmn:outgoing>
    </bpmn:startEvent>

    <bpmn:serviceTask id="Task_ValidateInput" name="Validate Input Data">
      <bpmn:incoming>SequenceFlow_1</bpmn:incoming>
      <bpmn:outgoing>SequenceFlow_2</bpmn:outgoing>
    </bpmn:serviceTask>

    <bpmn:endEvent id="EndEvent_1" name="Test Complete">
      <bpmn:incoming>SequenceFlow_2</bpmn:incoming>
    </bpmn:endEvent>

    <bpmn:sequenceFlow id="SequenceFlow_1" sourceRef="StartEvent_1" targetRef="Task_ValidateInput" />
    <bpmn:sequenceFlow id="SequenceFlow_2" sourceRef="Task_ValidateInput" targetRef="EndEvent_1" />
  </bpmn:process>
</bpmn:definitions>
```

#### OpenAPI Specification Template
```yaml
openapi: 3.0.3
info:
  title: Sample API
  version: 1.0.0

paths:
  /users:
    get:
      summary: Get users
      parameters:
        - name: limit
          in: query
          schema:
            type: integer
            minimum: 1
            maximum: 100
      responses:
        '200':
          description: Success
          content:
            application/json:
              schema:
                type: array
                items:
                  type: object
                  properties:
                    id: { type: integer }
                    name: { type: string }

components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
```

### Development Guidelines

#### Code Organization
```
Backend/
├── app/                           # Main application entry point
│   └── src/main/java/             # Application-specific logic
├── features/                      # Feature modules (modular architecture)
│   ├── analysis-pipeline/         # Integrated analysis workflows
│   ├── bpmn/                      # BPMN processing and analysis
│   ├── llm/                       # Large Language Model integration
│   ├── llm-providers/             # LLM provider management
│   ├── monitoring/                # System monitoring and metrics
│   ├── openapi/                   # OpenAPI specification handling
│   ├── orchestration/             # Workflow orchestration
│   ├── testdata/                  # Test data generation
│   └── workflow/                  # BPMN workflow processing
├── shared/                        # Cross-cutting concerns and common utilities
│   └── src/main/java/org/example/shared/
│       ├── common/                # Common DTOs and utilities
│       ├── domain/                # Shared domain entities
│       └── infrastructure/        # Shared infrastructure services
└── gradle configuration files
```

#### Testing
```bash
# Run all tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Generate coverage report
./gradlew jacocoTestReport
```

#### Code Quality
- **Test Coverage**: Minimum 80% coverage required
- **Code Style**: Google Java Style Guide
- **Security**: OWASP guidelines compliance
- **Performance**: Response time < 2 seconds for API calls

---

This comprehensive technical documentation provides everything developers need to understand, implement, deploy, and maintain the SecurityOrchestrator system. The documentation covers system architecture, API specifications, database design, deployment procedures, development guidelines, integration patterns, and troubleshooting guides.### BPMN & OpenAPI Analysis Pipeline Updates

1. **Storage layout**
   - BPMN files live under nalysis.processes.bpmn-storage-path (default data/analysis_processes/bpmn).
   - OpenAPI specs reuse the same pattern via nalysis.processes.openapi-storage-path with configurable max size (nalysis.processes.max-upload-size-bytes).
   - Datasets ship in dataset/bpmn and dataset/openapi (configurable via pmn.dataset-path / openapi.dataset-path).
2. **REST endpoints**
   - Process specific uploads: POST /api/analysis-processes/{id}/bpmn, POST /api/analysis-processes/{id}/openapi (multipart, size limited, validation + analyzer + Camunda/Camunda+Swagger stacks).
   - Process retrieval: GET /api/analysis-processes/{id}/bpmn, GET /api/analysis-processes/{id}/openapi (re-parse stored artifacts, return ApiResponse with analysis DTOs).
   - Ad-hoc analysis + examples: /api/bpmn/analyze, /api/bpmn/examples, /api/openapi/analyze, /api/openapi/examples.
3. **DTOs & UI**
   - BPMN responses continue to use `BpmnAnalysisResponse`.
   - OpenAPI responses are serialized as `OpenApiAnalysisResponse` (validation summary, endpoints, schema counts, security issues, recommendations, raw spec payload).
   - Flutter embeds paired upload sections and status badges on the Process Detail screen so both artifacts share one UX pattern.
4. **LLM Provider Management**
   - `config/llm-providers.yml` (sample in `config/llm-providers-example.yml`) defines provider metadata. Default active provider is `ollama` with the `llama3.2:3b` model, suitable for RTX 3070 8 GB cards—pull it via `ollama pull llama3.2:3b`.
   - Monitoring REST surface: `GET /api/monitoring/llm` (analytics), `POST /api/monitoring/llm/providers/{id}/activate` (switch), `GET /api/monitoring/llm/check` (connectivity probe hitting Ollama `/api/tags` or remote `/models`).
   - Flutter dashboard card shows provider stats, exposes switch and connectivity controls, and consumes the new endpoints.
