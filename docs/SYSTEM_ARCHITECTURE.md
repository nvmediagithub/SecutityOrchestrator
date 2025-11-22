# Security Orchestrator - System Architecture

## Overview

Security Orchestrator is an enterprise-grade platform for comprehensive security testing and orchestration. The system follows a modern microservices architecture with feature-first modular design, integrating LLM-powered analysis capabilities for BPMN workflows, API specifications, and automated security testing.

## High-Level System Architecture

```mermaid
graph TB
    %% User Interface Layer
    subgraph "Presentation Layer"
        WEB[Flutter Web Application]
        MOBILE[Mobile Responsive Interface]
        ADMIN[Admin Dashboard]
    end
    
    %% API Gateway Layer
    subgraph "API Gateway & Load Balancer"
        GATEWAY[Spring Cloud Gateway]
        LB[Load Balancer]
        AUTH[Authentication/Authorization]
    end
    
    %% Core Application Layer
    subgraph "Backend Services (Java 21+)"
        APP[Main Application Entry]
        FEATURES[Feature Modules]
        
        subgraph "Core Features"
            PIPELINE[Analysis Pipeline]
            BPMN_ENGINE[BPMN Engine]
            OPENAPI[OpenAPI Module]
            LLM_CORE[LLM Integration]
            ORCHESTRATION[Orchestration]
            MONITORING[Monitoring]
            TESTDATA[Test Data]
        end
        
        SHARED[Shared Components]
    end
    
    %% Data Layer
    subgraph "Data Storage Layer"
        DATABASE[H2 File Database]
        FILES[File Storage]
        CACHE[Caffeine Cache]
    end
    
    %% External Integrations
    subgraph "External Systems & LLM Providers"
        subgraph "LLM Providers"
            OLLAMA[Ollama Local]
            OPENROUTER[OpenRouter Cloud]
            QWEN[qwen3-coder:480b-cloud]
        end
        
        subgraph "Enterprise Integrations"
            SAP[SAP S/4HANA]
            SALESFORCE[Salesforce]
            SERVICENOW[ServiceNow]
            AD[Active Directory]
        end
        
        TARGET_APIS[Target APIs for Testing]
    end
    
    %% Security & Monitoring
    subgraph "Security & Observability"
        SECURITY[Security Layer]
        LOGGING[Centralized Logging]
        METRICS[Metrics Collection]
        ALERTS[Alert Management]
    end
    
    %% Connections
    WEB --> GATEWAY
    MOBILE --> GATEWAY
    ADMIN --> GATEWAY
    
    GATEWAY --> LB
    LB --> AUTH
    AUTH --> APP
    
    APP --> FEATURES
    APP --> SHARED
    
    FEATURES --> PIPELINE
    FEATURES --> BPMN_ENGINE
    FEATURES --> OPENAPI
    FEATURES --> LLM_CORE
    FEATURES --> ORCHESTRATION
    FEATURES --> MONITORING
    FEATURES --> TESTDATA
    
    FEATURES --> DATABASE
    FEATURES --> FILES
    FEATURES --> CACHE
    
    LLM_CORE --> OLLAMA
    LLM_CORE --> OPENROUTER
    LLM_CORE --> QWEN
    
    FEATURES --> SAP
    FEATURES --> SALESFORCE
    FEATURES --> SERVICENOW
    FEATURES --> AD
    
    PIPELINE --> TARGET_APIS
    OPENAPI --> TARGET_APIS
    
    MONITORING --> LOGGING
    MONITORING --> METRICS
    MONITORING --> ALERTS
    SECURITY --> MONITORING
    
    %% Styling
    classDef frontend fill:#e3f2fd
    classDef backend fill:#f3e5f5
    classDef data fill:#e8f5e8
    classDef external fill:#fff3e0
    classDef security fill:#ffebee
    
    class WEB,MOBILE,ADMIN frontend
    class APP,FEATURES,SHARED backend
    class DATABASE,FILES,CACHE data
    class OLLAMA,OPENROUTER,QWEN,SAP,SALESFORCE,SERVICENOW,AD,TARGET_APIS external
    class SECURITY,LOGGING,METRICS,ALERTS security
```

## System Components Overview

### 1. Presentation Layer
- **Flutter Web Application**: Modern, responsive web interface with Material Design components
- **State Management**: Provider pattern with flutter_riverpod for reactive state management
- **Real-time Updates**: WebSocket connections for live monitoring and notifications
- **Mobile Responsive**: Optimized for desktop, tablet, and mobile devices

### 2. API Gateway & Load Balancing
- **Spring Cloud Gateway**: Centralized API gateway with routing, authentication, and rate limiting
- **Load Balancer**: Distributes requests across multiple backend instances
- **Authentication/Authorization**: JWT-based authentication with enterprise SSO support

### 3. Backend Services Architecture

#### Core Application Entry
```mermaid
graph LR
    APP[Application Entry Point] --> CONFIG[Configuration Management]
    APP --> BEANS[Spring Beans Initialization]
    APP --> MODULES[Feature Module Bootstrap]
    
    MODULES --> FEATURE1[Analysis Pipeline]
    MODULES --> FEATURE2[BPMN Engine]
    MODULES --> FEATURE3[OpenAPI Module]
    MODULES --> FEATURE4[LLM Integration]
    MODULES --> FEATURE5[Orchestration]
    MODULES --> FEATURE6[Monitoring]
    MODULES --> FEATURE7[Test Data]
```

#### Feature-First Modular Architecture
Each feature module follows clean architecture with domain, application, infrastructure, and presentation layers:

- **Domain Layer**: Business entities, value objects, domain services
- **Application Layer**: Use cases, DTOs, application services
- **Infrastructure Layer**: External integrations, persistence, frameworks
- **Presentation Layer**: REST controllers, WebSocket handlers

### 4. Data Storage Strategy
- **H2 File Database**: Primary data persistence for development and small deployments
- **File Storage**: BPMN files, OpenAPI specifications, test reports
- **Caffeine Cache**: High-performance caching for frequently accessed data

### 5. LLM Integration Architecture

```mermaid
graph TB
    subgraph "LLM Integration Layer"
        LLM_API[LLM Service API]
        PROVIDER_REGISTRY[Provider Registry]
        CIRCUIT_BREAKER[Circuit Breaker]
    end
    
    subgraph "Provider Implementations"
        LOCAL_SERVICE[Local LLM Service]
        CLOUD_SERVICE[Cloud LLM Service]
        FALLBACK[Fallback Service]
    end
    
    subgraph "LLM Providers"
        OLLAMA[Ollama Local]
        OPENROUTER[OpenRouter Cloud]
        QWEN[qwen3-coder:480b-cloud]
    end
    
    FEATURES --> LLM_API
    LLM_API --> PROVIDER_REGISTRY
    PROVIDER_REGISTRY --> LOCAL_SERVICE
    PROVIDER_REGISTRY --> CLOUD_SERVICE
    LLM_API --> CIRCUIT_BREAKER
    CIRCUIT_BREAKER --> FALLBACK
    
    LOCAL_SERVICE --> OLLAMA
    CLOUD_SERVICE --> OPENROUTER
    CLOUD_SERVICE --> QWEN
```

### 6. Enterprise Integration Capabilities

```mermaid
graph LR
    subgraph "Enterprise Connectors"
        SAP_CONN[SAP Connector]
        SF_CONN[Salesforce Connector]
        SN_CONN[ServiceNow Connector]
        AD_CONN[AD/LDAP Connector]
    end
    
    subgraph "Message Brokers"
        KAFKA[Apache Kafka]
        RABBIT[RabbitMQ]
    end
    
    subgraph "Database Connectors"
        ORACLE[Oracle Database]
        POSTGRES[PostgreSQL]
        MSSQL[SQL Server]
    end
    
    FEATURES --> SAP_CONN
    FEATURES --> SF_CONN
    FEATURES --> SN_CONN
    FEATURES --> AD_CONN
    
    SAP_CONN --> ORACLE
    SF_CONN --> POSTGRES
    SN_CONN --> MSSQL
    
    CONN_QUEUES[Connectors] --> KAFKA
    CONN_QUEUES --> RABBIT
```

## Data Flow Architecture

### 1. Analysis Workflow Data Flow

```mermaid
sequenceDiagram
    participant UI as Flutter UI
    participant API as REST API
    participant ORCH as Orchestration
    participant BPMN as BPMN Engine
    participant OPENAPI as OpenAPI Module
    participant LLM as LLM Service
    participant DB as Database
    participant TARGET as Target APIs
    
    UI->>API: Upload BPMN & OpenAPI
    API->>ORCH: Create Analysis Job
    ORCH->>BPMN: Parse BPMN
    ORCH->>OPENAPI: Validate API Spec
    ORCH->>LLM: Generate Test Cases
    LLM->>LLM: Process with AI
    LLM-->>ORCH: Test Cases & Data
    ORCH->>TARGET: Execute Tests
    TARGET-->>ORCH: Test Results
    ORCH->>DB: Store Results
    ORCH-->>API: Analysis Complete
    API-->>UI: Display Results
```

### 2. Real-time Monitoring Flow

```mermaid
sequenceDiagram
    participant MON as Monitoring Service
    participant WS as WebSocket
    participant UI as Flutter UI
    participant METRICS as Metrics Collector
    participant ALERTS as Alert System
    
    MON->>METRICS: Collect Metrics
    METRICS->>ALERTS: Check Thresholds
    ALERTS->>WS: Send Alert
    WS->>UI: Real-time Update
    MON->>WS: Status Updates
    WS->>UI: Live Dashboard
```

## Technology Stack Summary

### Backend Technologies
- **Java 21+**: Latest LTS with enhanced performance and features
- **Spring Boot 3.x**: Modern application framework with native support
- **Gradle**: Advanced build automation and dependency management
- **Feature-First Architecture**: Business capability focused modular design

### Frontend Technologies
- **Flutter Web**: Cross-platform UI framework
- **Material Design 3**: Modern UI components and theming
- **Provider Pattern**: Reactive state management
- **GoRouter**: Declarative routing solution

### Data & Storage
- **H2 Database**: File-based relational database for portability
- **Caffeine**: High-performance caching library
- **File System**: Local storage for documents and reports

### AI & LLM Integration
- **Ollama**: Local LLM runtime for privacy-sensitive operations
- **OpenRouter**: Cloud LLM provider with multiple model access
- **qwen3-coder:480b-cloud**: Specialized coding assistant model
- **Circuit Breaker**: Resilient service communication

### Integration & Communication
- **Spring Cloud Gateway**: API gateway and load balancing
- **Apache Kafka**: Event streaming and message queuing
- **RabbitMQ**: Reliable message delivery
- **WebSocket**: Real-time communication

### Security & Monitoring
- **JWT Authentication**: Stateless authentication mechanism
- **SAML 2.0**: Enterprise single sign-on
- **SLF4J + Logback**: Structured logging framework
- **Micrometer**: Application metrics and monitoring

## Deployment Architecture

### Development Environment
```mermaid
graph TB
    subgraph "Local Development"
        DEV_WEB[Flutter Web Dev Server]
        DEV_BACKEND[Spring Boot Application]
        DEV_DB[H2 Database]
        DEV_OLLAMA[Ollama Local]
    end
    
    DEV_WEB --> DEV_BACKEND
    DEV_BACKEND --> DEV_DB
    DEV_BACKEND --> DEV_OLLAMA
```

### Production Environment
```mermaid
graph TB
    subgraph "Production Deployment"
        LB[Load Balancer]
        subgraph "Backend Cluster"
            INST1[Backend Instance 1]
            INST2[Backend Instance 2]
            INST3[Backend Instance N]
        end
        DB[Production Database]
        CACHE[Redis Cluster]
        LLM_CLUSTER[LLM Cluster]
        
        LB --> INST1
        LB --> INST2
        LB --> INST3
        
        INST1 --> DB
        INST2 --> DB
        INST3 --> DB
        
        INST1 --> CACHE
        INST2 --> CACHE
        INST3 --> CACHE
        
        INST1 --> LLM_CLUSTER
        INST2 --> LLM_CLUSTER
        INST3 --> LLM_CLUSTER
    end
```

## Scalability & Performance

### Horizontal Scaling
- **Stateless Backend Services**: Easy horizontal scaling with load balancing
- **Feature Module Independence**: Independent scaling of specific capabilities
- **Database Clustering**: Support for read replicas and sharding

### Performance Optimization
- **Asynchronous Processing**: Non-blocking I/O operations
- **Caching Strategy**: Multi-level caching for optimal performance
- **Connection Pooling**: Efficient resource utilization
- **Circuit Breaker**: Fault tolerance and resilience

## Security Architecture

### Multi-Layer Security
1. **Network Security**: HTTPS, VPN, firewall protection
2. **Application Security**: Input validation, authentication, authorization
3. **Data Security**: Encryption at rest and in transit
4. **API Security**: Rate limiting, API keys, JWT tokens

### Compliance & Audit
- **Audit Logging**: Comprehensive activity tracking
- **Data Protection**: GDPR and enterprise compliance
- **Security Monitoring**: Real-time threat detection
- **Incident Response**: Automated alert and response systems

This system architecture provides a robust, scalable, and secure foundation for the Security Orchestrator platform, supporting enterprise-grade security testing and orchestration capabilities.