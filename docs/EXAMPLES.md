# SecurityOrchestrator LLM Examples and Configuration

## Overview

This document provides practical examples and configuration samples for using the LLM features in SecurityOrchestrator. It includes API call examples, configuration samples, and common use cases for both OpenRouter and local Ollama integrations.

## Table of Contents

- [Quick Start Examples](#quick-start-examples)
- [Configuration Examples](#configuration-examples)
- [API Call Examples](#api-call-examples)
- [Local LLM Setup Examples](#local-llm-setup-examples)
- [OpenRouter Setup Examples](#openrouter-setup-examples)
- [Use Case Examples](#use-case-examples)
- [Troubleshooting Examples](#troubleshooting-examples)

## Quick Start Examples

### 1. Basic LLM Test

Test your LLM setup with a simple request:

```bash
curl -X POST http://localhost:8080/api/v1/llm/test \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "LOCAL",
    "model": "llama2",
    "testMessage": "Hello, can you help generate test data?"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "response": "Hello! I'd be happy to help you generate test data...",
    "tokensUsed": 15,
    "cost": null,
    "model": "llama2",
    "provider": "LOCAL"
  },
  "timestamp": "2025-11-07T17:26:46.509Z"
}
```

### 2. List Available Models

```bash
curl -X GET http://localhost:8080/api/v1/llm/models
```

**Response:**
```json
{
  "success": true,
  "data": {
    "providers": {
      "local": {
        "name": "Local Ollama",
        "status": "CONNECTED",
        "models": ["llama2", "codellama", "mistral"]
      },
      "openrouter": {
        "name": "OpenRouter",
        "status": "DISCONNECTED",
        "reason": "No API key configured"
      }
    },
    "activeProvider": "LOCAL",
    "activeModel": "llama2"
  }
}
```

## Configuration Examples

### Application Properties

Create or update `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# LLM Configuration
llm.openRouterApiKey=sk-or-v1-your-api-key-here
llm.openRouterBaseUrl=https://openrouter.ai/api/v1
llm.openRouterTimeout=30
llm.openRouterReferer=http://localhost:8080
llm.openRouterAppName=SecurityOrchestrator

# Local LLM Configuration
llm.localServerUrl=http://localhost:11434
llm.localServerPort=11434
llm.localTimeout=300
llm.maxLocalModels=3

# Performance Configuration
llm.maxRetries=3
llm.connectionPoolSize=10
llm.maxConcurrentRequests=5

# Model-specific configurations
llm.models.llama2.provider=LOCAL
llm.models.llama2.contextWindow=4096
llm.models.llama2.maxTokens=2048
llm.models.llama2.temperature=0.7

llm.models.claude-3-haiku.provider=OPENROUTER
llm.models.claude-3-haiku.contextWindow=8192
llm.models.claude-3-haiku.maxTokens=4096
llm.models.claude-3-haiku.temperature=0.1

llm.models.gpt-3.5-turbo.provider=OPENROUTER
llm.models.gpt-3.5-turbo.contextWindow=4096
llm.models.gpt-3.5-turbo.maxTokens=2048
llm.models.gpt-3.5-turbo.temperature=0.3
```

### Environment Variables

```bash
# OpenRouter API Key
export OPENROUTER_API_KEY="sk-or-v1-your-api-key-here"

# Local LLM Settings
export OLLAMA_HOST="http://localhost:11434"
export LLM_LOCAL_TIMEOUT="300"
export LLM_MAX_MODELS="3"

# Performance Settings
export LLM_MAX_CONCURRENT="5"
export LLM_CONNECTION_POOL="10"
```

## API Call Examples

### 1. Chat Completion - Local LLM

```bash
curl -X POST http://localhost:8080/api/v1/llm/chat/complete \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama2",
    "provider": "LOCAL",
    "messages": [
      {
        "role": "system",
        "content": "You are a security testing expert. Generate test data for API validation."
      },
      {
        "role": "user",
        "content": "Generate test data for a user registration API with email, password, and name fields."
      }
    ],
    "maxTokens": 500,
    "temperature": 0.7
  }'
```

**Response:**
```json
{
  "success": true,
  "data": {
    "response": "Here are some test data examples for a user registration API:\n\n1. Valid User:\n   - Email: testuser@example.com\n   - Password: SecurePass123!\n   - Name: John Doe\n\n2. Edge Cases:\n   - Email: user@domain.co.uk\n   - Password: A!1b2c3d4e5\n   - Name: María José\n\n3. Security Tests:\n   - Email: admin@test.com\n   - Password: password\n   - Name: <script>alert('xss')</script>",
    "tokensUsed": 78,
    "cost": null,
    "model": "llama2",
    "provider": "LOCAL"
  },
  "timestamp": "2025-11-07T17:26:46.509Z"
}
```

### 2. Chat Completion - OpenRouter

```bash
curl -X POST http://localhost:8080/api/v1/llm/chat/complete \
  -H "Content-Type: application/json" \
  -d '{
    "model": "anthropic/claude-3-haiku",
    "provider": "OPENROUTER",
    "messages": [
      {
        "role": "user",
        "content": "Analyze this API specification and suggest potential security test cases:\n\nPOST /api/users\n- Body: { email: string, password: string, name: string }\n- Response: 201 Created\n- Authentication: JWT Bearer token required"
      }
    ],
    "maxTokens": 1000,
    "temperature": 0.3
  }'
```

### 3. Get LLM Configuration

```bash
curl -X GET http://localhost:8080/api/v1/llm/config
```

**Response:**
```json
{
  "success": true,
  "data": {
    "activeProvider": "LOCAL",
    "activeModel": "llama2",
    "providers": {
      "local": {
        "name": "Local Ollama",
        "status": "CONNECTED",
        "baseUrl": "http://localhost:11434",
        "timeout": 300
      },
      "openrouter": {
        "name": "OpenRouter",
        "status": "DISCONNECTED",
        "baseUrl": "https://openrouter.ai/api/v1",
        "timeout": 30,
        "hasApiKey": true
      }
    },
    "models": {
      "llama2": {
        "provider": "LOCAL",
        "contextWindow": 4096,
        "maxTokens": 2048,
        "temperature": 0.7,
        "status": "AVAILABLE"
      },
      "claude-3-haiku": {
        "provider": "OPENROUTER",
        "contextWindow": 8192,
        "maxTokens": 4096,
        "temperature": 0.1,
        "status": "CONFIGURED"
      }
    }
  }
}
```

## Local LLM Setup Examples

### 1. Install and Start Ollama

```bash
# macOS
brew install ollama
ollama serve

# Linux
curl -fsSL https://ollama.ai/install.sh | sh
ollama serve

# Windows
# Download from https://ollama.ai and run installer
ollama serve
```

### 2. Pull Models

```bash
# Install popular models for security testing
ollama pull llama2
ollama pull codellama
ollama pull mistral
ollama pull phi3

# Check installed models
ollama list
```

### 3. List Local Models via API

```bash
curl -X GET http://localhost:8080/api/v1/llm/local/models
```

**Response:**
```json
{
  "success": true,
  "data": {
    "models": [
      {
        "name": "llama2",
        "size": "3.8 GB",
        "modifiedAt": "2025-11-07T10:30:00Z",
        "details": {
          "format": "gguf",
          "family": "llama",
          "parameterSize": "7B"
        }
      },
      {
        "name": "codellama",
        "size": "3.8 GB", 
        "modifiedAt": "2025-11-07T09:15:00Z",
        "details": {
          "format": "gguf",
          "family": "llama",
          "parameterSize": "7B",
          "specialization": "code"
        }
      }
    ],
    "totalSize": "7.6 GB",
    "loadedCount": 1,
    "maxCount": 3
  }
}
```

## OpenRouter Setup Examples

### 1. Sign up and Get API Key

1. Visit [https://openrouter.ai](https://openrouter.ai)
2. Create an account
3. Navigate to Keys section
4. Create a new API key
5. Add billing information (if using paid models)

### 2. Test OpenRouter Connection

```bash
# Direct API test
curl -H "Authorization: Bearer sk-or-v1-your-api-key-here" \
     https://openrouter.ai/api/v1/models

# Via SecurityOrchestrator
curl -X POST http://localhost:8080/api/v1/llm/test \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "OPENROUTER",
    "model": "anthropic/claude-3-haiku",
    "testMessage": "Hello, this is a test message."
  }'
```

### 3. Popular Models for Security Testing

```bash
# Cost-effective options
ollama test with: gpt-3.5-turbo (OpenAI)
ollama test with: mistralai/mistral-7b-instruct

# Advanced capabilities
ollama test with: anthropic/claude-3-sonnet
ollama test with: openai/gpt-4

# Open source options
ollama test with: meta-llama/llama-2-70b-chat
ollama test with: NousResearch/Nous-Hermes-2-Mixtral-8x7B-DPO
```

## Use Case Examples

### 1. Security Test Data Generation

```bash
curl -X POST http://localhost:8080/api/v1/llm/chat/complete \
  -H "Content-Type: application/json" \
  -d '{
    "model": "codellama",
    "provider": "LOCAL",
    "messages": [
      {
        "role": "system", 
        "content": "You are a security expert. Generate comprehensive test data for API security testing."
      },
      {
        "role": "user",
        "content": "Generate test data for a banking API endpoint: POST /api/accounts/{id}/transfer\n- Body: { fromAccount: string, toAccount: string, amount: number, description: string }\n- Authentication: Bearer token required\n- Create test cases for: SQL injection, XSS, input validation, authentication bypass"
      }
    ],
    "maxTokens": 1500,
    "temperature": 0.3
  }'
```

### 2. BPMN Process Analysis

```bash
curl -X POST http://localhost:8080/api/v1/llm/chat/complete \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama2",
    "provider": "LOCAL", 
    "messages": [
      {
        "role": "system",
        "content": "You are a business process analyst. Analyze BPMN processes for security testing opportunities."
      },
      {
        "role": "user",
        "content": "Analyze this BPMN workflow for security vulnerabilities:\n- Process: Credit Card Approval\n- Steps: Application Submission → Identity Verification → Credit Check → Approval Decision\n- Decision Points: Credit Score > 700, Income Verification, Criminal Background Check\n- Generate security test scenarios for each step."
      }
    ],
    "maxTokens": 1000,
    "temperature": 0.4
  }'
```

### 3. OpenAPI Specification Analysis

```bash
curl -X POST http://localhost:8080/api/v1/llm/chat/complete \
  -H "Content-Type: application/json" \
  -d '{
    "model": "claude-3-haiku",
    "provider": "OPENROUTER",
    "messages": [
      {
        "role": "system",
        "content": "You are an API security specialist. Analyze OpenAPI specifications for security testing."
      },
      {
        "role": "user", 
        "content": "Analyze this OpenAPI spec for security issues:\n\nopenapi: 3.0.0\ninfo:\n  title: User Management API\n  version: 1.0.0\npaths:\n  /users:\n    post:\n      requestBody:\n        required: true\n        content:\n          application/json:\n            schema:\n              type: object\n              properties:\n                email: { type: string }\n                password: { type: string }\n                role: { type: string }\n\nGenerate security test cases and identify potential vulnerabilities."
      }
    ],
    "maxTokens": 800,
    "temperature": 0.2
  }'
```

## Troubleshooting Examples

### 1. Connection Issues

**Problem: OpenRouter returns 401 Unauthorized**

```bash
# Test API key directly
curl -H "Authorization: Bearer sk-or-v1-invalid-key" \
     https://openrouter.ai/api/v1/models

# Check SecurityOrchestrator configuration
curl -X GET http://localhost:8080/api/v1/llm/config | jq '.data.providers.openrouter'
```

**Solution:**
```bash
# Verify environment variable
echo $OPENROUTER_API_KEY

# Update application.properties
llm.openRouterApiKey=sk-or-v1-valid-key-here
```

### 2. Local LLM Issues

**Problem: Local LLM not responding**

```bash
# Check if Ollama is running
curl http://localhost:11434/api/tags

# Check SecurityOrchestrator health
curl -X GET http://localhost:8080/actuator/health
```

**Solution:**
```bash
# Restart Ollama
killall ollama
ollama serve

# Verify model is downloaded
ollama list

# Test via SecurityOrchestrator
curl -X POST http://localhost:8080/api/v1/llm/test \
  -H "Content-Type: application/json" \
  -d '{"provider": "LOCAL", "model": "llama2", "testMessage": "Hello"}'
```

### 3. Performance Issues

**Problem: Slow response times**

```bash
# Check performance metrics
curl -X GET http://localhost:8080/api/v1/llm/performance

# Test with reduced complexity
curl -X POST http://localhost:8080/api/v1/llm/chat/complete \
  -H "Content-Type: application/json" \
  -d '{
    "model": "llama2",
    "provider": "LOCAL",
    "messages": [{"role": "user", "content": "Hello"}],
    "maxTokens": 100,
    "temperature": 0.1
  }'
```

**Solutions:**
```properties
# Reduce model complexity
llm.models.llama2.maxTokens=1000
llm.models.llama2.temperature=0.1

# Increase timeout
llm.localTimeout=600

# Reduce concurrent requests
llm.maxConcurrentRequests=3
```

### 4. Memory Issues

**Problem: OutOfMemoryError with large models**

```bash
# Monitor memory usage
jstat -gc $(pgrep java)

# Check model sizes
ollama list
```

**Solutions:**
```properties
# Use smaller models
ollama pull phi3  # 3.8B parameters vs 7B for llama2

# Limit concurrent models
llm.maxLocalModels=1

# Increase JVM memory
java -Xmx4g -jar securityorchestrator.jar
```

## Advanced Configuration

### Custom Retry Logic

```properties
# Custom retry configuration
llm.maxRetries=5
llm.retryDelayMs=1000
llm.backoffMultiplier=2.0
llm.maxRetryDelayMs=30000
```

### Circuit Breaker Configuration

```properties
# Circuit breaker settings
llm.circuitBreaker.failureRateThreshold=50
llm.circuitBreaker.waitDurationInOpenState=30s
llm.circuitBreaker.slidingWindowSize=10
llm.circuitBreaker.minimumNumberOfCalls=5
```

### Performance Monitoring

```properties
# Enable detailed metrics
llm.metrics.enabled=true
llm.metrics.histogram.enabled=true
llm.metrics.timer.enabled=true

# Health check configuration
llm.health.check-interval=30s
llm.health.timeout=5s
```

## Example Java Integration

```java
@RestController
@RequestMapping("/api/v1/security-tests")
public class SecurityTestController {
    
    @Autowired
    private LLMService llmService;
    
    @PostMapping("/generate-tests")
    public CompletableFuture<ResponseEntity<ApiResponse<GeneratedTests>>> generateSecurityTests(
            @RequestBody SecurityTestRequest request) {
        
        return llmService.generateCompletion(
            ChatCompletionRequest.builder()
                .provider(request.getProvider())
                .model(request.getModel())
                .prompt(buildSecurityTestPrompt(request))
                .maxTokens(2000)
                .temperature(0.3)
                .build()
        ).thenApply(response -> {
            GeneratedTests tests = parseSecurityTests(response.getResponse());
            return ResponseEntity.ok(ApiResponse.success(tests));
        });
    }
    
    private String buildSecurityTestPrompt(SecurityTestRequest request) {
        return String.format("""
            Generate comprehensive security test cases for:
            
            API: %s
            Method: %s
            Parameters: %s
            
            Include:
            1. Input validation tests
            2. Authentication bypass attempts
            3. Authorization tests
            4. Injection attack vectors
            5. Business logic vulnerabilities
            
            Format as JSON array of test cases.
            """, 
            request.getApiPath(), 
            request.getMethod(), 
            request.getParameters());
    }
}
```

This comprehensive examples document provides developers with practical guidance for implementing and using the LLM features in SecurityOrchestrator, from basic setup to advanced use cases and troubleshooting.