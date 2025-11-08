# BPMN Analysis System - Java Integration Examples

## Overview

This directory contains comprehensive Java code examples demonstrating how to integrate the BPMN Analysis System into your applications. The examples cover various use cases from basic process upload to advanced AI-enhanced analysis.

## Project Structure

```
java/
├── basic-integration/          # Basic BPMN integration examples
│   ├── pom.xml
│   └── src/main/java/
├── advanced-integration/       # Advanced features and customization
│   ├── pom.xml
│   └── src/main/java/
├── spring-boot-starter/        # Spring Boot Auto Configuration
│   ├── pom.xml
│   └── src/main/java/
├── test-examples/              # Unit and integration tests
│   ├── pom.xml
│   └── src/test/java/
└── microservices/              # Microservice integration examples
    ├── pom.xml
    └── src/main/java/
```

## Prerequisites

- Java 21 or higher
- Maven 3.8+ or Gradle 8+
- Spring Boot 3.x
- SecurityOrchestrator BPMN Analysis System running

## Getting Started

1. Clone the examples repository
2. Choose the appropriate example for your use case
3. Update the configuration with your SecurityOrchestrator instance
4. Run the example

### Basic Example

```bash
cd basic-integration
mvn spring-boot:run
```

### Advanced Example

```bash
cd advanced-integration
mvn spring-boot:run
```

## Examples Overview

### 1. Basic Process Upload and Analysis

Demonstrates:
- BPMN file upload
- Starting security analysis
- Retrieving results

**File**: `basic-integration/src/main/java/BasicBPMNExample.java`

### 2. Async Analysis with WebSocket

Demonstrates:
- Asynchronous analysis processing
- Real-time progress updates via WebSocket
- Error handling and retries

**File**: `advanced-integration/src/main/java/AsyncAnalysisExample.java`

### 3. AI-Enhanced Analysis

Demonstrates:
- Integration with AI providers
- Custom AI analysis prompts
- Fallback strategies

**File**: `advanced-integration/src/main/java/AIEnhancedAnalysisExample.java`

### 4. Compliance Testing

Demonstrates:
- GDPR compliance analysis
- PCI-DSS security validation
- Custom compliance rules

**File**: `advanced-integration/src/main/java/ComplianceAnalysisExample.java`

### 5. Microservice Integration

Demonstrates:
- Service-to-service communication
- Circuit breaker patterns
- Distributed tracing

**File**: `microservices/src/main/java/BPMNAnalysisMicroservice.java`

## Configuration

Update the following properties in `application.yml`:

```yaml
security-orchestrator:
  bpmn:
    base-url: "http://localhost:8080/api/v1/bpmn"
    api-key: "your-api-key"
    timeout: 30000
    max-retries: 3
```

## Running the Examples

### Prerequisites
- SecurityOrchestrator running on localhost:8080
- API key configured
- Optional: AI providers configured

### Execute Examples

```bash
# Build all examples
mvn clean install

# Run specific example
mvn spring-boot:run -Dspring-boot.run.profiles=basic

# Run with custom configuration
mvn spring-boot:run -Dspring-boot.run.arguments="--bpmn.base-url=http://your-host:8080/api/v1/bpmn"
```

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=BasicBPMNExampleTest

# Run integration tests
mvn verify -Pintegration-tests
```

## Customization

Each example is designed to be easily customizable:

- **Configuration**: Modify application properties
- **Extensibility**: Extend base classes for custom functionality
- **Integration**: Adapt to your existing Spring Boot applications

## Support

For issues with the examples:
1. Check the [Troubleshooting Guide](../TROUBLESHOOTING.md)
2. Review the [API Documentation](../API_DOCUMENTATION.md)
3. Contact the development team

## Next Steps

- Explore advanced features in the [Advanced Integration Guide](../INTEGRATION_GUIDE.md)
- Review the complete [API Documentation](../API_DOCUMENTATION.md)
- Check out the [Examples and Tutorials](../EXAMPLES_AND_TUTORIALS.md)