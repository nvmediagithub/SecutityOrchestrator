# Диаграммы архитектуры и визуализация системы автоматического тестирования

## 1. Общая системная архитектура

### High-Level System Architecture
```mermaid
graph TB
    subgraph "External Users"
        TESTER[Тестировщик]
        ANALYST[Аналитик]
        DEVOPS[DevOps Engineer]
    end
    
    subgraph "Presentation Layer"
        FLUTTER[Flutter Web App]
        DASHBOARD[Dashboard]
        UPLOAD_UI[Upload Interface]
        MONITOR_UI[Monitoring Interface]
        RESULTS_UI[Results Interface]
    end
    
    subgraph "API Gateway"
        GATEWAY[Spring Boot Gateway]
        AUTH[Authentication]
        RATE_LIMIT[Rate Limiting]
        CORS[CORS Handler]
    end
    
    subgraph "Core Application Services"
        ORCHESTRATOR[Test Orchestrator]
        SPEC_SERVICE[Specification Service]
        LLM_SERVICE[LLM Analysis Service]
        OWASP_SERVICE[OWASP Security Service]
        EXECUTION_ENGINE[Test Execution Engine]
        DATA_GENERATOR[Test Data Generator]
    end
    
    subgraph "Domain Layer"
        MODELS[Domain Models]
        REPOSITORIES[Repositories]
        DOMAIN_SERVICES[Domain Services]
    end
    
    subgraph "Infrastructure Layer"
        PERSISTENCE[PostgreSQL/H2]
        CACHE[Redis Cache]
        LLM_CLIENTS[LLM Clients]
        HTTP_CLIENTS[HTTP Clients]
        WEBSOCKET[WebSocket Service]
        FILE_STORAGE[File Storage]
    end
    
    subgraph "External LLM Providers"
        OPENROUTER[OpenRouter API]
        OLLAMA[Ollama Local]
    end
    
    subgraph "Test Target Systems"
        TARGET_API[Target APIs]
        BPMN_SYSTEM[BPMN Systems]
    end
    
    TESTER --> FLUTTER
    ANALYST --> DASHBOARD
    DEVOPS --> MONITOR_UI
    
    FLUTTER --> GATEWAY
    GATEWAY --> ORCHESTRATOR
    ORCHESTRATOR --> SPEC_SERVICE
    ORCHESTRATOR --> LLM_SERVICE
    ORCHESTRATOR --> OWASP_SERVICE
    ORCHESTRATOR --> EXECUTION_ENGINE
    
    ORCHESTRATOR --> MODELS
    MODELS --> REPOSITORIES
    REPOSITORIES --> PERSISTENCE
    
    LLM_SERVICE --> LLM_CLIENTS
    LLM_CLIENTS --> OPENROUTER
    LLM_CLIENTS --> OLLAMA
    
    EXECUTION_ENGINE --> HTTP_CLIENTS
    HTTP_CLIENTS --> TARGET_API
    EXECUTION_ENGINE --> BPMN_SYSTEM
    
    ORCHESTRATOR --> WEBSOCKET
    WEBSOCKET --> FLUTTER
    
    style ORCHESTRATOR fill:#e1f5fe
    style LLM_SERVICE fill:#f3e5f5
    style EXECUTION_ENGINE fill:#fff3e0
    style OWASP_SERVICE fill:#ffebee
```

## 2. Пользовательские потоки (User Flows)

### 2.1 Flow: Создание и анализ тестового проекта
```mermaid
flowchart TD
    A[Пользователь заходит в систему] --> B[Dashboard]
    B --> C[Нажать "Создать проект"]
    C --> D[Ввести название проекта]
    D --> E[Загрузить OpenAPI/BPMN]
    E --> F[Система анализирует файл]
    F --> G[LLM анализ содержания]
    G --> H[Показать результаты анализа]
    H --> I{Анализ завершен?}
    I -->|Да| J[Генерировать тесты]
    I -->|Нет| K[Показать ошибки]
    K --> L[Исправить файл]
    L --> E
    J --> M[Просмотр сгенерированных тестов]
    M --> N[Запуск тестов]
    
    style A fill:#e8f5e8
    style G fill:#f3e5f5
    style J fill:#fff3e0
    style N fill:#e1f5fe
```

### 2.2 Flow: Мониторинг выполнения тестов
```mermaid
sequenceDiagram
    participant U as Пользователь
    participant F as Flutter App
    participant G as API Gateway
    participant O as Test Orchestrator
    participant E as Execution Engine
    participant W as WebSocket
    participant T as Target API
    
    U->>F: Запуск тестов
    F->>G: POST /execute
    G->>O: Выполнить сценарий
    O->>E: Запустить выполнение
    E->>T: Первый API запрос
    
    Note over E,T: Выполнение тестовых шагов
    
    loop Для каждого шага
        E->>T: HTTP Request
        T-->>E: HTTP Response
        E->>O: Результат шага
        O->>W: Broadcast прогресс
        W->>F: WebSocket update
        F->>U: Real-time обновления
    end
    
    E->>O: Финальные результаты
    O->>W: Broadcast завершение
    W->>F: Execution complete
    F->>U: Показать результаты
```

## 3. Детальные компонентные диаграммы

### 3.1 LLM Analysis Module
```mermaid
graph TB
    subgraph "LLM Analysis Module"
        INPUT[Specification Input]
        PREPROCESSOR[Content Preprocessor]
        PROMPT_BUILDER[Prompt Builder]
        
        subgraph "LLM Providers"
            OR_CLIENT[OpenRouter Client]
            LOCAL_CLIENT[Ollama Client]
        end
        
        RESPONSE_PARSER[Response Parser]
        VALIDATOR[Results Validator]
        STORAGE[Results Storage]
        
        subgraph "Analysis Types"
            SECURITY[Security Analysis]
            BUSINESS[Business Logic]
            CONSISTENCY[Consistency Check]
        end
    end
    
    INPUT --> PREPROCESSOR
    PREPROCESSOR --> PROMPT_BUILDER
    PROMPT_BUILDER --> OR_CLIENT
    PROMPT_BUILDER --> LOCAL_CLIENT
    
    OR_CLIENT --> RESPONSE_PARSER
    LOCAL_CLIENT --> RESPONSE_PARSER
    
    RESPONSE_PARSER --> VALIDATOR
    VALIDATOR --> STORAGE
    
    STORAGE --> SECURITY
    STORAGE --> BUSINESS
    STORAGE --> CONSISTENCY
    
    style PROMPT_BUILDER fill:#f3e5f5
    style OR_CLIENT fill:#e1f5fe
    style LOCAL_CLIENT fill:#e1f5fe
```

### 3.2 Test Generation Engine
```mermaid
graph TB
    subgraph "Test Generation Engine"
        SPEC_INPUT[Specification Data]
        
        subgraph "Generation Pipeline"
            PARSER[Spec Parser]
            ANALYZER[Business Logic Analyzer]
            OWASP_GEN[OWASP Test Generator]
            DATA_GEN[Test Data Generator]
            SCENARIO_BUILDER[Scenario Builder]
        end
        
        subgraph "OWASP Categories"
            API1[BOLA Tests]
            API2[Auth Tests]
            API3[Data Exposure]
            API4[Rate Limiting]
            API5[AuthZ Tests]
        end
        
        VALIDATION[Test Validation]
        EXPORT[Test Export]
        
        SPEC_INPUT --> PARSER
        PARSER --> ANALYZER
        ANALYZER --> OWASP_GEN
        OWASP_GEN --> API1
        OWASP_GEN --> API2
        OWASP_GEN --> API3
        OWASP_GEN --> API4
        OWASP_GEN --> API5
        
        ANALYZER --> DATA_GEN
        DATA_GEN --> SCENARIO_BUILDER
        SCENARIO_BUILDER --> VALIDATION
        VALIDATION --> EXPORT
        
        style OWASP_GEN fill:#ffebee
        style DATA_GEN fill:#fff3e0
        style SCENARIO_BUILDER fill:#e8f5e8
```

### 3.3 Test Execution Engine
```mermaid
graph TB
    subgraph "Test Execution Engine"
        EXEC_INPUT[Test Scenarios]
        
        SCHEDULER[Test Scheduler]
        QUEUE[Execution Queue]
        
        subgraph "Execution Workers"
            WORKER1[Worker 1]
            WORKER2[Worker 2]
            WORKER3[Worker N]
        end
        
        subgraph "Execution Pipeline"
            STEP_EX[Step Executor]
            HTTP_CLIENT[HTTP Client]
            VALIDATOR[Response Validator]
            SECURITY_SCAN[Security Scanner]
        end
        
        AGGREGATOR[Result Aggregator]
        REPORTER[Report Generator]
        
        EXEC_INPUT --> SCHEDULER
        SCHEDULER --> QUEUE
        QUEUE --> WORKER1
        QUEUE --> WORKER2
        QUEUE --> WORKER3
        
        WORKER1 --> STEP_EX
        WORKER2 --> STEP_EX
        WORKER3 --> STEP_EX
        
        STEP_EX --> HTTP_CLIENT
        HTTP_CLIENT --> VALIDATOR
        VALIDATOR --> SECURITY_SCAN
        SECURITY_SCAN --> AGGREGATOR
        
        AGGREGATOR --> REPORTER
        
        style SCHEDULER fill:#e1f5fe
        style STEP_EX fill:#fff3e0
        style SECURITY_SCAN fill:#ffebee
```

## 4. BPMN Integration Architecture

### 4.1 BPMN Processing Flow
```mermaid
graph TB
    subgraph "BPMN Integration System"
        BPMN_UPLOAD[BPMN File Upload]
        
        subgraph "BPMN Processing Pipeline"
            XML_PARSER[XML Parser]
            MODEL_BUILDER[Process Model Builder]
            ELEMENT_EXTRACTOR[Element Extractor]
            
            subgraph "Element Analysis"
                TASK_ANALYZER[Task Analysis]
                GATEWAY_ANALYZER[Gateway Analysis]
                FLOW_ANALYZER[Flow Analysis]
            end
            
            LLM_BPMN_ANALYZER[LLM Business Logic Analysis]
            API_MAPPER[API Endpoint Mapper]
        end
        
        subgraph "Integration Points"
            API_DISCOVERY[API Discovery]
            ENDPOINT_MATCHING[Endpoint Matching]
            DEPENDENCY_MAP[Dependency Mapping]
        end
        
        TEST_GEN_INTEGRATION[Integration with Test Generation]
        
        BPMN_UPLOAD --> XML_PARSER
        XML_PARSER --> MODEL_BUILDER
        MODEL_BUILDER --> ELEMENT_EXTRACTOR
        ELEMENT_EXTRACTOR --> TASK_ANALYZER
        ELEMENT_EXTRACTOR --> GATEWAY_ANALYZER
        ELEMENT_EXTRACTOR --> FLOW_ANALYZER
        ELEMENT_EXTRACTOR --> LLM_BPMN_ANALYZER
        LLM_BPMN_ANALYZER --> API_MAPPER
        API_MAPPER --> API_DISCOVERY
        API_DISCOVERY --> ENDPOINT_MATCHING
        ENDPOINT_MATCHING --> DEPENDENCY_MAP
        DEPENDENCY_MAP --> TEST_GEN_INTEGRATION
        
        style LLM_BPMN_ANALYZER fill:#f3e5f5
        style API_MAPPER fill:#e1f5fe
        style DEPENDENCY_MAP fill:#fff3e0
```

## 5. OpenAPI Analysis Architecture

### 5.1 OpenAPI Security Analysis
```mermaid
graph TB
    subgraph "OpenAPI Security Analysis"
        OPENAPI_INPUT[OpenAPI Specification]
        
        subgraph "Analysis Components"
            SCHEMA_PARSER[Schema Parser]
            ENDPOINT_EXTRACTOR[Endpoint Extractor]
            SECURITY_SCANNER[Security Scanner]
            
            subgraph "Security Checks"
                AUTH_CHECK[Authentication Check]
                AUTHZ_CHECK[Authorization Check]
                INPUT_VALIDATION[Input Validation]
                RATE_LIMIT_CHECK[Rate Limiting Check]
                DATA_EXPOSURE[Data Exposure Check]
            end
            
            VULNERABILITY_SCANNER[Vulnerability Scanner]
            
            subgraph "OWASP API Security"
                OWASP1[BOLA Detection]
                OWASP2[Broken Auth Detection]
                OWASP3[Excessive Data Detection]
                OWASP4[Rate Limiting Detection]
                OWASP5[Broken AuthZ Detection]
            end
        end
        
        LLM_SECURITY_ANALYZER[LLM Security Analyzer]
        REPORT_GENERATOR[Security Report Generator]
        
        OPENAPI_INPUT --> SCHEMA_PARSER
        SCHEMA_PARSER --> ENDPOINT_EXTRACTOR
        ENDPOINT_EXTRACTOR --> SECURITY_SCANNER
        SECURITY_SCANNER --> AUTH_CHECK
        SECURITY_SCANNER --> AUTHZ_CHECK
        SECURITY_SCANNER --> INPUT_VALIDATION
        SECURITY_SCANNER --> RATE_LIMIT_CHECK
        SECURITY_SCANNER --> DATA_EXPOSURE
        
        SECURITY_SCANNER --> VULNERABILITY_SCANNER
        VULNERABILITY_SCANNER --> OWASP1
        VULNERABILITY_SCANNER --> OWASP2
        VULNERABILITY_SCANNER --> OWASP3
        VULNERABILITY_SCANNER --> OWASP4
        VULNERABILITY_SCANNER --> OWASP5
        
        ENDPOINT_EXTRACTOR --> LLM_SECURITY_ANALYZER
        LLM_SECURITY_ANALYZER --> REPORT_GENERATOR
        
        style LLM_SECURITY_ANALYZER fill:#f3e5f5
        style VULNERABILITY_SCANNER fill:#ffebee
        style REPORT_GENERATOR fill:#e8f5e8
```

## 6. Data Flow Diagrams

### 6.1 Test Data Generation Flow
```mermaid
flowchart LR
    A[Specification Data] --> B[Context Analysis]
    B --> C[Dependency Mapping]
    C --> D[LLM Data Request]
    D --> E[LLM Processing]
    E --> F[Data Validation]
    F --> G[Test Data Store]
    G --> H[API Endpoints]
    
    subgraph "LLM Processing"
        D --> I[Prompt Building]
        I --> J[Model Selection]
        J --> K[API Call]
        K --> L[Response Processing]
        L --> M[Data Extraction]
    end
    
    subgraph "Validation Pipeline"
        F --> N[Schema Validation]
        N --> O[Business Rules]
        O --> P[Security Rules]
        P --> Q[Dependency Check]
    end
    
    style D fill:#f3e5f5
    style G fill:#e8f5e8
    style K fill:#e1f5fe
```

## 7. System Integration Architecture

### 7.1 Integration with SecurityOrchestrator Core
```mermaid
graph TB
    subgraph "SecurityOrchestrator Core"
        CORE_LLM[Existing LLM System]
        CORE_ENTITIES[Core Entities]
        CORE_REPOSITORIES[Core Repositories]
    end
    
    subgraph "New Testing System"
        TEST_ORCHESTRATOR[Test Orchestrator]
        TEST_MODELS[Testing Models]
        TEST_REPOSITORIES[Testing Repositories]
        TEST_SERVICES[Testing Services]
    end
    
    subgraph "Shared Components"
        SHARED_CONFIG[Shared Configuration]
        SHARED_SECURITY[Security Layer]
        SHARED_MONITORING[Monitoring]
    end
    
    subgraph "Database Schema"
        CORE_SCHEMA[Core Tables]
        TEST_SCHEMA[Testing Tables]
        SHARED_TABLES[Shared Tables]
    end
    
    CORE_LLM --> TEST_ORCHESTRATOR
    CORE_ENTITIES --> TEST_MODELS
    CORE_REPOSITORIES --> TEST_REPOSITORIES
    
    TEST_ORCHESTRATOR --> SHARED_CONFIG
    TEST_MODELS --> SHARED_CONFIG
    SHARED_CONFIG --> SHARED_SECURITY
    SHARED_CONFIG --> SHARED_MONITORING
    
    TEST_REPOSITORIES --> TEST_SCHEMA
    CORE_REPOSITORIES --> CORE_SCHEMA
    SHARED_SECURITY --> SHARED_TABLES
    
    style CORE_LLM fill:#e1f5fe
    style TEST_ORCHESTRATOR fill:#fff3e0
    style SHARED_CONFIG fill:#e8f5e8
```

## 8. Performance and Scaling Architecture

### 8.1 Scalability Design
```mermaid
graph TB
    subgraph "Load Balancer"
        LB[Load Balancer]
    end
    
    subgraph "Application Tier"
        APP1[Application Instance 1]
        APP2[Application Instance 2]
        APP3[Application Instance N]
    end
    
    subgraph "Async Processing"
        QUEUE[Message Queue]
        WORKER_POOL[Worker Pool]
    end
    
    subgraph "Data Layer"
        CACHE[Redis Cache]
        DB_PRIMARY[Primary Database]
        DB_REPLICA[Read Replicas]
    end
    
    subgraph "External Services"
        LLM_CLUSTER[LLM Service Cluster]
        FILE_STORAGE[Distributed File Storage]
    end
    
    LB --> APP1
    LB --> APP2
    LB --> APP3
    
    APP1 --> QUEUE
    APP2 --> QUEUE
    APP3 --> QUEUE
    
    QUEUE --> WORKER_POOL
    
    APP1 --> CACHE
    APP2 --> CACHE
    APP3 --> CACHE
    
    APP1 --> DB_PRIMARY
    APP2 --> DB_REPLICA
    APP3 --> DB_REPLICA
    
    WORKER_POOL --> LLM_CLUSTER
    APP1 --> FILE_STORAGE
    
    style LB fill:#e1f5fe
    style QUEUE fill:#fff3e0
    style LLM_CLUSTER fill:#f3e5f5
    style DB_PRIMARY fill:#e8f5e8
```

## 9. User Interface Wireframes

### 9.1 Main Dashboard
```mermaid
graph TB
    subgraph "Main Dashboard"
        HEADER[Header with Navigation]
        METRICS[Key Metrics Cards]
        PROJECTS[Recent Projects]
        ACTIVITY[System Activity]
        STATUS[System Status]
    end
    
    subgraph "Metrics Cards"
        TOTAL_PROJECTS[Total Projects: 12]
        ACTIVE_TESTS[Active Tests: 45]
        SUCCESS_RATE[Success Rate: 94.2%]
        SECURITY_SCORE[Security Score: A+]
    end
    
    subgraph "Projects List"
        PROJECT1[Project Alpha - API Testing]
        PROJECT2[Project Beta - BPMN Analysis]
        PROJECT3[Project Gamma - Security Scan]
    end
    
    HEADER --> METRICS
    METRICS --> TOTAL_PROJECTS
    METRICS --> ACTIVE_TESTS
    METRICS --> SUCCESS_RATE
    METRICS --> SECURITY_SCORE
    HEADER --> PROJECTS
    PROJECTS --> PROJECT1
    PROJECTS --> PROJECT2
    PROJECTS --> PROJECT3
    HEADER --> ACTIVITY
    HEADER --> STATUS
```

### 9.2 Test Execution Monitor
```mermaid
graph TB
    subgraph "Execution Monitor"
        EXEC_HEADER[Execution Control Panel]
        PROGRESS[Progress Visualization]
        TIMELINE[Test Timeline]
        LOGS[Real-time Logs]
        RESULTS[Results Summary]
    end
    
    subgraph "Progress Section"
        STEP1[Step 1/10: API Authentication]
        STEP2[Step 2/10: User Registration]
        STEP3[Step 3/10: Data Validation]
        CURRENT[Current: Security Scan]
    end
    
    subgraph "Timeline"
        T1[08:30 Started]
        T2[08:32 Authentication]
        T3[08:34 Registration]
        T4[08:36 Current Step]
    end
    
    subgraph "Logs"
        INFO[INFO: Authentication successful]
        WARNING[WARNING: Rate limit approaching]
        ERROR[ERROR: Validation failed]
    end
    
    EXEC_HEADER --> PROGRESS
    PROGRESS --> STEP1
    PROGRESS --> STEP2
    PROGRESS --> STEP3
    PROGRESS --> CURRENT
    EXEC_HEADER --> TIMELINE
    TIMELINE --> T1
    TIMELINE --> T2
    TIMELINE --> T3
    TIMELINE --> T4
    EXEC_HEADER --> LOGS
    LOGS --> INFO
    LOGS --> WARNING
    LOGS --> ERROR
    EXEC_HEADER --> RESULTS
```

## 10. Security Architecture

### 10.1 Security Layers
```mermaid
graph TB
    subgraph "Network Security"
        FIREWALL[Firewall]
        DDoS[DDoS Protection]
        SSL[SSL/TLS]
    end
    
    subgraph "Application Security"
        AUTH[Authentication]
        AUTHZ[Authorization]
        INPUT_VAL[Input Validation]
        CORS[CORS Policy]
    end
    
    subgraph "Data Security"
        ENCRYPTION[Data Encryption]
        ACCESS_CTRL[Access Control]
        AUDIT[Audit Logging]
        BACKUP[Secure Backup]
    end
    
    subgraph "API Security"
        RATE_LIMIT[Rate Limiting]
        API_KEYS[API Key Management]
        OAUTH[OAuth 2.0]
        JWT[JWT Tokens]
    end
    
    FIREWALL --> AUTH
    DDoS --> AUTHZ
    SSL --> INPUT_VAL
    CORS --> ENCRYPTION
    
    ENCRYPTION --> RATE_LIMIT
    ACCESS_CTRL --> API_KEYS
    AUDIT --> OAUTH
    BACKUP --> JWT
    
    style FIREWALL fill:#ffebee
    style AUTH fill:#ffebee
    style ENCRYPTION fill:#e8f5e8
    style RATE_LIMIT fill:#e8f5e8
```

## 11. Monitoring and Observability

### 11.1 Monitoring Architecture
```mermaid
graph TB
    subgraph "Application Metrics"
        APP_METRICS[Application Metrics]
        CUSTOM_METRICS[Custom Metrics]
    end
    
    subgraph "Infrastructure Monitoring"
        SYSTEM_MON[System Monitoring]
        DB_MON[Database Monitoring]
        API_MON[API Monitoring]
    end
    
    subgraph "Logging"
        APP_LOGS[Application Logs]
        AUDIT_LOGS[Audit Logs]
        ERROR_LOGS[Error Logs]
    end
    
    subgraph "Alerting"
        ALERTS[Alert Manager]
        NOTIFICATIONS[Notifications]
        DASHBOARDS[Monitoring Dashboards]
    end
    
    APP_METRICS --> ALERTS
    CUSTOM_METRICS --> ALERTS
    SYSTEM_MON --> ALERTS
    DB_MON --> ALERTS
    API_MON --> ALERTS
    
    APP_LOGS --> DASHBOARDS
    AUDIT_LOGS --> DASHBOARDS
    ERROR_LOGS --> DASHBOARDS
    
    ALERTS --> NOTIFICATIONS
    DASHBOARDS --> NOTIFICATIONS
    
    style ALERTS fill:#ffebee
    style DASHBOARDS fill:#e8f5e8
    style NOTIFICATIONS fill:#e1f5fe
```

Эти диаграммы обеспечивают полное понимание архитектуры системы и помогут команде разработки в реализации каждого компонента. Они показывают как высокоуровневые потоки данных, так и детальную архитектуру каждого модуля системы автоматического тестирования.