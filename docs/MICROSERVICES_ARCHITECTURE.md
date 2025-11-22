# Security Orchestrator - Microservices Architecture

## Overview

The Security Orchestrator follows a **feature-first microservices architecture** where each business capability is implemented as an independent module with clear boundaries and well-defined interfaces. This architecture enables independent deployment, scaling, and development of each feature while maintaining loose coupling through well-defined APIs and event-driven communication.

## Microservices Architecture Overview

```mermaid
graph TB
    %% API Gateway Layer
    subgraph "API Gateway & Load Balancing"
        GATEWAY[Spring Cloud Gateway]
        LB[Load Balancer]
        REGISTRY[Service Discovery]
        CIRCUIT[Circuit Breaker]
    end
    
    %% Core Microservices
    subgraph "Core Feature Microservices"
        ANALYSIS[Analysis Pipeline Service]
        BPMN[BPMN Processing Service]
        OPENAPI[OpenAPI Service]
        ORCHESTRATION[Orchestration Service]
        MONITORING[Monitoring Service]
        LLM[LLM Integration Service]
        TESTDATA[Test Data Service]
    end
    
    %% Shared Services
    subgraph "Shared Infrastructure"
        CONFIG[Configuration Service]
        AUTH[Authentication Service]
        LOGGING[Centralized Logging]
        METRICS[Metrics Service]
    end
    
    %% Communication Patterns
    subgraph "Message Brokers & Communication"
        KAFKA[Apache Kafka]
        RABBIT[RabbitMQ]
        REST[REST APIs]
        WS[WebSocket]
    end
    
    %% Data Layer
    subgraph "Data Services"
        DB_SERVICE[Database Service]
        FILE_SERVICE[File Storage Service]
        CACHE_SERVICE[Cache Service]
    end
    
    %% External Systems
    subgraph "External Integrations"
        LLM_PROVIDERS[LLM Providers]
        ENTERPRISE[Enterprise Systems]
        TARGET_APIS[Target APIs]
    end
    
    %% Gateway connections
    GATEWAY --> LB
    LB --> REGISTRY
    REGISTRY --> CIRCUIT
    
    %% Service communications
    GATEWAY --> ANALYSIS
    GATEWAY --> BPMN
    GATEWAY --> OPENAPI
    GATEWAY --> ORCHESTRATION
    GATEWAY --> MONITORING
    
    %% Internal service communication
    ORCHESTRATION --> ANALYSIS
    ORCHESTRATION --> BPMN
    ORCHESTRATION --> OPENAPI
    ORCHESTRATION --> LLM
    ANALYSIS --> TESTDATA
    
    %% Message broker connections
    ANALYSIS --> KAFKA
    ORCHESTRATION --> KAFKA
    MONITORING --> KAFKA
    LLM --> KAFKA
    
    BPMN --> RABBIT
    OPENAPI --> RABBIT
    MONITORING --> RABBIT
    
    %% WebSocket connections
    MONITORING --> WS
    ORCHESTRATION --> WS
    
    %% Data services
    ANALYSIS --> DB_SERVICE
    BPMN --> DB_SERVICE
    OPENAPI --> DB_SERVICE
    ORCHESTRATION --> DB_SERVICE
    MONITORING --> DB_SERVICE
    
    ANALYSIS --> CACHE_SERVICE
    LLM --> CACHE_SERVICE
    
    BPMN --> FILE_SERVICE
    OPENAPI --> FILE_SERVICE
    
    %% External integrations
    LLM --> LLM_PROVIDERS
    ANALYSIS --> ENTERPRISE
    ORCHESTRATION --> ENTERPRISE
    OPENAPI --> TARGET_APIS
    
    %% Shared services
    ALL_SERVICES --> CONFIG
    ALL_SERVICES --> AUTH
    ALL_SERVICES --> LOGGING
    ALL_SERVICES --> METRICS
    
    %% Styling
    classDef gateway fill:#e3f2fd
    classDef service fill:#f3e5f5
    classDef shared fill:#e8f5e8
    classDef broker fill:#fff3e0
    classDef data fill:#ffebee
    classDef external fill:#f1f8e9
    
    class GATEWAY,LB,REGISTRY,CIRCUIT gateway
    class ANALYSIS,BPMN,OPENAPI,ORCHESTRATION,MONITORING,LLM,TESTDATA service
    class CONFIG,AUTH,LOGGING,METRICS shared
    class KAFKA,RABBIT,REST,WS broker
    class DB_SERVICE,FILE_SERVICE,CACHE_SERVICE data
    class LLM_PROVIDERS,ENTERPRISE,TARGET_APIS external
```

## Feature Microservices Detail

### 1. Analysis Pipeline Service

```mermaid
graph LR
    subgraph "Analysis Pipeline Service"
        PIPELINE_API[REST API Controller]
        PIPELINE_USECASE[Use Cases]
        PIPELINE_ORCHESTRATOR[Analysis Orchestrator]
        PIPELINE_VALIDATOR[Input Validator]
        PIPELINE_REPOSITORY[Repository Layer]
    end
    
    PIPELINE_API --> PIPELINE_USECASE
    PIPELINE_USECASE --> PIPELINE_ORCHESTRATOR
    PIPELINE_ORCHESTRATOR --> PIPELINE_VALIDATOR
    PIPELINE_ORCHESTRATOR --> BPMN
    PIPELINE_ORCHESTRATOR --> OPENAPI
    PIPELINE_ORCHESTRATOR --> LLM
    PIPELINE_ORCHESTRATOR --> TESTDATA
    PIPELINE_ORCHESTRATOR --> PIPELINE_REPOSITORY
```

**Responsibilities:**
- Coordinates end-to-end security analysis workflows
- Manages analysis sessions and their lifecycle
- Orchestrates communication between BPMN, OpenAPI, LLM, and Test Data services
- Provides comprehensive security assessment capabilities
- Handles analysis input validation and preprocessing

### 2. BPMN Processing Service

```mermaid
graph LR
    subgraph "BPMN Processing Service"
        BPMN_API[REST API Controller]
        BPMN_DOMAIN[Domain Layer]
        BPMN_PARSER[BPMN Parser]
        BPMN_VALIDATOR[BPMN Validator]
        BPMN_EXECUTOR[BPMN Executor]
        BPMN_ANALYZER[Security Analyzer]
    end
    
    BPMN_API --> BPMN_DOMAIN
    BPMN_DOMAIN --> BPMN_PARSER
    BPMN_DOMAIN --> BPMN_VALIDATOR
    BPMN_DOMAIN --> BPMN_EXECUTOR
    BPMN_DOMAIN --> BPMN_ANALYZER
    BPMN_PARSER --> FILE_SERVICE
    BPMN_EXECUTOR --> ORCHESTRATION
```

**Responsibilities:**
- Parse and validate BPMN 2.0 workflow definitions
- Execute BPMN workflows in isolation
- Analyze workflows for security vulnerabilities
- Provide workflow execution tracking and monitoring
- Generate security assessment reports for workflows

### 3. OpenAPI Service

```mermaid
graph LR
    subgraph "OpenAPI Service"
        OPENAPI_API[REST API Controller]
        OPENAPI_DOMAIN[Domain Layer]
        OPENAPI_PARSER[OpenAPI Parser]
        OPENAPI_VALIDATOR[Schema Validator]
        OPENAPI_TESTER[API Tester]
        OPENAPI_GENERATOR[Test Generator]
    end
    
    OPENAPI_API --> OPENAPI_DOMAIN
    OPENAPI_DOMAIN --> OPENAPI_PARSER
    OPENAPI_DOMAIN --> OPENAPI_VALIDATOR
    OPENAPI_DOMAIN --> OPENAPI_TESTER
    OPENAPI_DOMAIN --> OPENAPI_GENERATOR
    OPENAPI_PARSER --> FILE_SERVICE
    OPENAPI_TESTER --> TARGET_APIS
    OPENAPI_GENERATOR --> LLM
```

**Responsibilities:**
- Parse and validate OpenAPI/Swagger specifications
- Generate security test cases from API specifications
- Execute automated API security testing
- Analyze API schemas for security vulnerabilities
- Provide comprehensive API security reports

### 4. Orchestration Service

```mermaid
graph LR
    subgraph "Orchestration Service"
        ORCH_API[REST API Controller]
        ORCH_DOMAIN[Domain Layer]
        ORCH_ENGINE[Workflow Engine]
        ORCH_SCHEDULER[Task Scheduler]
        ORCH_TRACKER[Execution Tracker]
        ORCH_COORDINATOR[Service Coordinator]
    end
    
    ORCH_API --> ORCH_DOMAIN
    ORCH_DOMAIN --> ORCH_ENGINE
    ORCH_DOMAIN --> ORCH_SCHEDULER
    ORCH_DOMAIN --> ORCH_TRACKER
    ORCH_ENGINE --> ORCH_COORDINATOR
    ORCH_COORDINATOR --> ANALYSIS
    ORCH_COORDINATOR --> BPMN
    ORCH_COORDINATOR --> OPENAPI
    ORCH_COORDINATOR --> LLM
```

**Responsibilities:**
- Coordinate complex multi-service workflows
- Manage task scheduling and execution
- Track cross-service dependencies and state
- Provide workflow visualization and monitoring
- Handle error recovery and compensation logic

### 5. LLM Integration Service

```mermaid
graph LR
    subgraph "LLM Integration Service"
        LLM_API[REST API Controller]
        LLM_DOMAIN[Domain Layer]
        LLM_PROVIDER[Provider Manager]
        LLM_CACHE[Response Cache]
        LLM_FALLBACK[Fallback Handler]
        LLM_MONITOR[Performance Monitor]
    end
    
    LLM_API --> LLM_DOMAIN
    LLM_DOMAIN --> LLM_PROVIDER
    LLM_DOMAIN --> LLM_CACHE
    LLM_DOMAIN --> LLM_FALLBACK
    LLM_DOMAIN --> LLM_MONITOR
    LLM_PROVIDER --> LLM_PROVIDERS
    LLM_CACHE --> CACHE_SERVICE
```

**Responsibilities:**
- Manage multiple LLM providers (Ollama, OpenRouter)
- Provide intelligent analysis and test generation
- Handle provider failover and load balancing
- Cache and optimize LLM responses
- Monitor LLM performance and costs

### 6. Monitoring Service

```mermaid
graph LR
    subgraph "Monitoring Service"
        MON_API[REST API Controller]
        MON_DOMAIN[Domain Layer]
        MON_COLLECTOR[Metrics Collector]
        MON_ALERTS[Alert Manager]
        MON_DASHBOARD[Dashboard Service]
        MON_WEBSOCKET[WebSocket Handler]
    end
    
    MON_API --> MON_DOMAIN
    MON_DOMAIN --> MON_COLLECTOR
    MON_DOMAIN --> MON_ALERTS
    MON_DOMAIN --> MON_DASHBOARD
    MON_DOMAIN --> MON_WEBSOCKET
    MON_COLLECTOR --> ALL_SERVICES
    MON_WEBSOCKET --> WS
```

**Responsibilities:**
- Collect and aggregate system metrics
- Manage alerts and notifications
- Provide real-time monitoring dashboards
- Handle health checks and service discovery
- Support distributed tracing and monitoring

## Inter-Service Communication Patterns

### 1. Synchronous Communication (REST APIs)

```mermaid
sequenceDiagram
    participant CLI as Client
    participant GW as API Gateway
    participant ORCH as Orchestration
    participant BPMN as BPMN Service
    participant OPENAPI as OpenAPI Service
    participant LLM as LLM Service
    
    CLI->>GW: Analysis Request
    GW->>ORCH: Create Analysis
    ORCH->>BPMN: Validate BPMN
    BPMN-->>ORCH: Validation Result
    ORCH->>OPENAPI: Validate API
    OPENAPI-->>ORCH: Validation Result
    ORCH->>LLM: Generate Tests
    LLM-->>ORCH: Generated Tests
    ORCH-->>GW: Analysis Created
    GW-->>CLI: Response
```

### 2. Asynchronous Communication (Event-Driven)

```mermaid
sequenceDiagram
    participant ORCH as Orchestration
    participant KAFKA as Kafka
    participant BPMN as BPMN Service
    participant OPENAPI as OpenAPI Service
    participant MON as Monitoring
    
    ORCH->>KAFKA: AnalysisStarted Event
    KAFKA->>BPMN: AnalysisStarted Event
    KAFKA->>MON: AnalysisStarted Event
    
    BPMN->>KAFKA: BPMNProcessed Event
    KAFKA->>ORCH: BPMNProcessed Event
    KAFKA->>MON: BPMNProcessed Event
    
    OPENAPI->>KAFKA: APIProcessed Event
    KAFKA->>ORCH: APIProcessed Event
    KAFKA->>MON: APIProcessed Event
    
    ORCH->>KAFKA: AnalysisCompleted Event
    KAFKA->>MON: AnalysisCompleted Event
```

## Service Dependencies & Communication Matrix

```mermaid
graph LR
    subgraph "Service Communication Matrix"
        ANALYSIS[Analysis Pipeline]
        BPMN[BPMN Service]
        OPENAPI[OpenAPI Service]
        ORCH[Orchestration]
        MON[Monitoring]
        LLM[LLM Service]
        TEST[Test Data]
    end
    
    %% Analysis Pipeline dependencies
    ANALYSIS --> ORCH
    ANALYSIS --> BPMN
    ANALYSIS --> OPENAPI
    ANALYSIS --> LLM
    ANALYSIS --> TEST
    
    %% Orchestration dependencies
    ORCH --> ANALYSIS
    ORCH --> BPMN
    ORCH --> OPENAPI
    ORCH --> LLM
    ORCH --> MON
    
    %% BPMN dependencies
    BPMN --> ORCH
    BPMN --> MON
    BPMN --> LLM
    
    %% OpenAPI dependencies
    OPENAPI --> ORCH
    OPENAPI --> MON
    OPENAPI --> LLM
    OPENAPI --> TEST
    
    %% Monitoring dependencies (read-only)
    MON --> ANALYSIS
    MON --> BPMN
    MON --> OPENAPI
    MON --> ORCH
    MON --> LLM
    MON --> TEST
    
    %% LLM dependencies
    LLM --> ORCH
    LLM --> MON
    LLM --> ANALYSIS
    
    %% Test Data dependencies
    TEST --> ORCH
    TEST --> MON
    TEST --> LLM
```

## API Gateway & Service Discovery

### API Gateway Configuration

```mermaid
graph LR
    subgraph "Spring Cloud Gateway"
        ROUTES[Route Definitions]
        FILTERS[Request Filters]
        BALANCER[Load Balancer]
        CIRCUIT_BREAKER[Circuit Breaker]
        RATE_LIMIT[Rate Limiter]
    end
    
    ROUTES --> FILTERS
    FILTERS --> BALANCER
    BALANCER --> CIRCUIT_BREAKER
    CIRCUIT_BREAKER --> RATE_LIMIT
    RATE_LIMIT --> SERVICES[Backend Services]
```

**Gateway Routes:**
- `/api/analysis/*` → Analysis Pipeline Service
- `/api/bpmn/*` → BPMN Processing Service
- `/api/openapi/*` → OpenAPI Service
- `/api/orchestration/*` → Orchestration Service
- `/api/monitoring/*` → Monitoring Service
- `/api/llm/*` → LLM Integration Service

### Service Discovery Pattern

```mermaid
graph LR
    subgraph "Eureka Service Discovery"
        REGISTRY[Service Registry]
        HEARTBEAT[Heartbeat Monitor]
        HEALTH[Health Check]
        CLIENT[Service Client]
    end
    
    ANALYSIS -.-> REGISTRY: Register
    BPMN -.-> REGISTRY: Register
    OPENAPI -.-> REGISTRY: Register
    ORCHESTRATION -.-> REGISTRY: Register
    MONITORING -.-> REGISTRY: Register
    LLM -.-> REGISTRY: Register
    
    CLIENT --> REGISTRY: Service Lookup
    REGISTRY --> CLIENT: Available Instances
    
    HEARTBEAT --> ANALYSIS
    HEARTBEAT --> BPMN
    HEARTBEAT --> OPENAPI
    HEARTBEAT --> ORCHESTRATION
    HEARTBEAT --> MONITORING
    HEARTBEAT --> LLM
```

## Fault Tolerance & Resilience Patterns

### 1. Circuit Breaker Pattern

```mermaid
graph LR
    subgraph "Circuit Breaker Implementation"
        CLIENT[Service Client]
        CB[Circuit Breaker]
        SERVICE[Target Service]
        FALLBACK[Fallback Service]
    end
    
    CLIENT --> CB
    CB --> SERVICE
    CB -.-> FALLBACK: When OPEN
    SERVICE -.-> CB: Health Status
    
    CB --> CLIENT: Service Response
    CB --> CLIENT: Fallback Response
```

**Circuit Breaker Configuration:**
- **Failure Threshold**: 50% of requests fail
- **Timeout**: 30 seconds
- **Recovery Timeout**: 60 seconds
- **Fallback Strategy**: Cache responses or default values

### 2. Retry & Backoff Pattern

```mermaid
sequenceDiagram
    participant CLI as Client
    participant CB as Circuit Breaker
    participant SRV as Service
    participant LOG as Logger
    
    CLI->>CB: Request
    CB->>SRV: Call Service
    SRV-->>CB: Failure (500)
    CB->>LOG: Log Failure
    CB->>SRV: Retry (1)
    SRV-->>CB: Failure (500)
    CB->>LOG: Log Retry
    CB->>SRV: Retry (2)
    SRV-->>CB: Success (200)
    CB-->>CLI: Response
```

## Data Consistency & Transaction Management

### Event Sourcing Pattern

```mermaid
graph LR
    subgraph "Event Sourcing Implementation"
        COMMAND[Command Handler]
        AGGREGATE[Aggregate Root]
        EVENTS[Event Store]
        PROJECTION[Read Model]
        EVENT_BUS[Event Bus]
    end
    
    COMMAND --> AGGREGATE
    AGGREGATE --> EVENTS
    EVENTS --> PROJECTION
    EVENTS --> EVENT_BUS
    EVENT_BUS --> OTHER_SERVICES
```

### Saga Pattern for Distributed Transactions

```mermaid
sequenceDiagram
    participant ORCH as Orchestrator
    participant BPMN as BPMN Service
    participant OPENAPI as OpenAPI Service
    participant LLM as LLM Service
    participant COMP as Compensation
    
    ORCH->>BPMN: Process BPMN
    BPMN-->>ORCH: Success
    
    ORCH->>OPENAPI: Validate API
    OPENAPI-->>ORCH: Success
    
    ORCH->>LLM: Generate Tests
    LLM-->>ORCH: Failure
    
    ORCH->>COMP: Compensate
    COMP->>BPMN: Rollback
    COMP->>OPENAPI: Rollback
```

## Load Balancing & Scaling Strategies

### Horizontal Pod Autoscaling

```mermaid
graph TB
    subgraph "Kubernetes Scaling"
        HPA[Horizontal Pod Autoscaler]
        METRICS_SERVER[Metrics Server]
        POD1[Pod Instance 1]
        POD2[Pod Instance 2]
        POD3[Pod Instance N]
    end
    
    HPA --> METRICS_SERVER
    METRICS_SERVER --> POD1
    METRICS_SERVER --> POD2
    METRICS_SERVER --> POD3
    
    subgraph "Scaling Triggers"
        CPU[CPU Usage > 70%]
        MEMORY[Memory Usage > 80%]
        REQUESTS[Requests/sec > 1000]
    end
    
    METRICS_SERVER --> CPU
    METRICS_SERVER --> MEMORY
    METRICS_SERVER --> REQUESTS
```

### Service-Specific Scaling Rules

| Service | Scaling Trigger | Min Pods | Max Pods |
|---------|----------------|----------|----------|
| Analysis Pipeline | CPU > 60% | 2 | 10 |
| BPMN Service | Memory > 70% | 1 | 5 |
| OpenAPI Service | Request Rate > 500/s | 2 | 8 |
| Orchestration | Active Workflows > 100 | 1 | 6 |
| Monitoring | Metrics Volume > 10K/min | 1 | 4 |
| LLM Service | Queue Length > 50 | 2 | 12 |

## Service Monitoring & Observability

### Distributed Tracing

```mermaid
graph LR
    subgraph "OpenTelemetry Tracing"
        TRACER[Distributed Tracer]
        SPAN[Span Collector]
        JAEGER[Jaeger UI]
        EXPORTER[Trace Exporter]
    end
    
    ANALYSIS --> TRACER
    BPMN --> TRACER
    OPENAPI --> TRACER
    ORCHESTRATION --> TRACER
    LLM --> TRACER
    
    TRACER --> SPAN
    SPAN --> JAEGER
    SPAN --> EXPORTER
```

### Metrics Collection

```mermaid
graph LR
    subgraph "Micrometer Metrics"
        METRICS[Service Metrics]
        METER_REGISTRY[Meter Registry]
        PROMETHEUS[Prometheus]
        GRAFANA[Grafana]
    end
    
    ANALYSIS --> METRICS
    BPMN --> METRICS
    OPENAPI --> METRICS
    ORCHESTRATION --> METRICS
    MONITORING --> METRICS
    LLM --> METRICS
    
    METRICS --> METER_REGISTRY
    METER_REGISTRY --> PROMETHEUS
    PROMETHEUS --> GRAFANA
```

This microservices architecture provides a robust, scalable, and maintainable foundation for the Security Orchestrator platform, enabling independent development, deployment, and scaling of each business capability while maintaining system-wide coherence and reliability.