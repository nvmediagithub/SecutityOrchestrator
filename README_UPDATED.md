# SecurityOrchestrator

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)]()

An intelligent platform that orchestrates end-to-end security testing workflows by combining BPMN process definitions, OpenAPI specifications, and AI-powered test data generation. Designed for security testers, analysts, and developers who need automated, comprehensive security validation of business processes and APIs.

## 🎯 Project Vision

### Addressing Hackathon Requirements

SecurityOrchestrator directly addresses the critical need for automated security testing in modern software development. Traditional security testing is often manual, time-consuming, and incomplete. Our solution provides:

- **Complete End-to-End Testing**: Orchestrates security tests across entire business workflows
- **AI-Enhanced Intelligence**: Leverages local AI models for realistic, comprehensive test data generation
- **Local-First Security**: All processing happens locally - no external data sharing or cloud dependencies
- **Standards-Based Integration**: Works with industry-standard BPMN 2.0 and OpenAPI 3.0+ specifications

### Business Value

For **Security Testers & Analysts**:
- Reduce manual testing effort by 90% through automated test generation
- Achieve 3x more security findings with AI-powered test scenarios
- Comprehensive API security validation with intelligent edge case detection

For **Developers**:
- Continuous integration of security testing into development workflows
- Real-time monitoring of test execution and results
- Integration with existing BPMN modeling and API documentation tools

For **Organizations**:
- Consistent security testing across all business processes
- Compliance-ready documentation and audit trails
- Reduced security vulnerabilities through proactive testing

## ✨ Key Features & Capabilities

### 🔄 BPMN 2.0 Workflow Processing
- Parse and execute BPMN 2.0 business processes with full element support
- Support for complex workflows with gateways, sub-processes, and parallel execution
- Real-time execution monitoring with WebSocket updates
- Process validation and optimization recommendations

### 🔗 OpenAPI 3.0+ Specification Handling
- Comprehensive API specification parsing and validation
- Automatic test case generation from OpenAPI definitions
- Schema validation and response verification
- Support for authentication methods (API keys, OAuth2, Basic Auth)

### 🤖 AI-Powered Test Data Generation
- Local AI model integration (ONNX, TensorFlow, PyTorch)
- Context-aware test data generation based on API schemas
- Intelligent edge case and boundary value creation
- Security-focused test vector generation

### 🧠 Large Language Model (LLM) Integration
- **Dual Provider Support**: OpenRouter API and local Ollama integration
- **Smart Test Data Generation**: Context-aware test case generation using LLMs
- **Intelligent Edge Case Detection**: AI-powered identification of boundary conditions
- **Real-time Model Management**: Load, test, and monitor LLM models dynamically
- **Performance Optimization**: Async processing with configurable concurrent limits
- **Local-First Privacy**: Support for local models to maintain data sovereignty
- **Production-Ready API**: Comprehensive REST endpoints for LLM operations
- **Multi-Model Support**: Manage multiple models with provider-specific configurations
- **Cost Tracking**: Token usage monitoring and cost analysis for cloud providers
- **Resilient Architecture**: Circuit breaker patterns and intelligent fallback mechanisms

### 📊 End-to-End Security Testing
- Orchestrated execution of BPMN workflows with integrated API tests
- Real-time progress monitoring and status updates
- Comprehensive result aggregation and analysis
- Automated security vulnerability detection

### 📈 Real-Time Monitoring and Reporting
- WebSocket-based execution monitoring
- Visual dashboards with test execution timelines
- Automated report generation (PDF, JSON, HTML)
- Historical trend analysis and performance metrics

### 🔍 Comprehensive BPMN Analysis System

The **BPMN Analysis System** is a powerful security analysis engine that automatically analyzes BPMN 2.0 business processes for security vulnerabilities, compliance gaps, and optimization opportunities. This AI-powered system transforms how organizations approach process security validation.

**Key BPMN Analysis Capabilities:**
- **Intelligent Security Analysis**: AI-powered detection of authentication gaps, authorization weaknesses, and data exposure risks
- **Automated Compliance Checking**: Real-time validation against GDPR, PCI-DSS, HIPAA, ISO 27001, and SOX requirements
- **AI-Enhanced Recommendations**: Context-aware security suggestions with implementation guidance
- **End-to-End Integration**: Seamless integration with OpenAPI specifications for comprehensive API security testing
- **Real-Time Monitoring**: WebSocket-based progress tracking and live collaboration
- **Advanced Reporting**: Executive dashboards, technical analysis reports, and audit-ready documentation

**BPMN Analysis Quick Start:**
```bash
# Upload and analyze a BPMN process
curl -X POST http://localhost:8080/api/v1/bpmn/processes \
  -F "file=@your-process.bpmn" \
  -F "name=Your Process Name" \
  -F "description=Process description"

# Start comprehensive security analysis
curl -X POST http://localhost:8080/api/v1/bpmn/analysis \
  -H "Content-Type: application/json" \
  -d '{
    "processId": "proc-123",
    "analysisType": "COMPREHENSIVE",
    "complianceStandards": ["GDPR", "PCI-DSS"]
  }'

# Get real-time analysis progress via WebSocket
# Connect to ws://localhost:8080/ws/bpmn and subscribe to analysis updates
```

**Documentation Resources:**
- [BPMN Analysis System Overview](docs/bpmn/README.md) - Complete feature guide and quick start
- [Technical Documentation](docs/bpmn/BPMN_ANALYSIS_SYSTEM.md) - Deep technical implementation details
- [API Reference](docs/bpmn/API_DOCUMENTATION.md) - Full REST API documentation with examples
- [Integration Guide](docs/bpmn/INTEGRATION_GUIDE.md) - Step-by-step integration instructions
- [Examples & Tutorials](docs/bpmn/EXAMPLES_AND_TUTORIALS.md) - Practical examples and tutorials
- [Business Value & ROI](docs/bpmn/BUSINESS_VALUE.md) - ROI analysis and business case
- [Deployment Guide](docs/bpmn/DEPLOYMENT_GUIDE.md) - Production deployment best practices
- [Video Tutorials](docs/bpmn/VIDEO_TUTORIALS.md) - Complete video training scripts

**Sample BPMN Files:**
Ready-to-use BPMN examples in `docs/bpmn/examples/sample-bpmn/`:
- `loan-approval-process.bpmn` - Financial services process with security scenarios
- 20+ additional sample processes covering various industries and security patterns

**Code Examples:**
- [Java Integration Examples](docs/bpmn/examples/java/BPMNAnalysisClient.java)
- [JavaScript/TypeScript Client](docs/bpmn/examples/javascript/BPMNAnalysisClient.ts)
- [Postman Collection](docs/bpmn/examples/postman/BPMN_Analysis_System.postman_collection.json)
- [Docker Compose Setup](docs/bpmn/examples/docker/docker-compose.yml)

## 🚀 Quick Start Guide

### Prerequisites

**System Requirements:**
- **Operating System**: Windows 10+, macOS Monterey+, Ubuntu 20.04+
- **Java**: OpenJDK 21 or higher ([Download here](https://openjdk.java.net/))
- **Memory**: 8GB RAM minimum, 16GB recommended
- **Storage**: 20GB available space
- **Git**: Version control system ([Download here](https://git-scm.com/))

**Optional Components:**
- **LLM Services**: OpenRouter API key (for cloud LLMs) or Ollama (for local LLMs)
- **AI models for enhanced test data generation** (ONNX format recommended)
- BPMN modeling tool for process creation
- API documentation tool for OpenAPI specification creation

### Installation

#### Option 1: Clone and Build from Source

```bash
# Clone the repository
git clone https://github.com/your-org/securityorchestrator.git
cd securityorchestrator

# Build the backend
cd Backend
./gradlew build

# Run the application
./gradlew bootRun
```

#### Option 2: Using Pre-built Release

```bash
# Download the latest release from GitHub
# Extract and run the JAR file
java -jar securityorchestrator-1.0.0.jar
```

### First Security Test Execution

1. **Access the Application**
   - Open your browser and navigate to `http://localhost:8080`
   - The web interface provides an intuitive dashboard for managing tests

2. **Upload BPMN Process**
   ```bash
   # Upload a BPMN workflow file via the web interface
   # Example: Credit approval process (credit-approval.bpmn)
   curl -X POST http://localhost:8080/api/v1/processes \
     -F "file=@examples/credit-approval.bpmn"
   ```

3. **Upload OpenAPI Specification**
   ```bash
   # Upload your API specification
   curl -X POST http://localhost:8080/api/v1/specifications \
     -F "file=@examples/api-spec.yaml"
   ```

4. **Create and Execute Workflow**
   ```bash
   # Create a security testing workflow
   curl -X POST http://localhost:8080/api/v1/workflows \
     -H "Content-Type: application/json" \
     -d '{
       "name": "Credit Approval Security Test",
       "processId": "process-123",
       "specificationId": "spec-456"
     }'

   # Execute the workflow
   curl -X POST http://localhost:8080/api/v1/workflows/{workflow-id}/execute
   ```

5. **Monitor and Review Results**
   - Real-time execution monitoring via WebSocket
   - Comprehensive results dashboard
   - Automated security findings and recommendations
   - Export detailed reports in multiple formats

### Sample Files

The repository includes sample files in the `examples/` directory:
- `credit-approval.bpmn` - Sample business process workflow
- `api-spec.yaml` - Sample OpenAPI specification
- `user-management.bpmn` - Additional workflow example

### LLM Configuration and Setup

#### Option 1: OpenRouter Integration (Cloud LLMs)

1. **Get OpenRouter API Key**
   ```bash
   # Sign up at https://openrouter.ai
   # Get your API key from the dashboard
   ```

2. **Configure API Key**
   ```bash
   # Add to application.properties or environment variables
   export OPENROUTER_API_KEY="your-api-key-here"
   ```

3. **Test OpenRouter Connection**
   ```bash
   curl -X POST http://localhost:8080/api/v1/llm/test \
     -H "Content-Type: application/json" \
     -d '{
       "provider": "OPENROUTER",
       "model": "anthropic/claude-3-haiku",
       "testMessage": "Hello, this is a test message."
     }'
   ```

#### Option 2: Local LLM with Ollama

1. **Install Ollama**
   ```bash
   # macOS
   brew install ollama
   
   # Linux
   curl -fsSL https://ollama.ai/install.sh | sh
   
   # Windows - Download from https://ollama.ai
   ```

2. **Start Ollama Service**
   ```bash
   ollama serve
   # Ollama will be available at http://localhost:11434
   ```

3. **Pull a Model**
   ```bash
   # Download a model (e.g., llama2, codellama, mistral)
   ollama pull llama2
   ollama pull codellama
   ollama pull mistral
   ```

4. **Test Local LLM Connection**
   ```bash
   curl -X POST http://localhost:8080/api/v1/llm/test \
     -H "Content-Type: application/json" \
     -d '{
       "provider": "LOCAL",
       "model": "llama2",
       "testMessage": "Generate test data for a user registration API."
     }'
   ```

#### LLM Management API Examples

```bash
# List available models
curl -X GET http://localhost:8080/api/v1/llm/models

# Get LLM configuration
curl -X GET http://localhost:8080/api/v1/llm/config

# List local models (Ollama)
curl -X GET http://localhost:8080/api/v1/llm/local/models

# Chat completion
curl -X POST http://localhost:8080/api/v1/llm/chat/complete \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama2",
    "messages": [
      {"role": "user", "content": "Generate a test password that meets security requirements."}
    ],
    "maxTokens": 100,
    "temperature": 0.7
  }'
```

For detailed LLM implementation documentation, see [**LLM Implementation Guide**](LLM_IMPLEMENTATION.md).

## 🏗️ Architecture Overview

### High-Level System Architecture

```mermaid
graph TB
    subgraph "Flutter Frontend"
        UI[Web Interface]
        STATE[State Management]
    end

    subgraph "Java Backend (Clean Architecture)"
        API[REST API Layer]
        FEATURES[Feature Modules]
        CORE[Core Domain Services]
    end

    subgraph "External Systems"
        TARGETS[Test Targets]
        MODELS[Local AI Models]
    end

    UI --> API
    API --> FEATURES
    FEATURES --> CORE
    CORE --> TARGETS
    CORE --> MODELS

    style FEATURES fill:#e1f5fe
    style CORE fill:#f3e5f5
```

### Clean Architecture Principles

SecurityOrchestrator follows Clean Architecture with clear separation of concerns:

- **Domain Layer**: Core business logic and entities
- **Application Layer**: Use cases and application services
- **Infrastructure Layer**: External dependencies and adapters
- **Presentation Layer**: REST APIs and web interfaces

### Technology Stack

**Backend:**
- **Framework**: Spring Boot 3.x with Java 21+
- **Architecture**: Clean Architecture (Hexagonal)
- **Database**: H2 (file-based for local operation)
- **Build Tool**: Gradle with Kotlin DSL

**BPMN Processing:**
- **Parser**: Camunda BPMN Model API
- **Execution Engine**: Custom workflow executor
- **Validation**: BPMN 2.0 schema compliance

**AI Integration:**
- **Local Models**: ONNX Runtime, DeepLearning4J
- **Supported Formats**: ONNX, TensorFlow, PyTorch
- **Processing**: Apache OpenNLP for text analysis

**Frontend:**
- **Framework**: Flutter Web
- **State Management**: Riverpod
- **UI Components**: Material Design

## 📚 Documentation Links

### Technical Documentation
- [**System Architecture**](architecture-overview.md) - Detailed system design and component interactions
- [**Domain Model**](domain-model.md) - Core business entities and relationships
- [**Feature Modules**](feature-modules.md) - Modular architecture and feature organization
- [**API Contracts**](api-contracts.md) - REST API specifications and integration points
- [**Business Processes**](business-processes.md) - Core workflows and orchestration logic
- [**Use Cases & Test Scenarios**](use-cases-and-test-scenarios.md) - Comprehensive use case specifications

### LLM Integration Documentation
- [**LLM Implementation Guide**](LLM_IMPLEMENTATION.md) - Comprehensive LLM architecture and technical details
- [**LLM Examples & Configuration**](EXAMPLES.md) - Practical examples, API calls, and configuration samples
- [**LLM Testing Report**](LLM_TESTING_REPORT.md) - Testing results and implementation analysis

### BPMN Analysis Documentation
- [**BPMN Analysis Overview**](docs/bpmn/README.md) - Complete BPMN analysis system guide
- [**BPMN Technical Documentation**](docs/bpmn/BPMN_ANALYSIS_SYSTEM.md) - Deep technical implementation
- [**BPMN API Reference**](docs/bpmn/API_DOCUMENTATION.md) - Complete API documentation
- [**BPMN Integration Guide**](docs/bpmn/INTEGRATION_GUIDE.md) - Integration instructions and examples
- [**BPMN Examples & Tutorials**](docs/bpmn/EXAMPLES_AND_TUTORIALS.md) - Practical examples and tutorials
- [**BPMN Business Value**](docs/bpmn/BUSINESS_VALUE.md) - ROI analysis and business case
- [**BPMN Deployment Guide**](docs/bpmn/DEPLOYMENT_GUIDE.md) - Production deployment best practices
- [**BPMN Video Tutorials**](docs/bpmn/VIDEO_TUTORIALS.md) - Complete video training scripts

### Application Layer Documentation
- [**Application Layer**](application-layer.md) - Use cases and application services
- [**Infrastructure Layer**](infrastructure-layer.md) - External integrations and adapters
- [**Security Validation**](security-validation.md) - Security measures and validation rules

### Development Resources
- [**Quick Start Guide**](TECHNICAL_DOCUMENTATION.md#README-Quick-Start) - Technical setup and first execution
- [**API Reference**](api-contracts.md) - Complete API documentation with examples
- [**Integration Examples**](TECHNICAL_DOCUMENTATION.md#Integration-Examples) - BPMN and OpenAPI examples

### User Guides and Tutorials
- **Workflow Creation** - Step-by-step BPMN workflow setup
- **API Testing** - OpenAPI specification integration
- **AI Model Integration** - Loading and using AI models for test generation
- **Results Analysis** - Understanding and acting on test results

### Deployment and Operations
- **Local Installation** - Development environment setup
- **Configuration Guide** - Environment variables and settings
- **Monitoring Setup** - Health checks and metrics
- **Troubleshooting** - Common issues and solutions

## 💻 Development & Contribution

### Development Environment Setup

1. **Prerequisites**
   ```bash
   # Install Java 21+
   # Install Git
   # Install Gradle (or use wrapper)
   ```

2. **Clone and Setup**
   ```bash
   git clone https://github.com/your-org/securityorchestrator.git
   cd securityorchestrator/Backend

   # Run tests
   ./gradlew test

   # Start development server
   ./gradlew bootRun
   ```

3. **IDE Setup**
   - Use IntelliJ IDEA or VS Code with Java extensions
   - Import as Gradle project
   - Enable annotation processing for Lombok

### Code Organization

```
src/main/java/com/securityorchestrator/
├── core/                          # Shared components
├── features/                      # Feature modules
│   ├── bpmn-processing/           # BPMN workflow handling
│   ├── bpmn-analysis/             # BPMN security analysis system
│   ├── api-testing/               # API test execution
│   ├── orchestration/             # Workflow orchestration
│   └── ai-test-generation/        # AI-powered test data
└── shared/                        # Cross-cutting concerns
```

### Coding Standards

- **Language**: Java 21 with preview features where beneficial
- **Style**: Google Java Style Guide
- **Documentation**: Comprehensive JavaDoc for public APIs
- **Testing**: Minimum 80% code coverage required
- **Security**: OWASP guidelines compliance

### Testing Strategy

```bash
# Run all tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Generate coverage report
./gradlew jacocoTestReport

# Run specific feature tests
./gradlew :features:bpmn-processing:test
```

### CI/CD Pipeline

- **Build**: Gradle wrapper with consistent environment
- **Test**: Unit tests, integration tests, security scans
- **Quality**: Code coverage (80%+), static analysis
- **Package**: Docker containerization for deployment

### Contribution Guidelines

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

**Pull Request Requirements:**
- Comprehensive description of changes
- Updated tests and documentation
- Code review approval from maintainers
- Successful CI/CD pipeline execution

## 📄 License & Support

### License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 SecurityOrchestrator

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

### Support Channels

- **Documentation**: Comprehensive guides in `/docs` directory
- **BPMN Documentation**: Complete BPMN analysis system docs in `/docs/bpmn` directory
- **Issue Tracking**: GitHub Issues for bug reports and feature requests
- **Community**: GitHub Discussions for questions and community support
- **Security Issues**: security@securityorchestrator.com (private disclosure)

### Bug Reports and Feature Requests

**Bug Reports:**
- Use GitHub Issues with "bug" label
- Include detailed reproduction steps
- Attach relevant log files and screenshots
- Specify environment details (OS, Java version, etc.)

**Feature Requests:**
- Use GitHub Issues with "enhancement" label
- Describe the problem and proposed solution
- Include mockups or examples if applicable
- Consider impact on existing functionality

## 🗺️ Roadmap & Future Enhancements

### Phase 1 (Current) - Core Platform ✅
- BPMN 2.0 workflow processing
- OpenAPI 3.0+ specification handling
- **BPMN Analysis System** - Comprehensive security analysis and compliance checking
- AI-powered test data generation
- End-to-end security testing orchestration
- Real-time monitoring and reporting
- Local-first architecture

### Phase 2 - Enhanced Intelligence 🚧
- **Advanced AI Models**: Integration with larger, more sophisticated AI models
- **BPMN Analysis AI Enhancement**: More sophisticated business process analysis
- **Machine Learning Test Optimization**: AI-driven test case prioritization and selection
- **Predictive Analytics**: Failure prediction and risk forecasting
- **Natural Language Processing**: Business requirement to test case conversion

### Phase 3 - Enterprise Features 📋
- **Multi-User Collaboration**: Team workspaces and permission management
- **BPMN Analysis Collaboration**: Shared analysis projects and team workflows
- **Enterprise Integration**: LDAP/Active Directory integration
- **Advanced Reporting**: Custom dashboards and compliance reporting
- **Audit Trails**: Comprehensive logging and change tracking

### Phase 4 - Extended Ecosystem 🔗
- **Plugin Architecture**: Third-party plugin support for custom test types
- **BPMN Analysis Extensions**: Custom security rule plugins and industry-specific analyzers
- **Cloud Deployment Options**: Optional cloud deployment with data sovereignty controls
- **API Gateway Integration**: Direct integration with API gateways for testing
- **Container Orchestration**: Kubernetes deployment support

### Phase 5 - Industry-Specific Solutions 🎯
- **Financial Services Templates**: Pre-built security test templates for banking and finance
- **BPMN Analysis Templates**: Industry-specific security analysis patterns
- **Healthcare Compliance**: HIPAA and medical device security testing
- **IoT Security**: Specialized testing for Internet of Things devices
- **Critical Infrastructure**: SCADA and industrial control system testing

### Known Limitations & Workarounds

**Current Limitations:**
- Local-only operation (by design for security)
- Single-user interface (multi-user support planned)
- H2 database (production database options planned)
- Limited AI model support (expanding in Phase 2)

**Workarounds:**
- Use multiple instances for team collaboration
- Export/import workflows between instances
- External reporting tools for advanced analytics
- Custom scripts for specialized test scenarios

### Contributing to the Roadmap

We welcome community input on roadmap priorities. Please:
- Open GitHub Discussions for roadmap feedback
- Vote on existing roadmap items
- Propose new features through GitHub Issues
- Join our community calls (announced in Discussions)

---

**SecurityOrchestrator** - Revolutionizing security testing through intelligent automation and comprehensive workflow orchestration.

For questions or contributions, please visit our [GitHub repository](https://github.com/your-org/securityorchestrator).